#version 110

attribute vec3 position;
attribute vec2 textureCoords;
attribute vec3 normal;

out vec4 pass_worldPosition;
out vec3 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toLightVector[LIGHTS];

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

void main(void) {
	pass_textureCoords = normalize(position);
	pass_worldPosition = transformationMatrix * vec4(pass_textureCoords, 1.0);

	gl_Position = projectionMatrix * viewMatrix * pass_worldPosition;

	surfaceNormal = normalize((transformationMatrix * vec4(normal, 0.0)).xyz);
}
