package testing;

import java.io.File;
import java.util.ArrayList;

import org.lwjgl.input.Keyboard;

import kaba4cow.engine.Input;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.assets.Fonts;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.postProcessing.PostProcessingPipeline;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.Settings;
import kaba4cow.intersector.files.FractionFile;
import kaba4cow.intersector.files.PlanetFile;
import kaba4cow.intersector.files.SystemFile;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.gameobjects.Fraction;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.utils.FileUtils;
import kaba4cow.intersector.utils.GalaxyUtils;

public class SystemFileTesting extends MainProgram {

	public static RendererContainer renderers;

	private SystemObject system;
	public static long seed;

	public SystemFileTesting() {
		super("Testing", 30, 240, 240, 0);
	}

	@Override
	public void init() {
		Fonts.load("bank");
		postProcessing = new PostProcessingPipeline();

		PlanetFile.load(FileUtils.loadFiles(
				new File("resources/files/planets/"), null,
				new ArrayList<String>()));
		SystemFile.load(FileUtils.loadFiles(
				new File("resources/files/systems/"), null,
				new ArrayList<String>()));
		FractionFile.load(FileUtils.loadFiles(new File(
				"resources/files/fractions/"), null, new ArrayList<String>()));
		Fraction.init();

		renderer = new Renderer(Projection.DEFAULT, Settings.getFov(),
				0.001f, 100000f, 0f).setAmbientLighting(0.05f);
		renderers = new RendererContainer(renderer);

		// seed = 50285501;
		reset();
	}

	public void reset() {
		system = null;

		// while (system == null
		// || !(system.systemSize() <= 25 && system.stationsSize() >= 4 &&
		// system
		// .stationsSize() <= 10)) {
		// seed = RNG.randomLong();
		system = GalaxyUtils.generateSystem(3000, -8, 2184);
		// }
		system.print();
	}

	@Override
	public void update(float dt) {
		long prev = seed;
		if (Input.isKeyDown(Keyboard.KEY_R))
			seed = RNG.randomLong();
		if (Input.isKeyDown(Keyboard.KEY_Q))
			seed--;
		if (Input.isKeyDown(Keyboard.KEY_E))
			seed++;
		if (Input.isKeyDown(Keyboard.KEY_SPACE) || prev != seed)
			reset();
	}

	@Override
	public void render() {
		renderer.prepare();
		stopPostProcessing(null);
	}

	@Override
	public void onClose() {

	}

	public static void main(String[] args) {
		start(new SystemFileTesting());
	}

}
