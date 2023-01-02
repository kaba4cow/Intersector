package kaba4cow.editors.particlesystemeditor;

import javax.swing.JSlider;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.engine.toolbox.maths.Maths;

public class ParticleSystemEditorSettings extends AbstractEditorSettings {

	private float scale;
	private float life;

	private Vector3f tint;

	public ParticleSystemEditorSettings(ParticleSystemEditor editor) {
		super(editor);
		this.scale = 0f;
		this.life = 0f;
		this.tint = new Vector3f(1f, 1f, 1f);
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		ParticleSystemEditor editor = getEditor();
		JSlider slider;
		float newValue;

		slider = editor.getScaleSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 2f);
		scale = Maths.blend(newValue, scale, 2f * dt);

		slider = editor.getLifeSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		life = Maths.blend(newValue, life, 2f * dt);

		slider = editor.getTintRSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		tint.x = Maths.blend(newValue, tint.x, 2f * dt);

		slider = editor.getTintGSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		tint.y = Maths.blend(newValue, tint.y, 2f * dt);

		slider = editor.getTintBSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		tint.z = Maths.blend(newValue, tint.z, 2f * dt);
	}

	@Override
	public ParticleSystemEditor getEditor() {
		return (ParticleSystemEditor) editor;
	}

	public float getScale() {
		return scale;
	}

	public float getLife() {
		return life;
	}

	public Vector3f getTint() {
		return tint;
	}

}
