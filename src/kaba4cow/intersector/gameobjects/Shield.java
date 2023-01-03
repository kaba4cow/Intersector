package kaba4cow.intersector.gameobjects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class Shield extends GameObject {

	private static final float MAX_TIME = 0.45f;

	private float elapsedTime;

	public Shield(World world, Direction direction, Vector3f pos, Vector3f vel,
			float size) {
		super(world);
		this.direction.set(direction);
		this.pos.set(pos);
		this.vel.set(vel);
		this.size = size;
		this.elapsedTime = 0f;
	}

	@Override
	public void update(float dt) {
		elapsedTime += dt;
		if (elapsedTime >= MAX_TIME)
			destroy(this);
	}

	@Override
	public void render(RendererContainer renderers) {
		float normTime = elapsedTime / MAX_TIME;
		Matrix4f matrix = direction.getMatrix(pos, true, normTime * size);
		renderers.getShieldRenderer()
				.render(matrix, elapsedTime, 1f - normTime);
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	protected void collide(GameObject obj, float dt) {

	}

	@Override
	public TargetType getTargetType() {
		return null;
	}

	@Override
	public void damage(int colliderIndex, Projectile proj) {

	}

	@Override
	public void damage(float damage) {

	}

	@Override
	protected void onDamage(Projectile proj) {

	}

	@Override
	public void onSpawn() {

	}

	@Override
	protected void onDestroy(GameObject src) {

	}

	@Override
	protected void onDestroy() {

	}

}
