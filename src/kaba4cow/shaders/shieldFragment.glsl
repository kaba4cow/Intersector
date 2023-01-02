#version 110

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D diffuseMap;

uniform float brightness;

void main(void) {
	float alpha = texture2D(diffuseMap, pass_textureCoords).r * brightness;
	if (alpha <= 0.0)
		discard;
	out_Color = vec4(0.5, 0.85, 1.0, alpha);
}
