package kaba4cow.intersector.states;

import kaba4cow.intersector.Intersector;
import kaba4cow.intersector.menu.ButtonElement;
import kaba4cow.intersector.menu.MenuPanel;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.utils.GameUtils;

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

		panel.setHeader(Intersector.GAME_TITLE).setSubHeader(Intersector.GAME_VERSION);

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Intersector.switchState(States.game);
			}
		}, "New game");

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Intersector.switchState(States.settings);
			}
		}, "Settings");

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Intersector.requestClosing();
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
		Intersector.switchScene(true);
		game.setRenderer(GameUtils.getPlayerPov());
		game.switchPostProcessing(false);
	}

	@Override
	public void update(float dt) {
		Intersector.updateScene(dt);
	}

	@Override
	public void render(RendererContainer renderers) {
		Intersector.getScene().render(renderers);
	}
}
