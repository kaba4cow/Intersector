package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class ContrastShader extends AbstractShader {

	private static ContrastShader instance;

	public UniformFloat contrast;

	private ContrastShader() {
		super("postProcessing/simple", "postProcessing/contrast", false);
	}

	public static ContrastShader get() {
		if (instance == null)
			instance = new ContrastShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		contrast = new UniformFloat("contrast");
		storeUniformLocations(contrast);
	}

}
