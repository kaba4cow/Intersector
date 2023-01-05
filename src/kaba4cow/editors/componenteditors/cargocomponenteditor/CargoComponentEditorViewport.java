package kaba4cow.editors.componenteditors.cargocomponenteditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.intersector.files.ContainerGroupFile;
import kaba4cow.intersector.files.MachineFile;
import kaba4cow.intersector.gameobjects.objectcomponents.ContainerComponent;
import kaba4cow.intersector.renderEngine.renderers.ThrustRenderer;
import kaba4cow.intersector.utils.RenderUtils;

public class CargoComponentEditorViewport extends AbstractEditorViewport {

	private MachineFile file;

	private int index;
	private int dindex;

	private Direction direction;

	public CargoComponentEditorViewport(CargoComponentEditor editor) {
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
		if (file != null && getSettings().isFollowCargo()
				&& file.getContainers() > 0) {
			orbitPoint = new Vector3f(file.getContainer(index).pos);
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
			CargoComponentEditor editor = getEditor();

			if (index < 0)
				index = file.getContainers() - 1;
			if (index >= file.getContainers())
				index = 0;
			if (file.getContainers() > 0) {
				ContainerComponent cargoInfo = file.getContainer(index);
				float invMul = 1f / CargoComponentEditor.MUL;
				cargoInfo.pos.x = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentXSpinner());
				cargoInfo.pos.y = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentYSpinner());
				cargoInfo.pos.z = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentZSpinner());
				file.setSize(EditorUtils.getFloatValue(editor.getSizeSpinner()));
				cargoInfo.containerGroupName = editor.getCargoGroupNameButton()
						.getText();
				cargoInfo.containerGroupFile = ContainerGroupFile
						.get(cargoInfo.containerGroupName);
			}

			String textureSet = editor.getTextureSetButton().getText();

			Matrix4f mat = direction.getMatrix(null, true, file.getSize());
			RenderUtils.renderMachine(Vectors.UNIT3,
					file.getMetalTexturedModel(textureSet),
					file.getGlassTexturedModel(textureSet, file.isUseLight()),
					mat, renderers);
			RenderUtils.renderCargos(false, file.getSize(), mat, renderers,
					file.getContainerArray());
			RenderUtils.renderWeapons(Vectors.UNIT3, textureSet,
					file.getSize(), mat, renderers, file.getWeaponArray());
		}
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	public void setIndex(int index) {
		this.index = index;
		if (index < 0)
			index = file.getContainers() - 1;
		if (index >= file.getContainers())
			index = 0;
		getEditor().onIndexChanged();
	}

	public void changeIndex() {
		if (file == null || dindex == 0)
			return;
		index += dindex;
		dindex = 0;
		if (index < 0)
			index = file.getColliders() - 1;
		if (index >= file.getColliders())
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
		return file.getContainers();
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public CargoComponentEditor getEditor() {
		return (CargoComponentEditor) editor;
	}

	@Override
	public CargoComponentEditorSettings getSettings() {
		return (CargoComponentEditorSettings) settings;
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
