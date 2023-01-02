#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform sampler2D textureMap;
uniform float progress;

void main(void) {
	if (textureCoords.x > progress)
		discard;
	
	out_Color = texture2D(textureMap, textureCoords);
}
