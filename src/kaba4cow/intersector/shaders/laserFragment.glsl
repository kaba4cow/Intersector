#version 110

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D diffuseMap;

uniform float brightness;

void main(void) {
	vec4 textureColor = texture2D(diffuseMap, pass_textureCoords);
	textureColor.a *= brightness;
	
	if (textureColor.a <= 0.0)
		discard;

	out_Color = textureColor;
}
