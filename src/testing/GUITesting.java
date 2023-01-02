package testing;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.GameSettings;
import kaba4cow.engine.Input;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.postProcessing.PostProcessingPipeline;
import kaba4cow.engine.toolbox.Fonts;
import kaba4cow.engine.toolbox.MemoryAnalyzer;
import kaba4cow.engine.toolbox.ScreenshotManager;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.files.GameFile;
import kaba4cow.files.ShipFile;
import kaba4cow.gameobjects.Fraction;
import kaba4cow.gameobjects.machinecontrollers.shipcontrollers.ShipStaticController;
import kaba4cow.gameobjects.machines.Ship;
import kaba4cow.gui.ButtonElement;
import kaba4cow.gui.GUIPanel;
import kaba4cow.gui.GUIPanelManager;
import kaba4cow.gui.HologramElement;
import kaba4cow.gui.InfoElement;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.renderEngine.renderers.HologramRenderer;
import kaba4cow.toolbox.SoundContainer;
import kaba4cow.utils.FileUtils;
import kaba4cow.utils.GameUtils;

public class GUITesting extends MainProgram {

	public static RendererContainer renderers;

	public static final String TAG1 = "TEST1";
	public static final String TAG2 = "TEST2";

	private String tag = TAG1;

	private Ship ship;

	public GUITesting() {
		super("GUI Testing", 30, 980, 720, 0);
	}

	@Override
	public void init() {
		GameSettings.loadSettings();
		Fonts.loadAll();
		SoundContainer.loadAll();

		FileUtils.loadGameFiles();
		GameFile.prepareAllInit();
		GameFile.prepareAllPostInit();
		Fraction.init();

		postProcessing = new PostProcessingPipeline();
		// postProcessing = GameSettings.getPostProcessing();
		// postProcessing.switchLastEffects(
		// GameSettings.getPostProcessingBorder(), false);

		renderer = GameUtils.getPlayerPov();
		renderers = new RendererContainer(renderer);

		create();
	}

	private void createShip() {
		ShipFile file = ShipFile.getList().get(RNG.randomInt(ShipFile.getList().size()));
		ship = new Ship(null, Fraction.getRandom(), file, new Vector3f(), new ShipStaticController());
	}

	private void create() {
		createShip();
		GUIPanel frame = new GUIPanel(tag, 3, 2);

		frame.addElement(new HologramElement().setObject(ship).setTextString(ship.getFullName()), 1, 0, 2, 1);
		frame.addElement(new InfoElement().setTextString(ship.getFullName()), 2, 1);
		frame.addElement(new ButtonElement() {
			@Override
			public void onSelect() {
				create();
			}
		}.setTextString("Button"), 0, 1, 2, 1);
	}

	@Override
	public void update(float dt) {
		HologramRenderer.update(dt);
		if (Input.isKeyDown(Keyboard.KEY_RETURN))
			create();
	}

	@Override
	public void render() {
		renderer.prepare();

		renderers.processModelRenderers(null);

		stopPostProcessing(null);

		GUIPanelManager.render(tag, "FLOCK", renderers);
		renderers.processModelRenderers(null);

		if (Input.isKeyDown(Keyboard.KEY_F12))
			ScreenshotManager.takeScreenshot();
	}

	@Override
	public void onClose() {
		MemoryAnalyzer.printFinalInfo();
	}

	public static void main(String[] args) {
		start(new GUITesting());
	}

}
