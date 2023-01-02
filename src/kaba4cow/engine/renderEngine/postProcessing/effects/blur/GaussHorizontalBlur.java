package kaba4cow.engine.renderEngine.postProcessing.effects.blur;

import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.shaders.postProcessing.GaussHorizontalBlurShader;

public class GaussHorizontalBlur extends PostProcessingEffect {

	public GaussHorizontalBlur(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, GaussHorizontalBlurShader
				.get());
	}

	@Override
	protected void init(int[] ints) {
	}

	@Override
	public void prepare() {
		getShader().targetWidth.loadValue(width);
	}

	@Override
	public GaussHorizontalBlurShader getShader() {
		return (GaussHorizontalBlurShader) shader;
	}

}
