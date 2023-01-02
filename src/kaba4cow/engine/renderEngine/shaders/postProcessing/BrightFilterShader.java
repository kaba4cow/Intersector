package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class BrightFilterShader extends AbstractShader {

	private static BrightFilterShader instance;

	public UniformFloat gradient;

	private BrightFilterShader() {
		super("postProcessing/simple", "postProcessing/filters/brightFilter",
				false);
	}

	public static BrightFilterShader get() {
		if (instance == null)
			instance = new BrightFilterShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		gradient = new UniformFloat("gradient");
		storeUniformLocations(gradient);
	}

}
