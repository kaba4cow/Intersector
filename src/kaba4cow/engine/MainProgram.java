package kaba4cow.engine;

import java.awt.Canvas;

import kaba4cow.engine.audio.AudioManager;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.renderEngine.postProcessing.PostProcessingPipeline;
import kaba4cow.engine.toolbox.Loaders;
import kaba4cow.engine.toolbox.MemoryAnalyzer;
import kaba4cow.engine.toolbox.Printer;

import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

public abstract class MainProgram implements Runnable {

	private Canvas CANVAS;
	private String TITLE;
	public int WIDTH;
	public int HEIGHT;
	public int DOWNSCALE;
	public final boolean FULLSCREEN;
	public final boolean RESIZABLE;
	public float ASPECT_RATIO;
	public float INV_ASPECT_RATIO;

	private int FPS;

	public static boolean EXIT_ON_KEY = true;
	public static int EXIT_KEY = Keyboard.KEY_ESCAPE;

	private static long lastFrameTime;
	private static float deltaTime;

	private static float elapsedTime;

	private static MainProgram mainProgram;

	protected Renderer renderer;

	protected FrameBufferObject fbo;
	protected PostProcessingPipeline postProcessing;

	private boolean closeRequested;

	public MainProgram(String title, int fps, int... dimensions) {
		CANVAS = null;
		TITLE = title;
		FPS = fps;
		if (dimensions != null && dimensions.length >= 2) {
			WIDTH = dimensions[0];
			HEIGHT = dimensions[1];
			if (dimensions.length >= 3)
				RESIZABLE = dimensions[2] == 0 ? false : true;
			else
				RESIZABLE = false;
			FULLSCREEN = false;
		} else {
			WIDTH = Display.getDesktopDisplayMode().getWidth();
			HEIGHT = Display.getDesktopDisplayMode().getHeight();
			RESIZABLE = false;
			FULLSCREEN = true;
		}
		updateAspectRatio();
	}

	public MainProgram(int fps, Canvas canvas) {
		CANVAS = canvas;
		TITLE = "";
		FPS = fps;
		WIDTH = canvas.getWidth();
		HEIGHT = canvas.getHeight();
		FULLSCREEN = false;
		RESIZABLE = false;
		updateAspectRatio();
	}

	public void updateAspectRatio() {
		ASPECT_RATIO = (float) WIDTH / (float) HEIGHT;
		INV_ASPECT_RATIO = 1f / ASPECT_RATIO;
		Renderer.updateAspectRatio();
	}

	private void create() {
		fbo = new FrameBufferObject(WIDTH, HEIGHT,
				FrameBufferObject.DEPTH_RENDER_BUFFER,
				FrameBufferObject.NEAREST_SAMPLING);
		postProcessing = new PostProcessingPipeline();
	}

	public abstract void init();

	public abstract void update(float dt);

	public abstract void render();

	public abstract void onClose();

	public void startPostProcessing() {
		fbo.bindFrameBuffer();
	}

	public void stopPostProcessing(PostProcessingPipeline postProcessing) {
		fbo.unbindFrameBuffer();
		if (postProcessing == null)
			postProcessing = this.postProcessing;
		postProcessing.render(fbo.getTexture());
	}

	public static void start(MainProgram program) {
		Printer.println("STARTING PROGRAM...");
		mainProgram = program;
		new Thread(program, "Main Thread").start();
	}

	@Override
	public void run() {
		DisplayManager.create(mainProgram.FULLSCREEN,
				mainProgram.RESIZABLE, mainProgram.CANVAS);
		AudioManager.init();
		mainProgram.create();
		mainProgram.init();
		mainProgram.closeRequested = false;
		lastFrameTime = getCurrentTime();
		elapsedTime = 0f;

		Printer.println("PROGRAM STARTED");
		Printer.println("FULLSCREEN: " + mainProgram.FULLSCREEN
				+ ", WIDTH: " + mainProgram.WIDTH + ", HEIGHT: "
				+ mainProgram.HEIGHT);

		while (isRunning()) {
			Loaders.update();
			MemoryAnalyzer.update();
			AudioManager.update();
			Input.update();
			mainProgram.update(deltaTime);

			mainProgram.startPostProcessing();
			mainProgram.render();
			DisplayManager.update();

			long currentFrameTime = getCurrentTime();
			deltaTime = 0.001f * (currentFrameTime - lastFrameTime);
			lastFrameTime = currentFrameTime;
			elapsedTime += deltaTime;
		}

		Printer.println("FINISHING PROGRAM...");
		mainProgram.closeRequested = true;
		mainProgram.onClose();
		FrameBufferObject.cleanUp();
		Loaders.cleanUp();
		AudioManager.cleanUp();
		DisplayManager.close();
		Printer.println("PROGRAM FINISHED");
		System.exit(0);
	}

	public static boolean isRunning() {
		return !Display.isCloseRequested()
				&& !(EXIT_ON_KEY && Keyboard.isKeyDown(EXIT_KEY))
				&& !mainProgram.closeRequested;
	}

	public static void requestClosing() {
		mainProgram.closeRequested = true;
	}

	public static boolean isCloseRequested() {
		return mainProgram.closeRequested;
	}

	public static float getElapsedTime() {
		return elapsedTime;
	}

	public static float getDeltaTime() {
		return deltaTime;
	}

	private static long getCurrentTime() {
		return 1000l * Sys.getTime() / Sys.getTimerResolution();
	}

	public static int getCurrentFPS() {
		return (int) (1f / deltaTime);
	}

	public static String getTitle() {
		return mainProgram.TITLE;
	}

	public static void setTitle(String title) {
		mainProgram.TITLE = title;
		Display.setTitle(mainProgram.TITLE);
	}

	public static int getWidth() {
		return mainProgram.WIDTH;
	}

	public static int getHeight() {
		return mainProgram.HEIGHT;
	}

	public static float getAspectRatio() {
		return mainProgram.ASPECT_RATIO;
	}

	public static float getInvAspectRatio() {
		return mainProgram.INV_ASPECT_RATIO;
	}

	public static boolean isFullscreen() {
		return mainProgram.FULLSCREEN;
	}

	public static boolean isResizable() {
		return mainProgram.RESIZABLE;
	}

	public static int getFps() {
		return mainProgram.FPS;
	}

	public static void setFps(int fps) {
		mainProgram.FPS = fps;
	}

	public static MainProgram getMainProgram() {
		return mainProgram;
	}

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

}
