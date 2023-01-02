#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D textureMap;

void main(void) {
	out_Color = texture2D(textureMap, textureCoords);
}
