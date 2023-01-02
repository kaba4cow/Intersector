#version 110

in vec3 textureCoords;

out vec4 out_Color;

uniform samplerCube cubeMap;

void main(void) {
	out_Color = textureCube(cubeMap, textureCoords);
}
