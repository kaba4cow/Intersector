package kaba4cow.intersector.states;

import org.lwjgl.util.vector.Vector2f;

import kaba4cow.engine.assets.Cubemaps;
import kaba4cow.engine.assets.Fonts;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.renderEngine.renderers.TextRenderer;
import kaba4cow.intersector.Intersector;
import kaba4cow.intersector.files.GameFile;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.toolbox.containers.FontContainer;
import kaba4cow.intersector.toolbox.containers.SoundContainer;

public class InitState extends State {

	private TextRenderer textRenderer;
	private GUIText text;

	private boolean finished;

	private Thread thread;

	public InitState() {
		super("INIT");
	}

	@Override
	public boolean isInitializable() {
		return true;
	}

	@Override
	public void create() {

	}

	@Override
	public void init() {
		Cubemaps.load("skybox");
		Fonts.loadAll();

		textRenderer = new TextRenderer(new Renderer(Projection.DEFAULT, 90f,
				0.1f, 100f, 0f));
		text = new GUIText("loading...", FontContainer.get("menu"),
				new Vector2f(-1f, -0.9f), 2f, 1f, false);
		finished = false;

		thread = new Thread("init") {
			@Override
			public void run() {
				GameFile.prepareAllInit();
				SoundContainer.loadAll();
				finished = true;
			}
		};
	}

	@Override
	public void onStateSwitch() {
		thread.start();
	}

	@Override
	public void update(float dt) {

	}

	@Override
	public void render(RendererContainer renderers) {
		game.doPostProcessing();

		textRenderer.render(text);
		textRenderer.process();

		if (finished) {
			Intersector.getInstance().postInit();
			return;
		}

	}
}
