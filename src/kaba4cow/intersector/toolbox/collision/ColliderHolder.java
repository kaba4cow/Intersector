package kaba4cow.intersector.toolbox.collision;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.Settings;
import kaba4cow.intersector.files.ParticleSystemFile;
import kaba4cow.intersector.gameobjects.Shield;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.objectcomponents.ColliderComponent;

public interface ColliderHolder {

	public default void collide(ColliderHolder obj, float dt) {
		CollisionManager.collide(this, obj, dt);
	}

	public default float getColliderPosition(int index, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		if (index < 0 || index >= getColliders().length) {
			dest.set(getPos());
			return getSize();
		}
		return getColliders()[index].getPosition(getDirection().getMatrix(getPos(), true, getSize()), dest);
	}

	public default void hit(World world, Direction direction, Vector3f pos, float size) {
		if (hasShield())
			hitShield(world, direction, pos, size);
		else
			hitParticles(pos, size);
	}

	public default void hitParticles(Vector3f pos, float size) {
		int num = RNG.randomInt(-1, 2 + Settings.getParticles());
		if (num <= 0)
			return;

		float lifeLength = 0.5f;
		float scale = 0.194f * size;
		float speed = 0.38f * size;

		ParticleSystem system = ParticleSystemFile.get("EXPLOSION").get();

		Vector3f velocity = new Vector3f();
		for (int i = 0; i < num; i++) {
			Vectors.randomize(-1f, 1f, velocity).normalise();
			velocity.scale(speed * RNG.randomFloat(0.8f, 1.2f));
			float scaleFactor = RNG.randomFloat(0.25f, 1f);

			new Particle(system, pos, velocity.negate(null), lifeLength / scaleFactor, RNG.randomFloat(Maths.TWO_PI),
					scale * scaleFactor);
		}
	}

	public default void hitShield(World world, Direction direction, Vector3f pos, float size) {
		new Shield(world, direction, pos.negate(null), getVel(), RNG.randomFloat(0.75f, 1.25f) * size);
	}

	public boolean hasShield();

	public default float getCollidersScale() {
		return hasShield() ? Maths.SQRT2 : 1f;
	}

	public void burn(ColliderComponent colliderComponent, float damage);

	public World getWorld();

	public Direction getDirection();

	public float getSize();

	public float getMass();

	public Vector3f getPos();

	public Vector3f getVel();

	public ColliderComponent[] getColliders();

}
