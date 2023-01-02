package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.shaders.postProcessing.NoiseShader;

public class NoiseEffect extends PostProcessingEffect {

	private float power = 0f;

	public NoiseEffect(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, NoiseShader.get());
	}

	@Override
	protected void init(int[] ints) {
		setPower(0.1f);
	}

	@Override
	public void prepare() {
		getShader().time.loadValue(MainProgram.getElapsedTime());
		getShader().power.loadValue(power);
	}

	@Override
	public NoiseShader getShader() {
		return (NoiseShader) shader;
	}

	public NoiseEffect setPower(float power) {
		this.power = power;
		return this;
	}

}
