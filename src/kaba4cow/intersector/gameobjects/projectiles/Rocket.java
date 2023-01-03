package kaba4cow.intersector.gameobjects.projectiles;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.objectcomponents.ColliderComponent;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.utils.GameUtils;
import kaba4cow.intersector.utils.RenderUtils;

public class Rocket extends Projectile {

	protected int bestCollider;

	protected final Vector3f colliderPos;
	protected final Vector3f targetCoords;

	private final boolean broken;

	protected Rocket(Machine parent, Machine targetShip, ProjectileInfo projInfo) {
		super(parent, targetShip, projInfo);

		if (RNG.randomFloat(1f) < 0.002f) {
			broken = true;
			lifeLength *= RNG.randomFloat(0.25f);
		} else
			broken = false;

		this.thrust = 0f;
		this.bestCollider = getBestCollider();

		this.colliderPos = new Vector3f();
		this.targetCoords = new Vector3f();
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		if (broken) {
			thrust = RNG.randomFloat(1f);
			smoothRotateTo(RNG.randomFloat(-1f, 1f), RNG.randomFloat(-1f, 1f),
					dt);
		} else if (targetShip != null && bestCollider >= 0) {
			if (GameObject.isAlive(targetShip)) {
				if (bestCollider < -1)
					colliderPos.set(targetShip.getPos());
				else
					targetShip.getColliderPosition(bestCollider, colliderPos);

				Vectors.average(colliderPos, colliderPos.negate(null),
						targetShip.getPos());

				Renderer renderer = GameUtils.getAiPov();
				Camera camera = renderer.getCamera();
				camera.orbit(pos, 0f, 0f, 0f, direction);
				renderer.updateViewMatrix();
				WindowUtils.calculateScreenCoords(colliderPos, renderer,
						targetCoords);

				float rotationSpeed = 4f * Maths.limit(getElapsedTime()
						/ file.getDelay());
				if (targetCoords.z > 0f)
					rotateTo(targetCoords.x, targetCoords.y, rotationSpeed * dt);
				else
					rotateYaw(rotationSpeed * dt);
				if (isActivated()) {
					Vector3f dirToCollider = Maths.direction(pos, colliderPos);
					float dotDirection = Maths.max(
							Vector3f.dot(vel.normalise(null), dirToCollider),
							0f);
					dotDirection = Maths.pow(dotDirection, 8f);
					float dotVelocity = Maths.max(Vector3f.dot(vel
							.normalise(null), targetShip.getDirection()
							.getForward()), 0.75f);
					thrust = Maths.max(dotDirection * dotVelocity, 0.1f);
				} else
					thrust = 0.1f;
			} else if (RNG.chance(0.1f) && isActivated())
				destroy();
		} else {
			thrust = isActivated() ? 1f : 0.1f;
			smoothRotateTo(RNG.randomFloat(-0.01f, 0.01f),
					RNG.randomFloat(-0.01f, 0.01f), 2f * dt);
		}

		float lifeNorm = Maths.mapLimit(elapsedTime, file.getDelay(),
				lifeLength, 0f, 1f);
		thrust *= 1f - Maths.pow(lifeNorm, 8f);

		Vector3f newVel = direction.getForward();
		newVel.scale(speed * thrust);
		if (isActivated())
			Maths.blend(newVel, vel, file.getAiming() * dt, vel);
	}

	@Override
	public void render(RendererContainer renderers) {
		if (!isVisible(renderers.getRenderer()))
			return;

		Matrix4f matrix = direction.getMatrix(pos, true, size);
		matrix.rotate(Maths.HALF_PI * getElapsedTime(), Vectors.FORWARD);
		renderers.getModelRenderer().render(texturedModel, null, matrix);

		if (thrustComponent != null)
			RenderUtils.renderThrusts(thrustModel, thrust, 4f, size, matrix,
					renderers, thrustComponent);
	}

	private int getBestCollider() {
		if (targetShip == null)
			return -1;
		ColliderComponent[] colliders = targetShip.getColliders();
		float maxSize = Float.NEGATIVE_INFINITY;
		int minStrength = Integer.MAX_VALUE;
		int best = RNG.randomInt(colliders.length);
		if (RNG.randomBoolean())
			for (int i = 0; i < colliders.length; i++) {
				if (colliders[i].size > maxSize
						&& colliders[i].strength <= minStrength) {
					maxSize = colliders[i].size;
					minStrength = colliders[i].strength;
					best = i;
				}
			}
		return best;
	}

}
