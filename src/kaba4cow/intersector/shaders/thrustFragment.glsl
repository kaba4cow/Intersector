#version 110

in vec2 pass_textureCoords;
in float falloff;

out vec4 out_Color;

uniform sampler2D diffuseMap;

uniform float brightness;

void main(void) {
	out_Color = texture2D(diffuseMap, pass_textureCoords);
	if (out_Color.a <= 0.0)
		discard;
	out_Color.a *= falloff * brightness;
}
