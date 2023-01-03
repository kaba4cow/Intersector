package kaba4cow.intersector.gameobjects;

import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class Empty extends GameObject {

	public Empty() {
		super(null);
	}

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
	public TargetType getTargetType() {
		return null;
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

}
