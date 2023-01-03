package kaba4cow.intersector.gameobjects.objectcomponents;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.files.ParticleSystemFile;
import kaba4cow.intersector.GameSettings;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.toolbox.collision.ColliderHolder;

public class ColliderComponent extends ObjectComponent {

	public static final float MAX_BURN_TIME = 10f;

	public static final int MIN_STRENGTH = -2;
	public static final int MAX_STRENGTH = 2;

	public int strength;

	private float burnTime;

	public ColliderComponent(float x, float y, float z, float size, int strength) {
		super(x, y, z, new Direction(), size);
		this.strength = strength;
	}

	public ColliderComponent() {
		this(0f, 0f, 0f, 0.1f, 0);
	}

	public ColliderComponent(ColliderComponent colliderComponent) {
		this(colliderComponent.pos.x, colliderComponent.pos.y,
				colliderComponent.pos.z, colliderComponent.size,
				colliderComponent.strength);
	}

	public static ColliderComponent read(DataFile node) {
		float x = node.node("pos").getFloat(0);
		float y = node.node("pos").getFloat(1);
		float z = node.node("pos").getFloat(2);
		float size = node.node("size").getFloat();
		int strength = node.node("strength").getInt();
		return new ColliderComponent(x, y, z, size, strength);
	}

	@Override
	public void save(DataFile node) {
		node.node("pos").setFloat(pos.x).setFloat(pos.y).setFloat(pos.z);
		node.node("size").setFloat(size);
		node.node("strength").setInt(strength);
	}

	public void update(ColliderHolder holder, Matrix4f parentMatrix, float dt) {
		burnTime -= dt;
		if (burnTime > 0f)
			burn(holder, parentMatrix, dt);
	}

	public static float calculateCollisionSize(ColliderComponent[] colliderArray) {
		float maxDist = 0f;
		for (int i = 0; i < colliderArray.length; i++) {
			Vector3f pos = colliderArray[i].pos;
			float size = colliderArray[i].size;
			maxDist = Maths.max(maxDist, pos.length() + size);
		}
		return maxDist;
	}

	public static float calculateCollisionSize(
			List<ColliderComponent> colliderList) {
		float maxDist = 0f;
		for (int i = 0; i < colliderList.size(); i++) {
			Vector3f pos = colliderList.get(i).pos;
			float size = colliderList.get(i).size;
			maxDist = Maths.max(maxDist, pos.length() + size);
		}
		return maxDist;
	}

	private void burn(ColliderHolder holder, Matrix4f parentMatrix, float dt) {
		float burnDamage = holder.getSize() * size
				* Maths.map(strength, MIN_STRENGTH, MAX_STRENGTH, 8f, 0f) * dt;
		holder.burn(this, burnDamage);
		ParticleSystem system = ParticleSystemFile.get("FIRE").get();
		float scale = 1.4f * holder.getSize() * size;
		float speed = 1.8f * scale;
		float rotation = RNG.randomFloat(-0.25f, 0.25f);
		Vector3f right = holder.getDirection().getRight();
		Vector3f up = holder.getDirection().getUp();
		Vector3f forward = holder.getDirection().getForward();
		Vector3f vel = new Vector3f();
		Vector3f pos = new Vector3f();
		getPosition(parentMatrix, pos);
		int num = GameSettings.getParticles() == 0 ? 1
				: (RNG.randomInt(1, 3) + GameSettings.getParticles());
		for (int i = 0; i < num; i++) {
			vel.set(0f, 0f, 0f);
			Vectors.rotate(RNG.randomFloat(-0.2f, 0.2f), right, up, vel);
			Vectors.rotate(RNG.randomFloat(-0.2f, 0.2f), forward, vel, vel);
			vel.scale(speed * RNG.randomFloat(0.75f, 1.25f));

			Vector3f.add(vel, holder.getVel(), vel);
			new Particle(system, pos, vel.negate(null), 0.1f * burnTime
					+ RNG.randomFloat(0f, 1f), rotation, scale
					* RNG.randomFloat(0.8f, 1.2f));
		}
	}

	public void setOnFire() {
		burnTime = MAX_BURN_TIME * RNG.randomFloat(0.1f, 1f);
	}

	public void damage(float damage) {
		if (RNG.chance(Maths.map(strength, MIN_STRENGTH, MAX_STRENGTH, 0.25f,
				-1f)))
			setOnFire();
	}

	public float getDamage(Projectile proj) {
		return proj.getDamage()
				* Maths.map(strength, MIN_STRENGTH, MAX_STRENGTH, 2f, 0.25f);
	}

	public float getPosition(Matrix4f parentMatrix, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		Vector3f colliderScale = new Vector3f();
		Vectors.set(colliderScale, size);
		Matrix4f mat = new Matrix4f();
		mat.translate(pos.negate(null));
		mat.scale(colliderScale);
		Matrix4f.mul(parentMatrix, mat, mat);
		Matrices.getTranslation(mat, dest);
		return size;
	}

	@Override
	public ColliderComponent mirrorX() {
		return new ColliderComponent(-pos.x, pos.y, pos.z, size, strength);
	}

	@Override
	public ColliderComponent mirrorY() {
		return new ColliderComponent(pos.x, -pos.y, pos.z, size, strength);
	}

	@Override
	public ColliderComponent mirrorZ() {
		return new ColliderComponent(pos.x, pos.y, -pos.z, size, strength);
	}

}
