package kaba4cow.engine.renderEngine.postProcessing.effects.filters;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.shaders.postProcessing.CombineShader;

public class CombineFilter extends PostProcessingEffect {

	private float intensity;

	public CombineFilter(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, CombineShader.get());
	}

	@Override
	protected void init(int[] ints) {
		shader.start();
		getShader().connectTextureUnits();
		shader.stop();
	}

	@Override
	public void prepare() {
		getShader().intensity.loadValue(intensity);
	}

	public PostProcessingEffect render(int colorTexture, int highlightTexture) {
		shader.start();
		prepare();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexture);
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, highlightTexture);
		renderer.renderQuad();
		shader.stop();
		return this;
	}

	@Override
	public CombineShader getShader() {
		return (CombineShader) shader;
	}

	public CombineFilter setIntensity(float intensity) {
		this.intensity = intensity;
		return this;
	}

}
