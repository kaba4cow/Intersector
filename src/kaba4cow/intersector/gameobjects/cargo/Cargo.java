package kaba4cow.intersector.gameobjects.cargo;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.objectcomponents.ColliderComponent;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.toolbox.RawModelContainer;

public class Cargo extends CargoObject {

	private static final ColliderComponent[] COLLIDERS = new ColliderComponent[] { new ColliderComponent(
			0f, 0f, 0f, 0.5f, 0) };
	private static final float COLLISION_SIZE = ColliderComponent
			.calculateCollisionSize(COLLIDERS);

	public static final float MAX_HEALTH = 25f;

	private static TexturedModel MODEL;

	public Cargo(World world, CargoType type, Vector3f pos) {
		super(world, type, pos);

		this.model = getTexturedModel();

		this.colliders = createColliders(COLLIDERS);

		this.size = 1f;
		this.health = MAX_HEALTH;
	}

	private static TexturedModel getTexturedModel() {
		if (MODEL != null)
			return MODEL;
		return MODEL = new TexturedModel(RawModelContainer.get("MISC/CARGO"),
				ModelTextureFile.get("CARGO").get());
	}

	@Override
	public void update(float dt) {
		if (parent != null)
			destroy();
		else {
			rotate(rotationVel.x * dt, rotationVel.y * dt, rotationVel.z * dt);
			Maths.blend(Vectors.INIT3, rotationVel, 0.1f * dt, rotationVel);
			Maths.blend(Vectors.INIT3, vel, 0.1f * dt, vel);
		}
	}

	@Override
	public void render(RendererContainer renderers) {
		if (!isVisible(renderers.getRenderer()))
			return;
		Matrix4f matrix = direction.getMatrix(pos, true, size);
		renderers.getModelRenderer().render(model, null, matrix);
	}

	@Override
	public boolean isVisible(Renderer renderer) {
		return parent == null && super.isVisible(renderer);
	}

	@Override
	public float getCollisionSize() {
		return size * COLLISION_SIZE;
	}

	public void setParent(Machine parent) {
		this.parent = parent;
		this.parent.addMass(mass);
	}

	@Override
	public void removeParent() {
		if (parent != null)
			parent.addMass(-mass);
		parent = null;
	}

	@Override
	public float getMaxHealth() {
		return MAX_HEALTH;
	}

}
