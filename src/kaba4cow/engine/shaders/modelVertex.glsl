#version 110

attribute vec3 position;
attribute vec2 textureCoords;
attribute vec3 normal;

out vec4 pass_worldPosition;
out vec2 pass_textureCoords;
out vec3 surfaceNormal;
out vec3 toCameraVector;
out vec3 reflectedVector;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 invViewMatrix;
uniform mat4 transformationMatrix;

uniform vec3 cameraPosition;

uniform vec2 texOffset;
uniform float numberOfRows;

void main(void) {
	pass_worldPosition = transformationMatrix * vec4(position, 1.0);

	gl_Position = projectionMatrix * viewMatrix * pass_worldPosition;
	pass_textureCoords = textureCoords / numberOfRows + texOffset;

	surfaceNormal = normalize((transformationMatrix * vec4(normal, 0.0)).xyz);

	toCameraVector = (invViewMatrix * vec4(0.0, 0.0, 0.0, 1.0)).xyz
			- pass_worldPosition.xyz;

	vec3 viewVector = normalize(pass_worldPosition.xyz - cameraPosition);
	reflectedVector = reflect(viewVector, surfaceNormal);
}
