#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float blendFactor;
uniform vec3 color;

uniform sampler2D colorTexture;

void main(void) {
	vec3 a = texture2D(colorTexture, textureCoords).rgb;
	vec3 b = color;
	if (color.r < 0.0)
		b = texture2D(colorTexture, textureCoords).rgb;
	
	float greyscale = b.r * LUM.r + b.g * LUM.g + b.b * LUM.b;
	vec3 res = vec3(greyscale);
	
	out_Color = vec4(mix(a, res, blendFactor), 1.0);
}