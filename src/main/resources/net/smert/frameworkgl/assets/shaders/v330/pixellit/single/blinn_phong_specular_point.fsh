#version 330

////////////////////////////////////////////////////////////////////////////////
// http://www.glprogramming.com/red/chapter05.html                            //
//                                                                            //
// color = (matEmission + globalAmbient * matAmbient) +                       //
//         AttenuationFactor( 1.0 / ( Kc + Kl*d + Kq*d^2 ) ) *                //
//         [ (lightAmbient * matAmbient) +                                    //
//           (max(N.L,0) * lightDiffuse * matDiffuse) +                       //
//           (max(N.H,0)^matShininess * lightSpecular * matSpecular) ]        //
////////////////////////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////////
// In/Out Variables                                                           //
////////////////////////////////////////////////////////////////////////////////
in vec3 pass_Normal;
in vec4 pass_Vertex;
in vec4 pass_Color;
in vec4 pass_TexCoord0;
out vec4 out_Color;

////////////////////////////////////////////////////////////////////////////////
// Default Uniforms                                                           //
////////////////////////////////////////////////////////////////////////////////
uniform float uTextureFlag = 0.0;
uniform sampler2D uTexture0; // Diffuse texture

////////////////////////////////////////////////////////////////////////////////
// Structs                                                                    //
////////////////////////////////////////////////////////////////////////////////
struct CustomLightSourceParameters
{
    float constantAttenuation;
    float linearAttenuation;
    float quadraticAttenuation;
    float radius;
    float spotInnerCutoffCos;
    float spotOuterCutoffCos;
    float spotExponent;
    vec3 spotEyeDirection;
    vec4 ambient;
    vec4 diffuse;
    vec4 eyePosition;
    vec4 specular;
};

struct CustomMaterialParameters
{
    float shininess;
    vec4 ambient;
    vec4 diffuse;
    vec4 emission;
    vec4 specular;
};

////////////////////////////////////////////////////////////////////////////////
// Uniforms                                                                   //
////////////////////////////////////////////////////////////////////////////////
uniform CustomLightSourceParameters uLight;
uniform CustomMaterialParameters uMaterialLight;
uniform vec4 uGlobalAmbientLight;

////////////////////////////////////////////////////////////////////////////////
// Main                                                                       //
////////////////////////////////////////////////////////////////////////////////
void main(void)
{
    // Calculate emission and global ambient light
    vec4 emissionAmbient = uMaterialLight.emission + (uGlobalAmbientLight * uMaterialLight.ambient);

    // Calculate ambient
    vec4 lightAmbient = uLight.ambient * uMaterialLight.ambient;

    // Calculate the light direction
    vec3 eyeLightDir = uLight.eyePosition.xyz - pass_Vertex.xyz;
    float dist = length(eyeLightDir);
    eyeLightDir = normalize(eyeLightDir);

    // No attenuation for a directional light
    float attenuationFactor = 1.0 / (uLight.constantAttenuation
                                   + uLight.linearAttenuation * dist
                                   + uLight.quadraticAttenuation * dist * dist);
    attenuationFactor *= clamp(1.0 - (dist * dist) / (uLight.radius * uLight.radius), 0.0, 1.0);

    // Calculate lambert term
    float NdotL = max(dot(pass_Normal, eyeLightDir), 0.0);

    // Calculate diffuse
    vec4 textureColor = texture(uTexture0, pass_TexCoord0.st);
    vec4 color = mix(pass_Color, pass_Color * textureColor, uTextureFlag); // Mix interpolated color and texture
    vec4 lightDiffuse = NdotL * (uLight.diffuse * uMaterialLight.diffuse * color);

    // Calculate specular
    vec4 lightSpecular = vec4(0.0);
    if ( NdotL > 0.000001 )
    {
        vec3 halfVector = normalize(eyeLightDir - pass_Vertex.xyz);
        float NdotHV = max(dot(pass_Normal, halfVector), 0.0);
        lightSpecular = pow(NdotHV, uMaterialLight.shininess) * (uLight.specular * uMaterialLight.specular);
    }

    out_Color = emissionAmbient + attenuationFactor * (lightAmbient + lightDiffuse + lightSpecular);
}