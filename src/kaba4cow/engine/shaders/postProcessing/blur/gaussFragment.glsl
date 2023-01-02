#version 110

in vec2 blurTextureCoords[11];

out vec4 out_Color;

uniform sampler2D textureMap;

void main(void) {
	out_Color = vec4(0.0);
	int i = 0;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.0093;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.028002;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.065984;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.121703;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.175713;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.198596;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.175713;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.121703;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.065984;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.028002;
	out_Color += texture2D(textureMap, blurTextureCoords[i++]) * 0.0093;
}
