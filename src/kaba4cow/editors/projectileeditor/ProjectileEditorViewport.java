package kaba4cow.editors.projectileeditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.ProjectileFile;
import kaba4cow.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.renderEngine.models.LaserModel;
import kaba4cow.renderEngine.models.ThrustModel;
import kaba4cow.renderEngine.renderers.ThrustRenderer;
import kaba4cow.utils.RenderUtils;

public class ProjectileEditorViewport extends AbstractEditorViewport {

	private ProjectileFile file;

	private Direction direction;

	public ProjectileEditorViewport(ProjectileEditor editor) {
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
			file = ProjectileFile.load(getNewFile());
		} else {
			file = null;
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
		Vector3f orbitPoint = Vectors.INIT3;
		float orbitDist = 1f;
		if (file != null && getSettings().isFollowThrust()) {
			orbitPoint = new Vector3f(file.getThrustComponent().pos);
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
			ProjectileEditor editor = getEditor();

			file.setModel(editor.getProjectileButton().getText());
			file.setTexture(editor.getProjectileTextureButton().getText());
			file.setType(editor.getProjectileTypeButton().getText());
			file.setSound(editor.getSoundButton().getText());
			file.setExplode(editor.getExplodeCheckbox().isSelected());
			file.setAutoaim(editor.getAutoAimCheckbox().isSelected());
			file.setAiming(EditorUtils.getFloatValue(editor.getAimingSpinner()));
			file.setDelay(EditorUtils.getFloatValue(editor.getDelaySpinner()));
			file.setLifeLength(EditorUtils.getFloatValue(editor
					.getLifeLengthSpinner()));
			file.setSpeedScale(EditorUtils.getFloatValue(editor
					.getSpeedScaleSpinner()));
			file.setThrust(editor.getThrustButton().getText());
			file.setThrustTexture(editor.getThrustTextureButton().getText());

			ThrustComponent thrustInfo = file.getThrustComponent();
			if (thrustInfo != null) {
				thrustInfo.pos.x = EditorUtils.getFloatValue(editor
						.getThrustXSpinner());
				thrustInfo.pos.y = EditorUtils.getFloatValue(editor
						.getThrustYSpinner());
				thrustInfo.pos.z = EditorUtils.getFloatValue(editor
						.getThrustZSpinner());
				thrustInfo.size = EditorUtils.getFloatValue(editor
						.getThrustSizeSpinner());
			}

			Matrix4f mat = direction.getMatrix(null, true, 1f);
			ThrustModel thrustModel = file.createThrustModel();
			RenderUtils.renderThrusts(thrustModel, 1f, 4f, 1f, mat, renderers,
					file.getThrustComponent());

			if (file.usesLaserModel()) {
				LaserModel projectileModel = file.createLaserModel();
				renderers.getLaserRenderer().render(projectileModel, mat, null,
						1f);
			} else {
				TexturedModel projectileModel = file.createTexturedModel();
				renderers.getModelRenderer().render(projectileModel, null, mat);
			}
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
	public ProjectileEditor getEditor() {
		return (ProjectileEditor) editor;
	}

	@Override
	public ProjectileEditorSettings getSettings() {
		return (ProjectileEditorSettings) settings;
	}

	public ProjectileFile getProjectileFile() {
		return file;
	}

}
