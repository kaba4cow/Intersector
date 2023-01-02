package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.shaders.postProcessing.WarpShader;

public class WarpEffect extends PostProcessingEffect {

	private float power = 0f;

	public WarpEffect(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, WarpShader.get());
	}

	@Override
	protected void init(int[] ints) {
		setPower(0f);
	}

	@Override
	public void prepare() {
		getShader().power.loadValue(power);
	}

	@Override
	public WarpShader getShader() {
		return (WarpShader) shader;
	}

	public WarpEffect setPower(float power) {
		this.power = power;
		return this;
	}

}
