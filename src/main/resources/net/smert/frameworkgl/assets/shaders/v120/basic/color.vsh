#version 120

////////////////////////////////////////////////////////////////////////////////
// Default Uniforms                                                           //
////////////////////////////////////////////////////////////////////////////////
uniform mat4 uProjectionViewModelMatrix;

////////////////////////////////////////////////////////////////////////////////
// Main                                                                       //
////////////////////////////////////////////////////////////////////////////////
void main(void)
{
    gl_FrontColor = gl_Color;
    gl_Position = uProjectionViewModelMatrix * gl_Vertex;
}