#version 110

attribute vec2 position;

out vec2 screenPosition;
out vec2 textureCoords;

uniform mat4 transformationMatrix;

void main(void) {
	gl_Position = transformationMatrix * vec4(position, 0.0, 1.0);
	screenPosition = gl_Position.xy;
	textureCoords = vec2(0.5 * (position.x + 1.0),
			1.0 - 0.5 * (position.y + 1.0));
}
