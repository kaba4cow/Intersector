package kaba4cow.intersector.gameobjects;

import java.util.HashMap;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.audio.AudioManager;
import kaba4cow.engine.audio.Source;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.octtree.Searchable3D;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.toolbox.Constants;

public abstract class GameObject implements Searchable3D {

	protected final World world;

	protected Vector3f pos;
	protected Vector3f vel;
	protected Vector3f rotationVel;
	protected Direction direction;

	protected Vector3f off;

	protected float size;
	protected float mass;

	protected boolean alive;

	private HashMap<String, Source> sources;

	public GameObject(World world) {
		this.world = world;
		this.pos = new Vector3f();
		this.vel = new Vector3f();
		this.rotationVel = new Vector3f();
		this.direction = new Direction();
		this.off = new Vector3f();
		this.size = 1f;
		this.mass = 1f;
		this.alive = true;
		this.sources = new HashMap<String, Source>();
		if (world != null)
			world.addObject(this);
	}

	@Override
	protected void finalize() throws Throwable {
		for (String key : sources.keySet())
			sources.get(key).delete();
	}

	public Source getSound(String file) {
		if (!sources.containsKey(file))
			sources.put(file, new Source(Constants.GAMEPLAY));
		return sources.get(file);
	}

	public Source loopSound(String file) {
		Source source = getSound(file);
		source.setPosition(pos);
		if (source.isPlaying())
			return source;
		return source.play(AudioManager.get(file));
	}

	public Source playSound(String file) {
		Source source = getSound(file);
		source.setPitch(RNG.randomFloat(0.95f, 1.05f));
		source.setPosition(pos);
		return source.play(AudioManager.get(file));
	}

	public void collide(float dt) {
		if (!alive || !isCollidable())
			return;
		float collisionSize = getCollisionSize();
		List<GameObject> list = world.getOctTree().query(this, 2f * collisionSize);
		GameObject obj = null;
		float distSq = 0f;
		int listSize = list.size();
		for (int i = 0; i < listSize; i++) {
			obj = list.get(i);
			if (!isAlive(obj))
				continue;
			distSq = Maths.distSq(pos, obj.pos);
			if (distSq < Maths.sqr(collisionSize + obj.getCollisionSize()))
				collide(obj, dt);
		}
	}

	protected abstract void collide(GameObject obj, float dt);

	public boolean isCollidable() {
		return true;
	}

	public abstract void update(float dt);

	public void move(float dt) {
		Vectors.addScaled(pos, vel, dt, pos);
	}

	public void offset(Vector3f offset) {
		Vector3f.sub(pos, offset, pos);
		Vector3f.add(off, offset, off);
	}

	public abstract void render(RendererContainer renderers);

	public boolean isVisible(Renderer renderer) {
		return WindowUtils.isVisible(pos, Maths.SQRT2 * size, renderer);
	}

	public abstract TargetType getTargetType();

	public boolean isTargetable() {
		return getTargetType() != null;
	}

	public boolean isFarTargetable() {
		return false;
	}

	public String getTargetDescription() {
		return "";
	}

	public float getTargetParameter1() {
		return 0f;
	}

	public float getTargetParameter2() {
		return 0f;
	}

	public static boolean isAlive(GameObject object) {
		return object != null && object.alive;
	}

	public static boolean isEnvironment(GameObject object) {
		return object instanceof EnvironmentObject;
	}

	public static float getMassDivider(float mass) {
		return 1f / Maths.sqrt(mass);
	}

	public abstract void damage(int colliderIndex, Projectile proj);

	public abstract void damage(float damage);

	protected abstract void onDamage(Projectile proj);

	public void destroy(GameObject src) {
		if (!alive)
			return;
		alive = false;
		onDestroy(src);
	}

	public void destroy() {
		if (!alive)
			return;
		alive = false;
		onDestroy();
	}

	public abstract void onSpawn();

	protected abstract void onDestroy(GameObject src);

	protected abstract void onDestroy();

	public void rotate(Vector3f axis, float angle) {
		direction.rotate(axis, angle);
	}

	public void rotate(float pitch, float yaw, float roll) {
		rotate(direction.getRight(), pitch);
		rotate(direction.getUp(), yaw);
		rotate(direction.getForward(), roll);
	}

	public World getWorld() {
		return world;
	}

	public Vector3f getPos() {
		return pos;
	}

	public Vector3f getVel() {
		return vel;
	}

	public Vector3f getRotationVel() {
		return rotationVel;
	}

	public Direction getDirection() {
		return direction;
	}

	public float getSize() {
		return size;
	}

	public float getMass() {
		return mass;
	}

	public void addMass(float mass) {
		this.mass += mass;
	}

	public float getCollisionSize() {
		return size;
	}

	public boolean isAlive() {
		return alive;
	}

	@Override
	public float getTreeX() {
		return pos.x;
	}

	@Override
	public float getTreeY() {
		return pos.y;
	}

	@Override
	public float getTreeZ() {
		return pos.z;
	}

}
