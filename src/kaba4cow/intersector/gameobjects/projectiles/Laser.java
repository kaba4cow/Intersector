package kaba4cow.intersector.gameobjects.projectiles;

import org.lwjgl.util.vector.Matrix4f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class Laser extends Projectile {

	protected Laser(Machine parent, Machine targetShip, ProjectileInfo projInfo) {
		super(parent, targetShip, projInfo);
	}

	@Override
	public void render(RendererContainer renderers) {
		float scale = Maths.mapLimit(getElapsedTime(), 0f, 0.2f, 1f, 4f);
		Matrix4f matrix = direction.getMatrix(pos, true, size * scale);
		float brightness = 1f - getElapsedTime() / file.getLifeLength();
		renderers.getLaserRenderer().render(laserModel, matrix, null,
				brightness);
	}

}