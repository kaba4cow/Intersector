package kaba4cow.engine.renderEngine.renderers;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.shaders.AbstractShader;

public abstract class AbstractRenderer {

	protected Renderer renderer;
	protected AbstractShader shader;

	public AbstractRenderer(Renderer renderer, AbstractShader shader) {
		this.setRenderer(renderer);
		this.shader = shader;
	}

	public abstract void process();

	protected abstract void startRendering();

	protected abstract void finishRendering();

	public abstract AbstractShader getShader();

	public Renderer getRenderer() {
		return renderer;
	}

	public void setRenderer(Renderer renderer) {
		if (this.renderer != null)
			this.renderer.removeRenderer(this);
		this.renderer = renderer;
		this.renderer.addRenderer(this);
	}

	public Camera getCamera() {
		return renderer.getCamera();
	}

}
