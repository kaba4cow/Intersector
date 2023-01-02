package kaba4cow.editors.weaponeditor;

import javax.swing.JSlider;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.engine.toolbox.maths.Maths;

public class WeaponEditorSettings extends AbstractEditorSettings {

	private boolean showRotation;
	private boolean followFirePoint;

	private float yaw;
	private float pitch;

	public WeaponEditorSettings(WeaponEditor editor) {
		super(editor);
		this.showRotation = false;
		this.followFirePoint = false;
		this.yaw = 0f;
		this.pitch = 0f;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		showRotation = getEditor().getShowRotationMenuItem().isSelected();
		followFirePoint = getEditor().getFollowFirePointMenuItem().isSelected();

		JSlider slider;
		float newValue;

		slider = getEditor().getYawSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		yaw = Maths.blend(newValue, yaw, 16f * dt);

		slider = getEditor().getPitchSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0f, 1f);
		pitch = Maths.blend(newValue, pitch, 16f * dt);
	}

	@Override
	public WeaponEditor getEditor() {
		return (WeaponEditor) editor;
	}

	public boolean isShowRotation() {
		return showRotation;
	}

	public boolean isFollowFirePoint() {
		return followFirePoint;
	}

	public float getYaw() {
		return yaw;
	}

	public float getPitch() {
		return pitch;
	}

}
