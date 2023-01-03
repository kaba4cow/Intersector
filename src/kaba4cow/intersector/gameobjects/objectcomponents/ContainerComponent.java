package kaba4cow.intersector.gameobjects.objectcomponents;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.ContainerGroupFile;

public class ContainerComponent extends ObjectComponent {

	public ContainerGroupFile containerGroupFile;
	public String containerGroupName;
	public Vector3f translated;

	public boolean occupied;

	public ContainerComponent(String containerGroupName, float x, float y,
			float z, Direction direction) {
		super(x, y, z, direction, 1f);
		this.containerGroupName = containerGroupName;
		this.containerGroupFile = ContainerGroupFile.get(containerGroupName);
		this.translated = new Vector3f();
		this.occupied = false;
	}

	public ContainerComponent(String containerGroupName) {
		this(containerGroupName, 0f, 0f, 0f, new Direction());
	}

	public ContainerComponent(ContainerComponent containerComponent) {
		this(containerComponent.containerGroupName, containerComponent.pos.x,
				containerComponent.pos.y, containerComponent.pos.z,
				containerComponent.direction.copy());
		translated.set(containerComponent.translated);
	}

	public static ContainerComponent read(DataFile node) {
		float x = node.node("pos").getFloat(0);
		float y = node.node("pos").getFloat(1);
		float z = node.node("pos").getFloat(2);
		String containerGroupName = node.node("containergroupname").getString();
		Direction direction = Direction.fromString(node.node("direction")
				.getString());
		return new ContainerComponent(containerGroupName, x, y, z, direction);
	}

	@Override
	public void save(DataFile node) {
		node.node("pos").setFloat(pos.x).setFloat(pos.y).setFloat(pos.z);
		node.node("containergroupname").setString(containerGroupName);
		node.node("direction").setString(direction.toString());
	}

	public void calculateTranslated(float shipSize) {
		resetDirection();
		translated.set(0f, 0f, 0f);
		Vectors.addScaled(translated, pos, shipSize, translated);
	}

	public void rotateShip(Vector3f axis, float angle) {
		direction.rotate(axis, angle);
		Vectors.rotate(angle, axis, translated, translated);
	}

	@Override
	public ContainerComponent mirrorX() {
		return new ContainerComponent(containerGroupName, -pos.x, pos.y, pos.z,
				direction.copy());
	}

	@Override
	public ContainerComponent mirrorY() {
		return new ContainerComponent(containerGroupName, pos.x, -pos.y, pos.z,
				direction.copy());
	}

	@Override
	public ContainerComponent mirrorZ() {
		return new ContainerComponent(containerGroupName, pos.x, pos.y, -pos.z,
				direction.copy());
	}
}
