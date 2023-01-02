package kaba4cow.engine.toolbox.particles;

import kaba4cow.engine.renderEngine.textures.ParticleTexture;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class Particle {

	private Vector3f pos;
	private Vector3f velocity;

	private float lifeLength;

	private boolean calculateLifeFactor;
	private float lifeFactor;
	private boolean calculateBrightness;
	private float brightness;
	private float rotation;
	private float scale;
	private final float rotationDirection;

	private float elapsedTime;

	private Vector2f texOffset1;
	private Vector2f texOffset2;
	private float blendFactor;
	private Vector3f tint;

	private ParticleSystem system;

	public Particle(ParticleSystem system, Vector3f position,
			Vector3f velocity, float lifeLength, float rotation, float scale) {
		system.add(this);
		this.system = system;
		this.pos = new Vector3f(position);
		this.velocity = new Vector3f(velocity);
		this.lifeLength = lifeLength;
		this.rotation = rotation;
		this.scale = scale;
		this.rotationDirection = RNG.randomBoolean() ? 1f : -1f;
		this.elapsedTime = 0f;
		this.texOffset1 = new Vector2f();
		this.texOffset2 = new Vector2f();
		this.tint = null;
		this.lifeFactor = -1f;
		this.calculateLifeFactor = true;
		this.calculateBrightness = true;
	}

	public boolean update(float dt) {
		scale += system.getDScale() * dt;
		elapsedTime += dt;
		if (isAlive() && scale > 0f) {
			updateTextureCoordinates();
			Vectors.addScaled(pos, velocity, dt, pos);
			rotation += rotationDirection * system.getDRotation() * dt;
			return true;
		}
		return false;
	}

	public boolean isAlive() {
		return lifeLength == 0f || elapsedTime < lifeLength;
	}

	private void updateTextureCoordinates() {
		if (getTexture() == null)
			return;
		if (calculateLifeFactor)
			lifeFactor = lifeLength == 0f ? 0f : elapsedTime / lifeLength;
		if (calculateBrightness) {
			if (system.getBrightnessEasing() != null)
				brightness = 1f - system.getBrightnessEasing().getValue(
						lifeFactor);
			else
				brightness = 1f;
		}
		int stageCount = getTexture().getNumberOfRows()
				* getTexture().getNumberOfRows();
		float atlasProgression = lifeFactor * (stageCount - 1);
		int index1 = (int) atlasProgression;
		int index2 = index1 < stageCount - 1 ? index1 + 1 : index1;
		blendFactor = atlasProgression - index1;
		getTexture().calculateTextureOffset(index1, texOffset1);
		getTexture().calculateTextureOffset(index2, texOffset2);
	}

	public Particle removeFromSystem() {
		system.remove(this);
		return this;
	}

	public Particle returnToSystem() {
		if (!system.getParticles().contains(this))
			system.add(this);
		return this;
	}

	public ParticleSystem getSystem() {
		return system;
	}

	public Vector3f getPos() {
		return pos;
	}

	public float getBrightness() {
		return brightness;
	}

	public Particle setBrightness(float brightness) {
		this.brightness = brightness;
		this.calculateBrightness = brightness < 0f;
		return this;
	}

	public float getRotation() {
		return rotation;
	}

	public Particle setRotation(float rotation) {
		this.rotation = rotation;
		return this;
	}

	public float getScale() {
		return scale;
	}

	public Particle setScale(float scale) {
		this.scale = scale;
		return this;
	}

	public ParticleTexture getTexture() {
		return system.getParticleTexture();
	}

	public Particle setLifeFactor(float lifeFactor) {
		this.lifeFactor = lifeFactor;
		this.calculateLifeFactor = lifeFactor < 0f;
		return this;
	}

	public Vector2f getTexOffset1() {
		return texOffset1;
	}

	public Vector2f getTexOffset2() {
		return texOffset2;
	}

	public float getBlendFactor() {
		return blendFactor;
	}

	public Vector3f getTint() {
		return tint;
	}

	public Particle setTint(Vector3f tint, float power) {
		if (tint == null)
			this.tint = null;
		else {
			this.tint = new Vector3f(tint);
			this.tint.scale(power);
		}
		return this;
	}

}
