#version 110

in vec2 pass_textureCoords;

out vec4 out_Color;

uniform vec3 color;

uniform sampler2D textureMap;

void main(void) {
	vec4 textureColor = texture2D(textureMap, pass_textureCoords);
	out_Color = vec4(color, textureColor.a);
}
