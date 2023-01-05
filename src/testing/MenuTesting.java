package testing;

import org.lwjgl.input.Keyboard;

import kaba4cow.engine.Input;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.assets.Fonts;
import kaba4cow.engine.renderEngine.postProcessing.PostProcessingPipeline;
import kaba4cow.engine.toolbox.MemoryAnalyzer;
import kaba4cow.engine.toolbox.ScreenshotManager;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.Intersector;
import kaba4cow.intersector.Settings;
import kaba4cow.intersector.menu.ButtonElement;
import kaba4cow.intersector.menu.CheckboxElement;
import kaba4cow.intersector.menu.IteratorElement;
import kaba4cow.intersector.menu.MenuPanel;
import kaba4cow.intersector.menu.MenuPanelManager;
import kaba4cow.intersector.menu.PercentSliderElement;
import kaba4cow.intersector.menu.SliderElement;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.toolbox.containers.SoundContainer;
import kaba4cow.intersector.utils.GameUtils;

public class MenuTesting extends MainProgram {

	public static RendererContainer renderers;

	public static final String TAG1 = "TEST1";
	public static final String TAG2 = "TEST2";

	private String tag = TAG1;

	private SliderElement slider1;
	private SliderElement slider2;
	private SliderElement slider3;

	public MenuTesting() {
		super("GUI Testing", 30, 980, 720, 0);
	}

	@Override
	public void init() {
		Settings.loadSettings();
		Fonts.load("bank");
		SoundContainer.loadAll();

		postProcessing = new PostProcessingPipeline();

		renderer = GameUtils.getPlayerPov();
		renderers = new RendererContainer(renderer);

		MenuPanel panel1 = new MenuPanel(TAG1);
		panel1.setHeader("MENU").setSubHeader("SubHeader");
		panel1.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				tag = TAG2;
			}
		}, "New Game");
		panel1.addElement(new ButtonElement(), "Load Game");
		panel1.addElement(new ButtonElement(), "Settings");
		panel1.addElement(new SliderElement().setBounds(30f, 120f).setValue(70f), "Fov");
		panel1.addElement(new PercentSliderElement().setValue(RNG.randomFloat(1f)), "Controls");
		panel1.addElement(new IteratorElement().setList(IteratorElement.DEFAULT5).setIndex(4), "Type");
		panel1.addElement(new CheckboxElement().setSelected(true), "Checkbox");
		panel1.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Intersector.requestClosing();
			}
		}, "Quit");

		// /////////////////////////////////////////////

		MenuPanel panel2 = new MenuPanel(TAG2);
		panel2.setHeader("PAUSE");
		panel2.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				tag = TAG1;
			}
		}, "Resume");
		panel2.addElement(new ButtonElement(), "New Game");
		panel2.addElement(slider1 = new PercentSliderElement().setValue(RNG.randomFloat(1f)), "Slider 1");
		panel2.addElement(slider2 = new PercentSliderElement().setValue(RNG.randomFloat(1f)), "Slider 2");
		panel2.addElement(slider3 = new PercentSliderElement().setValue(RNG.randomFloat(1f)), "Slider 3");
		panel2.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				Intersector.requestClosing();
			}
		}, "Quit");
	}

	@Override
	public void update(float dt) {
		float sum = 0f;
		sum += slider1.getFloatValue();
		sum += slider2.getFloatValue();
		sum += slider3.getFloatValue();

		slider1.setValue(slider1.getFloatValue() / sum);
		slider2.setValue(slider2.getFloatValue() / sum);
		slider3.setValue(slider3.getFloatValue() / sum);
	}

	@Override
	public void render() {
		renderer.prepare();

		renderers.processModelRenderers(null);

		stopPostProcessing(null);

		MenuPanelManager.render(tag, renderers);
		renderers.getGuiRenderer().process();
		renderers.getStaticFrameRenderer().process();
		renderers.getDynamicFrameRenderer().process();
		renderers.getSliderFrameRenderer().process();
		renderers.getTextRenderer().process();

		if (Input.isKeyDown(Keyboard.KEY_F12))
			ScreenshotManager.takeScreenshot();
	}

	@Override
	public void onClose() {
		MemoryAnalyzer.printFinalInfo();
	}

	public static void main(String[] args) {
		start(new MenuTesting());
	}

}
