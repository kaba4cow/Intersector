package kaba4cow.galaxyengine.objects;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.rng.RandomLehmer;
import kaba4cow.files.ParticleSystemFile;
import kaba4cow.gameobjects.targets.TargetType;
import kaba4cow.utils.GalaxyUtils;

public class NebulaObject extends GalacticObject {

	public static final ParticleSystem particleSystem = ParticleSystemFile.get("NEBULA").get();

	private final float tintColor;
	private final float tintPower;
	private final float lifeFactor;
	private final float rotation;

	public NebulaObject(int posX, int posY, int posZ, float sectorDensity) {
		super(posX, posY, posZ);
		this.seed = GalaxyUtils.seedNebula(posX, posY, posZ);
		this.rng = new RandomLehmer(seed);

		this.offX = posX + GalaxyUtils.MAX_SYSTEM_OFF * rng.nextFloat(-1f, 1f);
		this.offY = posY + GalaxyUtils.MAX_SYSTEM_OFF * rng.nextFloat(-1f, 1f);
		this.offZ = posZ + GalaxyUtils.MAX_SYSTEM_OFF * rng.nextFloat(-1f, 1f);
		this.pos = new Vector3f(offX, offY, offZ);

		float sizeValue = Maths.blend(rng.nextFloat(0f, 1f), GalaxyUtils.getNebulaSize(posX, posY, posZ),
				rng.nextFloat(0f, 0.25f));
		this.size = Maths.map(sizeValue, 0f, 1f, PlanetObject.NEBULA.getMinSize(), PlanetObject.NEBULA.getMaxSize());

		float colorValue = Maths.blend(rng.nextFloat(0f, 1f), GalaxyUtils.getNebulaColor(posX, posY, posZ),
				rng.nextFloat(0f, 0.25f));
		this.tintColor = (1f + colorValue + 0.01f * rng.nextFloat(-1f, 1f)) % 1f;

		this.tintPower = Maths.map(sizeValue, 0f, 1f, PlanetObject.NEBULA.getMinEmission(),
				PlanetObject.NEBULA.getMaxEmission())
				* Maths.map(sectorDensity, GalaxyUtils.NEBULA_CUTOFF, 1f, 0.5f, 1f);
		this.lifeFactor = rng.nextFloat(0f, 1f);
		this.rotation = rng.nextFloat(0f, Maths.TWO_PI);
	}

	@Override
	protected Vector3f getParticleTintColor() {
		return PlanetObject.NEBULA.getColor(tintColor, null);
	}

	@Override
	protected float getParticleTintPower() {
		return tintPower;
	}

	@Override
	protected float getParticleLifeFactor() {
		return lifeFactor;
	}

	@Override
	protected float getParticleRotation() {
		return rotation;
	}

	@Override
	protected ParticleSystem getParticleSystem() {
		return particleSystem;
	}

	@Override
	public TargetType getTargetType() {
		return null;
	}

}
