package kaba4cow.intersector.galaxyengine;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.toolbox.rng.RandomLehmer;
import kaba4cow.files.PlanetFile;
import kaba4cow.intersector.galaxyengine.objects.PlanetObject;
import kaba4cow.intersector.toolbox.ColorRamp;

public class TerrainGenerator {

	public static final float MIN_NOISE_SEED = -1000f;
	public static final float MAX_NOISE_SEED = 1000f;
	public static final float MIN_GENERATION = 0f;
	public static final float MAX_GENERATION = 1f;

	public final long seed;

	public final float noiseSeed;
	public final float generation;
	public final float emission;
	public final Vector3f light;
	public final ColorRamp colorRamp;
	public final Vector3f scale;
	public final Vector3f info;
	public final Vector3f ringColor;
	public final Vector3f ringScale;
	public final Vector3f ringInfo;
	public final float ringEmission;

	public TerrainGenerator(PlanetFile file, Vector3f color, long terrainSeed) {
		this.seed = terrainSeed;

		RNG rng = new RandomLehmer(seed);

		noiseSeed = rng.nextFloat(MIN_NOISE_SEED, MAX_NOISE_SEED);
		generation = rng.nextFloat(MIN_GENERATION, MAX_GENERATION);
		scale = new Vector3f();
		info = new Vector3f();
		ringInfo = new Vector3f();
		ringScale = new Vector3f();

		int numBands = 0;
		float minAvgColorBlend = 0f;
		float maxAvgColorBlend = 0f;
		float positionFactor = 0f;
		boolean invertPosition = false;
		float minBlendPower = 0f;
		float maxBlendPower = 0f;

		numBands = rng.nextInt(file.getMinBands(), file.getMaxBands());
		minAvgColorBlend = file.getMinAvgColorBlend();
		maxAvgColorBlend = file.getMaxAvgColorBlend();
		positionFactor = rng.nextFloat(file.getMinPositionFactor(),
				file.getMaxPositionFactor());
		invertPosition = file.isInvertPosition() ? rng.nextBoolean() : false;
		minBlendPower = file.getMinBlendPower();
		maxBlendPower = file.getMaxBlendPower();

		if (file.getInfoSigns()[0] == 0)
			info.x = rng.nextSign();
		else
			info.x = file.getInfoSigns()[0];
		if (file.getInfoSigns()[1] == 0)
			info.y = rng.nextSign();
		else
			info.y = file.getInfoSigns()[1];
		if (file.getInfoSigns()[2] == 0)
			info.z = rng.nextSign();
		else
			info.z = file.getInfoSigns()[2];

		info.x *= rng.nextFloat(file.getMinInfo().x, file.getMaxInfo().x);
		info.y *= rng.nextFloat(file.getMinInfo().y, file.getMaxInfo().y);
		info.z *= rng.nextFloat(file.getMinInfo().z, file.getMaxInfo().z);

		scale.x = rng.nextFloat(file.getMinScale().x, file.getMaxScale().x);
		scale.y = rng.nextFloat(file.getMinScale().y, file.getMaxScale().y);
		scale.z = rng.nextFloat(file.getMinScale().z, file.getMaxScale().z);

		if (file.getLight() > 0f) {
			emission = rng.nextFloat(file.getMinEmission(),
					file.getMaxEmission());
			light = getLightColor(color, file.getLight());
		} else {
			emission = -1f;
			light = null;
		}

		colorRamp = ColorRamp.generate(numBands, color, file.getColors(),
				minAvgColorBlend, maxAvgColorBlend, positionFactor,
				minBlendPower, maxBlendPower, invertPosition, seed);

		ringColor = new Vector3f(PlanetObject.RING.getColors()[rng.nextInt(0,
				PlanetObject.RING.getColors().length)]);
		Maths.blend(color, ringColor, rng.nextFloat(0.05f, 0.15f), ringColor);
		Maths.blend(Vectors.UNIT3, ringColor, rng.nextFloat(0.2f, 0.4f),
				ringColor);

		ringScale.x = rng.nextFloat(PlanetObject.RING.getMinScale().x,
				PlanetObject.RING.getMaxScale().x);
		ringScale.y = rng.nextFloat(PlanetObject.RING.getMinScale().y,
				PlanetObject.RING.getMaxScale().y);
		ringScale.z = rng.nextFloat(PlanetObject.RING.getMinScale().z,
				PlanetObject.RING.getMaxScale().z);

		ringInfo.x = rng.nextFloat(PlanetObject.RING.getMinInfo().x,
				PlanetObject.RING.getMaxInfo().x);
		ringInfo.y = rng.nextFloat(PlanetObject.RING.getMinInfo().y,
				PlanetObject.RING.getMaxInfo().y);
		ringInfo.z = rng.nextFloat(PlanetObject.RING.getMinInfo().z,
				PlanetObject.RING.getMaxInfo().z);

		ringEmission = rng.nextFloat(PlanetObject.RING.getMinEmission(),
				PlanetObject.RING.getMaxEmission());
	}

	private Vector3f getLightColor(Vector3f color, float power) {
		Vector3f light = Maths.blend(Vectors.UNIT3, color, 0.75f, null);
		light.scale(power);
		return light;
	}

	public Light createLight(float size, float power) {
		if (light == null)
			return null;
		return new Light(new Vector3f(), light, new Vector3f(power,
				0.001f / size, 0f));
	}

}
