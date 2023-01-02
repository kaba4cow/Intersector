#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float time;
uniform float power;

uniform sampler2D colorTexture;

float random(vec2 coords);

void main(void) {
	out_Color = texture2D(colorTexture, textureCoords);
	
	vec2 timeVec = vec2(time, -time);
	out_Color.r += power * random(textureCoords.xy + timeVec.xy);
	out_Color.g += power * random(textureCoords.xy + timeVec.yx);
	out_Color.b += power * random(textureCoords.yx + timeVec.xx);
}

float random(vec2 coords) {
	return fract(sin(dot(coords.xy, vec2(12.9898, 78.233))) * 43758.5453);
}
