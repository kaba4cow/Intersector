package kaba4cow.gameobjects.projectiles;

import org.lwjgl.util.vector.Matrix4f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.renderEngine.RendererContainer;

public class Cluster extends Projectile {

	protected Cluster(Machine parent, Machine targetShip,
			ProjectileInfo projInfo) {
		super(parent, targetShip, projInfo);
		this.vel.scale(0.5f);
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		Maths.blend(Vectors.INIT3, vel, 0.5f * dt, vel);
	}

	@Override
	public void render(RendererContainer renderers) {
		Matrix4f matrix = direction.getMatrix(pos, true, size);
		renderers.getModelRenderer().render(texturedModel, null, matrix);
	}

}