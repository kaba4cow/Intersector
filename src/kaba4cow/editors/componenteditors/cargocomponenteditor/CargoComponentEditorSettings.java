package kaba4cow.editors.componenteditors.cargocomponenteditor;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.editors.componenteditors.ComponentEditorSettings;

public class CargoComponentEditorSettings extends AbstractEditorSettings
		implements ComponentEditorSettings {

	private boolean followCargo;
	private boolean rotateLocal;
	private boolean rotateAroundCenter;

	public CargoComponentEditorSettings(CargoComponentEditor editor) {
		super(editor);
		this.followCargo = false;
		this.rotateLocal = false;
		this.rotateAroundCenter = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		followCargo = getEditor().getFollowCargoMenuItem().isSelected();
		rotateLocal = getEditor().getRotateLocalMenuItem().isSelected();
		rotateAroundCenter = getEditor().getRotateAroundCenterMenuItem()
				.isSelected();
	}

	@Override
	public CargoComponentEditor getEditor() {
		return (CargoComponentEditor) editor;
	}

	public boolean isFollowCargo() {
		return followCargo;
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
