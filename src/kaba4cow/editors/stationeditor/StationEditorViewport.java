package kaba4cow.editors.stationeditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.StationFile;
import kaba4cow.gameobjects.machines.classes.StationClass;
import kaba4cow.gameobjects.objectcomponents.WeaponComponent;
import kaba4cow.renderEngine.renderers.ThrustRenderer;
import kaba4cow.utils.RenderUtils;

public class StationEditorViewport extends AbstractEditorViewport {

	private StationFile file;

	private WeaponComponent[] weapons;

	private Direction direction;

	public StationEditorViewport(StationEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();
		direction = new Direction();
		loadRequestedFile();
	}

	@Override
	protected void loadRequestedFile() {
		renderer.clearLights();
		renderer.addLight(light);
		if (getNewFile() != null) {
			file = StationFile.load(getNewFile());
			weapons = file.getWeaponArray();
		} else {
			file = null;
			weapons = new WeaponComponent[0];
		}
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
		float fileSize = file == null ? 1f : file.getSize();
		camera.orbit(Vectors.INIT3, 0f, 0f, fileSize, cameraManager);
		if (settings.isStaticLight())
			light.setPos(new Vector3f(-50000f, 25000f, 50000f));
		else
			light.setPos(camera.getPos());
		renderer.setWireframe(settings.isRenderWireframe());

		renderer.prepare();

		Cubemap cubemap = settings.isRenderSkybox() ? skybox : cells;
		renderers.getCubemapRenderer().render(cubemap);
		renderers.getCubemapRenderer().process();

		if (file != null) {
			StationEditor editor = getEditor();

			file.setMachineName(editor.getNameTextField().getText());
			StationClass stationClass = StationClass.valueOf(editor
					.getClassRankButton().getText());
			file.setClassRank(stationClass.getRank());
			file.setClassName(stationClass.getNameIndex(editor
					.getClassNameButton().getText()));
			file.setSize(EditorUtils.getFloatValue(editor.getSizeSpinner()));
			file.setHealth(EditorUtils.getFloatValue(editor.getHealthSpinner()));
			file.setMetalModel(editor.getModelButton().getText());
			file.setGlassModel(editor.getGlassButton().getText());
			file.setUseLight(editor.getUseLightCheckbox().isSelected());

			String textureSet = editor.getTextureSetButton().getText();

			Matrix4f mat = direction.getMatrix(Vectors.INIT3, true,
					file.getSize());
			RenderUtils.renderMachine(Vectors.UNIT3,
					file.getMetalTexturedModel(textureSet),
					file.getGlassTexturedModel(textureSet, file.isUseLight()),
					mat, renderers);

			RenderUtils.renderWeapons(Vectors.UNIT3, textureSet,
					file.getSize(), mat, renderers, weapons);
		}
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public StationEditor getEditor() {
		return (StationEditor) editor;
	}

	@Override
	public StationEditorSettings getSettings() {
		return (StationEditorSettings) settings;
	}

	public StationFile getStationFile() {
		return file;
	}

}
