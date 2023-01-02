package kaba4cow.states;

import org.lwjgl.util.vector.Vector2f;

import kaba4cow.Game;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.renderEngine.renderers.TextRenderer;
import kaba4cow.engine.toolbox.Cubemaps;
import kaba4cow.engine.toolbox.Fonts;
import kaba4cow.files.GameFile;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.toolbox.FontContainer;
import kaba4cow.toolbox.SoundContainer;

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

		thread = new Thread("Init") {
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
			Game.getInstance().postInit();
			return;
		}

	}
}
