package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class GaussVerticalBlurShader extends AbstractShader {

	private static GaussVerticalBlurShader instance;

	public UniformFloat targetHeight;

	private GaussVerticalBlurShader() {
		super("postProcessing/blur/gaussVertical", "postProcessing/blur/gauss",
				false);
	}

	public static GaussVerticalBlurShader get() {
		if (instance == null)
			instance = new GaussVerticalBlurShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		targetHeight = new UniformFloat("targetHeight");
		storeUniformLocations(targetHeight);
	}
}
