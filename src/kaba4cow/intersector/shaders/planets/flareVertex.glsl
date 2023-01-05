#version 110

attribute vec3 position;
attribute vec2 textureCoords;

out vec2 pass_textureCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

uniform vec3 texOffset;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;

	pass_textureCoords.x = 2.0 * texOffset.y * textureCoords.x + 0.5 * texOffset.z * textureCoords.y + 0.02 * texOffset.x;
	pass_textureCoords.y = textureCoords.y;
}
