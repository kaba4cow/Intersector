package kaba4cow.editors.shipeditor;

import javax.swing.JSlider;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.engine.toolbox.maths.Maths;

public class ShipEditorSettings extends AbstractEditorSettings {

	private float thrustBrightness;

	public ShipEditorSettings(ShipEditor editor) {
		super(editor);
		this.thrustBrightness = 0f;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		ShipEditor editor = getEditor();
		JSlider slider;
		float newValue;

		slider = editor.getThrustBrightnessSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		thrustBrightness = Maths.blend(newValue, thrustBrightness, 2f * dt);
	}

	@Override
	public ShipEditor getEditor() {
		return (ShipEditor) editor;
	}

	public float getThrustBrightness() {
		return thrustBrightness;
	}

	public void setThrustBrightness(float thrustBrightness) {
		this.thrustBrightness = thrustBrightness;
	}

}
