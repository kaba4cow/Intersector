package kaba4cow.editors.stationeditor;

import kaba4cow.editors.AbstractEditorSettings;

public class StationEditorSettings extends AbstractEditorSettings {

	public StationEditorSettings(StationEditor editor) {
		super(editor);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
	}

	@Override
	public StationEditor getEditor() {
		return (StationEditor) editor;
	}

}
