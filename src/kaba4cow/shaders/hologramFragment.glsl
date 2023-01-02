#version 110

in vec3 pass_position;
in vec2 pass_textureCoords;
in vec2 pass_hologramCoords;

out vec4 out_Color;

uniform vec3 color;
uniform float brightness;

uniform float usesTexture;

uniform sampler2D colorTexture;
uniform samplerCube colorCube;
uniform sampler2D hologramTexture;

void main(void) {
	float hologramColor = texture2D(hologramTexture, pass_hologramCoords).r;
	if (hologramColor <= 0.0)
		discard;
		
	vec4 textureColor = vec4(1.0);
	if (usesTexture >= 1.0)
		textureColor = texture2D(colorTexture, pass_textureCoords);
	else if (usesTexture <= -1.0)
		textureColor = textureCube(colorCube, pass_position);
	
	float greyscale = textureColor.r * LUM.r + textureColor.g * LUM.g + textureColor.b * LUM.b;
	float value = hologramColor * greyscale * brightness;
	out_Color = vec4(color, value);
}
