package kaba4cow.editors.thrusttextureeditor;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.ThrustTextureFile;
import kaba4cow.renderEngine.models.ThrustModel;
import kaba4cow.renderEngine.renderers.ThrustRenderer;
import kaba4cow.toolbox.RawModelContainer;

public class ThrustTextureEditorViewport extends AbstractEditorViewport {

	private ThrustModel model;

	private ThrustTextureFile file;

	private Direction direction;

	public ThrustTextureEditorViewport(ThrustTextureEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();

		model = new ThrustModel(null, null);
		direction = new Direction();
		loadRequestedFile();
	}

	@Override
	protected void loadRequestedFile() {
		renderer.clearLights();
		renderer.addLight(light);
		file = ThrustTextureFile.load(getNewFile());
		if (file != null)
			model.setTexture(file.get());
		else
			model.setTexture(null);
		finishLoadingFile(file);
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		ThrustRenderer.update(dt);

		if (isLoadingRequested())
			loadRequestedFile();
	}

	@Override
	public void render() {
		if (file != null) {
			ThrustTextureEditor editor = getEditor();
			file.setSpeed(EditorUtils.getFloatValue(editor.getSpeedSpinner()));
			file.setSound(editor.getSoundButton().getText());
		}

		camera.orbit(Vectors.INIT3, 0f, 0f, 1f, cameraManager);
		if (settings.isStaticLight())
			light.setPos(new Vector3f(-5000f, 2500f, 5000f));
		else
			light.setPos(camera.getPos());
		renderer.setWireframe(settings.isRenderWireframe());

		renderer.prepare();

		Cubemap cubemap = settings.isRenderSkybox() ? skybox : cells;
		renderers.getCubemapRenderer().render(cubemap);
		renderers.getCubemapRenderer().process();

		RawModel modelFile = RawModelContainer.get(getEditor().getModelButton()
				.getText());
		if (modelFile != null)
			model.setRawModel(modelFile);

		float brightness = getEditor().getSettings().getThrustBrightness();
		if (model.getTexture() != null)
			renderers.getThrustRenderer().render(model,
					direction.getMatrix(Vectors.INIT3, false), brightness,
					model.getTexture().getSpeed());
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public ThrustTextureEditor getEditor() {
		return (ThrustTextureEditor) editor;
	}

	@Override
	public ThrustTextureEditorSettings getSettings() {
		return (ThrustTextureEditorSettings) settings;
	}

	public ThrustTextureFile getTextureFile() {
		return file;
	}

}
