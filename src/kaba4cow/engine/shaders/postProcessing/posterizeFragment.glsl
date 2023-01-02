#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float levels;

uniform sampler2D colorTexture;

void main(void) {
	out_Color = texture2D(colorTexture, textureCoords);
	
	float levelR = round(out_Color.r * levels);
	out_Color.r = levelR / levels;
	
	float levelG = round(out_Color.g * levels);
	out_Color.g = levelG / levels;
	
	float levelB = round(out_Color.b * levels);
	out_Color.b = levelB / levels;
}
