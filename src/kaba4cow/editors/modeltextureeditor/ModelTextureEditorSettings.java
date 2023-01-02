package kaba4cow.editors.modeltextureeditor;

import kaba4cow.editors.AbstractEditorSettings;

public class ModelTextureEditorSettings extends AbstractEditorSettings {

	public ModelTextureEditorSettings(ModelTextureEditor editor) {
		super(editor);
	}

	@Override
	public void update(float dt) {
		super.update(dt);
	}

	@Override
	public ModelTextureEditor getEditor() {
		return (ModelTextureEditor) editor;
	}

}
