#version 110

attribute vec2 position;

out float pass_textureCoords;

void main(void) {
	gl_Position = vec4(position, 0.0, 1.0);
	pass_textureCoords = 0.5 * (position.y + 1.0);
}
