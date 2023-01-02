package kaba4cow.editors.projectileeditor;

import kaba4cow.editors.AbstractEditorSettings;

public class ProjectileEditorSettings extends AbstractEditorSettings {

	private boolean followThrust;

	public ProjectileEditorSettings(ProjectileEditor editor) {
		super(editor);
		this.followThrust = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);
		
		followThrust = getEditor().getFollowThrustMenuItem().isSelected();
	}

	@Override
	public ProjectileEditor getEditor() {
		return (ProjectileEditor) editor;
	}

	public boolean isFollowThrust() {
		return followThrust;
	}

}
