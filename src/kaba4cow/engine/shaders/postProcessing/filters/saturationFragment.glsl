#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 saturation;

uniform sampler2D colorTexture;

void main(void) {	
	out_Color = texture2D(colorTexture, textureCoords);
	float greyscale = out_Color.r * LUM.r + out_Color.g * LUM.g + out_Color.b * LUM.b;
	out_Color.r = mix(greyscale, out_Color.r, saturation.r);
	out_Color.g = mix(greyscale, out_Color.g, saturation.g);
	out_Color.b = mix(greyscale, out_Color.b, saturation.b);
}
