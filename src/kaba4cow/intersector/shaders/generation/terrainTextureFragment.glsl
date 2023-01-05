#version 110

in float pass_generation;
in float pass_generationBlendFactor;
in vec3 pass_position;
in vec3 pass_scale;

out vec4 out_Color;

uniform float seed;
uniform vec3 info;

uniform vec3 elementColor[ELEMENTS];
uniform vec2 elementInfo[ELEMENTS];

vec3 calculateColor(float value);

float generation_1(vec3 p);
float generation_2(vec3 p);
float generation_3(vec3 p);
float generation_4(vec3 p);

float noiseValue(vec3 p, int levels);
float simplex(vec3 pos);
float noise(vec3 pos);

void main(void) {
	vec3 normPosition = normalize(pass_position);
	vec3 p = pass_scale * normPosition + vec3(37.19 * pass_generation);
	float noise = 0.0;
	if (pass_generation < 0.25) {
		noise += generation_1(p) * (1.0 - pass_generationBlendFactor);
		noise += generation_2(p) * pass_generationBlendFactor;
	} else if (pass_generation < 0.5) {
		noise += generation_2(p) * (1.0 - pass_generationBlendFactor);
		noise += generation_3(p) * pass_generationBlendFactor;
	} else if (pass_generation < 0.75) {
		noise += generation_3(p) * (1.0 - pass_generationBlendFactor);
		noise += generation_4(p) * pass_generationBlendFactor;
	} else {
		noise += generation_4(p) * (1.0 - pass_generationBlendFactor);
		noise += generation_1(p) * pass_generationBlendFactor;
	}
	noise = clamp(noise, 0.0, 1.0);
	
	if (info.x >= 0.0)
		noise = pow(noise, info.x);
	if (info.y >= 0.0)
		noise = abs(2.0 * noise - 1.0);
	if (info.z >= 0.0)
		noise = 0.75 * noise + 0.25 * (1.0 - noise);
	if (info.x < 0.0)
		noise = pow(noise, -info.x);
	
	noise = 0.98 * noise + 0.02 * noiseValue(32.0 * normPosition + noise, 5);
	noise = clamp(noise, 0.0, 1.0);
	
	vec3 color = calculateColor(noise);
	
	out_Color = vec4(color, 1.0);
}

vec3 calculateColor(float value) {
	value = clamp(value, 0.0, 1.0);
	int index = 0;
	int lastIndex = 0;
	for (int i = 0; i < ELEMENTS; i++) {
		if (lastIndex > 0) {
			continue;
		} else if (elementColor[i].r < 0.0) {
			lastIndex = i;
		}
		if (value >= elementInfo[i].x) {
			index = i;
		}
	}
	if (index == 0 && value <= elementInfo[index].x || index == lastIndex - 1 && value >= elementInfo[index].x) {
		return elementColor[index];
	}
	float blendFactor = (value - elementInfo[index].x) / (elementInfo[index + 1].x - elementInfo[index].x);
	blendFactor = pow(blendFactor, elementInfo[index].y);
	return elementColor[index + 1] * vec3(blendFactor) + elementColor[index] * vec3(1.0 - blendFactor);
}

float generation_1(vec3 p) {
	float value = noiseValue(p, 5);
	return value;
}

float generation_2(vec3 p) {
	float value = 0.1 * noiseValue(0.1 * p, 3) + 0.9 * noiseValue(p, 5);
	return value;
}

float generation_3(vec3 p) {
	float value = noiseValue(2.0 * p, 3);
	value = noiseValue(p + vec3(0.1 * abs(info.y) * value), 5);
	return value;
}

float generation_4(vec3 p) {
	float value = noiseValue(0.5 * p, 3);
	value = noiseValue(p + vec3(0.1 * abs(info.y) * value), 5);
	return value;
}

float noiseValue(vec3 p, int levels) {
    float dp = 1.0;
    float da = clamp(abs(info.z), 0.25, 0.75);
    float noise = 0.0;
    for (int i = 0; i < levels; i++) {
    	noise += da * simplex(dp * p);
    	dp *= 2.0;
    	da *= 0.5;
    }
	noise = 0.5 * (1.0 + noise);
	return noise;
}

