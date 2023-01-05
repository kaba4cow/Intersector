#version 110

in vec2 screenPosition;
in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform float time;

void main(void) {
	float sin = pow(sin(200.0 * screenPosition.y + 10.0 * time), 2.0);
	sin = mix(sin, 1.0, 0.9);
	
	vec2 centerDist = abs(2.0 * textureCoords - 1.0);
	centerDist.x = 1.0 - pow(centerDist.x, 8.0);
	centerDist.y = 1.0 - pow(centerDist.y, 8.0);
	
	float alpha = centerDist.x * centerDist.y * sin;
	
	out_Color = vec4(color, 0.75 * alpha);
}
