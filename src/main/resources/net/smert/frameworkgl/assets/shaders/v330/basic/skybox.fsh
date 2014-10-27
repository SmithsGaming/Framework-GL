#version 330

////////////////////////////////////////////////////////////////////////////////
// In/Out Variables                                                           //
////////////////////////////////////////////////////////////////////////////////
in vec4 pass_Color;
in vec4 pass_TexCoord0;
out vec4 out_Color;

////////////////////////////////////////////////////////////////////////////////
// Default Uniforms                                                           //
////////////////////////////////////////////////////////////////////////////////
uniform float uTextureFlag = 0.0;
uniform samplerCube uTexture0; // Environment texture

////////////////////////////////////////////////////////////////////////////////
// Main                                                                       //
////////////////////////////////////////////////////////////////////////////////
void main(void)
{
    vec4 textureColor = texture(uTexture0, pass_TexCoord0.xyz);
    out_Color = mix(pass_Color, pass_Color * textureColor, uTextureFlag); // Mix interpolated color and texture
}