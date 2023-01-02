package kaba4cow.gameobjects.projectiles;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.files.ProjectileFile;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.utils.GameUtils;

public class ClusterRocket extends Projectile {

	protected final Vector3f targetCoords;

	private final boolean broken;

	protected ClusterRocket(Machine parent, Machine targetShip,
			ProjectileInfo projInfo) {
		super(parent, targetShip, projInfo);

		if (RNG.randomFloat(1f) < 0.002f) {
			broken = true;
			lifeLength *= RNG.randomFloat(0.25f);
		} else
			broken = false;

		this.thrust = 0f;

		this.targetCoords = new Vector3f();
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		if (broken) {
			thrust = RNG.randomFloat(1f);
			smoothRotateTo(RNG.randomFloat(-1f, 1f), RNG.randomFloat(-1f, 1f),
					dt);
		} else if (targetShip != null) {
			if (GameObject.isAlive(targetShip)) {
				Renderer renderer = GameUtils.getAiPov();
				Camera camera = renderer.getCamera();
				camera.orbit(pos, 0f, 0f, 0f, direction);
				renderer.updateViewMatrix();

				WindowUtils.calculateScreenCoords(targetShip.getPos(),
						renderer, targetCoords);

				float rotationSpeed = 4f * Maths.limit(getElapsedTime()
						/ file.getDelay());
				if (targetCoords.z > 0f)
					rotateTo(targetCoords.x, targetCoords.y, rotationSpeed * dt);
				else
					rotateYaw(rotationSpeed * dt);
				if (isActivated()) {
					Vector3f dirToCollider = Maths.direction(pos,
							targetShip.getPos());
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

				float distSq = Maths.distSq(pos, targetShip.getPos());
				if (canExplode()
						&& distSq < Maths.sqr(2f * targetShip.getSize()))
					destroy();
			} else if (RNG.chance(0.1f) && canExplode())
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

	private boolean canExplode() {
		return elapsedTime > file.getDelay() + 0.5f;
	}

	@Override
	public void explode() {
		super.explode();
		ProjectileFile file = ProjectileFile.get("CLUSTER");
		Direction clusterDirection = new Direction();
		ProjectileInfo clusterProjInfo = null;
		Cluster cluster = null;
		for (int i = 0; i < 10; i++) {
			clusterDirection.set(direction).rotateRandom(-0.2f, 0.2f);
			clusterProjInfo = new ProjectileInfo(targetShip, pos,
					clusterDirection, weapon, file, 0.25f * size, 0.1f * damage);
			cluster = new Cluster(parent, targetShip, clusterProjInfo);
			Vector3f.add(cluster.getVel(), vel, cluster.getVel());
		}
	}

}
