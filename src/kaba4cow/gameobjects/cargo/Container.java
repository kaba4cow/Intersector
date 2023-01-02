package kaba4cow.gameobjects.cargo;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.files.ContainerFile;
import kaba4cow.files.ContainerGroupFile;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.gameobjects.World;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.gameobjects.objectcomponents.ColliderComponent;
import kaba4cow.gameobjects.objectcomponents.ContainerComponent;
import kaba4cow.renderEngine.RendererContainer;

public class Container extends CargoObject {

	private static final ColliderComponent[] COLLIDERS = new ColliderComponent[] {
			new ColliderComponent(0f, 0f, -0.3f, 0.25f, 0),
			new ColliderComponent(0f, 0f, 0.0f, 0.25f, 0),
			new ColliderComponent(0, 0f, 0.3f, 0.25f, 0) };
	private static final float COLLISION_SIZE = ColliderComponent
			.calculateCollisionSize(COLLIDERS);

	private final ContainerGroupFile groupFile;
	private final ContainerFile file;

	private ContainerComponent component;

	public Container(World world, String containerGroupName, CargoType type,
			Vector3f pos) {
		super(world, type, pos);

		this.component = null;
		this.groupFile = ContainerGroupFile.get(containerGroupName);
		this.file = ContainerFile.get(groupFile.getRandomContainer());
		this.model = file.getTexturedModel(RNG.randomInt());

		this.colliders = createColliders(COLLIDERS);

		this.size = groupFile.getSize();
		this.mass = groupFile.getMass();
		this.health = groupFile.getHealth();
	}

	@Override
	public void update(float dt) {
		if (parent != null) {
			rotationVel.set(0f, 0f, 0f);
			vel.set(parent.getVel());
			pos.set(component.translated);
			Vector3f.add(pos, parent.getPos(), pos);
			direction.set(component.direction);
		} else {
			rotate(rotationVel.x * dt, rotationVel.y * dt, rotationVel.z * dt);
			Maths.blend(Vectors.INIT3, rotationVel, 0.1f * rotationVel.length()
					* dt, rotationVel);
			Maths.blend(Vectors.INIT3, vel, 0.01f * vel.length() * dt, vel);
		}
	}

	@Override
	public void render(RendererContainer renderers) {
		if (!isVisible(renderers.getRenderer()))
			return;
		if (parent != null) {
			rotationVel.set(0f, 0f, 0f);
			pos.set(component.translated);
			Vector3f.add(pos, parent.getPos(), pos);
			direction.set(component.direction);
		}
		if (parent != null)
			direction.set(component.direction);
		Matrix4f matrix = direction.getMatrix(pos, true, size);
		renderers.getModelRenderer().render(model, null, matrix);
	}

	@Override
	public boolean isVisible(Renderer renderer) {
		return (parent == null || parent.isVisible(renderer))
				&& super.isVisible(renderer);
	}

	@Override
	public float getCollisionSize() {
		return size * COLLISION_SIZE;
	}

	@Override
	protected void onDestroy(GameObject source) {
		for (int i = 0; i < mass; i++) {
			Cargo cargo = new Cargo(world, type, pos);
			Vectors.randomize(-size, size, cargo.getVel());
			Vectors.randomize(-size, size, cargo.getRotationVel());
			Vector3f.add(cargo.getVel(), vel, cargo.getVel());
		}
		super.onDestroy();
	}

	public ContainerComponent getComponent() {
		return component;
	}

	public void setParent(Machine parent, ContainerComponent component) {
		this.parent = parent;
		this.component = component;
		this.component.occupied = true;
		this.parent.addMass(mass);
	}

	@Override
	public void removeParent() {
		if (parent != null)
			parent.addMass(-mass);
		parent = null;
		if (component != null)
			component.occupied = false;
		component = null;
	}

	public ContainerGroupFile getGroupFile() {
		return groupFile;
	}

	public ContainerFile getFile() {
		return file;
	}

	@Override
	public float getMaxHealth() {
		return groupFile.getHealth();
	}

}
