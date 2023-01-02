package kaba4cow.editors.planetterraineditor;

import kaba4cow.editors.AbstractEditorSettings;

public class PlanetTerrainEditorSettings extends AbstractEditorSettings {

	public PlanetTerrainEditorSettings(PlanetTerrainEditor editor) {
		super(editor);
	}

	@Override
	public PlanetTerrainEditor getEditor() {
		return (PlanetTerrainEditor) editor;
	}

}
