package kaba4cow.editors.componenteditors.collidercomponenteditor;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.editors.componenteditors.ComponentEditorSettings;

public class ColliderComponentEditorSettings extends AbstractEditorSettings
		implements ComponentEditorSettings {

	private boolean followCollider;

	public ColliderComponentEditorSettings(ColliderComponentEditor editor) {
		super(editor);
		this.followCollider = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		followCollider = getEditor().getFollowColliderMenuItem().isSelected();
	}

	@Override
	public ColliderComponentEditor getEditor() {
		return (ColliderComponentEditor) editor;
	}

	public boolean isFollowCollider() {
		return followCollider;
	}

	public void setFollowCollider(boolean followCollider) {
		this.followCollider = followCollider;
	}

	@Override
	public boolean isRotateLocal() {
		return false;
	}

	@Override
	public boolean isRotateAroundCenter() {
		return true;
	}

}
