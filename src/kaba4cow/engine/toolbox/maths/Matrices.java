package kaba4cow.engine.toolbox.maths;

import kaba4cow.engine.renderEngine.Camera;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Matrices {

	private static final Vector3f tempScale = new Vector3f();

	public static Matrix4f set(Matrix4f dest, Matrix4f src) {
		dest.m00 = src.m00;
		dest.m01 = src.m01;
		dest.m02 = src.m02;
		dest.m03 = src.m03;
		dest.m10 = src.m10;
		dest.m11 = src.m11;
		dest.m12 = src.m12;
		dest.m13 = src.m13;
		dest.m20 = src.m20;
		dest.m21 = src.m21;
		dest.m22 = src.m22;
		dest.m23 = src.m23;
		dest.m30 = src.m30;
		dest.m31 = src.m31;
		dest.m32 = src.m32;
		dest.m33 = src.m33;
		return dest;
	}

	public static Matrix4f scale(Matrix4f dest, float scaleX, float scaleY,
			float scaleZ) {
		tempScale.set(scaleX, scaleY, scaleZ);
		dest.scale(tempScale);
		return dest;
	}

	public static Matrix4f scale(Matrix4f dest, float scale) {
		return scale(dest, scale, scale, scale);
	}

	public static Vector3f getTranslation(Matrix4f matrix, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		dest.x = matrix.m30;
		dest.y = matrix.m31;
		dest.z = matrix.m32;
		return dest;
	}

	public static Vector3f getScale(Matrix4f matrix, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		dest.x = matrix.m00;
		dest.y = matrix.m11;
		dest.z = matrix.m22;
		return dest;
	}

	public static Vector3f transform(Matrix4f mat, Vector3f src, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		Vector4f vec4 = new Vector4f(src.x, src.y, src.z, 1f);
		Matrix4f.transform(mat, vec4, vec4);
		dest.x = vec4.x;
		dest.y = vec4.y;
		dest.z = vec4.z;
		return dest;
	}

	public static Matrix4f inverse(Matrix4f matrix) {
		matrix = new Matrix4f(matrix);
		matrix.invert();
		return matrix;
	}

	public static Matrix4f createPerspectiveProjectionMatrix(float near,
			float far, float fov, float aspectRatio) {
		float scaleY = 1f / (float) Math.tan(Math.toRadians(0.5f * fov));
		float scaleX = scaleY / aspectRatio;
		float frustumSize = far - near;

		Matrix4f matrix = new Matrix4f();
		matrix.m00 = scaleX;
		matrix.m11 = scaleY;
		matrix.m22 = -(far + near) / frustumSize;
		matrix.m23 = -1f;
		matrix.m32 = -2f * near * far / frustumSize;
		matrix.m33 = 0f;
		return matrix;
	}

	public static Matrix4f createOrthographicProjectionMatrix(float near,
			float far, float aspectRatio) {
		float left = -aspectRatio;
		float right = aspectRatio;
		float bottom = -1f;
		float top = 1f;

		Matrix4f matrix = new Matrix4f();
		matrix.m00 = 2f / (right - left);
		matrix.m11 = 2f / (top - bottom);
		matrix.m22 = -2f / (far - near);
		matrix.m30 = -(right + left) / (right - left);
		matrix.m31 = -(top + bottom) / (top - bottom);
		matrix.m32 = -(far + near) / (far - near);
		return matrix;
	}

	public static Matrix4f createTranslationMatrix(Vector3f translation) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector3f translation,
			float pitch, float yaw, float roll, float scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.rotate(pitch, Vectors.RIGHT, matrix, matrix);
		Matrix4f.rotate(yaw, Vectors.UP, matrix, matrix);
		Matrix4f.rotate(roll, Vectors.FORWARD, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale, scale, scale), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createTransformationMatrix(Vector2f translation,
			Vector2f scale) {
		Matrix4f matrix = new Matrix4f();
		matrix.setIdentity();
		Matrix4f.translate(translation, matrix, matrix);
		Matrix4f.scale(new Vector3f(scale.x, scale.y, 1f), matrix, matrix);
		return matrix;
	}

	public static Matrix4f createViewMatrix(Camera camera) {
		return camera.getViewMatrix();
	}

}