float simplex(vec3 p) {
	float f3 = 1.0 / 3.0;
	float s = (p.x + p.y + p.z) * f3;
	float i = floor(p.x + s);
	float j = floor(p.y + s);
	float k = floor(p.z + s);
	
	float g3 = 1.0 / 6.0;
	float t = (i + j + k) * g3;
	float x0 = i - t;
	float y0 = j - t;
	float z0 = k - t;
	x0 = p.x - x0;
	y0 = p.y - y0;
	z0 = p.z - z0;
	
	float i1, j1, k1;
	float i2, j2, k2;
	
	if (x0 >= y0) {
		if (y0 >= z0) { 
			i1 = 1.0; 
			j1 = 0.0; 
			k1 = 0.0; 
			i2 = 1.0; 
			j2 = 1.0; 
			k2 = 0.0; 
		} else if (x0 >= z0) {
			i1 = 1.0; 
			j1 = 0.0; 
			k1 = 0.0; 
			i2 = 1.0; 
			j2 = 0.0; 
			k2 = 1.0; 
		} else { 
			i1 = 0.0; 
			j1 = 0.0; 
			k1 = 1.0; 
			i2 = 1.0; 
			j2 = 0.0; 
			k2 = 1.0; 
		}
	} else {
		if (y0 < z0) {
			i1 = 0.0;
			j1 = 0.0;
			k1 = 1.0;
			i2 = 0.0;
			j2 = 1.0;
			k2 = 1.0;
		} else if (x0 < z0) {
			i1 = 0.0;
			j1 = 1.0;
			k1 = 0.0;
			i2 = 0.0;
			j2 = 1.0;
			k2 = 1.0;
		} else {
			i1 = 0.0;
			j1 = 1.0;
			k1 = 0.0;
			i2 = 1.0;
			j2 = 1.0;
			k2 = 0.0;
		}
	}

	float x1 = x0 - i1 + g3;
	float y1 = y0 - j1 + g3;
	float z1 = z0 - k1 + g3;
	float x2 = x0 - i2 + 2.0 * g3;
	float y2 = y0 - j2 + 2.0 * g3;
	float z2 = z0 - k2 + 2.0 * g3;
	float x3 = x0 - 1.0 + 3.0 * g3;
	float y3 = y0 - 1.0 + 3.0 * g3;
	float z3 = z0 - 1.0 + 3.0 * g3;

	vec3 ijk0 = vec3(i, j, k);
	vec3 ijk1 = vec3(i + i1, j + j1, k + k1);
	vec3 ijk2 = vec3(i + i2, j + j2, k + k2);
	vec3 ijk3 = vec3(i + 1.0, j + 1.0, k + 1.0);

	vec3 gr0 = normalize(vec3(noise(ijk0), noise(ijk0 * 2.01), noise(ijk0 * 2.02)));
	vec3 gr1 = normalize(vec3(noise(ijk1), noise(ijk1 * 2.01), noise(ijk1 * 2.02)));
	vec3 gr2 = normalize(vec3(noise(ijk2), noise(ijk2 * 2.01), noise(ijk2 * 2.02)));
	vec3 gr3 = normalize(vec3(noise(ijk3), noise(ijk3 * 2.01), noise(ijk3 * 2.02)));

	float n0 = 0.0;
	float n1 = 0.0;
	float n2 = 0.0;
	float n3 = 0.0;

	float t0 = 0.5 - x0 * x0 - y0 * y0 - z0 * z0;
	if(t0 >= 0.0) {
		t0 *= t0;
		n0 = t0 * t0 * dot(gr0, vec3(x0, y0, z0));
	}
	
	float t1 = 0.5 - x1 * x1 - y1 * y1 - z1 * z1;
	if(t1 >= 0.0) {
		t1 *= t1;
		n1 = t1 * t1 * dot(gr1, vec3(x1, y1, z1));
	}
	
	float t2 = 0.5 - x2 * x2 - y2 * y2 - z2 * z2;
	if(t2 >= 0.0) {
		t2 *= t2;
		n2 = t2 * t2 * dot(gr2, vec3(x2, y2, z2));
	}
	
	float t3 = 0.5 - x3 * x3 - y3 * y3 - z3 * z3;
	if(t3 >= 0.0) {
		t3 *= t3;
		n3 = t3 * t3 * dot(gr3, vec3(x3, y3, z3));
	}
	
	return 96.0 * (n0 + n1 + n2 + n3);
}

float noise(vec3 p) {
	return fract(sin(dot(p, vec3(12.9898 + seed, 78.233 + seed, 128.852 + seed))) * (43758.5453 - seed)) * 2.0 - 1.0;
}
