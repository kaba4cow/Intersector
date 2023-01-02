package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.shaders.postProcessing.ContrastShader;

public class ContrastEffect extends PostProcessingEffect {

	private float contrast = 0f;

	public ContrastEffect(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, ContrastShader.get());
	}

	@Override
	protected void init(int[] ints) {
		setContrast(0f);
	}

	@Override
	public void prepare() {
		getShader().contrast.loadValue(contrast);
	}

	@Override
	public ContrastShader getShader() {
		return (ContrastShader) shader;
	}

	public ContrastEffect setContrast(float contrast) {
		this.contrast = contrast;
		return this;
	}
}
