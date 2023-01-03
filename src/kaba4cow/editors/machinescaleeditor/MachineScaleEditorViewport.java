package kaba4cow.editors.machinescaleeditor;

import java.util.Collections;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.MachineFile;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.files.ShipFile;
import kaba4cow.intersector.gameobjects.machines.Ship;
import kaba4cow.intersector.renderEngine.models.ThrustModel;
import kaba4cow.intersector.renderEngine.renderers.ThrustRenderer;
import kaba4cow.intersector.toolbox.RawModelContainer;
import kaba4cow.intersector.utils.FileUtils;
import kaba4cow.intersector.utils.RenderUtils;

public class MachineScaleEditorViewport extends AbstractEditorViewport {

	private List<MachineFile> files;
	private int index;
	private int dindex;

	private Direction direction;

	private float dist;
	private float newDist;

	public MachineScaleEditorViewport(MachineScaleEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();
		direction = new Direction();

		renderer.clearLights();
		renderer.addLight(light);

		files = MachineFile.getChildrenList();
		Collections.sort(files, FileUtils.MachineSizeComparator.instance);
		index = 0;
		dindex = 0;
		editor.onNewFileLoaded();
	}

	@Override
	protected void loadRequestedFile() {

	}

	@Override
	public void update(float dt) {
		super.update(dt);

		ThrustRenderer.update(dt);

		dist = Maths.blend(newDist, dist, 4f * dt);
	}

