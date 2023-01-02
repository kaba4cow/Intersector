#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float exposure;

uniform sampler2D colorTexture;

void main(void) {	
	out_Color = exposure * texture2D(colorTexture, textureCoords);
	out_Color = clamp(out_Color, vec4(0.0), vec4(1.0));
}
