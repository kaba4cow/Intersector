package testing;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.GameSettings;
import kaba4cow.engine.Input;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.renderEngine.postProcessing.PostProcessingPipeline;
import kaba4cow.engine.toolbox.CameraManager;
import kaba4cow.engine.toolbox.Cubemaps;
import kaba4cow.engine.toolbox.Fonts;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.ParticleSystemManager;
import kaba4cow.files.GameFile;
import kaba4cow.galaxyengine.objects.SystemObject;
import kaba4cow.gameobjects.Fraction;
import kaba4cow.gameobjects.Planet;
import kaba4cow.gameobjects.World;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.renderEngine.fborendering.RingRendering;
import kaba4cow.renderEngine.fborendering.SkyRendering;
import kaba4cow.renderEngine.fborendering.TerrainRendering;
import kaba4cow.renderEngine.renderers.HologramRenderer;
import kaba4cow.renderEngine.renderers.ThrustRenderer;
import kaba4cow.toolbox.FontContainer;
import kaba4cow.toolbox.Measures;
import kaba4cow.utils.FileUtils;
import kaba4cow.utils.GalaxyUtils;
import kaba4cow.utils.GameUtils;
import kaba4cow.utils.InfoUtils;

public class SystemTesting extends MainProgram {

	public static float SCALE = 1f;

	public static RendererContainer renderers;
	private Camera camera;
	private CameraManager cameraManager;

	private Cubemap skybox;

	private List<GUIText> texts;

	private SystemObject system;
	private List<Planet> planets;
	private int index = 0;

	private float timeIndex = 1f;
	private float timeStep;

	public SystemTesting() {
		super("Testing", 30, 1000, 800, 0);
		SCALE = 1f / 1000000000f;
	}

	@Override
	public void init() {
		GameSettings.loadSettings();
		Fonts.loadAll();
		Cubemaps.load("skybox");
		postProcessing = new PostProcessingPipeline();

		skybox = new Cubemap(
				GameSettings.CUBEMAP_SIZES[GameSettings.getCubemaps()]);

		FileUtils.loadGameFiles();
		GameFile.prepareAllInit();
		GameFile.prepareAllPostInit();
		Fraction.init();

		texts = new ArrayList<GUIText>();

		renderer = new Renderer(Projection.DEFAULT, GameSettings.getFov(),
				0.001f, 100000f, 0f).setAmbientLighting(0.05f);
		renderers = new RendererContainer(renderer);
		camera = renderer.getCamera();

		cameraManager = new CameraManager()
				.setDistParameters(1f, 16f, 0.001f, 4f).setPointParameters(16f)
				.setPitchParameters(-Maths.HALF_PI, Maths.HALF_PI, 0.2f, 8f)
				.setYawParameters(0.2f, 8f);
		cameraManager.setInitParameters(2f, 0.1f * Maths.HALF_PI, 0f).reset()
				.resetParameters();
		cameraManager.setInitKey(Keyboard.KEY_X);

		String font = FontContainer.get("menu");
		texts.add(new GUIText("", font, new Vector2f(0f, 1f), 1f, 1f, false));
		texts.add(new GUIText("", font, new Vector2f(0f, 0.95f), 1f, 1f, false));

		randomReset();
	}

	public void randomReset() {
		system = GalaxyUtils.getRandomSystem();
		planets = GalaxyUtils.createPlanets(new World(), system);
		index = 0;
		system.print();
		SkyRendering.render(system, skybox);
	}

	public void nebulaReset() {
		system = GalaxyUtils.getRandomNebula();
		planets = GalaxyUtils.createPlanets(new World(), system);
		index = 0;
		system.print();
		SkyRendering.render(system, skybox);
	}

	public void systemReset() {
		system = planets.get(0).getPlanetObject().system;
		system = GalaxyUtils.generateSystem(system.posX, system.posY,
				system.posZ);
		planets = GalaxyUtils.createPlanets(new World(), system);
		system.print();
		SkyRendering.render(system, skybox);
	}

	public void positionReset() {
		system = GalaxyUtils.generateSystem(-7, 0, -277);
		system = GalaxyUtils.generateSystem(2563, -3, 1706);

		planets = GalaxyUtils.createPlanets(new World(), system);
		system.print();
		SkyRendering.render(system, skybox);
	}

	@Override
	public void update(float dt) {
		cameraManager.update(true, dt);

		if (Input.isKeyDown(Keyboard.KEY_Q))
			index--;
		if (Input.isKeyDown(Keyboard.KEY_E))
			index++;
		if (index < 0)
			index = planets.size() - 1;
		if (index >= planets.size())
			index = 0;

		if (Input.isKey(Keyboard.KEY_W))
			timeIndex += 0.1f * dt;
		if (Input.isKey(Keyboard.KEY_S))
			timeIndex -= 0.1f * dt;
		if (Input.isKeyDown(Keyboard.KEY_R))
			timeIndex = (int) (timeIndex + 1);
		if (Input.isKeyDown(Keyboard.KEY_F))
			timeIndex = (int) (timeIndex - 1);
		timeIndex = Maths.limit(timeIndex, 1f, Measures.TIME.length);
		if (timeIndex >= Measures.TIME.length)
			timeIndex = Measures.TIME.length - 1f / Measures.MONTH;

		if (Input.isKeyDown(Keyboard.KEY_1))
			randomReset();
		if (Input.isKeyDown(Keyboard.KEY_2))
			nebulaReset();
		if (Input.isKeyDown(Keyboard.KEY_3))
			systemReset();
		if (Input.isKeyDown(Keyboard.KEY_4))
			positionReset();

		ThrustRenderer.update(dt);
		HologramRenderer.update(dt);
		ParticleSystemManager.update(dt);

		timeStep = (timeIndex % 1f) * Measures.TIME[(int) timeIndex];
		if ((int) timeIndex > 0)
			timeStep += Measures.TIME[(int) timeIndex - 1];
		for (Planet planet : planets)
			planet.update(timeStep * dt);

		GameUtils.sleep(1l);
	}

	@Override
	public void render() {
		SkyRendering.process();
		TerrainRendering.process();
		RingRendering.process();
		Vector3f orbitPoint = Input.isKey(Keyboard.KEY_B) ? Vectors.INIT3
				: planets.get(index).getPos();
		camera.orbit(orbitPoint, 0f, 0f, 0.99f * planets.get(index).getSize(),
				cameraManager);
		renderer.prepare();

		if (Input.isKey(Keyboard.KEY_H))
			renderers.getCubemapRenderer().render(Cubemaps.get("skybox"));
		else
			renderers.getCubemapRenderer().render(skybox);
		renderers.getCubemapRenderer().process();

		renderers.getRenderer().clearLights();
		for (Planet planet : planets)
			planet.renderModel(renderers);

		renderers.processModelRenderers(skybox);

		ParticleSystemManager.render(renderers.getParticleRenderer());
		renderers.getParticleRenderer().process();

		stopPostProcessing(null);

		texts.get(0).setTextString("FPS: " + getCurrentFPS());
		texts.get(1).setTextString("timestep: " + InfoUtils.time(timeStep));
		for (int i = 0; i < texts.size(); i++)
			renderers.getTextRenderer().render(texts.get(i));
		renderers.getTextRenderer().process();
	}

	@Override
	public void onClose() {

	}

	public static void main(String[] args) {
		start(new SystemTesting());
	}

}
