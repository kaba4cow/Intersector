package kaba4cow.engine.renderEngine.postProcessing.effects.filters;

import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.shaders.postProcessing.SharpnessShader;

public class SharpnessFilter extends PostProcessingEffect {

	private float sharpness;

	public SharpnessFilter(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, SharpnessShader.get());
	}

	@Override
	protected void init(int[] ints) {
		setSharpness(0f);
	}

	@Override
	public void prepare() {
		getShader().targetWidth.loadValue(width);
		getShader().targetHeight.loadValue(height);
		getShader().sharpness.loadValue(sharpness);
	}

	@Override
	public SharpnessShader getShader() {
		return (SharpnessShader) shader;
	}

	public SharpnessFilter setSharpness(float sharpness) {
		this.sharpness = sharpness;
		return this;
	}

}
