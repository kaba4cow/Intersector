package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.renderEngine.postProcessing.ImageRenderer;
import kaba4cow.engine.renderEngine.shaders.AbstractShader;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public abstract class PostProcessingEffect {

	protected final int width;
	protected final int height;

	protected ImageRenderer renderer;
	protected AbstractShader shader;

	private boolean enabled;

	public PostProcessingEffect(int targetFboWidth, int targetFboHeight,
			int sampling, AbstractShader shader, int... ints) {
		this.width = targetFboWidth;
		this.height = targetFboHeight;
		this.renderer = new ImageRenderer(targetFboWidth, targetFboHeight,
				sampling);
		this.shader = shader;
		this.enabled = true;
		this.init(ints);
	}

	public PostProcessingEffect(int targetFboWidth, int targetFboHeight,
			AbstractShader shader, int... ints) {
		this(targetFboWidth, targetFboHeight,
				FrameBufferObject.LINEAR_SAMPLING, shader, ints);
	}

	public PostProcessingEffect(AbstractShader shader, int... ints) {
		this.width = Display.getWidth();
		this.height = Display.getHeight();
		this.renderer = new ImageRenderer();
		this.shader = shader;
		this.enabled = true;
		this.init(ints);
	}

	protected abstract void init(int[] ints);

	public abstract void prepare();

	public PostProcessingEffect render(int texture) {
		if (!enabled)
			return this;
		shader.start();
		prepare();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		renderer.renderQuad();
		shader.stop();
		return this;
	}

	public int getOutputTexture() {
		return renderer.getOutputTexture();
	}

	public abstract AbstractShader getShader();

	public void switchEnable() {
		enabled = !enabled;
	}

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

	public boolean isEnabled() {
		return enabled;
	}

}
