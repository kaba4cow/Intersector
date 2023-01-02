#version 110

in vec2 textureCoords;

out vec4 out_Color;

uniform float targetWidth;
uniform float targetHeight;
uniform float curvature;
uniform float rayFrequency;
uniform float time;

uniform sampler2D colorTexture;

void main(void) {
	float invCurvature = 1.0 / curvature;

	float minWarpedCoords = -0.5 * invCurvature * invCurvature;
	float maxWarpedCoords = 1.0 + 0.5 * invCurvature * invCurvature;
	
	vec2 warpedCoords = textureCoords * 2.0 - 1.0;
	vec2 offset = warpedCoords * invCurvature;
	warpedCoords = warpedCoords + warpedCoords * offset * offset;
	warpedCoords = warpedCoords * 0.5 + 0.5;
	
	warpedCoords.x = 1.0 * ((warpedCoords.x - minWarpedCoords) / (maxWarpedCoords - minWarpedCoords));
	warpedCoords.y = 1.0 * ((warpedCoords.y - minWarpedCoords) / (maxWarpedCoords - minWarpedCoords));
	
	if (warpedCoords.x < 0.0 || warpedCoords.y < 0.0 || warpedCoords.x > 1.0 || warpedCoords.y > 1.0)
		discard;
	
	vec2 vignetteCoords = warpedCoords * 2.0 - 1.0;
	vignetteCoords = 1.0 - abs(vignetteCoords);
	vec2 dimensions = 0.1 * vec2(targetHeight / targetWidth, 1.0);
	vec2 brightness = smoothstep(vec2(0.0), dimensions, vignetteCoords);
	
	float rayCoord = 2.0 * rayFrequency * textureCoords.y * targetHeight + time;
	float raySin = 1.0 + 0.15 * (1.0 + sin(rayCoord));
	float rayCos = 1.0 + 0.1 * (1.0 + cos(rayCoord));

	out_Color = brightness.x * brightness.y * texture2D(colorTexture, warpedCoords);
	out_Color.g *= raySin;
	out_Color.rb *= rayCos;
}
