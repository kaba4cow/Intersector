package kaba4cow.editors.machinescaleeditor;

import javax.swing.JSlider;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.engine.toolbox.maths.Maths;

public class MachineScaleEditorSettings extends AbstractEditorSettings {

	private float distance;
	private boolean sortHealth;

	private float thrustBrightness;

	public MachineScaleEditorSettings(MachineScaleEditor editor) {
		super(editor);
		this.distance = 0.5f;
		this.sortHealth = false;
		this.thrustBrightness = 0f;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		MachineScaleEditor editor = getEditor();
		JSlider slider;
		float newValue;

		slider = editor.getDistanceSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 1f, 1.5f);
		distance = Maths.blend(newValue, distance, 2f * dt);

		slider = editor.getThrustBrightnessSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		thrustBrightness = Maths.blend(newValue, thrustBrightness, 2f * dt);
	}

	@Override
	public MachineScaleEditor getEditor() {
		return (MachineScaleEditor) editor;
	}

	public float getDistance() {
		return distance;
	}

	public boolean isSortHealth() {
		return sortHealth;
	}

	public void setSortHealth(boolean sortHealth) {
		this.sortHealth = sortHealth;
	}

	public float getThrustBrightness() {
		return thrustBrightness;
	}

}
