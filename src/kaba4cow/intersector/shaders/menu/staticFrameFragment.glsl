#version 110

in vec2 screenPosition;
in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform vec2 mouse;

void main(void) {
	float mouseDist = 1.0 - 2.0 * length(screenPosition - mouse);
	mouseDist = pow(max(mouseDist, 0.0), 2.0);
	mouseDist = (mouseDist + 0.5) / 1.5;
	
	vec2 centerDist = abs(2.0 * textureCoords - 1.0);
	centerDist.x = 1.0 - pow(centerDist.x, 4.0);
	centerDist.y = 1.0 - pow(centerDist.y, 4.0);
	
	float dist = 1.0 - 2.0 * length(vec2(0.5) - textureCoords);
	float alpha = clamp(1.5 * dist, 0.0, 1.0);
	out_Color = vec4(color, alpha * mouseDist * centerDist.x * centerDist.y);
}
