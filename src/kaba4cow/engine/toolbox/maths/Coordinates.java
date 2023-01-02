package kaba4cow.engine.toolbox.maths;

import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import kaba4cow.engine.renderEngine.Renderer;

public class Coordinates {

	public static Vector2f calculateScreenPos(Renderer renderer, Vector3f position) {
		Vector4f coords = new Vector4f(position.x, position.y, position.z, 1f);
		Matrix4f.transform(renderer.getViewMatrix(), coords, coords);
		Matrix4f.transform(renderer.getProjectionMatrix(), coords, coords);
		if (coords.w <= 0f)
			return null;
		float x = 0.5f * (coords.x / coords.w + 1f);
		float y = 1f - 0.5f * (coords.y / coords.w + 1f);
		return new Vector2f(x, y);
	}

	public static Vector2f calculateScreenPos(Renderer renderer, Matrix4f matrix) {
		Vector4f coords = new Vector4f(matrix.m30, matrix.m31, matrix.m32, 1f);
		Matrix4f.transform(renderer.getViewMatrix(), coords, coords);
		Matrix4f.transform(renderer.getProjectionMatrix(), coords, coords);
		if (coords.w <= 0f)
			return null;
		float x = 0.5f * (coords.x / coords.w + 1f);
		float y = 1f - 0.5f * (coords.y / coords.w + 1f);
		return new Vector2f(x, y);
	}

	public static Vector3f calculateCameraRay(Renderer renderer, float screenX, float screenY) {
		Vector2f normalizedCoords = getNormalizedCoords(screenX, screenY);
		Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1f, 1f);
		Vector4f eyeCoords = toEyeCoords(renderer, clipCoords);
		Vector3f worldRay = toWorldCoords(renderer, eyeCoords);
		return worldRay;
	}

	private static Vector3f toWorldCoords(Renderer renderer, Vector4f eyeCoords) {
		Matrix4f inverseView = Matrix4f.invert(renderer.getViewMatrix(), null);
		Vector4f rayWorld = Matrix4f.transform(inverseView, eyeCoords, null);
		Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
		mouseRay.normalise();
		return mouseRay;
	}

	private static Vector4f toEyeCoords(Renderer renderer, Vector4f clipCoords) {
		Matrix4f inverseProjection = Matrix4f.invert(renderer.getProjectionMatrix(), null);
		Vector4f eyeCoords = Matrix4f.transform(inverseProjection, clipCoords, null);
		return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
	}

	private static Vector2f getNormalizedCoords(float mouseX, float mouseY) {
		float x = 2f * mouseX / (float) Display.getWidth() - 1f;
		float y = 2f * mouseY / (float) Display.getHeight() - 1f;
		return new Vector2f(x, y);
	}

}
