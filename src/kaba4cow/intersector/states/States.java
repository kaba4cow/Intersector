package kaba4cow.intersector.states;

import kaba4cow.intersector.states.settings.AudioSettingsState;
import kaba4cow.intersector.states.settings.SettingsState;
import kaba4cow.intersector.states.settings.VideoSettingsState;

public final class States {

	public static InitState init;

	public static MenuState menu;
	public static PauseState pause;

	public static SettingsState settings;
	public static AudioSettingsState audioSettings;
	public static VideoSettingsState videoSettings;

	public static SceneState scene;
	public static GameState game;
	public static MapState map;

	private States() {

	}

	public static void init() {
		init = new InitState();

		menu = new MenuState();
		pause = new PauseState();

		settings = new SettingsState();
		audioSettings = new AudioSettingsState();
		videoSettings = new VideoSettingsState();

		scene = new SceneState();
		game = new GameState();
		map = new MapState();
	}

}
