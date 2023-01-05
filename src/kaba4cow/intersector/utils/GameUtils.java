package kaba4cow.intersector.utils;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.intersector.Settings;

public final class GameUtils {

	private static final Renderer aiPov = new Renderer(Projection.SQUARE, 120f,
			1f, 1000000f, 0f);

	private static final Renderer playerPov = new Renderer(Projection.DEFAULT,
			Settings.getFov(), 0.1f, 100000f, 0f).setAmbientLighting(0.05f);

	private static final Renderer mapPov = new Renderer(Projection.DEFAULT,
			70f, 0.01f, 1000f, 0f).setAmbientLighting(0.1f).addLight(
			new Light(new Vector3f(100000f, 0f, 0f), new Vector3f()));

	private static final Renderer guiPov = new Renderer(Projection.DEFAULT,
			90f, 0.1f, 100000f, 0f);

	private static final Renderer hudPov = new Renderer(Projection.DEFAULT,
			70f, 0.001f, 1000f, 0f).setAmbientLighting(0.05f).addLight(
			new Light(new Vector3f(0f, 0f, -16f), new Vector3f(1f, 1f, 1f)));

	private GameUtils() {

	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {

		}
	}

	public static float getTime() {
		return MainProgram.getElapsedTime();
	}

	public static Renderer getAiPov() {
		return aiPov;
	}

	public static Renderer getPlayerPov() {
		return playerPov;
	}

	public static Renderer getMapPov() {
		return mapPov;
	}

	public static Renderer getGuiPov() {
		return guiPov;
	}

	public static Renderer getHudPov() {
		return hudPov;
	}

}
