package kaba4cow.editors.weaponscaleeditor;

import javax.swing.JSlider;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.engine.toolbox.maths.Maths;

public class WeaponScaleEditorSettings extends AbstractEditorSettings {

	private float distance;
	private boolean sortDamage;

	public WeaponScaleEditorSettings(WeaponScaleEditor editor) {
		super(editor);
		this.distance = 1f;
		this.sortDamage = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		sortDamage = getEditor().getSortByDamageMenuItem().isSelected();

		JSlider slider;
		float newValue;

		slider = getEditor().getDistanceSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0.5f, 1.5f);
		distance = Maths.blend(newValue, distance, 2f * dt);
	}

	@Override
	public WeaponScaleEditor getEditor() {
		return (WeaponScaleEditor) editor;
	}

	public float getDistance() {
		return distance;
	}

	public boolean isSortDamage() {
		return sortDamage;
	}

}
