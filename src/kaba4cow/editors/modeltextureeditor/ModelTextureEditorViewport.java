package kaba4cow.editors.modeltextureeditor;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.intersector.files.ModelTextureFile;
import kaba4cow.intersector.toolbox.containers.RawModelContainer;

public class ModelTextureEditorViewport extends AbstractEditorViewport {

	private TexturedModel model;

	private ModelTextureFile file;

	private Direction direction;

	public ModelTextureEditorViewport(ModelTextureEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();

		model = new TexturedModel(null, null);
		direction = new Direction();
		loadRequestedFile();
	}

	@Override
	protected void loadRequestedFile() {
		renderer.clearLights();
		renderer.addLight(light);
		file = ModelTextureFile.load(getNewFile());
		if (file != null)
			model.setTexture(file.get());
		else
			model.setTexture(null);
		finishLoadingFile(file);
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		if (isLoadingRequested())
			loadRequestedFile();
	}

	@Override
	public void render() {
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

		if (file != null) {
			ModelTextureEditor editor = getEditor();
			file.setShininess(EditorUtils.getFloatValue(editor
					.getShininessSpinner()));
			file.setShineDamper(EditorUtils.getFloatValue(editor
					.getShinedamperSpinner()));
			file.setReflectivity(EditorUtils.getFloatValue(editor
					.getReflectivitySpinner()));
			file.setEmission(EditorUtils.getFloatValue(editor
					.getEmissionSpinner()));
			file.setTransparent(editor.getTransparentCheckbox().isSelected());
			file.setAdditive(editor.getAdditiveCheckbox().isSelected());
		}

		RawModel modelFile = RawModelContainer.get(getEditor().getModelButton()
				.getText());
		if (modelFile != null)
			model.setRawModel(modelFile);
		else
			model.setRawModel(null);

		renderers.getModelRenderer().render(model, null,
				direction.getMatrix(Vectors.INIT3, false));
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public ModelTextureEditor getEditor() {
		return (ModelTextureEditor) editor;
	}

	@Override
	public ModelTextureEditorSettings getSettings() {
		return (ModelTextureEditorSettings) settings;
	}

	public ModelTextureFile getTextureFile() {
		return file;
	}

}
