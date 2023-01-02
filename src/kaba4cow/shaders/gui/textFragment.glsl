#version 110

in vec2 screenPosition;
in vec2 pass_textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform float time;

uniform sampler2D textureMap;

void main(void) {
	float sin = pow(sin(200.0 * screenPosition.y + 10.0 * time), 2.0);
	sin = mix(sin, 1.0, 0.9);
	
	float alpha = pow(sin, 4.0);
	
	vec4 textureColor = texture2D(textureMap, pass_textureCoords);
	out_Color = vec4(color, alpha * textureColor.a);
}
