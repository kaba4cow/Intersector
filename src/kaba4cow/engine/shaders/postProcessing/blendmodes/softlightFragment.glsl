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
	if (L <= 0.5)
		res = 2.0 * a * b + a * a * (1.0 - 2.0 * b);
	else {
		vec3 a_sqr = vec3(pow(a.r, 0.5), pow(a.g, 0.5), pow(a.b, 0.5));
		res = 2.0 * a * (1.0 - b) + a_sqr * (2.0 * b - 1.0);
	}

	out_Color = vec4(mix(a, res, blendFactor), 1.0);
}
