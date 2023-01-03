package kaba4cow.intersector.gameobjects.projectiles;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.files.ParticleSystemFile;
import kaba4cow.files.ProjectileFile;
import kaba4cow.intersector.GameSettings;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.WeaponComponent;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.models.LaserModel;
import kaba4cow.intersector.renderEngine.models.ThrustModel;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner.Explosion;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner.Scale;

public class Projectile extends GameObject {

	public static final float SPEED = 500f;

	protected final ProjectileFile file;

	protected final Machine parent;
	protected final Machine targetShip;
	protected final WeaponComponent weapon;

	protected final TexturedModel texturedModel;
	protected final LaserModel laserModel;
	protected final ThrustModel thrustModel;
	protected final ThrustComponent thrustComponent;

	protected float speed;
	protected float thrust;
	protected float damage;

	protected boolean active;
	protected float elapsedTime;
	protected float lifeLength;

	protected Projectile(Machine parent, Machine targetShip,
			ProjectileInfo projInfo) {
		super(parent.getWorld());

		this.file = projInfo.file;

		this.parent = parent;
		this.targetShip = targetShip;
		this.weapon = projInfo.weapon;

		this.texturedModel = file.getTexturedModel();
		this.laserModel = file.getLaserModel();
		this.thrustModel = file.getThrustModel();
		this.thrustComponent = file.getThrustComponent() == null ? null
				: new ThrustComponent(file.getThrustComponent());

		this.size = projInfo.scale;
		this.damage = projInfo.damage;
		this.speed = file.getSpeedScale() * SPEED;
		this.lifeLength = file.getLifeLength() * RNG.randomFloat(0.8f, 1f);
		this.active = true;
		this.elapsedTime = 0f;

		this.pos = new Vector3f(projInfo.pos);
		this.direction = projInfo.direction.copy().rotateRandom(
				-RNG.randomFloat(0.005f), RNG.randomFloat(0.005f));
		this.vel = direction.getForward();
		this.vel.scale(speed);
		if (file.isAutoaim())
			this.vel.scale(0.05f);
		Vector3f.add(parent.getVel(), this.vel, this.vel);
	}

	public static boolean canReach(Vector3f source, Vector3f destination,
			ProjectileFile projectile, float fireScale) {
		float objDistSq = Maths.distSq(source, destination);
		float projDistSq = 0f;
		if (ProjectileType.valueOf(projectile.getType()) == ProjectileType.RAY)
			projDistSq = Maths.sqr(Ray.LENGTH * fireScale);
		else
			projDistSq = Maths.sqr(projectile.getSpeedScale() * SPEED
					* (projectile.getLifeLength() - projectile.getDelay()));
		return projDistSq >= objDistSq;
	}

	public static Vector3f getNextTargetPos(GameObject source, GameObject target) {
		Vector3f sourceVel = Vectors.addScaled(source.getVel(), source
				.getDirection().getForward(), SPEED, null);
		float dist = Maths.dist(source.getPos(), target.getPos());
		float time = sourceVel.length() / dist;
		Vector3f nextPos = Vectors.addScaled(target.getPos(), target.getVel(),
				time, null);
		return nextPos;
	}

	public static void create(Machine parent, Machine targetShip,
			ProjectileInfo projInfo) {
		ProjectileType type = ProjectileType.valueOf(projInfo.file.getType());
		switch (type) {
		case CLUSTERROCKET:
			new ClusterRocket(parent, targetShip, projInfo);
			return;
		case LASER:
			new Laser(parent, targetShip, projInfo);
			return;
		case PROJECTILE:
			new Projectile(parent, targetShip, projInfo);
			return;
		case RAY:
			new Ray(parent, targetShip, projInfo);
			return;
		case ROCKET:
			new Rocket(parent, targetShip, projInfo);
			return;
		case CLUSTER:
			return;
		default:
			return;
		}
	}

	@Override
	public void update(float dt) {
		elapsedTime += dt;
		if (alive && elapsedTime >= lifeLength)
			destroy();

		if (thrustComponent != null && thrust > 0f)
			smokeTrail(thrust, dt);
	}

	public void smokeTrail(float density, float dt) {
		if (GameSettings.getParticles() == 0)
			return;

		ParticleSystem system = ParticleSystemFile.get("SMOKE").get();
		float scale = Maths.PI * density * size;

		if (GameSettings.getParticles() == 1) {
			new Particle(system, pos.negate(null), Vectors.INIT3, 2f,
					RNG.randomFloat(Maths.TWO_PI), RNG.randomFloat(0.9f, 1.1f)
							* scale);
		} else {
			float stepSize = density * dt * vel.length();
			int length = (int) stepSize;
			Vector3f position = Vectors.addScaled(pos, direction.getForward(),
					-size, null).negate(null);
			Vector3f step = vel.normalise(null);
			step.scale(stepSize / (float) length);
			for (int i = 0; i < length; i++)
				if (RNG.chance(0.8f))
					new Particle(system,
							Vector3f.sub(position, step, position),
							Vectors.INIT3, RNG.randomFloat(0.5f, 1.5f),
							RNG.randomFloat(Maths.TWO_PI), scale);
		}
	}

	@Override
	public void render(RendererContainer renderers) {
		Matrix4f matrix = direction.getMatrix(pos, true, size);
		renderers.getModelRenderer().render(texturedModel, null, matrix);
	}

	@Override
	public TargetType getTargetType() {
		return null;
	}

	protected void rotateTo(float x, float y, float dt) {
		float dYaw = dt * Maths.signum(x)
				* Maths.limit(Maths.abs(x), 0.25f, 1f);
		rotateYaw(dYaw);
		float dPitch = dt * Maths.signum(y)
				* Maths.limit(Maths.abs(y), 0.25f, 1f);
		rotatePitch(dPitch);
	}

	protected void smoothRotateTo(float x, float y, float dt) {
		float dYaw = dt * Maths.signum(x) * Maths.limit(Maths.abs(x), 0f, 1f);
		rotateYaw(dYaw);
		float dPitch = dt * Maths.signum(y) * Maths.limit(Maths.abs(y), 0f, 1f);
		rotatePitch(dPitch);
	}

	protected void rotatePitch(float angle) {
		if (angle == 0f)
			return;
		angle = Maths.HALF_PI * Maths.limit(angle, -1f, 1f);
		direction.rotate(direction.getRight(), angle);
	}

	protected void rotateYaw(float angle) {
		if (angle == 0f)
			return;
		angle = Maths.HALF_PI * Maths.limit(angle, -1f, 1f);
		direction.rotate(direction.getUp(), angle);
	}

	public boolean isActivated() {
		return getElapsedTime() >= file.getDelay();
	}

	@Override
	protected void collide(GameObject obj, float dt) {
		return;
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	public void onSpawn() {
		return;
	}

	@Override
	protected void onDestroy(GameObject src) {
		if (file.isExplode())
			explode();
	}

	@Override
	protected void onDestroy() {
		if (file.isExplode())
			explode();
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

	protected void explode() {
		ExplosionSpawner.spawn(this, pos, Vectors.INIT3, 8f * size,
				Explosion.SPHERE, Scale.SMALL);
	}

	public Machine getParent() {
		return parent;
	}

	public ProjectileFile getFile() {
		return file;
	}

	public Machine getTargetShip() {
		return targetShip;
	}

	public float getDamage() {
		return damage;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public float getElapsedTime() {
		return elapsedTime;
	}

}
