#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float correction;

uniform sampler2D colorTexture;

void main(void) {
	out_Color = texture2D(colorTexture, textureCoords);
	out_Color.r = pow(out_Color.r, correction);
	out_Color.g = pow(out_Color.g, correction);
	out_Color.b = pow(out_Color.b, correction);
}