	@Override
	public void render() {
		changeIndex();
		camera.orbit(cameraManager.getPoint(), 0f, 0f, dist, cameraManager);
		if (settings.isStaticLight())
			light.setPos(new Vector3f(-50000f, 25000f, 50000f));
		else
			light.setPos(camera.getPos());
		renderer.setWireframe(settings.isRenderWireframe());

		renderer.prepare();

		Cubemap cubemap = settings.isRenderSkybox() ? skybox : cells;
		renderers.getCubemapRenderer().render(cubemap);
		renderers.getCubemapRenderer().process();

		MachineScaleEditor editor = getEditor();
		MachineFile currentFile = getMachineFile();

		currentFile.setMachineName(editor.getNameTextField().getText());
		currentFile.setClassRank(currentFile.getMachineClass().getMachineClass(
				editor.getClassRankButton().getText()));
		currentFile.setClassName(currentFile.getMachineClass().getNameIndex(
				editor.getClassNameButton().getText()));
		currentFile.setManufacturer(editor.getManufacturerButton().getText());
		currentFile.setMass(EditorUtils.getFloatValue(editor.getMassSpinner()));
		currentFile.setMaxCargo(EditorUtils.getIntValue(editor
				.getMaxCargoSpinner()));
		currentFile.setSize(EditorUtils.getFloatValue(editor.getSizeSpinner()));
		currentFile.setHealth(EditorUtils.getFloatValue(editor
				.getHealthSpinner()));
		currentFile.setShield(EditorUtils.getFloatValue(editor
				.getShieldSpinner()));
		if (currentFile instanceof ShipFile) {
			ShipFile shipFile = (ShipFile) currentFile;
			shipFile.setHorSpeed(EditorUtils.getFloatValue(editor
					.getHorSpeedSpinner()));
			shipFile.setVerSpeed(EditorUtils.getFloatValue(editor
					.getVerSpeedSpinner()));
			shipFile.setHyperSpeed(EditorUtils.getFloatValue(editor
					.getHyperSpeedSpinner()));
		}
		currentFile.setUseLight(editor.getUseLightCheckbox().isSelected());
		currentFile.setThrust(editor.getThrustButton().getText());
		currentFile.setThrustTexture(editor.getThrustTextureButton().getText());

		newDist = currentFile.getSize();
		if (editor.getSettings().isSortHealth())
			Collections.sort(files, FileUtils.MachineHealthComparator.instance);
		else
			Collections.sort(files, FileUtils.MachineSizeComparator.instance);

		Vector3f position = new Vector3f();
		float distance = getSettings().getDistance();

		String textureSet = editor.getTextureSetButton().getText();

		for (int i = 0; i < files.size(); i++) {
			MachineFile file = files.get(i);
			position.x -= distance * file.getSize() * file.getCollisionSize();

			if (file == currentFile)
				index = i;

			Vector3f currentPosition = new Vector3f(position);
			if (i == index) {
				cameraManager.moveTo(currentPosition);
				renderDummy(currentPosition, file.getSize());

				if (file instanceof ShipFile
						&& file.getMetalTexturedModel(textureSet) != null) {
					ShipFile shipFile = (ShipFile) file;
					float horSpeed1 = shipFile.getHorSpeed()
							* Ship.getMassDivider(shipFile.getMinTotalMass());
					Vector3f posForward1 = Vectors.addScaled(currentPosition,
							direction.getForward(), horSpeed1, null);
					Matrix4f matForward1 = direction.getMatrix(posForward1,
							true, file.getSize());
					RenderUtils.renderHologramMachine(
							"BLUE",
							file.getMetalTexturedModel(textureSet),
							file.getGlassTexturedModel(textureSet,
									file.isUseLight()), 1f, file.getSize(),
							matForward1, renderers);
					float horSpeed2 = shipFile.getHorSpeed()
							* Ship.getMassDivider(shipFile.getMaxTotalMass());
					Vector3f posForward2 = Vectors.addScaled(currentPosition,
							direction.getForward(), horSpeed2, null);
					Matrix4f matForward2 = direction.getMatrix(posForward2,
							true, file.getSize());
					RenderUtils.renderHologramMachine(
							"RED",
							file.getMetalTexturedModel(textureSet),
							file.getGlassTexturedModel(textureSet,
									file.isUseLight()), 1f, file.getSize(),
							matForward2, renderers);

					float verSpeed1 = shipFile.getVerSpeed()
							* Ship.getMassDivider(shipFile.getMinTotalMass());
					Vector3f posUp1 = Vectors.addScaled(currentPosition,
							direction.getUp(), verSpeed1, null);
					Matrix4f matUp1 = direction.getMatrix(posUp1, true,
							file.getSize());
					RenderUtils.renderHologramMachine(
							"BLUE",
							file.getMetalTexturedModel(textureSet),
							file.getGlassTexturedModel(textureSet,
									file.isUseLight()), 1f, file.getSize(),
							matUp1, renderers);
					float verSpeed2 = shipFile.getVerSpeed()
							* Ship.getMassDivider(shipFile.getMaxTotalMass());
					Vector3f posUp2 = Vectors.addScaled(currentPosition,
							direction.getUp(), verSpeed2, null);
					Matrix4f matUp2 = direction.getMatrix(posUp2, true,
							file.getSize());
					RenderUtils.renderHologramMachine(
							"RED",
							file.getMetalTexturedModel(textureSet),
							file.getGlassTexturedModel(textureSet,
									file.isUseLight()), 1f, file.getSize(),
							matUp2, renderers);
				}
			}

			if (Maths.dist(i, index) < 10) {
				Matrix4f mat = direction.getMatrix(currentPosition, true,
						file.getSize());
				RenderUtils.renderMachine(
						Vectors.UNIT3,
						file.getMetalTexturedModel(textureSet),
						file.getGlassTexturedModel(textureSet,
								file.isUseLight()), mat, renderers);

				ThrustModel thrustModel = file.createThrustModel();
				RenderUtils.renderThrusts(thrustModel, editor.getSettings()
						.getThrustBrightness(), 1f, file.getSize(), mat,
						renderers, file.getThrustArray());
				RenderUtils.renderCargos(true, file.getSize(), mat, renderers,
						file.getContainerArray());
				RenderUtils.renderWeapons(Vectors.UNIT3, textureSet,
						file.getSize(), mat, renderers, file.getWeaponArray());
			}

			position.x -= distance * file.getSize() * file.getCollisionSize();
		}
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	private void renderDummy(Vector3f pos, float size) {
		pos = new Vector3f(pos);
		pos.x += 0f * size;
		pos.z -= 0.6f * size;
		Matrix4f mat = direction.getMatrix(pos, true, 1f);
		RawModel model = RawModelContainer.get("MISC/human");
		renderers.getModelRenderer()
				.render(new TexturedModel(model, ModelTextureFile
						.get("METAL/6").get()), null, mat);
	}

	public void changeIndex() {
		if (dindex == 0)
			return;
		index += dindex;
		dindex = 0;
		if (index < 0)
			index = files.size() - 1;
		if (index >= files.size())
			index = 0;
		getEditor().onNewFileLoaded();
	}

	public void changeIndex(int dir) {
		dindex = dir;
	}

	public int getIndex() {
		return index;
	}

	public int getMaxIndex() {
		return files.size();
	}

	@Override
	public void save() {
		for (int i = 0; i < files.size(); i++)
			files.get(i).save();
	}

	@Override
	public MachineScaleEditor getEditor() {
		return (MachineScaleEditor) editor;
	}

	@Override
	public MachineScaleEditorSettings getSettings() {
		return (MachineScaleEditorSettings) settings;
	}

	public MachineFile getMachineFile() {
		try {
			return files.get(index);
		} catch (Exception e) {
			return files.get(0);
		}
	}

}
