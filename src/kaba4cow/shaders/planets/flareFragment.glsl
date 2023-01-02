#version 110

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform sampler2D diffuseMap;

uniform vec3 color;
uniform float brightness;

void main(void) {
	if (brightness <= 0.0)
		discard;
	vec4 textureColor = texture2D(diffuseMap, pass_textureCoords);

	out_Color = brightness * vec4(color, 1.0) * textureColor;
}
