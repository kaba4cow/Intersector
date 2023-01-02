#version 110

attribute vec2 position;

out vec2 textureCoords1;
out vec2 textureCoords2;
out float blendFactor;
out float brightness;

uniform mat4 projectionMatrix;
uniform mat4 modelViewMatrix;

uniform vec2 texOffset1;
uniform vec2 texOffset2;
uniform vec3 texInfo;

void main(void) {
	vec2 textureCoords = position + vec2(0.5, 0.5);
	textureCoords.y = 1.0 - textureCoords.y;
	textureCoords /= texInfo.x;
	textureCoords1 = textureCoords + texOffset1;
	textureCoords2 = textureCoords + texOffset2;
	blendFactor = texInfo.y;
	brightness = texInfo.z;

	gl_Position = projectionMatrix * modelViewMatrix * vec4(position, 0.0, 1.0);
}
