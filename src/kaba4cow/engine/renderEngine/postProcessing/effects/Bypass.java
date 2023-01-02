package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.shaders.postProcessing.BypassShader;

public class Bypass extends PostProcessingEffect {

	public Bypass(int targetFboWidth, int targetFboHeight, int sampling) {
		super(targetFboWidth, targetFboHeight, sampling, BypassShader
				.get());
	}

	public Bypass() {
		super(BypassShader.get());
	}

	@Override
	public void prepare() {

	}

	@Override
	protected void init(int[] ints) {

	}

	@Override
	public BypassShader getShader() {
		return (BypassShader) shader;
	}

}
