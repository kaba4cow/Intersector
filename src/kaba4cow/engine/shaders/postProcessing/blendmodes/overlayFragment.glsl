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
	
	float L = a.r * LUM.r + a.g * LUM.g + a.b * LUM.b;
	
	vec3 res;
	if (L >= 0.5)
		res = 1.0 - 2.0 * (1.0 - a) * (1.0 - b);
	else
		res = 2.0 * a * b;
	
	out_Color = vec4(mix(a, res, blendFactor), 1.0);
}
