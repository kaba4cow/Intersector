package kaba4cow.intersector.gameobjects.cargo;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.objectcomponents.ColliderComponent;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.toolbox.collision.ColliderHolder;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner.Explosion;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner.Scale;

public abstract class CargoObject extends GameObject implements ColliderHolder {

	protected TexturedModel model;

	protected ColliderComponent[] colliders;

	protected final CargoType type;

	protected float health;
	protected Machine parent;

	public CargoObject(World world, CargoType type, Vector3f pos) {
		super(world);
		this.pos = new Vector3f(pos);
		this.type = type;
		this.parent = null;
		this.model = null;
	}

	protected static ColliderComponent[] createColliders(ColliderComponent[] colliders) {
		ColliderComponent[] newColliders = new ColliderComponent[colliders.length];
		for (int i = 0; i < newColliders.length; i++)
			newColliders[i] = new ColliderComponent(colliders[i]);
		return newColliders;
	}

	@Override
	protected void onDestroy(GameObject src) {
		Explosion explosion = RNG.randomBoolean() ? Explosion.RAYS : Explosion.SPHERE;
		ExplosionSpawner.spawn(this, pos, vel, size, explosion, Scale.MEDIUM);
		onDestroy();
	}

	public abstract void removeParent();

	public void onEject() {
		if (parent == null || parent == null || !parent.isAlive())
			return;
		float mag = 0.04f * Maths.HALF_PI;
		rotationVel.set(RNG.randomFloat(-mag, mag), RNG.randomFloat(-mag, mag), RNG.randomFloat(-mag, mag));
		Vector3f dir = Maths.direction(parent.getPos(), pos);
		Vectors.rotateRandom(-0.1f, 0.1f, dir, dir);
		Vectors.addScaled(vel, dir, 0.1f * size, vel);
		Vector3f.add(vel, parent.getVel(), vel);
		removeParent();
	}

	public void onParentDestroy() {
		if (parent == null || parent != null && parent.isAlive())
			return;
		float mag = 0.57f * Maths.HALF_PI;
		rotationVel.set(RNG.randomFloat(-mag, mag), RNG.randomFloat(-mag, mag), RNG.randomFloat(-mag, mag));
		removeParent();
	}

	@Override
	public void burn(ColliderComponent colliderComponent, float damage) {
		if (!alive)
			return;
		health -= damage;
		if (health <= 0f)
			destroy(this);
	}

	public TexturedModel getModel() {
		return model;
	}

	@Override
	public boolean isCollidable() {
		if (parent != null)
			return super.isCollidable() && parent.isCollidable();
		return super.isCollidable();
	}

	public void resetDirection() {
		direction.reset();
	}

	@Override
	public boolean hasShield() {
		return false;
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
	protected void collide(GameObject obj, float dt) {
		if (obj == parent)
			return;
		if (obj instanceof CargoObject) {
			CargoObject cargo = (CargoObject) obj;
			if (cargo.getParent() != null && cargo.getParent() == parent)
				return;
		}
		if (obj instanceof ColliderHolder) {
			ColliderHolder.super.collide((ColliderHolder) obj, dt);
		} else if (obj instanceof Projectile) {
			Projectile proj = (Projectile) obj;
			if (proj.getParent() == parent || !proj.isActive())
				return;
			Matrix4f shipMatrix = direction.getMatrix(pos, true, size);
			ColliderComponent[] infos = colliders;
			Matrix4f mat1 = null;
			Vector3f colliderPos = new Vector3f();
			Vector3f colliderScale = new Vector3f();
			Vector3f laserPos = proj.getPos().negate(null);
			for (int i = 0; i < infos.length; i++) {
				Vectors.set(colliderScale, infos[i].size);
				mat1 = new Matrix4f();
				mat1.translate(infos[i].pos.negate(null));
				mat1.scale(colliderScale);
				Matrix4f.mul(shipMatrix, mat1, mat1);
				Matrices.getTranslation(mat1, colliderPos);

				float radius = (size * infos[i].size + 1f);
				float distSq = Maths.distSq(colliderPos, laserPos) - radius * radius;
				if (distSq <= 0f) {
					damage(i, proj);
					obj.destroy(this);
					return;
				}
			}
		}
	}

	public void damage(int colliderIndex, Projectile proj) {
		if (!alive)
			return;
		float damage = colliders[colliderIndex].getDamage(proj);
		colliders[colliderIndex].damage(damage);
		health -= damage;
		if (health <= 0f)
			destroy(proj);
	}

	public void damage(float damage) {
		health -= damage;
		if (health <= 0f)
			destroy(this);
	}

	@Override
	protected void onDestroy() {
		removeParent();
	}

	@Override
	public TargetType getTargetType() {
		if (parent != null)
			return null;
		return TargetType.CARGO;
	}

	@Override
	public String getTargetDescription() {
		return "CARGO: " + type.toString();
	}

	@Override
	public float getTargetParameter1() {
		return 0f;
	}

	@Override
	public float getTargetParameter2() {
		return getHealth() / getMaxHealth();
	}

	public Machine getParent() {
		return parent;
	}

	public float getHealth() {
		return health;
	}

	public CargoType getType() {
		return type;
	}

	public abstract float getMaxHealth();

	@Override
	public ColliderComponent[] getColliders() {
		return colliders;
	}

}
