package kaba4cow.editors.containergroupeditor;

import javax.swing.JSlider;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.engine.toolbox.maths.Maths;

public class ContainerGroupEditorSettings extends AbstractEditorSettings {

	private boolean scrollTextures;
	private float distance;

	public ContainerGroupEditorSettings(ContainerGroupEditor editor) {
		super(editor);
		this.scrollTextures = false;
		this.distance = 1f;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		scrollTextures = getEditor().getScrollTexturesMenuItem().isSelected();

		ContainerGroupEditor editor = getEditor();
		JSlider slider;
		float newValue;

		slider = editor.getDistanceSlider();
		newValue = Maths.map(slider.getValue(), slider.getMinimum(),
				slider.getMaximum(), 0.5f, 1f);
		distance = Maths.blend(newValue, distance, 2f * dt);
	}

	@Override
	public ContainerGroupEditor getEditor() {
		return (ContainerGroupEditor) editor;
	}

	public boolean isScrollTextures() {
		return scrollTextures;
	}

	public float getDistance() {
		return distance;
	}

}
