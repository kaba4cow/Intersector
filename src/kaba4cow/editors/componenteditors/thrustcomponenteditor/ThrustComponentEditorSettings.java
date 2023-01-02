package kaba4cow.editors.componenteditors.thrustcomponenteditor;

import javax.swing.JSlider;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.editors.componenteditors.ComponentEditorSettings;
import kaba4cow.engine.toolbox.maths.Maths;

public class ThrustComponentEditorSettings extends AbstractEditorSettings
		implements ComponentEditorSettings {

	private float thrustBrightness;

	private boolean followThrust;
	private boolean rotateLocal;
	private boolean rotateAroundCenter;

	public ThrustComponentEditorSettings(ThrustComponentEditor editor) {
		super(editor);
		this.thrustBrightness = 0f;
		this.followThrust = false;
		this.rotateLocal = false;
		this.rotateAroundCenter = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		followThrust = getEditor().getFollowThrustMenuItem().isSelected();
		rotateLocal = getEditor().getRotateLocalMenuItem().isSelected();
		rotateAroundCenter = getEditor().getRotateAroundCenterMenuItem()
				.isSelected();

		JSlider slider;
		float newValue;

		slider = getEditor().getThrustBrightnessSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		thrustBrightness = Maths.blend(newValue, thrustBrightness, 2f * dt);
	}

	@Override
	public ThrustComponentEditor getEditor() {
		return (ThrustComponentEditor) editor;
	}

	public float getThrustBrightness() {
		return thrustBrightness;
	}

	public boolean isFollowThrust() {
		return followThrust;
	}

	@Override
	public boolean isRotateLocal() {
		return rotateLocal;
	}

	@Override
	public boolean isRotateAroundCenter() {
		return rotateAroundCenter;
	}

}
