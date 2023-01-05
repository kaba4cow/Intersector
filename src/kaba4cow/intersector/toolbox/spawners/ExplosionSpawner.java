package kaba4cow.intersector.toolbox.spawners;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.Settings;
import kaba4cow.intersector.files.ParticleSystemFile;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.machines.Machine;

public final class ExplosionSpawner {

	private static final ParticleSystem EXPLOSION = ParticleSystemFile.get(
			"EXPLOSION").get();
	private static final ParticleSystem SMOKE = ParticleSystemFile.get("SMOKE")
			.get();
	private static final ParticleSystem LIGHT = ParticleSystemFile.get("LIGHT")
			.get();

	private ExplosionSpawner() {

	}

	public static void spawn(GameObject source, Vector3f position,
			Vector3f velocity, float size, Explosion explosion, Scale scale) {
		if (source == null)
			return;
		if (explosion == null)
			explosion = Explosion.values()[RNG
					.randomInt(Explosion.values().length)];
		explosion.explode(source, position, velocity, size, scale);
		source.playSound("explode_" + scale.toString().toLowerCase());
	}

	private static void damage(GameObject source, Vector3f pos, float size,
			Scale scale) {
		size *= 2f;
		List<GameObject> list = source.getWorld().getOctTree()
				.query(pos.x, pos.y, pos.z, size);
		GameObject current = null;
		float distSq = 0f;
		float sizeSq = Maths.sqr(size);
		float minDistSq = Maths.sqr(0.25f * size);
		float power = 0f;
		float damage = 0f;
		float speed = 0f;
		float rotation = 0f;
		Vector3f direction = null;
		Vector3f rotationVel = new Vector3f(1f, 1f, 1f);
		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			if (!GameObject.isAlive(current) || !current.isCollidable())
				continue;
			distSq = Maths.distSq(pos, current.getPos());
			distSq = Maths.max(distSq, minDistSq);
			if (distSq < Maths.sqr(size + current.getSize())) {
				power = sizeSq / distSq;
				damage = RNG.randomFloat(1f, 4f) * power;
				if (current instanceof Machine == false
						|| !((Machine) current).hasShield())
					current.damage(damage);
				direction = Maths.direction(pos, current.getPos());
				speed = RNG.randomFloat(0.5f, 1f) * size * power
						/ current.getSize();
				Vectors.addScaled(current.getVel(), direction, speed,
						current.getVel());
				rotation = RNG.randomFloat(0.5f) * power / current.getSize();
				Vectors.rotateRandom(-Maths.TWO_PI, Maths.TWO_PI, rotationVel,
						rotationVel);
				Vectors.addScaled(current.getRotationVel(), rotationVel,
						rotation, current.getRotationVel());
			}
		}
	}

	private static void addVelocity(Vector3f parentVel, Vector3f vel) {
		Vectors.addScaled(vel, parentVel, RNG.randomFloat(0.975f, 1.025f), vel);
	}

	private static void addExplosionParticle(Vector3f position,
			Vector3f velocity, float lifeLength, float scale) {
		boolean smoke = RNG.chance(0.1f);
		new Particle(smoke ? SMOKE : EXPLOSION, position,
				velocity.negate(null), (smoke ? 2f : 0f) + lifeLength,
				RNG.randomFloat(Maths.TWO_PI), scale);
	}

	private static void addLightParticle(Vector3f position, Vector3f velocity,
			float lifeLength, float scale) {
		new Particle(LIGHT, position, velocity.negate(null),
				0.25f * lifeLength, RNG.randomFloat(Maths.TWO_PI), 8f * scale);
	}

	private static boolean skip() {
		return RNG.randomBoolean()
				&& RNG.randomFloat(1f) < MainProgram.getCurrentFPS()
						/ (float) MainProgram.getFps();
	}

	public static enum Explosion {

		SPHERE {
			@Override
			protected void explode(GameObject source, Vector3f position,
					Vector3f velocity, float size, Scale scaleType) {
				int num = RNG.randomInt(scaleType.getMinNum(),
						scaleType.getMaxNum());
				float ratio = RNG.randomFloat(0f, 0.5f);
				int num1 = (int) ((1f - ratio) * num);
				int num2 = (int) (ratio * num);

				float lifeLength = 8f * scaleType.lifeLength;
				float scale = 0.18f * size * scaleType.scale;
				float speed = 0.8f * size * scaleType.speed;

				Vector3f pos = position.negate(null);
				Vector3f vel = new Vector3f();

				for (int i = 0; i < num1; i++) {
					if (skip())
						continue;
					Vectors.randomize(-1f, 1f, vel).normalise();
					vel.scale(speed * RNG.randomFloat(1f, 1.35f));
					if (RNG.randomFloat(1f) < 0.1f)
						vel.scale(RNG.randomFloat(0.5f, 1.5f));

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.7f, 1.3f), scale
									* RNG.randomFloat(0f, 1f));
				}

				for (int i = 0; i < num2; i++) {
					if (skip())
						continue;
					Vectors.randomize(-1f, 1f, vel).normalise();
					vel.scale(speed * RNG.randomFloat(0f, 0.25f));
					if (RNG.randomFloat(1f) < 0.1f)
						vel.scale(8f);

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.5f, 1f), 0.5f
									* scale * RNG.randomFloat(0f, 1f));
				}
				addLightParticle(pos, velocity, lifeLength, scale);
				damage(source, position, size, scaleType);
			}
		},
		RING {
			@Override
			protected void explode(GameObject source, Vector3f position,
					Vector3f velocity, float size, Scale scaleType) {
				int num = RNG.randomInt(scaleType.getMinNum(),
						scaleType.getMaxNum());
				float ratio = RNG.randomFloat(0.25f, 0.75f);
				int numRing = (int) ((1f - ratio) * num);
				int numCenter1 = (int) (0.9f * ratio * num);
				int numCenter2 = (int) (0.1f * ratio * num);

				float lifeLength = 14f * scaleType.lifeLength;
				float scale = 0.14f * size * scaleType.scale;
				float speed = 0.75f * size * scaleType.speed;

				float rotX = RNG.randomFloat(-1f, 1f) * Maths.HALF_PI;
				float rotZ = RNG.randomFloat(-1f, 1f) * Maths.QUARTER_PI;

				float angle = 0f;
				float step = 2f * Maths.TWO_PI / (float) numRing;

				Vector3f pos = position.negate(null);
				Vector3f vel = new Vector3f();

				for (int i = 0; i < numRing; i++) {
					if (skip())
						continue;
					vel.set(1f, 0f, 0f);
					vel.scale(speed * RNG.randomFloat(0.9f, 1.1f));
					Vectors.rotate(angle, Vectors.UP, vel, vel);
					Vectors.rotate(rotX * RNG.randomFloat(0.9f, 1.1f),
							Vectors.RIGHT, vel, vel);
					Vectors.rotate(rotZ * RNG.randomFloat(0.9f, 1.1f),
							Vectors.FORWARD, vel, vel);

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.7f, 1.3f), scale
									* RNG.randomFloat(0.5f, 2f));

					angle += step;
				}

				for (int i = 0; i < numCenter1; i++) {
					if (skip())
						continue;
					Vectors.randomize(-1f, 1f, vel).normalise();
					vel.scale(0.25f * speed * RNG.randomFloat(0f, 1f));

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.1f, 0.5f), 0.5f
									* scale * RNG.randomFloat(0f, 1f));
				}

				for (int i = 0; i < numCenter2; i++) {
					if (skip())
						continue;
					Vectors.randomize(-1f, 1f, vel).normalise();
					vel.scale(2f * speed * RNG.randomFloat(0.5f, 1.5f));

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0f, 1f),
							scale * RNG.randomFloat(0f, 0.5f));
				}
				addLightParticle(pos, velocity, lifeLength, scale);
				damage(source, position, size, scaleType);
			}
		},
		CONES {
			@Override
			protected void explode(GameObject source, Vector3f position,
					Vector3f velocity, float size, Scale scaleType) {
				int num = RNG.randomInt(scaleType.getMinNum(),
						scaleType.getMaxNum());
				float ratio = RNG.randomFloat(0.25f, 0.75f);
				int numCones = (int) ((1f - ratio) * num);
				int numCenter1 = (int) (0.9f * ratio * num);
				int numCenter2 = (int) (0.1f * ratio * num);

				float lifeLength = 12f * scaleType.lifeLength;
				float scale = 0.13f * size * scaleType.scale;
				float speed = 0.8f * size * scaleType.speed;

				float rotX = RNG.randomFloat(-1f, 1f) * Maths.HALF_PI;
				float rotY = RNG.randomFloat(-1f, 1f) * Maths.HALF_PI;

				Vector3f pos = position.negate(null);
				Vector3f vel = new Vector3f();

				for (int i = 0; i < numCones; i++) {
					if (skip())
						continue;
					vel.set(0.1f * RNG.randomFloat(-1f, 1f),
							RNG.randomFloat(0.25f, 2f),
							0.1f * RNG.randomFloat(-1f, 1f));
					vel.normalise();
					if (i % 2 == 0)
						vel.y *= -1f;
					Vectors.rotate(rotX * RNG.randomFloat(0.9f, 1.1f),
							Vectors.RIGHT, vel, vel);
					Vectors.rotate(rotY * RNG.randomFloat(0.9f, 1.1f),
							Vectors.UP, vel, vel);
					vel.scale(speed * RNG.randomFloat(0.05f, 1f));

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.75f, 1f), scale
									* RNG.randomFloat(0.5f, 1.5f));
				}

				for (int i = 0; i < numCenter1; i++) {
					if (skip())
						continue;
					vel.set(RNG.randomFloat(-1f, 1f), RNG.randomFloat(-1f, 1f),
							RNG.randomFloat(-1f, 1f));
					vel.normalise();
					vel.scale(speed * RNG.randomFloat(0f, 0.25f));

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.25f, 1f), scale
									* RNG.randomFloat(0.25f, 0.5f));
				}

				for (int i = 0; i < numCenter2; i++) {
					if (skip())
						continue;
					vel.set(RNG.randomFloat(-1f, 1f), RNG.randomFloat(-1f, 1f),
							RNG.randomFloat(-1f, 1f));
					vel.normalise();
					vel.scale(speed * RNG.randomFloat(0.5f, 1.5f));

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.25f, 0.5f), scale
									* RNG.randomFloat(0f, 0.25f));
				}
				addLightParticle(pos, velocity, lifeLength, scale);
				damage(source, position, size, scaleType);
			}
		},
		RAYS {
			@Override
			protected void explode(GameObject source, Vector3f position,
					Vector3f velocity, float size, Scale scaleType) {
				int numRays = RNG.randomInt(8, 12);
				float ratio = RNG.randomFloat(0.25f, 0.5f);
				int num = RNG.randomInt(scaleType.getMinNum(),
						scaleType.getMaxNum());
				int numRay = (int) ((1f - ratio) * num / numRays);
				int numCenter1 = (int) (0.9f * ratio * num);
				int numCenter2 = (int) (0.1f * ratio * num);

				float lifeLength = 10f * scaleType.lifeLength;
				float scale = 0.15f * size * scaleType.scale;
				float speed = 0.9f * size * scaleType.speed;

				Vector3f pos = position.negate(null);
				Vector3f vel = new Vector3f();

				Vector3f dir = new Vector3f();
				for (int j = 0; j < numRays; j++) {
					if (skip())
						continue;
					Vectors.set(dir, RNG.randomFloat(0.25f, 1f));
					if (RNG.randomBoolean())
						dir.scale(-1f);
					dir.normalise();
					float rotX = RNG.randomFloat(-1f, 1f) * Maths.HALF_PI;
					float rotY = RNG.randomFloat(-1f, 1f) * Maths.HALF_PI;
					Vectors.rotate(rotX * RNG.randomFloat(0.9f, 1.1f),
							Vectors.RIGHT, dir, dir);
					Vectors.rotate(rotY * RNG.randomFloat(0.9f, 1.1f),
							Vectors.UP, dir, dir);
					float speedScale = RNG.randomFloat(0.5f, 1f);
					for (int i = 0; i < numRay; i++) {
						pos = new Vector3f(pos);
						vel.set(dir.x * RNG.randomFloat(0.9f, 1.1f), dir.y
								* RNG.randomFloat(0.9f, 1.1f),
								dir.z * RNG.randomFloat(0.9f, 1.1f));
						vel.normalise();
						if (i % 2 == 0)
							vel.scale(-1f);
						vel.scale(speedScale * speed
								* RNG.randomFloat(0.1f, 1f));

						addVelocity(velocity, vel);
						addExplosionParticle(pos, vel,
								lifeLength * RNG.randomFloat(0.75f, 1f), scale
										* RNG.randomFloat(0.75f, 1.5f));
					}
				}

				for (int i = 0; i < numCenter1; i++) {
					if (skip())
						continue;
					vel.set(RNG.randomFloat(-1f, 1f), RNG.randomFloat(-1f, 1f),
							RNG.randomFloat(-1f, 1f));
					vel.normalise();
					vel.scale(speed * RNG.randomFloat(0f, 0.1f));

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.75f, 1.25f), scale
									* RNG.randomFloat(0.25f, 0.75f));
				}

				for (int i = 0; i < numCenter2; i++) {
					if (skip())
						continue;
					vel.set(RNG.randomFloat(-1f, 1f), RNG.randomFloat(-1f, 1f),
							RNG.randomFloat(-1f, 1f));
					vel.normalise();
					vel.scale(speed * RNG.randomFloat(1f, 1.5f));

					addVelocity(velocity, vel);
					addExplosionParticle(pos, vel,
							lifeLength * RNG.randomFloat(0.5f, 1f),
							scale * RNG.randomFloat(0.1f, 0.25f));
				}
				addLightParticle(pos, velocity, lifeLength, scale);
				damage(source, position, size, scaleType);
			}
		};

		protected abstract void explode(GameObject source, Vector3f position,
				Vector3f velocity, float size, Scale scale);

	}

	public enum Scale {

		SMALL(25, 75, 0.31f, 0.62f, 0.58f), MEDIUM(150, 300, 0.77f, 0.79f,
				0.74f), LARGE(500, 750, 1f, 1f, 1f);

		private final int[] minNum;
		private final int[] maxNum;
		private final float lifeLength;
		private final float scale;
		private final float speed;

		private Scale(int minNum, int maxNum, float lifeLength, float scale,
				float speed) {
			this.minNum = new int[] { minNum / 2, minNum, 2 * minNum };
			this.maxNum = new int[] { maxNum / 2, maxNum, 2 * maxNum };
			this.lifeLength = lifeLength;
			this.scale = scale;
			this.speed = speed;
		}

		public int getMinNum() {
			return minNum[Settings.getParticles()];
		}

		public int getMaxNum() {
			return maxNum[Settings.getParticles()];
		}

		public float getLifeLength() {
			return lifeLength;
		}

		public float getScale() {
			return scale;
		}

		public float getSpeed() {
			return speed;
		}

	}

}
