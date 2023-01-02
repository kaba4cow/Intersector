package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class GammaCorrectionShader extends AbstractShader {

	private static GammaCorrectionShader instance;

	public UniformFloat correction;

	private GammaCorrectionShader() {
		super("postProcessing/simple", "postProcessing/gamma", false);
	}

	public static GammaCorrectionShader get() {
		if (instance == null)
			instance = new GammaCorrectionShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		correction = new UniformFloat("correction");
		storeUniformLocations(correction);
	}

}
