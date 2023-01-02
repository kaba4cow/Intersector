package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class ExposureShader extends AbstractShader {

	private static ExposureShader instance;

	public UniformFloat exposure;

	private ExposureShader() {
		super("postProcessing/simple", "postProcessing/filters/exposure", false);
	}

	public static ExposureShader get() {
		if (instance == null)
			instance = new ExposureShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		exposure = new UniformFloat("exposure");
		storeUniformLocations(exposure);
	}

}
