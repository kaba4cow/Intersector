package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.shaders.postProcessing.CRTShader;

public class CRTEffect extends PostProcessingEffect {

	private float curvature = 0f;
	private float frequency = 0f;
	private float deltaTime = 1f;

	public CRTEffect(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, CRTShader.get());
	}

	@Override
	protected void init(int[] ints) {
		setCurvature(10f);
		setRayFrequency(1f);
		setDeltaTime(1f);
	}

	@Override
	public void prepare() {
		getShader().targetWidth.loadValue(width);
		getShader().targetHeight.loadValue(height);
		getShader().curvature.loadValue(curvature);
		getShader().rayFrequency.loadValue(frequency);
		getShader().time.loadValue(deltaTime * MainProgram.getElapsedTime());
	}

	@Override
	public CRTShader getShader() {
		return (CRTShader) shader;
	}

	public CRTEffect setCurvature(float curvature) {
		this.curvature = curvature;
		return this;
	}

	public CRTEffect setRayFrequency(float frequency) {
		this.frequency = frequency;
		return this;
	}
	
	public CRTEffect setDeltaTime(float deltaTime) {
		this.deltaTime = deltaTime;
		return this;
	}

}
