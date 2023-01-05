#version 110

attribute vec3 position;
attribute vec2 textureCoords;

out float pass_generation;
out float pass_generationBlendFactor;
out vec3 pass_position;
out vec3 pass_scale;

uniform mat4 projectionMatrix;
uniform mat4 viewMatrix;
uniform mat4 transformationMatrix;

uniform vec3 scale;
uniform float generation;

void main(void) {
	vec4 worldPosition = transformationMatrix * vec4(position, 1.0);

	gl_Position = projectionMatrix * viewMatrix * worldPosition;
	
	pass_generation = fract(generation);
	pass_generationBlendFactor = fract(4.0 * pass_generation);
	pass_position = position;
	pass_scale = 0.5 * scale.z * scale.xyx;
}
