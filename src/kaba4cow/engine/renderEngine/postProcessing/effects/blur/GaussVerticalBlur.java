package kaba4cow.engine.renderEngine.postProcessing.effects.blur;

import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.shaders.postProcessing.GaussVerticalBlurShader;

public class GaussVerticalBlur extends PostProcessingEffect {

	public GaussVerticalBlur(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, GaussVerticalBlurShader
				.get());
	}

	@Override
	protected void init(int[] ints) {

	}

	@Override
	public void prepare() {
		getShader().targetHeight.loadValue(height);
	}

	@Override
	public GaussVerticalBlurShader getShader() {
		return (GaussVerticalBlurShader) shader;
	}
}
