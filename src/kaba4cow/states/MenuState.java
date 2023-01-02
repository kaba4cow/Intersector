package kaba4cow.states;

import kaba4cow.Game;
import kaba4cow.menu.ButtonElement;
import kaba4cow.menu.MenuPanel;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.utils.GameUtils;

public class MenuState extends State {

	public MenuState() {
		super("MENU");
	}

	@Override
	public boolean isInitializable() {
		return false;
	}

	@Override
	public void create() {
		MenuPanel panel = new MenuPanel(name);

		panel.setHeader(Game.GAME_TITLE).setSubHeader(Game.GAME_VERSION);

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Game.switchState(States.game);
			}
		}, "New game");

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Game.switchState(States.settings);
			}
		}, "Settings");

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Game.requestClosing();
			}
		}, "Quit");
	}

	@Override
	public void init() {

	}

	@Override
	public void onStateSwitch() {
		if (States.scene.isInitializable())
			States.scene.init();
		Game.switchScene(true);
		game.setRenderer(GameUtils.getPlayerPov());
		game.switchPostProcessing(false);
	}

	@Override
	public void update(float dt) {
		Game.updateScene(dt);
	}

	@Override
	public void render(RendererContainer renderers) {
		Game.getScene().render(renderers);
	}
}
