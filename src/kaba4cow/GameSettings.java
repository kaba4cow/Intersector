package kaba4cow;

import java.io.File;

import kaba4cow.engine.toolbox.Shaders;
import kaba4cow.engine.toolbox.files.ConfigFile;
import kaba4cow.engine.utils.ProgramUtils;

public final class GameSettings {

	private static final ConfigFile file = ConfigFile.read(new File("settings"));

	public static final int[] CUBEMAP_SIZES = { 512, 1024, 2048 };
	public static final int[] COLLISION_STEPS = { 8, 16, 24, 32, 40 };

	private static int cubemaps;
	private static int collisions;
	private static int particles;
	private static int fov;
	private static boolean fullscreen;
	private static boolean logs;
	private static float audio;
	private static float music;
	private static float gameplay;
	private static float menu;
	private static boolean postProcessingEnable;

	private GameSettings() {

	}

	public static void loadSettings() {
		Shaders.setLights(4);
		ProgramUtils.setScreenshotLocation("screenshots/");
		ProgramUtils.setShaderLocation("/kaba4cow/shaders/");
		ProgramUtils.setFontLocation("fonts/");
		ProgramUtils.setCubemapLocation("cubemaps/");
		ProgramUtils.setAudioLocation("sounds/");

		cubemaps = file.getInt("cubemaps");
		collisions = file.getInt("collisions");
		particles = file.getInt("particles");
		fov = file.getInt("fov");

		fullscreen = file.getBoolean("fullscreen");
		postProcessingEnable = file.getBoolean("postprocessing");
		logs = file.getBoolean("logs");

		audio = file.getFloat("audio");
		music = file.getFloat("music");
		gameplay = file.getFloat("gameplay");
		menu = file.getFloat("menu");
	}

	public static void saveSettings() {
		file.setInt("cubemaps", cubemaps);
		file.setInt("collisions", collisions);
		file.setInt("particles", particles);
		file.setInt("fov", fov);

		file.setBoolean("fullscreen", fullscreen);
		file.setBoolean("postprocessing", postProcessingEnable);
		file.setBoolean("logs", logs);

		file.setFloat("audio", audio);
		file.setFloat("music", music);
		file.setFloat("gameplay", gameplay);
		file.setFloat("menu", menu);

		ConfigFile.write(file, new File("settings"));
	}

	public static int getCubemaps() {
		return cubemaps;
	}

	public static int setCubemaps(int newCubemaps) {
		return cubemaps = newCubemaps;
	}

	public static int getCollisions() {
		return collisions;
	}

	public static int setCollisions(int newCollisions) {
		return collisions = newCollisions;
	}

	public static int getParticles() {
		return particles;
	}

	public static int setParticles(int newParticles) {
		return particles = newParticles;
	}

	public static int getFov() {
		return fov;
	}

	public static int setFov(int newFov) {
		return fov = newFov;
	}

	public static boolean getFullscreen() {
		return fullscreen;
	}

	public static boolean setFullscreen(boolean newFullscreen) {
		return fullscreen = newFullscreen;
	}

	public static boolean getLogs() {
		return logs;
	}

	public static boolean setLogs(boolean newLogs) {
		return logs = newLogs;
	}

	public static float getAudioVolume() {
		return audio;
	}

	public static void setAudioVolume(float volume) {
		GameSettings.audio = volume;
	}

	public static float getMusicVolume() {
		return music;
	}

	public static void setMusicVolume(float volume) {
		GameSettings.music = volume;
	}

	public static float getGameplayVolume() {
		return gameplay;
	}

	public static void setGameplayVolume(float volume) {
		GameSettings.gameplay = volume;
	}

	public static float getMenuVolume() {
		return menu;
	}

	public static void setMenuVolume(float volume) {
		GameSettings.menu = volume;
	}

	public static boolean getPostProcessingEnable() {
		return postProcessingEnable;
	}

	public static boolean setPostProcessingEnable(boolean newPostProcessingEnable) {
		return postProcessingEnable = newPostProcessingEnable;
	}

}
