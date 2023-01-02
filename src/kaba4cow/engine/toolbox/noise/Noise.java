package kaba4cow.engine.toolbox.noise;

import java.io.Serializable;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Noise implements Serializable {

	private static final long serialVersionUID = 5143518645915493068L;

	private long seed;
	private OpenSimplexNoise noise;

	public Noise() {
		this.seed = RNG.randomLong();
		this.noise = new OpenSimplexNoise(seed);
	}

	public Noise(long seed) {
		this.seed = seed;
		this.noise = new OpenSimplexNoise(seed);
	}

	public void setNoiseSeed(long seed) {
		this.seed = seed;
		this.noise = new OpenSimplexNoise(seed);
	}

	public long getNoiseSeed() {
		return seed;
	}

	private static float limit01(double value) {
		return Maths.limit((float) value, 0f, 1f);
	}

	private static float limit11(double value) {
		return Maths.limit((float) value, -1f, 1f);
	}

	public static float to01(double value) {
		return limit01(0.5f * (value + 1f));
	}

	public static float to11(double value) {
		return limit11(2f * value - 1f);
	}

	public static Vector2f to01(Vector2f value) {
		return new Vector2f(to01(value.x), to01(value.y));
	}

	public static Vector2f to11(Vector2f vector) {
		return new Vector2f(to11(vector.x), to11(vector.y));
	}

	public static Vector3f to01(Vector3f value) {
		return new Vector3f(to01(value.x), to01(value.y), to01(value.z));
	}

	public static Vector3f to11(Vector3f vector) {
		return new Vector3f(to11(vector.x), to11(vector.y), to11(vector.z));
	}

	public static Vector4f to01(Vector4f value) {
		return new Vector4f(to01(value.x), to01(value.y), to01(value.z),
				to01(value.w));
	}

	public static Vector4f to11(Vector4f vector) {
		return new Vector4f(to11(vector.x), to11(vector.y), to11(vector.z),
				to11(vector.w));
	}

	public float getNoiseValue(float x, float y) {
		return to01(noise.eval(x, y));
	}

	public float getNoiseValue(float x, float y, float z) {
		return to01(noise.eval(x, y, z));
	}

	public float getNoiseValue(float x, float y, float z, float w) {
		return to01(noise.eval(x, y, z, w));
	}

	public float getNoiseValue2(float d, Vector2f vector) {
		return getNoiseValue(d * vector.x, d * vector.y);
	}

	public float getNoiseValue3(float d, Vector3f vector) {
		return getNoiseValue(d * vector.x, d * vector.y, d * vector.z);
	}

	public float getNoiseValue4(float d, Vector4f vector) {
		return getNoiseValue(d * vector.x, d * vector.y, d * vector.z, d
				* vector.w);
	}

	public float getCombinedValue(float x, float y, int numOctaves) {
		float f = 1f;
		float a = 1f;
		float t = 0f;
		for (int i = 0; i < numOctaves; i++) {
			float value = to11(getNoiseValue(x * f, y * f));
			t += a * value;
			f *= 2.0f;
			a *= 0.5f;
		}
		return to01(t);
	}

	public float getCombinedValue(float x, float y, float z, int numOctaves) {
		float f = 1f;
		float a = 1f;
		float t = 0f;
		for (int i = 0; i < numOctaves; i++) {
			float value = to11(getNoiseValue(x * f, y * f, z * f));
			t += a * value;
			f *= 2.0f;
			a *= 0.5f;
		}
		return to01(t);
	}

	public float getCombinedValue(float x, float y, float z, float w,
			int numOctaves) {
		float f = 1f;
		float a = 1f;
		float t = 0f;
		for (int i = 0; i < numOctaves; i++) {
			float value = to11(getNoiseValue(x * f, y * f, z * f, w * f));
			t += a * value;
			f *= 2.0f;
			a *= 0.5f;
		}
		return to01(t);
	}

	public float getCombinedValue2(float d, Vector2f vector, int numOctaves) {
		return getCombinedValue(d * vector.x, d * vector.y, numOctaves);
	}

	public float getCombinedValue3(float d, Vector3f vector, int numOctaves) {
		return getCombinedValue(d * vector.x, d * vector.y, d * vector.z,
				numOctaves);
	}

	public float getCombinedValue4(float d, Vector4f vector, int numOctaves) {
		return getCombinedValue(d * vector.x, d * vector.y, d * vector.z, d
				* vector.w, numOctaves);
	}

	public float getWarpedValue(float x, float y, int sharpness, int complexity) {
		Vector2f fbmVec = new Vector2f(x, y);
		float fbm = 0;
		for (int i = 0; i < sharpness; i++) {
			fbm = to11(getCombinedValue(fbmVec.x, fbmVec.y, complexity));
			Vector2f.add(fbmVec, new Vector2f(fbm, fbm), fbmVec);
		}
		return getCombinedValue(fbmVec.x, fbmVec.y, complexity);
	}

	public float getWarpedValue(float x, float y, float z, int sharpness,
			int complexity) {
		Vector3f fbmVec = new Vector3f(x, y, z);
		float fbm = 0;
		for (int i = 0; i < sharpness; i++) {
			fbm = to11(getCombinedValue(fbmVec.x, fbmVec.y, fbmVec.z,
					complexity));
			Vector3f.add(fbmVec, new Vector3f(fbm, fbm, fbm), fbmVec);
		}
		return getCombinedValue(fbmVec.x, fbmVec.y, fbmVec.z, complexity);
	}

	public float getWarpedValue(float x, float y, float z, float w,
			int sharpness, int complexity) {
		Vector4f fbmVec = new Vector4f(x, y, z, w);
		float fbm = 0;
		for (int i = 0; i < sharpness; i++) {
			fbm = to11(getCombinedValue(fbmVec.x, fbmVec.y, fbmVec.z, fbmVec.w,
					complexity));
			Vector4f.add(fbmVec, new Vector4f(fbm, fbm, fbm, fbm), fbmVec);
		}
		return getCombinedValue(fbmVec.x, fbmVec.y, fbmVec.z, fbmVec.w,
				complexity);
	}

	public float getWarpedValue(float x, float y, int sharpness,
			int complexity, float power) {
		Vector2f fbmVec = new Vector2f(x, y);
		Vector2f addVec = new Vector2f();
		float fbm = 0;
		for (int i = 0; i < sharpness; i++) {
			fbm = to11(getCombinedValue(fbmVec.x, fbmVec.y, complexity));
			addVec.set(power * fbm, power * fbm);
			Vector2f.add(fbmVec, addVec, fbmVec);
		}
		return getCombinedValue(fbmVec.x, fbmVec.y, complexity);
	}

	public float getWarpedValue(float x, float y, float z, int sharpness,
			int complexity, float power) {
		Vector3f fbmVec = new Vector3f(x, y, z);
		Vector3f addVec = new Vector3f();
		float fbm = 0;
		for (int i = 0; i < sharpness; i++) {
			fbm = to11(getCombinedValue(fbmVec.x, fbmVec.y, fbmVec.z,
					complexity));
			addVec.set(power * fbm, power * fbm, power * fbm);
			Vector3f.add(fbmVec, addVec, fbmVec);
		}
		return getCombinedValue(fbmVec.x, fbmVec.y, fbmVec.z, complexity);
	}

	public float getWarpedValue(float x, float y, float z, float w,
			int sharpness, int complexity, float power) {
		Vector4f fbmVec = new Vector4f(x, y, z, w);
		Vector4f addVec = new Vector4f();
		float fbm = 0;
		for (int i = 0; i < sharpness; i++) {
			fbm = to11(getCombinedValue(fbmVec.x, fbmVec.y, fbmVec.z, fbmVec.w,
					complexity));
			addVec.set(power * fbm, power * fbm, power * fbm, power * fbm);
			Vector4f.add(fbmVec, addVec, fbmVec);
		}
		return getCombinedValue(fbmVec.x, fbmVec.y, fbmVec.z, fbmVec.w,
				complexity);
	}

	public float getWarpedValue2(float d, Vector2f vector, int sharpness,
			int complexity) {
		return getWarpedValue(d * vector.x, d * vector.y, sharpness, complexity);
	}

	public float getWarpedValue3(float d, Vector3f vector, int sharpness,
			int complexity) {
		return getWarpedValue(d * vector.x, d * vector.y, d * vector.z,
				sharpness, complexity);
	}

	public float getWarpedValue4(float d, Vector4f vector, int sharpness,
			int complexity) {
		return getWarpedValue(d * vector.x, d * vector.y, d * vector.z, d
				* vector.w, sharpness, complexity);
	}

	public float getWarpedValue2(float d, Vector2f vector, int sharpness,
			int complexity, float power) {
		return getWarpedValue(d * vector.x, d * vector.y, sharpness,
				complexity, power);
	}

	public float getWarpedValue3(float d, Vector3f vector, int sharpness,
			int complexity, float power) {
		return getWarpedValue(d * vector.x, d * vector.y, d * vector.z,
				sharpness, complexity, power);
	}

	public float getWarpedValue4(float d, Vector4f vector, int sharpness,
			int complexity, float power) {
		return getWarpedValue(d * vector.x, d * vector.y, d * vector.z, d
				* vector.w, sharpness, complexity, power);
	}

	public float getVoronoiValue(float x, float y, float manhattan,
			float randomness) {
		float icx = Maths.floor(x);
		float icy = Maths.floor(y);
		float minEuclidianDist = 1f;
		float minManhattanDist = 1f;
		for (int px = -1; px <= 1; px++) {
			for (int py = -1; py <= 1; py++) {
				float picx = icx + px;
				float picy = icy + py;
				float fcx = x - picx;
				float fcy = y - picy;
				float dx = hash(picx, picy, randomness) - fcx;
				float dy = hash(153f + picx, 153f + picy, randomness) - fcy;

				float euclidianDist = distanceToCenterEuclidian(dx, dy);
				float manhattanDist = distanceToCenterManhattan(dx, dy);

				minEuclidianDist = Maths.min(minEuclidianDist, euclidianDist);
				minManhattanDist = Maths.min(minManhattanDist, manhattanDist);
			}
		}
		minEuclidianDist = Maths.sqrt(minEuclidianDist);
		return Maths.blend(minManhattanDist, minEuclidianDist, manhattan);
	}

	private float distanceToCenterManhattan(float dx, float dy) {
		return Maths.manhattan(0f, 0f, dx, dy);
	}

	private float distanceToCenterEuclidian(float dx, float dy) {
		return dx * dx + dy * dy;
	}

	private float hash(float x, float y, float randomness) {
		x = 17f * ((x * 0.2183099f + 0.1f) % 1f);
		y = 17f * ((y * 0.2183099f + 0.1f) % 1f);
		return Maths.blend(Maths.abs(x * y * (x + y)) % 1f, 0f, randomness);
	}

	public float getVoronoiValue(float x, float y, float z, float manhattan,
			float randomness) {
		float icx = Maths.floor(x);
		float icy = Maths.floor(y);
		float icz = Maths.floor(z);
		float minEuclidianDist = 1f;
		float minManhattanDist = 1f;
		for (int px = -1; px <= 1; px++) {
			for (int py = -1; py <= 1; py++) {
				for (int pz = -1; pz <= 1; pz++) {
					float picx = icx + px;
					float picy = icy + py;
					float picz = icz + pz;
					float fcx = x - picx;
					float fcy = y - picy;
					float fcz = z - picz;
					float dx = hash(picx, picy, picz, randomness) - fcx;
					float dy = hash(153f + picx, 153f + picy, 153f + picz,
							randomness) - fcy;
					float dz = hash(87f + picx, 87f + picy, 87f + picz,
							randomness) - fcz;

					float euclidianDist = distanceToCenterEuclidian(dx, dy, dz);
					float manhattanDist = distanceToCenterManhattan(dx, dy, dz);

					minEuclidianDist = Maths.min(minEuclidianDist,
							euclidianDist);
					minManhattanDist = Maths.min(minManhattanDist,
							manhattanDist);
				}
			}
		}
		minEuclidianDist = Maths.sqrt(minEuclidianDist);
		return Maths.blend(minManhattanDist, minEuclidianDist, manhattan);
	}

	private float distanceToCenterManhattan(float dx, float dy, float dz) {
		return Maths.manhattan(0f, 0f, 0f, dx, dy, dz);
	}

	private float distanceToCenterEuclidian(float dx, float dy, float dz) {
		return dx * dx + dy * dy + dz * dz;
	}

	private float hash(float x, float y, float z, float randomness) {
		x = 17f * ((x * 0.2183099f + 0.1f) % 1f);
		y = 17f * ((y * 0.2183099f + 0.1f) % 1f);
		z = 17f * ((y * 0.2183099f + 0.1f) % 1f);
		return Maths.blend(Maths.abs(x * y * z * (x + y + z)) % 1f, 0f,
				randomness);
	}
}