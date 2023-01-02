package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class PosterizeShader extends AbstractShader {

	private static PosterizeShader instance;

	public UniformFloat levels;

	private PosterizeShader() {
		super("postProcessing/simple", "postProcessing/posterize", false);
	}

	public static PosterizeShader get() {
		if (instance == null)
			instance = new PosterizeShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		levels = new UniformFloat("levels");
		storeUniformLocations(levels);
	}

}
