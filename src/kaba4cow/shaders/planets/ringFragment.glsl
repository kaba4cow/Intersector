#version 110

in vec4 pass_worldPosition;
in vec2 pass_textureCoords;
in vec3 surfaceNormal;
in vec3 toCameraVector;
in vec3 reflectedVector;

out vec4 out_Color;

uniform sampler2D diffuseMap;

uniform vec3 lightPosition[LIGHTS];
uniform vec3 lightColor[LIGHTS];
uniform vec3 lightAttenuation[LIGHTS];
uniform vec3 ambientLighting;

uniform vec3 color;
uniform vec2 texInfo;

void main(void) {
	vec3 unitToCameraVector = normalize(toCameraVector);

	vec3 totalDiffuse = vec3(0.0);
	vec3 totalSpecular = vec3(0.0);

	float shininess = texInfo.x;
	float shineDamperPower = texInfo.y;
	float shineDamper = pow(2.0, shineDamperPower);

	vec4 textureColor = vec4(color, 1.0);
	float alpha = texture2D(diffuseMap, pass_textureCoords).r;
	if (alpha <= 0.0)
		discard;

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
		float brightness = abs(nDot1);
		brightness = 0.5 * brightness + 0.5;
			
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

	out_Color = vec4(totalDiffuse, 1.0) * textureColor
			+ vec4(totalSpecular, 0.0);
	out_Color.a = alpha;
}
