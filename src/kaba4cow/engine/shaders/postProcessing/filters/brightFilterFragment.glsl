#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float gradient;

uniform sampler2D colorTexture;

void main(void) {
	vec4 color = texture2D(colorTexture, textureCoords);
	float brightness = color.r * LUM.r + color.g * LUM.g + color.b * LUM.b;
	out_Color = color * pow(brightness, gradient);
}
