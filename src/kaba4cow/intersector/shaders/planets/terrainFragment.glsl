#version 110

in vec4 pass_worldPosition;
in vec3 pass_textureCoords;
in vec3 surfaceNormal;

out vec4 out_Color;

uniform vec3 lightPosition[LIGHTS];
uniform vec3 lightColor[LIGHTS];
uniform vec3 lightAttenuation[LIGHTS];
uniform vec3 ambientLighting;

uniform float emission;

uniform samplerCube cubeMap;

void main(void) {
	vec3 totalDiffuse = vec3(0.0);

	if (emission >= 0.0) {
		totalDiffuse = vec3(emission);
	} else {
		for (int i = 0; i < LIGHTS; i++) {
			if (lightAttenuation[i].x == 0.0)
				continue;
			vec3 toLightVector = lightPosition[i] - pass_worldPosition.xyz;

			float distance = length(toLightVector);
			float attenuationFactor = lightAttenuation[i].x
					+ (lightAttenuation[i].y * distance)
					+ (lightAttenuation[i].z * distance * distance);

			vec3 unitToLightVector = normalize(toLightVector);
			float nDot1 = dot(surfaceNormal, unitToLightVector);
			float brightness = max(nDot1, 0.0);
			
			totalDiffuse = totalDiffuse
					+ (brightness * lightColor[i]) / attenuationFactor;
		}
		totalDiffuse = max(totalDiffuse, ambientLighting);
	}

	vec3 textureColor = textureCube(cubeMap, pass_textureCoords).rgb;
	out_Color = vec4(totalDiffuse * textureColor, 1.0);
}
