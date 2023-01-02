package kaba4cow.engine.renderEngine.postProcessing.effects.filters;

import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.shaders.postProcessing.ExposureShader;

public class ExposureFilter extends PostProcessingEffect {

	private float exposure;

	public ExposureFilter(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, ExposureShader.get());
	}

	@Override
	protected void init(int[] ints) {
		setExposure(1f);
	}

	@Override
	public void prepare() {
		getShader().exposure.loadValue(exposure);
	}

	@Override
	public ExposureShader getShader() {
		return (ExposureShader) shader;
	}

	public ExposureFilter setExposure(float exposure) {
		this.exposure = exposure;
		return this;
	}

}
