#version 110

attribute vec3 position;
attribute vec2 textureCoords;

out vec3 pass_position;
out vec2 pass_textureCoords;
out vec2 pass_hologramCoords;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

uniform vec2 texOffset;
uniform float time;
uniform float scale;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	
	pass_position = position;

	pass_textureCoords.x = textureCoords.x + texOffset.x * texOffset.y;
	pass_textureCoords.y = textureCoords.y;

	worldPosition /= scale;
	pass_hologramCoords.x = 0.5 + 0.5 * sin(0.1 * time);
	pass_hologramCoords.y = worldPosition.y / HEIGHT_SCALE - time;
}
