package testing;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.Input;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.assets.Cubemaps;
import kaba4cow.engine.assets.Fonts;
import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.postProcessing.PostProcessingPipeline;
import kaba4cow.engine.renderEngine.textures.GUITexture;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.CameraManager;
import kaba4cow.engine.toolbox.ScreenshotManager;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.ParticleSystemManager;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.files.PlanetFile;
import kaba4cow.intersector.galaxyengine.TerrainGenerator;
import kaba4cow.intersector.galaxyengine.objects.PlanetObject;
import kaba4cow.intersector.gameobjects.Empty;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.fborendering.RingRendering;
import kaba4cow.intersector.renderEngine.fborendering.TerrainRendering;
import kaba4cow.intersector.renderEngine.renderers.HologramRenderer;
import kaba4cow.intersector.renderEngine.renderers.ThrustRenderer;
import kaba4cow.intersector.toolbox.containers.FontContainer;
import kaba4cow.intersector.utils.FileUtils;
import kaba4cow.intersector.utils.GameUtils;

public class TerrainTesting extends MainProgram {

	public static RendererContainer renderers;
	private Cubemap skybox;
	private Camera camera;
	private CameraManager cameraManager;

	private List<GUIText> texts;
	private List<GUITexture> guis;

	private Cubemap cubemap;
	private ModelTexture texture;
	private TerrainGenerator terrainGenerator;

	private float ringRadius;

	private float lodIndex = 1f;

	public TerrainTesting() {
		super("Testing", 30, 800, 800, 0);
	}

	@Override
	public void init() {
		Fonts.load("bank");
		Cubemaps.load("skybox");
		postProcessing = new PostProcessingPipeline();

		PlanetFile.load(FileUtils.loadFiles(new File("resources/files/planets/"), null, new ArrayList<String>()));

		texts = new ArrayList<GUIText>();
		guis = new ArrayList<GUITexture>();

		renderer = GameUtils.getPlayerPov();
		renderers = new RendererContainer(renderer);
		skybox = Cubemaps.get("skybox");
		camera = renderer.getCamera();

		renderer.addLight(new Light(new Vector3f(0f, 0f, -10000f), new Vector3f(1f, 1f, 1f)));

		cameraManager = new CameraManager().setDistParameters(0f, 16f, 0.002f, 1f).setPointParameters(16f)
				.setPitchParameters(-Maths.HALF_PI, Maths.HALF_PI, 0.2f, 8f).setYawParameters(0.2f, 8f);
		cameraManager.setInitParameters(2f, 0.1f * Maths.HALF_PI, 0f).reset().resetParameters();
		cameraManager.setInitKey(Keyboard.KEY_X);

		texts.add(new GUIText("", FontContainer.get("menu"), new Vector2f(0f, 1f), 1f, 1f, false));

		cubemap = null;
		texture = null;

		fileReset();
	}

	public void fileReset() {
		PlanetFile file = PlanetFile.get("GAS_GIANT");
		file.getChildrenMap();
		terrainGenerator = new TerrainGenerator(file, file.getColor(RNG.randomFloat(1f), null), RNG.randomLong());
		cubemap = TerrainRendering.getCubemap(2);
		texture = RingRendering.setTexture(new Empty());
		TerrainRendering.render(cubemap, terrainGenerator);
		RingRendering.render(texture, terrainGenerator);
		ringRadius = RNG.randomFloat(PlanetObject.RING.getMinSize(), PlanetObject.RING.getMaxSize());
	}

	@Override
	public void update(float dt) {
		cameraManager.update(true, dt);

		ThrustRenderer.update(dt);
		HologramRenderer.update(dt);
		ParticleSystemManager.update(dt);

		if (Input.isKeyDown(Keyboard.KEY_1))
			fileReset();

		if (Input.isKey(Keyboard.KEY_W))
			lodIndex += dt;
		if (Input.isKey(Keyboard.KEY_S))
			lodIndex -= dt;
		lodIndex = Maths.limit(lodIndex);

		GameUtils.sleep(1l);
	}

	@Override
	public void render() {
		TerrainRendering.process();
		RingRendering.process();
		camera.orbit(Vectors.INIT3, 0f, 0f, 1f, cameraManager);
		renderer.prepare();

		if (Input.isKey(Keyboard.KEY_C))
			renderers.getRenderer().getLights().get(0).getPos().set(camera.getPos());

		renderers.getCubemapRenderer().render(skybox);
		renderers.getCubemapRenderer().process();

		RawModel model = Planet.getModel(lodIndex);
		Direction direction = new Direction();
		direction.rotate(Vectors.RIGHT, 0.1f);
		Matrix4f matrix = direction.getMatrix(Vectors.INIT3, true, 1f);
		renderers.getTerrainRenderer().render(model, cubemap, terrainGenerator.emission, matrix);
		matrix = direction.getMatrix(Vectors.INIT3, true, ringRadius);
		renderers.getRingRenderer().render(texture, terrainGenerator.ringColor, matrix);

		renderers.processModelRenderers(skybox);

		ParticleSystemManager.render(renderers.getParticleRenderer());
		renderers.getParticleRenderer().process();

		for (int i = 0; i < guis.size(); i++)
			renderers.getGuiRenderer().render(guis.get(i));
		renderers.getGuiRenderer().process();

		stopPostProcessing(null);

		if (Input.isKeyDown(Keyboard.KEY_F12))
			ScreenshotManager.takeScreenshot();

		texts.get(0).setTextString("FPS: " + getCurrentFPS());
		for (int i = 0; i < texts.size(); i++)
			renderers.getTextRenderer().render(texts.get(i));
		renderers.getTextRenderer().process();
	}

	@Override
	public void onClose() {

	}

	public static void main(String[] args) {
		start(new TerrainTesting());
	}

}
