package kaba4cow.intersector;

import java.io.File;
import java.util.List;

import org.lwjgl.input.Keyboard;

import kaba4cow.engine.DisplayManager;
import kaba4cow.engine.Input;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.audio.AudioManager;
import kaba4cow.engine.audio.Source;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.postProcessing.PostProcessingPipeline;
import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.toolbox.MemoryAnalyzer;
import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.toolbox.ScreenshotManager;
import kaba4cow.engine.toolbox.files.ConfigFile;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.ParticleSystemManager;
import kaba4cow.intersector.files.GameFile;
import kaba4cow.intersector.files.InfosFile;
import kaba4cow.intersector.gameobjects.Fraction;
import kaba4cow.intersector.menu.MenuPanelManager;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.fborendering.RingRendering;
import kaba4cow.intersector.renderEngine.fborendering.SkyRendering;
import kaba4cow.intersector.renderEngine.fborendering.TerrainRendering;
import kaba4cow.intersector.states.State;
import kaba4cow.intersector.states.States;
import kaba4cow.intersector.toolbox.Constants;
import kaba4cow.intersector.toolbox.InfoPanel;
import kaba4cow.intersector.utils.FileUtils;
import kaba4cow.intersector.utils.GameUtils;

public class Intersector extends MainProgram {

	public static String GAME_TITLE;
	public static String GAME_VERSION;

	private RendererContainer renderers;
	private PostProcessingPipeline postProcessingDisabled;

	private InfoPanel infoPanel;

	private static Intersector instance;

	private static State currentState;
	private static State nextState;
	private static boolean renderScene;

	public Intersector(int... dimensions) {
		super(GAME_TITLE + " [" + GAME_VERSION + "]", 30, dimensions);
		instance = this;
		EXIT_ON_KEY = false;
	}

	@Override
	public void init() {
		States.init();
		switchState(States.init);
	}

	public void postInit() {
		AudioManager.setDistanceModel(AudioManager.LINEAR_DISTANCE_CLAMPED);

		Source.setSourcesRolloffFactor(Constants.GAMEPLAY, 0.5f);

		Source.setSourcesRolloffFactor(Constants.MUSIC, 0f);
		Source.setSourcesRolloffFactor(Constants.MENU, 0f);

		GameFile.prepareAllPostInit();
		Fraction.init();

		List<PostProcessingEffect> list = FileUtils.readPostProcessingPipeline();
		postProcessing = new PostProcessingPipeline().addAll(list);
		postProcessingDisabled = new PostProcessingPipeline();

		renderer = GameUtils.getPlayerPov();
		renderers = new RendererContainer(renderer);

		infoPanel = new InfoPanel();

		renderScene = true;
		State.createAll();
		switchState(States.menu);
	}

	@Override
	public void update(float dt) {
		if (renderer != null)
			AudioManager.setListenerData(renderer.getCamera().getPos(), Vectors.INIT3);

		processNextState();
		currentState.update(dt);

		float audioVolume = Settings.getAudioVolume();
		Source.setSourcesVolume(Constants.GAMEPLAY, audioVolume * Settings.getGameplayVolume());
		Source.setSourcesVolume(Constants.MUSIC, audioVolume * Settings.getMusicVolume());
		Source.setSourcesVolume(Constants.MENU, audioVolume * Settings.getMenuVolume());

		if (Input.isKey(Keyboard.KEY_LSHIFT) && Input.isKey(Keyboard.KEY_ESCAPE))
			System.exit(0);
	}

	@Override
	public void render() {
		SkyRendering.process();
		TerrainRendering.process();
		RingRendering.process();

		GameUtils.getPlayerPov().setFov(Settings.getFov());

		currentState.render(renderers);
		if (currentState == States.init)
			return;

		MenuPanelManager.render(currentState.getName(), renderers);
		renderers.processGUIRenderers();

		infoPanel.render(renderers.getTextRenderer());

		if (Input.isKeyDown(Keyboard.KEY_F12))
			ScreenshotManager.takeScreenshot();

	}

	public void doPostProcessing() {
		stopPostProcessing(Settings.getPostProcessingEnable() ? postProcessing : postProcessingDisabled);
	}

	public void switchPostProcessing(boolean gameplay) {
		postProcessing.switchLastEffects(InfosFile.postprocessing.data().node("border").getInt(), !gameplay);
	}

	public void clearParticles(State state) {
		ParticleSystemManager.clear(state.getName());
	}

	public void updateParticles(State state, float dt) {
		ParticleSystemManager.update(state.getName(), dt);
	}

	public void renderParticles(State state) {
		ParticleSystemManager.render(state.getName(), renderers.getParticleRenderer());
		renderers.getParticleRenderer().process();
	}

	@Override
	public void onClose() {
		Settings.saveSettings();
		MemoryAnalyzer.printFinalInfo();
		if (Settings.getLogs())
			FileUtils.saveLog();
	}

	@Override
	public void setRenderer(Renderer renderer) {
		super.setRenderer(renderer);
		renderers.setRenderer(renderer);
	}

	public static Intersector getInstance() {
		return instance;
	}

	public static State getState() {
		return currentState;
	}

	public static State getScene() {
		return renderScene ? States.scene : States.game;
	}

	public static void updateScene(float dt) {
		if (renderScene)
			States.scene.update(dt);
	}

	public static void switchScene(boolean scene) {
		renderScene = scene;
	}

	public static void switchState(State state) {
		nextState = state;
	}

	public static void processNextState() {
		if (nextState == null)
			return;
		nextState.setPrevState(currentState);
		if (currentState == null || nextState.isInitializable())
			nextState.init();
		nextState.onStateSwitch();
		currentState = nextState;
		nextState = null;
	}

	public static void main(String[] args) {
		ConfigFile info = ConfigFile.read(new File("version"));

		GAME_TITLE = "Intersector";
		GAME_VERSION = info.getString("id");

		Printer.println("LAUNCHING " + GAME_TITLE + " " + GAME_VERSION + " [" + info.getString("date") + "]");
		Settings.loadSettings();
		FileUtils.loadGameFiles();
		DisplayManager.setIcons("icons/icon");
		if (Settings.getFullscreen())
			new Intersector();
		else
			new Intersector(920, 680);
		start(instance);
	}

}
