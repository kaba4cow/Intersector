package kaba4cow.gameobjects.objectcomponents;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Direction.Coordinate;
import kaba4cow.engine.toolbox.maths.Vectors;

public class ThrustComponent extends ObjectComponent {

	public float rotation;

	public ThrustComponent(float x, float y, float z, float rotation, float size) {
		super(x, y, z, new Direction(), size);
		this.rotation = rotation;
	}

	public ThrustComponent() {
		this(0f, 0f, 0f, 0f, 0.1f);
	}

	public ThrustComponent(ThrustComponent thrustComponent) {
		this(thrustComponent.pos.x, thrustComponent.pos.y,
				thrustComponent.pos.z, thrustComponent.rotation,
				thrustComponent.size);
	}

	public static ThrustComponent read(DataFile node) {
		float x = node.node("pos").getFloat(0);
		float y = node.node("pos").getFloat(1);
		float z = node.node("pos").getFloat(2);
		float size = node.node("size").getFloat();
		float rotation = node.node("rotation").getFloat();
		return new ThrustComponent(x, y, z, rotation, size);
	}

	@Override
	public void save(DataFile node) {
		node.node("pos").setFloat(pos.x).setFloat(pos.y).setFloat(pos.z);
		node.node("size").setFloat(size);
		node.node("rotation").setFloat(rotation);
	}

	@Override
	public void rotate(Coordinate axisCoordinate, float angle, boolean local,
			boolean aroundCenter) {
		rotation += angle;
		Vector3f axis = null;
		switch (axisCoordinate) {
		case X:
			axis = local ? getDirection().getRight() : Vectors.RIGHT;
			break;
		case Y:
			axis = local ? getDirection().getUp() : Vectors.UP;
			break;
		case Z:
			axis = local ? getDirection().getForward() : Vectors.FORWARD;
			break;
		}
		if (aroundCenter)
			Vectors.rotate(angle, Vectors.INIT3, axis, getPos(), getPos());
		getDirection().rotate(axis, angle);
	}

	@Override
	public void resetRotations() {
		rotation = 0f;
	}

	@Override
	public ThrustComponent mirrorX() {
		return new ThrustComponent(-pos.x, pos.y, pos.z, rotation, size);
	}

	@Override
	public ThrustComponent mirrorY() {
		return new ThrustComponent(pos.x, -pos.y, pos.z, rotation, size);
	}

	@Override
	public ThrustComponent mirrorZ() {
		return new ThrustComponent(pos.x, pos.y, -pos.z, rotation, size);
	}

}
