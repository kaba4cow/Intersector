package kaba4cow.engine.renderEngine.postProcessing.effects.filters;

import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.shaders.postProcessing.SaturationShader;

import org.lwjgl.util.vector.Vector3f;

public class SaturationFilter extends PostProcessingEffect {

	private Vector3f saturation;

	public SaturationFilter(int targetFboWidth, int targetFboHeight) {
		super(targetFboWidth, targetFboHeight, SaturationShader.get());
	}

	@Override
	protected void init(int[] ints) {
		saturation = new Vector3f();
		setSaturation(0f);
	}

	@Override
	public void prepare() {
		getShader().saturation.loadValue(saturation.x, saturation.y,
				saturation.z);
	}

	@Override
	public SaturationShader getShader() {
		return (SaturationShader) shader;
	}

	public SaturationFilter setSaturation(float saturation) {
		this.saturation.set(saturation, saturation, saturation);
		return this;
	}

	public SaturationFilter setSaturation(float saturationRed,
			float saturationGreen, float saturationBlue) {
		this.saturation.set(saturationRed, saturationGreen, saturationBlue);
		return this;
	}

}
