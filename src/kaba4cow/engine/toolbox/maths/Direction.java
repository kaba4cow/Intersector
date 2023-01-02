package kaba4cow.engine.toolbox.maths;

import kaba4cow.engine.toolbox.rng.RNG;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Direction {

	public static final Direction INIT = new Direction();

	private Vector3f right;
	private Vector3f up;
	private Vector3f forward;

	private Matrix4f matrix;

	public Direction() {
		this.right = new Vector3f(Vectors.RIGHT);
		this.up = new Vector3f(Vectors.UP);
		this.forward = new Vector3f(Vectors.FORWARD);
		this.matrix = new Matrix4f();
		this.matrix.setIdentity();
	}

	public Direction(Vector3f right, Vector3f up, Vector3f forward,
			Matrix4f matrix) {
		this.right = right;
		this.up = up;
		this.forward = forward;
		this.matrix = matrix;
	}

	public Direction rotateRandom(float minAngle, float maxAngle) {
		rotate(Vectors.RIGHT, RNG.randomFloat(minAngle, maxAngle));
		rotate(Vectors.UP, RNG.randomFloat(minAngle, maxAngle));
		rotate(Vectors.FORWARD, RNG.randomFloat(minAngle, maxAngle));
		return this;
	}

	public Direction rotate(Vector3f axis, float angle) {
		if (angle == 0f)
			return this;
		Matrix4f.rotate(-angle, axis, matrix, matrix);
		Vectors.rotate(angle, axis, right, right);
		Vectors.rotate(angle, axis, up, up);
		Vectors.rotate(angle, axis, forward, forward);
		return this;
	}

	public Matrix4f getMatrix(Vector3f pos, boolean invert, float scaleX,
			float scaleY, float scaleZ) {
		Matrix4f mat = new Matrix4f(matrix);
		if (pos != null)
			Matrix4f.translate(pos, mat, mat);
		if (invert)
			mat.invert();
		Matrices.scale(mat, scaleX, scaleY, scaleZ);
		return mat;
	}

	public Matrix4f getMatrix(Vector3f pos, boolean invert, float scale) {
		return getMatrix(pos, invert, scale, scale, scale);
	}

	public Matrix4f getMatrix(Vector3f pos, boolean invert) {
		return getMatrix(pos, invert, 1f);
	}

	public Vector3f getRight() {
		return new Vector3f(right);
	}

	public Vector3f getUp() {
		return new Vector3f(up);
	}

	public Vector3f getForward() {
		return new Vector3f(forward);
	}

	public Direction reset() {
		forward.set(Vectors.FORWARD);
		right.set(Vectors.RIGHT);
		up.set(Vectors.UP);
		matrix = new Matrix4f();
		matrix.setIdentity();
		return this;
	}

	public Direction set(Direction direction) {
		forward.set(direction.forward);
		right.set(direction.right);
		up.set(direction.up);
		Matrices.set(matrix, direction.matrix);
		return this;
	}

	public Direction copy() {
		Direction copy = new Direction();
		copy.forward = new Vector3f(forward);
		copy.right = new Vector3f(right);
		copy.up = new Vector3f(up);
		copy.matrix = new Matrix4f(matrix);
		return copy;
	}

	@Override
	public String toString() {
		String string = new String();
		string += right.x + "/" + right.y + "/" + right.z + "/";
		string += up.x + "/" + up.y + "/" + up.z + "/";
		string += forward.x + "/" + forward.y + "/" + forward.z + "/";
		string += matrix.m00 + "/" + matrix.m01 + "/" + matrix.m02 + "/"
				+ matrix.m03 + "/";
		string += matrix.m10 + "/" + matrix.m11 + "/" + matrix.m12 + "/"
				+ matrix.m13 + "/";
		string += matrix.m20 + "/" + matrix.m21 + "/" + matrix.m22 + "/"
				+ matrix.m23 + "/";
		string += matrix.m30 + "/" + matrix.m31 + "/" + matrix.m32 + "/"
				+ matrix.m33;
		return string;
	}

	public static Direction fromString(String string) {
		if (string == null || string.isEmpty())
			return null;
		String[] array = string.split("/");
		int i = 0;
		Vector3f right = new Vector3f(Float.parseFloat(array[i++]),
				Float.parseFloat(array[i++]), Float.parseFloat(array[i++]));
		Vector3f up = new Vector3f(Float.parseFloat(array[i++]),
				Float.parseFloat(array[i++]), Float.parseFloat(array[i++]));
		Vector3f forward = new Vector3f(Float.parseFloat(array[i++]),
				Float.parseFloat(array[i++]), Float.parseFloat(array[i++]));
		Matrix4f matrix = new Matrix4f();
		matrix.m00 = Float.parseFloat(array[i++]);
		matrix.m01 = Float.parseFloat(array[i++]);
		matrix.m02 = Float.parseFloat(array[i++]);
		matrix.m03 = Float.parseFloat(array[i++]);
		matrix.m10 = Float.parseFloat(array[i++]);
		matrix.m11 = Float.parseFloat(array[i++]);
		matrix.m12 = Float.parseFloat(array[i++]);
		matrix.m13 = Float.parseFloat(array[i++]);
		matrix.m20 = Float.parseFloat(array[i++]);
		matrix.m21 = Float.parseFloat(array[i++]);
		matrix.m22 = Float.parseFloat(array[i++]);
		matrix.m23 = Float.parseFloat(array[i++]);
		matrix.m30 = Float.parseFloat(array[i++]);
		matrix.m31 = Float.parseFloat(array[i++]);
		matrix.m32 = Float.parseFloat(array[i++]);
		matrix.m33 = Float.parseFloat(array[i++]);
		return new Direction(right, up, forward, matrix);
	}

	public static enum Coordinate {

		X, Y, Z;

	}
}
