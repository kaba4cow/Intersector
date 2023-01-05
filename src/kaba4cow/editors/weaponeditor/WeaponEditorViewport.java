package kaba4cow.editors.weaponeditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.intersector.files.ProjectileFile;
import kaba4cow.intersector.files.WeaponFile;
import kaba4cow.intersector.utils.RenderUtils;

public class WeaponEditorViewport extends AbstractEditorViewport {

	private WeaponFile file;

	private int index;
	private int dindex;

	private Direction direction;

	public WeaponEditorViewport(WeaponEditor editor) {
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
			file = WeaponFile.load(getNewFile());
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

		if (isLoadingRequested())
			loadRequestedFile();
	}

	@Override
	public void render() {
		changeIndex();
		float fileSize = file == null ? 1f : file.getSize();
		Vector3f orbitPoint = Vectors.INIT3;
		float orbitDist = fileSize;
		if (file != null && getSettings().isFollowFirePoint() && file.getFirePoints() > 0) {
			orbitPoint = new Vector3f(file.getFirePoint(index));
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
			WeaponEditor editor = getEditor();

			file.setName(editor.getNameTextField().getText());
			file.setSize(EditorUtils.getFloatValue(editor.getSizeSpinner()));
			file.setReload(EditorUtils.getFloatValue(editor.getReloadTimeSpinner()));
			file.setCooldown(EditorUtils.getFloatValue(editor.getCooldownTimeSpinner()));
			file.setScale(EditorUtils.getFloatValue(editor.getFireScaleSpinner()));
			file.setOriginPoint(EditorUtils.getVector3fValue(editor.getOriginPointXSpinner(),
					editor.getOriginPointYSpinner(), editor.getOriginPointZSpinner()));
			file.setStaticModel(editor.getStaticModelButton().getText());
			file.setYawModel(editor.getYawModelButton().getText());
			file.setPitchModel(editor.getPitchModelButton().getText());
			file.setProjectile(editor.getProjectileButton().getText());
			file.setDamage(EditorUtils.getFloatValue(editor.getDamageSpinner()));
			file.setDamageDeviation(EditorUtils.getFloatValue(editor.getDamageDeviationSpinner()));
			file.setAutomatic(editor.getAutomaticCheckbox().isSelected());
			file.setRepeat(EditorUtils.getIntValue(editor.getRepetitionsSpinner()));
			file.setRotationSpeed(EditorUtils.getFloatValue(editor.getRotationSpeedSpinner()));
			file.setLimitPitch(editor.getLimitPitchCheckbox().isSelected());
			file.setMinPitch(EditorUtils.getFloatValue(editor.getMinPitchSpinner()) * Maths.PI);
			file.setMaxPitch(EditorUtils.getFloatValue(editor.getMaxPitchSpinner()) * Maths.PI);
			file.setLimitYaw(editor.getLimitYawCheckbox().isSelected());
			file.setMinYaw(EditorUtils.getFloatValue(editor.getMinYawSpinner()) * Maths.PI);
			file.setMaxYaw(EditorUtils.getFloatValue(editor.getMaxYawSpinner()) * Maths.PI);

			if (index < 0)
				index = file.getFirePoints() - 1;
			if (index >= file.getFirePoints())
				index = 0;
			if (file.getFirePoints() > 0) {
				Vector3f firePoint = file.getFirePoint(index);
				float invMul = 1f / WeaponEditor.MUL;
				firePoint.x = invMul * EditorUtils.getFloatValue(editor.getFirePointXSpinner());
				firePoint.y = invMul * EditorUtils.getFloatValue(editor.getFirePointYSpinner());
				firePoint.z = invMul * EditorUtils.getFloatValue(editor.getFirePointZSpinner());
			}

			Matrix4f mat = direction.getMatrix(Vectors.INIT3, true, file.getSize());
			String texture = editor.getTextureButton().getText();

			float yaw = 0f;
			float pitch = 0f;

			if (getSettings().isShowRotation()) {
				if (file.isLimitYaw())
					yaw = Maths.map(getSettings().getYaw(), 0f, 1f, file.getMinYaw(), file.getMaxYaw());
				else
					yaw = Maths.map(getSettings().getYaw(), 0f, 1f, 0f, Maths.TWO_PI);
				if (file.isLimitPitch())
					pitch = Maths.map(getSettings().getPitch(), 0f, 1f, file.getMinPitch(), file.getMaxPitch());
				else
					pitch = Maths.map(getSettings().getPitch(), 0f, 1f, 0f, Maths.TWO_PI);
			}

			TexturedModel staticModel = file.getTexturedStaticModel(texture);
			if (staticModel != null) {
				Matrix4f staticMatrix = new Matrix4f();
				staticMatrix.scale(new Vector3f(file.getSize(), file.getSize(), file.getSize()));

				renderers.getModelRenderer().render(staticModel, null, staticMatrix);
			}

			Vector3f originPoint = new Vector3f(file.getOriginPoint());

			TexturedModel yawModel = file.getTexturedYawModel(texture);
			if (yawModel != null) {
				Matrix4f dynamicMatrix = new Matrix4f();
				dynamicMatrix.scale(new Vector3f(file.getSize(), file.getSize(), file.getSize()));
				Matrix4f.rotate(yaw, Vectors.UP, dynamicMatrix, dynamicMatrix);

				renderers.getModelRenderer().render(yawModel, null, dynamicMatrix);
			}

			TexturedModel pitchModel = file.getTexturedPitchModel(texture);
			if (pitchModel != null) {
				Matrix4f dynamicMatrix = new Matrix4f();
				dynamicMatrix.scale(new Vector3f(file.getSize(), file.getSize(), file.getSize()));
				Matrix4f.rotate(yaw, Vectors.UP, dynamicMatrix, dynamicMatrix);
				dynamicMatrix.translate(originPoint.negate(null));
				Matrix4f.rotate(pitch, Vectors.RIGHT, dynamicMatrix, dynamicMatrix);
				dynamicMatrix.translate(originPoint);

				renderers.getModelRenderer().render(pitchModel, null, dynamicMatrix);
			}

			RenderUtils.renderDebugPoints(0.25f * file.getScale(), mat, renderers, file.getFirePointArray());
			ProjectileFile projectile = file.getProjectileFile();
			Vector3f pointsPos = new Vector3f();
			Vector3f pointScale = new Vector3f();
			Vectors.set(pointScale, file.getScale());
			for (int i = 0; i < file.getFirePoints(); i++) {
				pointsPos.set(file.getFirePoint(i));
				Matrix4f pointMatrix = new Matrix4f();
				pointMatrix.translate(pointsPos.negate(null));
				pointMatrix.scale(pointScale);
				Matrix4f.mul(mat, pointMatrix, pointMatrix);
				render(projectile, i, pointMatrix);
			}
			RenderUtils.renderDebugPoints(0.05f, new Vector3f(0f, 0.5f, 0.1f), mat, renderers, originPoint);
		}
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	private void render(ProjectileFile projectile, int firePoint, Matrix4f matrix) {
		if (projectile == null)
			return;
		if (projectile.usesLaserModel()) {
			TexturedModel projectileModel = projectile.createLaserModel();
			renderers.getLaserRenderer().render(projectileModel, matrix, null, 1f);
		} else {
			TexturedModel projectileModel = projectile.createTexturedModel();
			renderers.getModelRenderer().render(projectileModel, null, matrix);
		}
	}

	public void changeIndex() {
		if (file == null || dindex == 0)
			return;
		index += dindex;
		dindex = 0;
		if (index < 0)
			index = file.getFirePoints() - 1;
		if (index >= file.getFirePoints())
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
		return file.getFirePoints();
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public WeaponEditor getEditor() {
		return (WeaponEditor) editor;
	}

	@Override
	public WeaponEditorSettings getSettings() {
		return (WeaponEditorSettings) settings;
	}

	public WeaponFile getWeaponFile() {
		return file;
	}

	public float getWeaponSize() {
		if (file == null)
			return 1f;
		return file.getSize();
	}

}
