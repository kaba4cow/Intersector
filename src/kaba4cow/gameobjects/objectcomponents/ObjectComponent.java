package kaba4cow.gameobjects.objectcomponents;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;

public abstract class ObjectComponent {

	public Vector3f pos;
	public final Direction initDirection;
	public Direction direction;
	public float size;

	public ObjectComponent(float x, float y, float z, Direction direction,
			float size) {
		this.pos = new Vector3f(x, y, z);
		this.initDirection = direction.copy();
		this.direction = direction;
		this.size = size;
	}

	public abstract void save(DataFile node);

	public void rotate(Direction.Coordinate axisCoordinate, float angle,
			boolean local, boolean aroundCenter) {
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

	public void resetDirection() {
		direction.set(initDirection);
	}

	public void resetRotations() {
		getDirection().reset();
	}

	public Direction getDirection() {
		return direction;
	}

	public Vector3f getPos() {
		return pos;
	}

	public float getSize() {
		return size;
	}

	public abstract ObjectComponent mirrorX();

	public abstract ObjectComponent mirrorY();

	public abstract ObjectComponent mirrorZ();

}
