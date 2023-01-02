package kaba4cow.engine.renderEngine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.CameraManager;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;

public class Camera {

	private Vector3f pos;

	private Direction direction;

	public Camera() {
		this.reset();
	}

	public void update(float dt) {
		float speed = 16f * dt;
		float angle = 2f * dt;

		if (Keyboard.isKeyDown(Keyboard.KEY_W))
			Vectors.addScaled(pos, direction.getForward(), speed, pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_S))
			Vectors.subScaled(pos, direction.getForward(), speed, pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_A))
			Vectors.addScaled(pos, direction.getRight(), speed, pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_D))
			Vectors.subScaled(pos, direction.getRight(), speed, pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
			Vectors.addScaled(pos, direction.getUp(), speed, pos);
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE))
			Vectors.subScaled(pos, direction.getUp(), speed, pos);

		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN))
			changePitch(-angle);
		if (Keyboard.isKeyDown(Keyboard.KEY_UP))
			changePitch(angle);

		if (Keyboard.isKeyDown(Keyboard.KEY_Q))
			changeYaw(-angle);
		if (Keyboard.isKeyDown(Keyboard.KEY_E))
			changeYaw(angle);

		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT))
			changeRoll(-angle);
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT))
			changeRoll(angle);
	}

	public void orbit(Vector3f off, float distX, float distY, float distZ,
			float pitch, float yaw, Direction newDir) {
		direction = newDir.copy();

		Vector3f right = newDir.getRight();
		Vector3f up = newDir.getUp();
		Vector3f forward = newDir.getForward();

		pos.set(0f, 0f, 0f);
		if (distX != 0f)
			Vectors.addScaled(pos, right, -distX, pos);
		if (distY != 0f)
			Vectors.addScaled(pos, up, -distY, pos);
		if (distZ != 0f)
			Vectors.addScaled(pos, forward, -distZ, pos);

		yaw += Maths.PI;
		Vectors.rotate(pitch, right, pos, pos);
		Vectors.rotate(yaw, up, pos, pos);
		Vector3f.sub(pos, off, pos);

		direction.rotate(right, pitch);
		direction.rotate(up, yaw);
	}

	public void orbit(Vector3f off, float distX, float distY, float distZ,
			CameraManager cameraManager, Direction newDir) {
		float dist = cameraManager.getDist();
		orbit(off, distX * dist, distY * dist, distZ * dist,
				cameraManager.getPitch(), cameraManager.getYaw(), newDir);
	}

	public void orbit(Vector3f off, float distX, float distY, float distZ,
			CameraManager cameraManager) {
		orbit(off, distX, distY, distZ, cameraManager, Direction.INIT);
	}

	public void orbit(Vector3f off, float distX, float distY, float distZ,
			float pitch, float yaw) {
		orbit(off, distX, distY, distZ, pitch, yaw, Direction.INIT);
	}

	public void orbit(Vector3f off, float dist, float pitch, float yaw,
			Direction newDir) {
		orbit(off, 0f, 0f, dist, pitch, yaw, newDir);
	}

	public void orbit(Vector3f off, float dist, float pitch, float yaw) {
		orbit(off, 0f, 0f, dist, pitch, yaw, Direction.INIT);
	}

	public void reset() {
		pos = new Vector3f();
		direction = new Direction();
	}

	public void rotate(Vector3f axis, float angle) {
		direction.rotate(axis, angle);
	}

	public void changePitch(float angle) {
		direction.rotate(direction.getRight(), angle);
	}

	public void changeYaw(float angle) {
		direction.rotate(direction.getUp(), angle);
	}

	public void changeRoll(float angle) {
		direction.rotate(direction.getForward(), angle);
	}

	public Camera copy() {
		Camera copy = new Camera();
		copy.pos = new Vector3f(pos);
		copy.direction = direction.copy();
		return copy;
	}

	public Matrix4f getViewMatrix() {
		return direction.getMatrix(pos.negate(null), false);
	}

	public Direction getDirection() {
		return direction;
	}

	public Vector3f getPos() {
		return pos;
	}

	public void setPos(Vector3f pos) {
		this.pos = pos;
	}

}
