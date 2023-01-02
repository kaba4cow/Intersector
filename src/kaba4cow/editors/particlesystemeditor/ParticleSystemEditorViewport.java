package kaba4cow.editors.particlesystemeditor;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Easing;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.files.ParticleSystemFile;

public class ParticleSystemEditorViewport extends AbstractEditorViewport {

	private ParticleSystemFile file;

	private float elapsedTime;

	public ParticleSystemEditorViewport(ParticleSystemEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();

		elapsedTime = 0f;

		loadRequestedFile();
	}

	@Override
	protected void loadRequestedFile() {
		renderer.clearLights();
		renderer.addLight(light);
		file = ParticleSystemFile.load(getNewFile());
		finishLoadingFile(file);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		elapsedTime += dt;

		if (file != null) {
			ParticleSystem particleSystem = file.get();
			particleSystem.setAverageSpeed(0f);
			particleSystem.setAverageScale(getSettings().getScale());
			particleSystem.setAverageLifeLength(Maths.map(getSettings()
					.getLife(), 0f, 1f, 0f, 2f));

			particleSystem.update(dt);

			float maxTime = 1f / (float) getEditor().getPpsSlider().getValue();
			if (particleSystem.getParticles().isEmpty()
					|| elapsedTime >= maxTime) {
				particleSystem.emitParticle(Vectors.INIT3);
				elapsedTime = 0f;
			}
		}

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

		renderer.prepare();

		Cubemap cubemap = settings.isRenderSkybox() ? skybox : cells;
		renderers.getCubemapRenderer().render(cubemap);
		renderers.getCubemapRenderer().process();

		if (file != null) {
			ParticleSystemEditor editor = getEditor();
			if (editor.getEasingButton().getText().equalsIgnoreCase("null"))
				file.setEasing(null);
			else
				file.setEasing(Easing.valueOf(editor.getEasingButton()
						.getText()));
			file.setTexture(editor.getTextureButton().getText());
			file.setErrorLife(EditorUtils.getFloatValue(editor
					.getErrorlifeSpinner()));
			file.setErrorScale(EditorUtils.getFloatValue(editor
					.getErrorscaleSpinner()));
			file.setdScale(EditorUtils.getFloatValue(editor.getDscaleSpinner()));
			file.setdRotation(EditorUtils.getFloatValue(editor
					.getDrotationSpinner()));

			file.get().setTint(editor.getSettings().getTint(), 1f);
			file.get().render(renderers.getParticleRenderer());
		}
		renderers.getParticleRenderer().process();

		stopPostProcessing(null);
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public ParticleSystemEditor getEditor() {
		return (ParticleSystemEditor) editor;
	}

	@Override
	public ParticleSystemEditorSettings getSettings() {
		return (ParticleSystemEditorSettings) settings;
	}

	public ParticleSystemFile getSystemFile() {
		return file;
	}

}
