package kaba4cow.editors.componenteditors.weaponcomponenteditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.intersector.files.MachineFile;
import kaba4cow.intersector.gameobjects.objectcomponents.WeaponComponent;
import kaba4cow.intersector.renderEngine.ThrustModel;
import kaba4cow.intersector.renderEngine.renderers.ThrustRenderer;
import kaba4cow.intersector.utils.RenderUtils;

public class WeaponComponentEditorViewport extends AbstractEditorViewport {

	private MachineFile file;

	private int index;
	private int dindex;

	private Direction direction;

	public WeaponComponentEditorViewport(WeaponComponentEditor editor) {
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
		if (file != null && getSettings().isFollowWeapon()
				&& file.getWeapons() > 0) {
			if (index < 0)
				index = file.getWeapons() - 1;
			if (index >= file.getWeapons())
				index = 0;
			orbitPoint = new Vector3f(file.getWeapon(index).pos);
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
			WeaponComponentEditor editor = getEditor();

			if (index < 0)
				index = file.getWeapons() - 1;
			if (index >= file.getWeapons())
				index = 0;

			String textureSet = editor.getTextureSetButton().getText();

			if (file.getWeapons() > 0) {
				WeaponComponent weaponInfo = file.getWeapon(index);
				float invMul = 1f / WeaponComponentEditor.MUL;

				weaponInfo.weaponName = editor.getWeaponNameButton().getText();
				weaponInfo.copyTarget = editor.getWeaponCopyTargetCheckbox()
						.isSelected();
				weaponInfo.pos.x = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentXSpinner());
				weaponInfo.pos.y = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentYSpinner());
				weaponInfo.pos.z = invMul
						* EditorUtils.getFloatValue(editor
								.getComponentZSpinner());
			}

			Matrix4f mat = direction.getMatrix(null, true, file.getSize());
			ThrustModel thrustModel = file.createThrustModel();
			RenderUtils.renderMachine(Vectors.UNIT3,
					file.getMetalTexturedModel(textureSet),
					file.getGlassTexturedModel(textureSet, file.isUseLight()),
					mat, renderers);
			RenderUtils.renderThrusts(thrustModel, 1f, 1f, file.getSize(), mat,
					renderers, file.getThrustArray());
			RenderUtils.renderCargos(true, file.getSize(), mat, renderers,
					file.getContainerArray());
			RenderUtils.renderWeapons(Vectors.UNIT3, textureSet,
					file.getSize(), mat, renderers, getSettings()
							.isRenderTargetInfo(), file.getWeaponArray());
		}
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	public void setIndex(int index) {
		this.index = index;
		if (index < 0)
			index = file.getWeapons() - 1;
		if (index >= file.getWeapons())
			index = 0;
		getEditor().onIndexChanged();
	}

	public void changeIndex() {
		if (file == null || dindex == 0)
			return;
		index += dindex;
		dindex = 0;
		if (index < 0)
			index = file.getWeapons() - 1;
		if (index >= file.getWeapons())
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
		return file.getWeapons();
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public WeaponComponentEditor getEditor() {
		return (WeaponComponentEditor) editor;
	}

	@Override
	public WeaponComponentEditorSettings getSettings() {
		return (WeaponComponentEditorSettings) settings;
	}

	public MachineFile getShipFile() {
		return file;
	}

	public float getShipSize() {
		if (file == null)
			return 1f;
		return file.getSize();
	}

}
