#version 110

attribute vec3 position;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
}
