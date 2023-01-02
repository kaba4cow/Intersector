#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float contrast;

uniform sampler2D colorTexture;

void main(void) {
	out_Color = texture2D(colorTexture, textureCoords);
	out_Color.rgb = (out_Color.rgb - vec3(0.5)) * (1.0 + contrast) + vec3(0.5);
}
