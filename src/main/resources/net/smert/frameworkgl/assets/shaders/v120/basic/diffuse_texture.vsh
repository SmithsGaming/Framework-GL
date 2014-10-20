#version 120

////////////////////////////////////////////////////////////////////////////////
// In/Out Variables                                                           //
////////////////////////////////////////////////////////////////////////////////
attribute vec4 in_Vertex;
attribute vec4 in_Color;
attribute vec4 in_TexCoord0;
varying vec4 pass_Color;
varying vec4 pass_TexCoord0;

////////////////////////////////////////////////////////////////////////////////
// Default Uniforms                                                           //
////////////////////////////////////////////////////////////////////////////////
uniform mat4 uProjectionViewModelMatrix;

////////////////////////////////////////////////////////////////////////////////
// Main                                                                       //
////////////////////////////////////////////////////////////////////////////////
void main(void)
{
    pass_Color = in_Color;
    pass_TexCoord0 = in_TexCoord0;
    gl_Position = uProjectionViewModelMatrix * in_Vertex;
}