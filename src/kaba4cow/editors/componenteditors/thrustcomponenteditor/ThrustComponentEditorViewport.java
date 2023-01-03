package kaba4cow.editors.componenteditors.thrustcomponenteditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.ShipFile;
import kaba4cow.intersector.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.intersector.renderEngine.models.ThrustModel;
import kaba4cow.intersector.renderEngine.renderers.ThrustRenderer;
import kaba4cow.intersector.utils.RenderUtils;

public class ThrustComponentEditorViewport extends AbstractEditorViewport {

	private ShipFile resetFile;
	private ShipFile file;

	private int index;
	private int dindex;

	private Direction direction;

	public ThrustComponentEditorViewport(ThrustComponentEditor editor) {
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
			file = ShipFile.load(getNewFile());
		} else {
			file = null;
		}
		index = 0;
		dindex = 0;
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
		changeIndex();
		float fileSize = file == null ? 1f : file.getSize();
		Vector3f orbitPoint = Vectors.INIT3;
		float orbitDist = fileSize;
		if (file != null && getSettings().isFollowThrust()
				&& file.getThrusts() > 0) {
			orbitPoint = new Vector3f(file.getThrust(index).pos);
			orbitPoint.scale(fileSize);
			orbitDist *= 0.25f;
		}
		camera.orbit(orbitPoint, 0f, 0f, orbitDist, cameraManager);
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
			ThrustComponentEditor editor = getEditor();

			file.setThrust(editor.getThrustButton().getText());
			file.setThrustTexture(editor.getThrustTextureButton().getText());

			if (index < 0)
				index = file.getThrusts() - 1;
			if (index >= file.getThrusts())
				index = 0;
			if (file.getThrusts() > 0) {
				ThrustComponent thrustInfo = file.getThrust(index);
				float invMul = 1f / ThrustComponentEditor.MUL;
				thrustInfo.pos.x = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentXSpinner());
				thrustInfo.pos.y = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentYSpinner());
				thrustInfo.pos.z = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentZSpinner());
				thrustInfo.size = invMul
						* EditorUtils.getFloatValue(editor
								.getThrustSizeSpinner());
			}

			String textureSet = editor.getTextureSetButton().getText();

			Matrix4f mat = direction.getMatrix(null, true, file.getSize());
			RenderUtils.renderMachine(Vectors.UNIT3,
					file.getMetalTexturedModel(textureSet),
					file.getGlassTexturedModel(textureSet, file.isUseLight()),
					mat, renderers);

			ThrustModel thrustModel = file.createThrustModel();
			RenderUtils.renderThrusts(thrustModel, editor.getSettings()
					.getThrustBrightness(), 1f, file.getSize(), mat, renderers,
					file.getThrustArray());
			RenderUtils.renderWeapons(Vectors.UNIT3, textureSet,
					file.getSize(), mat, renderers, file.getWeaponArray());
		}
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	public void changeIndex() {
		if (file == null || dindex == 0)
			return;
		index += dindex;
		dindex = 0;
		if (index < 0)
			index = file.getThrusts() - 1;
		if (index >= file.getThrusts())
			index = 0;
		getEditor().onIndexChanged();
	}

	public void changeIndex(int dir) {
		dindex = dir;
	}

	public int getIndex() {
		return index;
	}

	public int getMaxIndex() {
		if (file == null)
			return 0;
		return file.getThrusts();
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public ThrustComponentEditor getEditor() {
		return (ThrustComponentEditor) editor;
	}

	@Override
	public ThrustComponentEditorSettings getSettings() {
		return (ThrustComponentEditorSettings) settings;
	}

	public ShipFile getShipFile() {
		return file;
	}

	public float getShipSize() {
		if (file == null)
			return 1f;
		return file.getSize();
	}

	public ShipFile getResetFile() {
		return resetFile;
	}

}
