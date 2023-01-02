package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class FastBlurShader extends AbstractShader {

	private static FastBlurShader instance;

	public UniformFloat targetWidth;
	public UniformFloat targetHeight;
	public UniformFloat intensity;

	private FastBlurShader() {
		super("postProcessing/simple", "postProcessing/blur/fast", false);
	}

	public static FastBlurShader get() {
		if (instance == null)
			instance = new FastBlurShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		targetWidth = new UniformFloat("targetWidth");
		targetHeight = new UniformFloat("targetHeight");
		intensity = new UniformFloat("intensity");
		storeUniformLocations(targetWidth, targetHeight, intensity);
	}

}
