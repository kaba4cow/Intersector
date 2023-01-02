package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class GaussHorizontalBlurShader extends AbstractShader {

	private static GaussHorizontalBlurShader instance;

	public UniformFloat targetWidth;

	private GaussHorizontalBlurShader() {
		super("postProcessing/blur/gaussHorizontal",
				"postProcessing/blur/gauss", false);
	}

	public static GaussHorizontalBlurShader get() {
		if (instance == null)
			instance = new GaussHorizontalBlurShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		targetWidth = new UniformFloat("targetWidth");
		storeUniformLocations(targetWidth);
	}

}
