#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float targetWidth;
uniform float targetHeight;
uniform float sharpness;

uniform sampler2D colorTexture;

void main(void) {
	float pixelWidth = 1.0 / targetWidth;
	float pixelHeight = 1.0 / targetHeight;
	
	vec4 center = texture2D(colorTexture, textureCoords);
	vec4 neighbor1 = texture2D(colorTexture, textureCoords + vec2(-pixelWidth, 0.0));
	vec4 neighbor2 = texture2D(colorTexture, textureCoords + vec2(pixelWidth, 0.0));
	vec4 neighbor3 = texture2D(colorTexture, textureCoords + vec2(0.0, -pixelHeight));
	vec4 neighbor4 = texture2D(colorTexture, textureCoords + vec2(0.0, pixelHeight));
	
	out_Color = (1.0 + 4.0 * sharpness) * center - sharpness * (neighbor1 + neighbor2 + neighbor3 + neighbor4);
}
