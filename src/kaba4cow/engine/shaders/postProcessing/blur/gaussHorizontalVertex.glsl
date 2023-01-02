#version 110

attribute vec2 position;

out vec2 blurTextureCoords[11];

uniform float targetWidth;

void main(void) {
	gl_Position = vec4(position, 0.0, 1.0);
	vec2 centerTexCoords = position * 0.5 + 0.5;

	float pixelSize = 1.0 / targetWidth;
	
	float x = -5.0 * pixelSize;
	for (int i = 0; i <= 10; i++) {
		blurTextureCoords[i] = centerTexCoords + vec2(x, 0.0);
		x += pixelSize;
	}
}
