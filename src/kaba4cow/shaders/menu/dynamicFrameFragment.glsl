#version 110

in vec2 screenPosition;
in vec2 textureCoords;

out vec4 out_Color;

uniform vec3 color;
uniform float time;
uniform vec2 mouse;

void main(void) {
	float mouseDist = 1.0 - 2.0 * length(screenPosition - mouse);
	mouseDist = pow(max(mouseDist, 0.0), 2.0);
	mouseDist = (mouseDist + 0.5) / 1.5;
	
	float sin = sin(time + 0.5 * screenPosition.y);
	float border = 0.5 + 0.3 * sin;
	
	float distX = 1.0 - abs(textureCoords.x - border);
	float distY = 1.0 - 2.0 * abs(0.5 - textureCoords.y);
	distY = 0.25 * distY + 0.75;
	
	float alpha = mouseDist * distX * distX * distY;
	alpha = (alpha + 0.1) / 1.1;
	
	vec2 centerDist = abs(2.0 * textureCoords - 1.0);
	centerDist.x = 1.0 - pow(centerDist.x, 8.0);
	centerDist.y = 1.0 - pow(centerDist.y, 8.0);
	
	out_Color = vec4(color, alpha * centerDist.x * centerDist.y);
}
