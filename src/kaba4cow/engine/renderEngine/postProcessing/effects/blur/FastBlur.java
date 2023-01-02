package kaba4cow.engine.renderEngine.postProcessing.effects.blur;

import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.shaders.postProcessing.FastBlurShader;

public class FastBlur extends PostProcessingEffect {

	private float intensity = 0f;

	public FastBlur(int targetFboWidth, int targetFboHeight, int... ints) {
		super(targetFboWidth / ints[0], targetFboHeight / ints[0],
				FastBlurShader.get(), ints);
	}

	@Override
	protected void init(int[] ints) {
		shader.start();
		shader.stop();
	}

	@Override
	public void prepare() {
		getShader().targetWidth.loadValue(width);
		getShader().targetHeight.loadValue(height);
		getShader().intensity.loadValue(intensity);
	}

	public FastBlur setIntensity(float intensity) {
		this.intensity = intensity;
		return this;
	}

	@Override
	public FastBlurShader getShader() {
		return (FastBlurShader) shader;
	}

}
