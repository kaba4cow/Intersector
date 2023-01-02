package kaba4cow.editors.componenteditors.portcomponenteditor;

import kaba4cow.editors.AbstractEditorSettings;
import kaba4cow.editors.componenteditors.ComponentEditorSettings;

public class PortComponentEditorSettings extends AbstractEditorSettings
		implements ComponentEditorSettings {

	private boolean followPort;
	private boolean rotateAroundCenter;

	public PortComponentEditorSettings(PortComponentEditor editor) {
		super(editor);
		this.followPort = false;
		this.rotateAroundCenter = false;
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		followPort = getEditor().getFollowPortMenuItem().isSelected();
		rotateAroundCenter = getEditor().getRotateAroundCenterMenuItem()
				.isSelected();
	}

	@Override
	public PortComponentEditor getEditor() {
		return (PortComponentEditor) editor;
	}

	public boolean isFollowPort() {
		return followPort;
	}

	public void setFollowPort(boolean followPort) {
		this.followPort = followPort;
	}

	@Override
	public boolean isRotateLocal() {
		return false;
	}

	@Override
	public boolean isRotateAroundCenter() {
		return rotateAroundCenter;
	}

}
