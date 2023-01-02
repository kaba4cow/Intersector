package kaba4cow.galaxyengine.objects;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.gameobjects.projectiles.Projectile;
import kaba4cow.renderEngine.RendererContainer;

public abstract class GalacticObject extends GameObject {

	public long seed;
	protected RNG rng;

	public int posX;
	public int posY;
	public int posZ;
	public float offX;
	public float offY;
	public float offZ;

	private Particle particle;

	public GalacticObject(int posX, int posY, int posZ) {
		super(null);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
	}

	public Particle getParticle(List<Particle> freeParticles) {
		if (freeParticles == null || freeParticles.isEmpty()) {
			if (particle == null) {
				particle = new Particle(getParticleSystem(), pos.negate(null),
						Vectors.INIT3, 0f, getParticleRotation(), size);
				particle.setTint(getParticleTintColor(), getParticleTintPower());
			}
		} else if (particle == null) {
			particle = freeParticles.remove(0);
			pos.negate(particle.getPos());
			particle.setRotation(getParticleRotation());
			particle.setScale(size);
			particle.setTint(getParticleTintColor(), getParticleTintPower());
			particle.setLifeFactor(getParticleLifeFactor());
			particle.returnToSystem();
		}
		return particle;
	}

	protected abstract ParticleSystem getParticleSystem();

	protected abstract Vector3f getParticleTintColor();

	protected abstract float getParticleTintPower();

	protected abstract float getParticleLifeFactor();

	protected abstract float getParticleRotation();

	@Override
	protected void collide(GameObject obj, float dt) {
		return;
	}

	@Override
	public void update(float dt) {
		return;
	}

	@Override
	public void render(RendererContainer renderers) {
		return;
	}

	@Override
	public void damage(int colliderIndex, Projectile proj) {
		return;
	}

	@Override
	public void damage(float damage) {
		return;
	}

	@Override
	protected void onDamage(Projectile proj) {
		return;
	}

	@Override
	public void onSpawn() {
		return;
	}

	@Override
	protected void onDestroy(GameObject src) {
		return;
	}

	@Override
	protected void onDestroy() {
		return;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(offX);
		result = prime * result + Float.floatToIntBits(offY);
		result = prime * result + Float.floatToIntBits(offZ);
		result = prime * result + posX;
		result = prime * result + posY;
		result = prime * result + posZ;
		result = prime * result + (int) (seed ^ (seed >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GalacticObject other = (GalacticObject) obj;
		if (Float.floatToIntBits(offX) != Float.floatToIntBits(other.offX))
			return false;
		if (Float.floatToIntBits(offY) != Float.floatToIntBits(other.offY))
			return false;
		if (Float.floatToIntBits(offZ) != Float.floatToIntBits(other.offZ))
			return false;
		if (posX != other.posX)
			return false;
		if (posY != other.posY)
			return false;
		if (posZ != other.posZ)
			return false;
		if (seed != other.seed)
			return false;
		return true;
	}

}
