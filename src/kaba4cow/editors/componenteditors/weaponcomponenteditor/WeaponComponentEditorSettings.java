package kaba4cow.editors.componenteditors.weaponcomponenteditor;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.editors.componenteditors.ComponentEditorSettings;

public class WeaponComponentEditorSettings extends AbstractEditorSettings
		implements ComponentEditorSettings {

	private boolean followWeapon;
	private boolean rotateLocal;
	private boolean rotateAroundCenter;
	private boolean renderTargetInfo;

	public WeaponComponentEditorSettings(WeaponComponentEditor editor) {
		super(editor);
		this.followWeapon = false;
		this.rotateLocal = true;
		this.rotateAroundCenter = false;
		this.renderTargetInfo = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		followWeapon = getEditor().getFollowWeaponMenuItem().isSelected();
		rotateLocal = getEditor().getRotateLocalMenuItem().isSelected();
		rotateAroundCenter = getEditor().getRotateAroundCenterMenuItem()
				.isSelected();
		renderTargetInfo = getEditor().getRenderTargetInfoMenuItem()
				.isSelected();
	}

	@Override
	public WeaponComponentEditor getEditor() {
		return (WeaponComponentEditor) editor;
	}

	public boolean isFollowWeapon() {
		return followWeapon;
	}

	@Override
	public boolean isRotateLocal() {
		return rotateLocal;
	}

	@Override
	public boolean isRotateAroundCenter() {
		return rotateAroundCenter;
	}

	public boolean isRenderTargetInfo() {
		return renderTargetInfo;
	}

}
