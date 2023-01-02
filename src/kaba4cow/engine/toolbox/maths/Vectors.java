package kaba4cow.engine.toolbox.maths;

import kaba4cow.engine.toolbox.rng.RNG;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class Vectors {

	public static final Vector2f INIT2 = new Vector2f();
	public static final Vector3f INIT3 = new Vector3f();
	public static final Vector4f INIT4 = new Vector4f();

	public static final Vector2f UNIT2 = new Vector2f(1f, 1f);
	public static final Vector3f UNIT3 = new Vector3f(1f, 1f, 1f);
	public static final Vector4f UNIT4 = new Vector4f(1f, 1f, 1f, 1f);

	public static final Vector3f RIGHT = new Vector3f(-1f, 0f, 0f);
	public static final Vector3f UP = new Vector3f(0f, -1f, 0f);
	public static final Vector3f FORWARD = new Vector3f(0, 0f, -1f);

	public static Vector3f fromInteger(int src, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		src &= 0x00FFFFFF;
		dest.x = ((src >> 16) & 0xFF) * Maths.DIV255;
		dest.y = ((src >> 8) & 0xFF) * Maths.DIV255;
		dest.z = ((src >> 0) & 0xFF) * Maths.DIV255;
		return dest;
	}

	public static Vector2f negate(Vector2f src, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		dest.x = -src.x;
		dest.y = -src.y;
		return dest;
	}

	public static Vector3f negate(Vector3f src, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		dest.x = -src.x;
		dest.y = -src.y;
		dest.z = -src.z;
		return dest;
	}

	public static Vector2f set(Vector2f dest, float value) {
		if (dest == null)
			dest = new Vector2f();
		dest.x = value;
		dest.y = value;
		return dest;
	}

	public static Vector3f set(Vector3f dest, float value) {
		if (dest == null)
			dest = new Vector3f();
		dest.x = value;
		dest.y = value;
		dest.z = value;
		return dest;
	}

	public static Vector4f set(Vector4f dest, float value) {
		if (dest == null)
			dest = new Vector4f();
		dest.x = value;
		dest.y = value;
		dest.z = value;
		dest.w = value;
		return dest;
	}

	public static Vector2f scale(Vector2f src, float scale, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		float x = src.x * scale;
		float y = src.y * scale;
		dest.x = x;
		dest.y = y;
		return dest;
	}

	public static Vector3f scale(Vector3f src, float scale, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		float x = src.x * scale;
		float y = src.y * scale;
		float z = src.z * scale;
		dest.x = x;
		dest.y = y;
		dest.z = z;
		return dest;
	}

	public static Vector2f scaleToOrigin(Vector2f src, Vector2f origin,
			float scale, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		float x = (src.x - origin.x) * scale + origin.x * scale;
		float y = (src.y - origin.y) * scale + origin.y * scale;
		dest.x = x;
		dest.y = y;
		return dest;
	}

	public static Vector3f scaleToOrigin(Vector3f src, Vector3f origin,
			float scale, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		float x = (src.x - origin.x) * scale + origin.x * scale;
		float y = (src.y - origin.y) * scale + origin.y * scale;
		float z = (src.z - origin.z) * scale + origin.z * scale;
		dest.x = x;
		dest.y = y;
		dest.z = z;
		return dest;
	}

	public static Vector2f sum(Vector2f dest, Vector2f... vectors) {
		if (dest == null)
			dest = new Vector2f();
		float invLength = 1f / vectors.length;
		dest.set(0f, 0f);
		for (int i = 0; i < vectors.length; i++)
			addScaled(dest, vectors[i], invLength, dest);
		return dest;
	}

	public static Vector3f sum(Vector3f dest, Vector3f... vectors) {
		if (dest == null)
			dest = new Vector3f();
		float invLength = 1f / vectors.length;
		dest.set(0f, 0f, 0f);
		for (int i = 0; i < vectors.length; i++)
			addScaled(dest, vectors[i], invLength, dest);
		return dest;
	}

	public static Vector2f addScaled(Vector2f left, Vector2f right,
			float scale, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		float x = left.x + right.x * scale;
		float y = left.y + right.y * scale;
		dest.x = x;
		dest.y = y;
		return dest;
	}

	public static Vector2f subScaled(Vector2f left, Vector2f right,
			float scale, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		float x = left.x - right.x * scale;
		float y = left.y - right.y * scale;
		dest.x = x;
		dest.y = y;
		return dest;
	}

	public static Vector3f addScaled(Vector3f left, Vector3f right,
			float scale, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		float x = left.x + right.x * scale;
		float y = left.y + right.y * scale;
		float z = left.z + right.z * scale;
		dest.x = x;
		dest.y = y;
		dest.z = z;
		return dest;
	}

	public static Vector3f subScaled(Vector3f left, Vector3f right,
			float scale, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		float x = left.x - right.x * scale;
		float y = left.y - right.y * scale;
		float z = left.z - right.z * scale;
		dest.x = x;
		dest.y = y;
		dest.z = z;
		return dest;
	}

	public static Vector2f average(Vector2f dest, Vector2f... vectors) {
		if (dest == null)
			dest = new Vector2f();
		float d = 1f / (float) vectors.length;
		float x = 0f;
		float y = 0f;
		for (int i = 0; i < vectors.length; i++) {
			x += d * vectors[i].x;
			y += d * vectors[i].y;
		}
		dest.x = x;
		dest.y = y;
		return dest;
	}

	public static Vector3f average(Vector3f dest, Vector3f... vectors) {
		if (dest == null)
			dest = new Vector3f();
		float d = 1f / (float) vectors.length;
		float x = 0f;
		float y = 0f;
		float z = 0f;
		for (int i = 0; i < vectors.length; i++) {
			x += d * vectors[i].x;
			y += d * vectors[i].y;
			z += d * vectors[i].z;
		}
		dest.x = x;
		dest.y = y;
		dest.z = z;
		return dest;
	}

	public static Vector2f randomize(float minValue, float maxValue,
			Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		dest.x = RNG.randomFloat(minValue, maxValue);
		dest.y = RNG.randomFloat(minValue, maxValue);
		return dest;
	}

	public static Vector3f randomize(float minValue, float maxValue,
			Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		dest.x = RNG.randomFloat(minValue, maxValue);
		dest.y = RNG.randomFloat(minValue, maxValue);
		dest.z = RNG.randomFloat(minValue, maxValue);
		return dest;
	}

	public static Vector3f rotateRandom(float minAngle, float maxAngle,
			Vector3f src, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		rotate(RNG.randomFloat(minAngle, maxAngle), RIGHT, src, dest);
		rotate(RNG.randomFloat(minAngle, maxAngle), UP, src, dest);
		rotate(RNG.randomFloat(minAngle, maxAngle), FORWARD, src, dest);
		return dest;
	}

	public static Vector3f rotate(float angle, Vector3f axis, Vector3f src,
			Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		if (src == null || axis == null)
			return dest;
		axis = axis.normalise(null);
		float cos = Maths.cos(angle);
		float sin = Maths.sin(angle);
		float inX = src.x;
		float inY = src.y;
		float inZ = src.z;
		float sum = (axis.x * src.x + axis.y * src.y + axis.z * src.z)
				* (1f - cos);
		dest.x = axis.x * sum + inX * cos + (-axis.z * inY + axis.y * inZ)
				* sin;
		dest.y = axis.y * sum + inY * cos + (axis.z * inX - axis.x * inZ) * sin;
		dest.z = axis.z * sum + inZ * cos + (-axis.y * inX + axis.x * inY)
				* sin;
		return dest;
	}

	public static Vector3f rotate(float angle, Vector3f origin, Vector3f axis,
			Vector3f src, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		if (src == null || axis == null)
			return dest;
		if (origin == null)
			origin = INIT3;
		axis = axis.normalise(null);
		float cos = Maths.cos(angle);
		float sin = Maths.sin(angle);
		float inX = src.x - origin.x;
		float inY = src.y - origin.y;
		float inZ = src.z - origin.z;
		float sum = (axis.x * src.x + axis.y * src.y + axis.z * src.z)
				* (1f - cos);
		dest.x = axis.x * sum + inX * cos + (-axis.z * inY + axis.y * inZ)
				* sin + origin.x;
		dest.y = axis.y * sum + inY * cos + (axis.z * inX - axis.x * inZ) * sin
				+ origin.y;
		dest.z = axis.z * sum + inZ * cos + (-axis.y * inX + axis.x * inY)
				* sin + origin.z;
		return dest;
	}

}
