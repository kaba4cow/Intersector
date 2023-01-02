#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float targetWidth;
uniform float targetHeight;
uniform float intensity;

uniform sampler2D textureMap;

void main(void) {
	float pixelWidth = 1.0 / targetWidth;
	float pixelHeight = 1.0 / targetHeight;
	
	float center = 1.0 - 0.5 * intensity;
	float neighbor = 0.125 * intensity;
	
	out_Color = center * texture2D(textureMap, textureCoords);
	out_Color += neighbor * texture2D(textureMap, textureCoords + vec2(pixelWidth, 0.0));
	out_Color += neighbor * texture2D(textureMap, textureCoords + vec2(-pixelWidth, 0.0));
	out_Color += neighbor * texture2D(textureMap, textureCoords + vec2(0.0, pixelHeight));
	out_Color += neighbor * texture2D(textureMap, textureCoords + vec2(0.0, -pixelHeight));
}
