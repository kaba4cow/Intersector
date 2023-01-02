#version 110

in vec2 textureCoords1;
in vec2 textureCoords2;
in float blendFactor;
in float brightness;

out vec4 out_Color;

uniform vec3 tint;

uniform sampler2D textureMap;

void main(void) {
	if (brightness <= 0.0)
		discard;
	vec4 color1 = texture2D(textureMap, textureCoords1);
	vec4 color2 = texture2D(textureMap, textureCoords2);
	vec4 color = mix(color1, color2, blendFactor);
	if (color.a <= 0.0)
		discard;
	out_Color = brightness * color * vec4(tint, 1.0);
}
