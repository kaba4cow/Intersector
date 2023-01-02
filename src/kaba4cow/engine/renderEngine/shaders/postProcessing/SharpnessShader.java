package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class SharpnessShader extends AbstractShader {

	private static SharpnessShader instance;

	public UniformFloat targetWidth;
	public UniformFloat targetHeight;
	public UniformFloat sharpness;

	private SharpnessShader() {
		super("postProcessing/simple", "postProcessing/filters/sharpness", false);
	}

	public static SharpnessShader get() {
		if (instance == null)
			instance = new SharpnessShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		targetWidth = new UniformFloat("targetWidth");
		targetHeight = new UniformFloat("targetHeight");
		sharpness = new UniformFloat("sharpness");
		storeUniformLocations(targetWidth, targetHeight, sharpness);
	}

}
