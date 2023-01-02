package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.shaders.postProcessing.GammaCorrectionShader;

public class GammaCorrection extends PostProcessingEffect {

	private float correction = 0f;

	public GammaCorrection(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, GammaCorrectionShader.get());
	}

	@Override
	protected void init(int[] ints) {
		setCorrection(1f);
	}

	@Override
	public void prepare() {
		getShader().correction.loadValue(correction);
	}

	@Override
	public GammaCorrectionShader getShader() {
		return (GammaCorrectionShader) shader;
	}

	public GammaCorrection setCorrection(float correction) {
		this.correction = correction;
		return this;
	}

}
