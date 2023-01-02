package kaba4cow.engine.utils;

import kaba4cow.engine.renderEngine.Renderer;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public final class WindowUtils {

	private WindowUtils() {

	}

	public static Vector2f toNormalizedCoords(float x, float y, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		dest.x = toNormalizedX(x);
		dest.y = toNormalizedY(y);
		return dest;
	}

	public static float toNormalizedX(float x) {
		return 2f * x / (float) Display.getWidth() - 1f;
	}

	public static float toNormalizedY(float y) {
		return 2f * y / (float) Display.getHeight() - 1f;
	}

	public static Vector2f toWindowCoords(float x, float y, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		dest.x = toWindowX(x);
		dest.y = toWindowY(y);
		return dest;
	}

	public static float toWindowX(float x) {
		return Display.getWidth() * 0.5f * (x + 0.5f);
	}

	public static float toWindowY(float y) {
		return Display.getHeight() * 0.5f * (y + 0.5f);
	}

	public static Vector3f calculateScreenCoords(Vector3f worldPos,
			Matrix4f projectionMatrix, Matrix4f viewMatrix, Vector3f dest) {
		if (dest == null)
			dest = new Vector3f();
		worldPos = worldPos.negate(null);
		Vector4f coords = new Vector4f(worldPos.x, worldPos.y, worldPos.z, 1f);
		Matrix4f.transform(viewMatrix, coords, coords);
		Matrix4f.transform(projectionMatrix, coords, coords);
		dest.x = -1f + 2f * (0.5f * (1f + coords.x / coords.w));
		dest.y = 1f - 2f * (1f - 0.5f * (1f + coords.y / coords.w));
		dest.z = coords.w;
		return dest;
	}

	public static Vector3f calculateScreenCoords(Vector3f worldPos,
			Renderer renderer, Vector3f dest) {
		return calculateScreenCoords(worldPos, renderer.getProjectionMatrix(),
				renderer.getViewMatrix(), dest);
	}

	public static boolean isVisible(Vector3f pos, float size,
			Matrix4f projectionMatrix, Matrix4f viewMatrix) {
		Vector3f screenCoords = calculateScreenCoords(pos, projectionMatrix,
				viewMatrix, null);
		float screenSize = size / screenCoords.z;
		return screenSize >= 1f / Display.getWidth()
				&& screenCoords.x >= -1f - screenSize
				&& screenCoords.x <= 1f + screenSize
				&& screenCoords.y >= -1f - screenSize
				&& screenCoords.y <= 1f + screenSize
				&& screenCoords.z >= -2f * screenSize;
	}

	public static boolean isVisible(Vector3f pos, float size, Renderer renderer) {
		return isVisible(pos, size, renderer.getProjectionMatrix(),
				renderer.getViewMatrix());
	}

	public static float getScreenSize(Vector3f pos, float size,
			Matrix4f projectionMatrix, Matrix4f viewMatrix) {
		Vector3f screenCoords = calculateScreenCoords(pos, projectionMatrix,
				viewMatrix, null);
		float screenSize = size / screenCoords.z;
		return screenSize;
	}

	public static float getScreenSize(Vector3f pos, float size,
			Renderer renderer) {
		return getScreenSize(pos, size, renderer.getProjectionMatrix(),
				renderer.getViewMatrix());
	}

}
