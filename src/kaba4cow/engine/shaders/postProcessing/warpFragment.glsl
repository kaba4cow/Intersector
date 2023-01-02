#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float power;

uniform sampler2D colorTexture;

void main(void) {
	vec4 originalColor = texture2D(colorTexture, textureCoords);
	float offset = power * length(originalColor.rgb);
	vec2 warpedCoords = textureCoords + vec2(offset, -offset);
	vec4 warpedColor = texture2D(colorTexture, warpedCoords);
	out_Color = mix(originalColor, warpedColor, 0.5);
}
