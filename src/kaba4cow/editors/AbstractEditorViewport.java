package kaba4cow.editors;

import java.io.File;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.GameSettings;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.postProcessing.PostProcessingPipeline;
import kaba4cow.engine.toolbox.CameraManager;
import kaba4cow.engine.toolbox.Cubemaps;
import kaba4cow.engine.toolbox.Fonts;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.files.GameFile;
import kaba4cow.gameobjects.Fraction;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.toolbox.RawModelContainer;
import kaba4cow.toolbox.SoundContainer;
import kaba4cow.utils.FileUtils;

public abstract class AbstractEditorViewport extends MainProgram {

	protected RendererContainer renderers;

	protected Camera camera;
	protected CameraManager cameraManager;

	protected Light light;

	protected final AbstractEditor editor;
	protected final AbstractEditorSettings settings;

	protected Cubemap cells;
	protected Cubemap skybox;

	private boolean loadingRequested;
	private String newFile;

	protected int fileCooldown;

	public AbstractEditorViewport(AbstractEditor editor) {
		super(60, editor.getCanvas());
		this.editor = editor;
		this.settings = editor.getSettings();
		EXIT_ON_KEY = false;
	}

	@Override
	public void init() {
		GameSettings.loadSettings();
		Fonts.loadAll();
		Cubemaps.load("cells", 1);
		Cubemaps.load("skybox");
		RawModelContainer.loadAll();
		SoundContainer.loadAll();
		FileUtils.loadGameFiles();
		GameFile.prepareAllInit();
		GameFile.prepareAllPostInit();
		Fraction.init();

		cells = Cubemaps.get("cells");
		skybox = Cubemaps.get("skybox");

		renderer = new Renderer(Projection.DEFAULT, 70f, 0.1f, 10000000f, 0.1f);
		renderer.setAmbientLighting(0.1f);
		camera = renderer.getCamera();
		renderers = new RendererContainer(renderer);

		light = new Light(new Vector3f(-10000f, 0f, 0f), new Vector3f(1f, 1f, 1f));

		cameraManager = new CameraManager().setDistParameters(0f, 32f, 0.001f, 2f).setPointParameters(4f)
				.setPitchParameters(-Maths.HALF_PI, Maths.HALF_PI, 0.2f, 8f).setYawParameters(0.2f, 8f);
		cameraManager.setInitParameters(2f, 0.1f * Maths.HALF_PI, Maths.PI).reset().resetParameters();

		postProcessing = new PostProcessingPipeline();

		loadingRequested = false;
		newFile = null;
	}

	@Override
	public void update(float dt) {
		settings.update(dt);
		cameraManager.update(true, dt);
	}

	@Override
	public void render() {

	}

	@Override
	public void onClose() {

	}

	public abstract void save();

	public abstract AbstractEditor getEditor();

	public abstract AbstractEditorSettings getSettings();

	protected abstract void loadRequestedFile();

	public void loadNewFile(File file) {
		loadingRequested = true;
		if (file == null)
			newFile = null;
		else {
			String rootDirectory = editor.getRootDirectory();
			newFile = file.getName();
			File current = file;
			while (true) {
				if (!current.getParentFile().getName().equals(rootDirectory)) {
					newFile = current.getParentFile().getName() + "/" + newFile;
					current = current.getParentFile();
				} else
					break;
			}
		}
	}

	protected void finishLoadingFile(GameFile file) {
		loadingRequested = false;
		newFile = null;
		if (file != null) {
			file.prepareInit();
			file.preparePostInit();
		}
		editor.onNewFileLoaded();
		editor.updateTitle(file);
	}

	public String getNewFile() {
		return newFile;
	}

	public boolean isLoadingRequested() {
		return loadingRequested;
	}

}
