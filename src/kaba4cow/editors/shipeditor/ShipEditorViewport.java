package kaba4cow.editors.shipeditor;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.Input;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.ShipFile;
import kaba4cow.gameobjects.machines.classes.ShipClass;
import kaba4cow.gameobjects.objectcomponents.WeaponComponent;
import kaba4cow.gameobjects.parametercontrols.RotationControl;
import kaba4cow.renderEngine.models.ThrustModel;
import kaba4cow.renderEngine.renderers.ThrustRenderer;
import kaba4cow.utils.RenderUtils;

public class ShipEditorViewport extends AbstractEditorViewport {

	private ShipFile file;

	private WeaponComponent[] weapons;

	private Direction direction;

	private RotationControl rotationControl;

	public ShipEditorViewport(ShipEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();
		direction = new Direction();
		rotationControl = new RotationControl(null, 1f, 1f, 1f);
		loadRequestedFile();
	}

	@Override
	protected void loadRequestedFile() {
		renderer.clearLights();
		renderer.addLight(light);
		if (getNewFile() != null) {
			file = ShipFile.get(getNewFile());
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

		updateRotationControl(dt);
		rotationControl.update(dt);

		direction.rotate(direction.getRight(), rotationControl.getPitch());
		direction.rotate(direction.getUp(), rotationControl.getYaw());
		direction.rotate(direction.getForward(), rotationControl.getRoll());

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
			ShipEditor editor = getEditor();

			file.setMachineName(editor.getNameTextField().getText());
			ShipClass shipClass = ShipClass.valueOf(editor.getClassRankButton()
					.getText());
			file.setClassRank(shipClass.getRank());
			file.setClassName(shipClass.getNameIndex(editor
					.getClassNameButton().getText()));
			file.setSize(EditorUtils.getFloatValue(editor.getSizeSpinner()));
			file.setHealth(EditorUtils.getFloatValue(editor.getHealthSpinner()));
			file.setShield(EditorUtils.getFloatValue(editor.getShieldSpinner()));
			file.setHorSpeed(EditorUtils.getFloatValue(editor
					.getHorSpeedSpinner()));
			file.setVerSpeed(EditorUtils.getFloatValue(editor
					.getVerSpeedSpinner()));
			file.setHyperSpeed(EditorUtils.getFloatValue(editor
					.getHyperSpeedSpinner()));
			file.setHorThrust(EditorUtils.getFloatValue(editor
					.getHorThrustSpinner()));
			file.setHorBrake(EditorUtils.getFloatValue(editor
					.getHorBrakeSpinner()));
			file.setVerThrust(EditorUtils.getFloatValue(editor
					.getVerThrustSpinner()));
			file.setVerBrake(EditorUtils.getFloatValue(editor
					.getVerBrakeSpinner()));
			file.setHyperThrust(EditorUtils.getFloatValue(editor
					.getHyperThrustSpinner()));
			file.setAftPower(EditorUtils.getFloatValue(editor
					.getAftPowerSpinner()));
			file.setAftTime(EditorUtils.getFloatValue(editor
					.getAftTimeSpinner()));
			file.setAftCooldown(EditorUtils.getFloatValue(editor
					.getAftCooldownSpinner()));
			file.setAftSmoothness(EditorUtils.getFloatValue(editor
					.getAftSmoothnessSpinner()));
			file.setPitchSens(EditorUtils.getFloatValue(editor
					.getPitchSensSpinner()));
			file.setYawSens(EditorUtils.getFloatValue(editor
					.getYawSensSpinner()));
			file.setRollSens(EditorUtils.getFloatValue(editor
					.getRollSensSpinner()));
			file.setMetalModel(editor.getModelButton().getText());
			file.setGlassModel(editor.getGlassButton().getText());
			file.setUseLight(editor.getUseLightCheckbox().isSelected());
			file.setThrust(editor.getThrustButton().getText());
			file.setThrustTexture(editor.getThrustTextureButton().getText());

			String textureSet = editor.getTextureSetButton().getText();

			Matrix4f mat = direction.getMatrix(Vectors.INIT3, true,
					file.getSize());
			RenderUtils.renderMachine(Vectors.UNIT3,
					file.getMetalTexturedModel(textureSet),
					file.getGlassTexturedModel(textureSet, file.isUseLight()),
					mat, renderers);

			if (file.getMetalTexturedModel(textureSet) != null) {
				Vector3f posForward = Vectors.addScaled(Vectors.INIT3,
						direction.getForward(), file.getHorSpeed(), null);
				Matrix4f matForward = direction.getMatrix(posForward, true,
						file.getSize());
				RenderUtils.renderHologramMachine(
						"HOLOGRAM_BLUE",
						file.getMetalTexturedModel(textureSet),
						file.getGlassTexturedModel(textureSet,
								file.isUseLight()), 1f, file.getSize(),
						matForward, renderers);

				Vector3f posUp = Vectors.addScaled(Vectors.INIT3,
						direction.getUp(), file.getVerSpeed(), null);
				Matrix4f matUp = direction.getMatrix(posUp, true,
						file.getSize());
				RenderUtils.renderHologramMachine(
						"HOLOGRAM_BLUE",
						file.getMetalTexturedModel(textureSet),
						file.getGlassTexturedModel(textureSet,
								file.isUseLight()), 1f, file.getSize(), matUp,
						renderers);
			}

			ThrustModel thrustModel = file.createThrustModel();
			RenderUtils.renderThrusts(thrustModel, editor.getSettings()
					.getThrustBrightness(), 1f, file.getSize(), mat, renderers,
					file.getThrustArray());

			RenderUtils.renderWeapons(Vectors.UNIT3, textureSet,
					file.getSize(), mat, renderers, weapons);
		}
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	private void updateRotationControl(float dt) {
		if (file == null || Input.isKey(Keyboard.KEY_SPACE)) {
			rotationControl.reset();
			direction.reset();
			return;
		}
		rotationControl.setPitchSensitivity(file.getPitchSens());
		rotationControl.setYawSensitivity(file.getYawSens());
		rotationControl.setRollSensitivity(file.getRollSens());

		if (Input.isKey(Keyboard.KEY_E))
			rotationControl.yawUp(dt);
		if (Input.isKey(Keyboard.KEY_Q))
			rotationControl.yawDown(dt);

		if (Input.isKey(Keyboard.KEY_A))
			rotationControl.rollUp(dt);
		if (Input.isKey(Keyboard.KEY_D))
			rotationControl.rollDown(dt);

		if (Input.isKey(Keyboard.KEY_DOWN))
			rotationControl.pitchUp(dt);
		if (Input.isKey(Keyboard.KEY_UP))
			rotationControl.pitchDown(dt);
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public ShipEditor getEditor() {
		return (ShipEditor) editor;
	}

	@Override
	public ShipEditorSettings getSettings() {
		return (ShipEditorSettings) settings;
	}

	public ShipFile getShipFile() {
		return file;
	}

}
