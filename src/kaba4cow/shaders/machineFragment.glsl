#version 110

in vec4 pass_worldPosition;
in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toCameraVector;
in vec3 reflectedVector;

out vec4 out_Color;

uniform sampler2D diffuseMap;
uniform samplerCube cubeMap;

uniform vec3 lightPosition[LIGHTS];
uniform vec3 lightColor[LIGHTS];
uniform vec3 lightAttenuation[LIGHTS];
uniform vec3 ambientLighting;

uniform vec4 texInfo;
uniform vec3 color;

void main(void) {
	vec3 unitToCameraVector = normalize(toCameraVector);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	float shininess = texInfo.x;
	float shineDamperPower = texInfo.y;
	float shineDamper = pow(2.0, shineDamperPower);
	float reflectivity = texInfo.z;
	float emission = texInfo.w;

	vec4 textureColor = texture2D(diffuseMap, pass_textureCoords);
	if (emission < 0.0)
		textureColor = vec4(color, 1.0) * textureColor;
	else
		textureColor = vec4(0.5 * (color + vec3(1.0)), 1.0) * textureColor;

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

			if (shininess > 0.0) {
				vec3 lightDirection = -unitToLightVector;
				vec3 reflectedLightDirection = reflect(lightDirection,
						surfaceNormal);

				float specularFactor = dot(reflectedLightDirection,
						unitToCameraVector);
				specularFactor = max(specularFactor, 0.0);
				float dampFactor = pow(specularFactor, shineDamper);

				totalSpecular = totalSpecular
						+ (dampFactor * shininess * lightColor[i])
								/ attenuationFactor;
			}
		}
		totalDiffuse = max(totalDiffuse, ambientLighting);
	}

	out_Color = vec4(totalDiffuse, 1.0) * textureColor
			+ vec4(totalSpecular, 0.0);
	if (emission < 0.0 && reflectivity > 0.0) {
		vec4 reflectedColor = textureCube(cubeMap, reflectedVector);
		out_Color = out_Color + reflectedColor * reflectivity;
	}
	out_Color.a = 1.0;
}
