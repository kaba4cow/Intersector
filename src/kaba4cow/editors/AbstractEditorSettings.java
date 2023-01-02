package kaba4cow.editors;

public abstract class AbstractEditorSettings {

	protected final AbstractEditor editor;

	private boolean staticLight;
	private boolean renderSkybox;
	private boolean renderWireframe;

	public AbstractEditorSettings(AbstractEditor editor) {
		this.editor = editor;
		this.staticLight = false;
		this.renderSkybox = false;
		this.renderWireframe = false;
	}

	public void update(float dt) {
		staticLight = editor.getStaticLightMenuItem().isSelected();
		renderSkybox = editor.getRenderSkyboxMenuItem().isSelected();
		renderWireframe = editor.getRenderWireframeMenuItem().isSelected();
	}

	public abstract AbstractEditor getEditor();

	public boolean isStaticLight() {
		return staticLight;
	}

	public boolean isRenderSkybox() {
		return renderSkybox;
	}

	public boolean isRenderWireframe() {
		return renderWireframe;
	}

}
