package kaba4cow.gameobjects.objectcomponents;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Direction.Coordinate;
import kaba4cow.engine.toolbox.maths.Vectors;

public class PortComponent extends ObjectComponent {

	public Vector3f translated;

	public float rotation;

	public float min;
	public float max;
	public boolean visible;

	public boolean occupied;

	public PortComponent(float x, float y, float z, float min, float max,
			boolean visible, float rotation) {
		super(x, y, z, new Direction(), 1f);
		this.rotation = rotation;
		this.min = min;
		this.max = max;
		this.visible = visible;
		this.translated = new Vector3f();
		this.occupied = false;
	}

	public PortComponent(PortComponent portComponent) {
		this(portComponent.pos.x, portComponent.pos.y, portComponent.pos.z,
				portComponent.min, portComponent.max, portComponent.visible,
				portComponent.rotation);
		translated.set(portComponent.translated);
	}

	public static PortComponent read(DataFile node) {
		float x = node.node("pos").getFloat(0);
		float y = node.node("pos").getFloat(1);
		float z = node.node("pos").getFloat(2);
		float min = node.node("size").getFloat(0);
		float max = node.node("size").getFloat(1);
		boolean visible = node.node("visible").getBoolean();
		float rotation = node.node("rotation").getFloat();
		return new PortComponent(x, y, z, min, max, visible, rotation);
	}

	@Override
	public void save(DataFile node) {
		node.node("pos").setFloat(pos.x).setFloat(pos.y).setFloat(pos.z);
		node.node("size").setFloat(min).setFloat(max);
		node.node("visible").setBoolean(visible);
		node.node("rotation").setFloat(rotation);
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
	public void rotate(Coordinate axisCoordinate, float angle, boolean local,
			boolean aroundCenter) {
		if (aroundCenter)
			Vectors.rotate(angle, Vectors.INIT3, Vectors.UP, getPos(), getPos());
		getDirection().rotate(Vectors.UP, angle);
		rotation += angle;
	}

	@Override
	public void resetRotations() {
		super.resetRotations();
		rotation = 0f;
	}

	@Override
	public PortComponent mirrorX() {
		return new PortComponent(-pos.x, pos.y, pos.z, min, max, visible,
				rotation);
	}

	@Override
	public PortComponent mirrorY() {
		return new PortComponent(pos.x, -pos.y, pos.z, min, max, visible,
				rotation);
	}

	@Override
	public PortComponent mirrorZ() {
		return new PortComponent(pos.x, pos.y, -pos.z, min, max, visible,
				rotation);
	}

}
