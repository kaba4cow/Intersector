package kaba4cow.gameobjects.projectiles;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import kaba4cow.engine.toolbox.maths.Easing;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.renderEngine.RendererContainer;

public class Subray extends Projectile {

	private static final Vector2f TEX_OFFSET_STEP = new Vector2f(1f, 4f);

	private final ProjectileInfo projInfo;

	private final Ray ray;
	private final int index;

	private final Vector2f texOffset;

	private float damageDt;

	protected Subray(Machine parent, Machine targetShip,
			ProjectileInfo projInfo, Ray ray, int index) {
		super(parent, targetShip, projInfo);
		this.ray = ray;
		this.index = index;
		this.projInfo = projInfo;
		this.lifeLength = projInfo.file.getLifeLength()
				* projInfo.weapon.weaponFile.getCooldown();
		this.texOffset = new Vector2f();
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		if (!GameObject.isAlive(parent)) {
			destroy();
			return;
		}
		if (!isActive())
			return;
		damageDt = damage * dt;

		Vectors.addScaled(texOffset, TEX_OFFSET_STEP, dt, texOffset);

		Matrix4f shipMatrix = parent.getDirection().getMatrix(parent.getPos(),
				true, parent.getSize());
		ProjectileInfo newProjInfo = ProjectileInfo.calculate(projInfo.weapon,
				projInfo.firePoint, parent, targetShip, shipMatrix);
		direction.set(newProjInfo.direction);
		pos.set(Vectors.addScaled(newProjInfo.pos, direction.getForward(), size
				* index, null));
		vel.set(parent.getVel());
	}

	@Override
	public void render(RendererContainer renderers) {
		if (isActive()) {
			Matrix4f matrix = direction.getMatrix(pos, true, size);
			float brightness = Easing.EASE_OUT_CIRC.getValue(1f - Ray.STEP
					* index);
			renderers.getLaserRenderer().render(laserModel, matrix, texOffset,
					brightness);
		}
		setActive(true);
	}

	@Override
	public float getDamage() {
		return damageDt;
	}

	@Override
	public void destroy(GameObject src) {
		ray.collide(this);
	}

	public int getIndex() {
		return index;
	}

}