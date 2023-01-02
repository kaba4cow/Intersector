#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D colorTexture1;
uniform sampler2D colorTexture2;

uniform float intensity;

void main(void) {
	vec4 color1 = texture2D(colorTexture1, textureCoords);
	vec4 color2 = texture2D(colorTexture2, textureCoords);
	out_Color = color1 + intensity * color2;
}
