package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.shaders.postProcessing.BlendModeShader;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Vector3f;

public class BlendMode extends PostProcessingEffect {

	private static final Vector3f INVALID = new Vector3f(-1f, -1f, -1f);

	private Vector3f color = new Vector3f();
	private float blendFactor = 0f;

	public BlendMode(int targetFboWidth, int targetFboHeight,
			String blendModeName) {
		super(targetFboWidth, targetFboHeight, BlendModeShader
				.get(blendModeName));
	}

	@Override
	protected void init(int[] ints) {
		shader.start();
		getShader().connectTextureUnits();
		shader.stop();
	}

	@Override
	public void prepare() {
		getShader().color.loadValue(color);
		getShader().blendFactor.loadValue(blendFactor);
	}

	@Override
	public PostProcessingEffect render(int colorTexture) {
		if (!isEnabled())
			return this;
		shader.start();
		prepare();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		renderer.renderQuad();
		shader.stop();
		return this;
	}

	@Override
	public BlendModeShader getShader() {
		return (BlendModeShader) shader;
	}

	public BlendMode setColor(Vector3f color) {
		if (color == null)
			this.color.set(INVALID);
		else
			this.color.set(color);
		return this;
	}

	public BlendMode setBlendFactor(float blendFactor) {
		this.blendFactor = blendFactor;
		return this;
	}

}
