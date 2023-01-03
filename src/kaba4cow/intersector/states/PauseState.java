package kaba4cow.intersector.states;

import org.lwjgl.input.Keyboard;

import kaba4cow.engine.Input;
import kaba4cow.intersector.Intersector;
import kaba4cow.intersector.menu.ButtonElement;
import kaba4cow.intersector.menu.MenuElement;
import kaba4cow.intersector.menu.MenuPanel;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class PauseState extends State {

	public PauseState() {
		super("PAUSE");
	}

	@Override
	public boolean isInitializable() {
		return false;
	}

	@Override
	public void create() {
		MenuPanel panel = new MenuPanel(name);

		panel.setHeader("PAUSE");

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Intersector.switchState(States.game);
			}
		}, "Resume");

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Intersector.switchState(States.settings);
			}
		}, "Settings");

		panel.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Intersector.switchState(States.menu);
			}
		}, "Quit");
	}

	@Override
	public void init() {

	}

	@Override
	public void onStateSwitch() {
		game.switchPostProcessing(false);
	}

	@Override
	public void update(float dt) {
		if (Input.isKeyDown(Keyboard.KEY_ESCAPE)) {
			MenuElement.playSound();
			Intersector.switchState(States.game);
		}
	}

	@Override
	public void render(RendererContainer renderers) {
		Intersector.getScene().render(renderers);
	}

}
