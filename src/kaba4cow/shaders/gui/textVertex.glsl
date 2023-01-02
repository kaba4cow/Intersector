#version 110

attribute vec2 position;
attribute vec2 textureCoords;

out vec2 screenPosition;
out vec2 pass_textureCoords;

uniform vec2 translation;

void main(void) {
	gl_Position = vec4(position + translation - vec2(0.0, 1.0), 0.0, 1.0);
	screenPosition = gl_Position.xy;
	pass_textureCoords = textureCoords;
}
