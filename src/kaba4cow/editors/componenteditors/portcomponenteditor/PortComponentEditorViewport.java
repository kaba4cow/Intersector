package kaba4cow.editors.componenteditors.portcomponenteditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.MachineFile;
import kaba4cow.intersector.gameobjects.objectcomponents.PortComponent;
import kaba4cow.intersector.renderEngine.renderers.ThrustRenderer;
import kaba4cow.intersector.utils.RenderUtils;

public class PortComponentEditorViewport extends AbstractEditorViewport {

	private MachineFile file;

	private int index;
	private int dindex;

	private Direction direction;

	public PortComponentEditorViewport(PortComponentEditor editor) {
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
			file = MachineFile.load(getNewFile());
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
		if (file != null && getSettings().isFollowPort() && file.getPorts() > 0) {
			orbitPoint = new Vector3f(file.getPort(index).pos);
			orbitPoint.scale(fileSize);
			orbitDist *= file.getPort(index).size;
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
			PortComponentEditor editor = getEditor();

			if (index < 0)
				index = file.getPorts() - 1;
			if (index >= file.getPorts())
				index = 0;
			if (file.getPorts() > 0) {
				PortComponent port = file.getPort(index);
				float invMul = 1f / PortComponentEditor.MUL;
				port.pos.x = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentXSpinner());
				port.pos.y = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentYSpinner());
				port.pos.z = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentZSpinner());
				port.min = EditorUtils
						.getFloatValue(editor.getPortMinSpinner());
				port.max = port.min
						+ EditorUtils.getFloatValue(editor
								.getPortRangeSpinner());
				port.visible = editor.getPortVisibleCheckbox().isSelected();
			}

			String textureSet = editor.getTextureSetButton().getText();

			Matrix4f mat = direction.getMatrix(null, true, file.getSize());
			renderers.getModelRenderer().render(
					file.getMetalTexturedModel(textureSet), null, mat);
			renderers.getModelRenderer().render(
					file.getGlassTexturedModel(textureSet, file.isUseLight()),
					null, mat);

			RenderUtils.renderPorts(Vectors.UNIT3, textureSet, file.getSize(),
					mat, renderers, file.getPortArray());
			RenderUtils.renderWeapons(Vectors.UNIT3, textureSet,
					file.getSize(), mat, renderers, file.getWeaponArray());
			RenderUtils.renderCargos(true, file.getSize(), mat, renderers,
					file.getContainerArray());
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
			index = file.getPorts() - 1;
		if (index >= file.getPorts())
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
		return file.getPorts();
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public PortComponentEditor getEditor() {
		return (PortComponentEditor) editor;
	}

	@Override
	public PortComponentEditorSettings getSettings() {
		return (PortComponentEditorSettings) settings;
	}

	public MachineFile getMachineFile() {
		return file;
	}

	public float getShipSize() {
		if (file == null)
			return 1f;
		return file.getSize();
	}

}
