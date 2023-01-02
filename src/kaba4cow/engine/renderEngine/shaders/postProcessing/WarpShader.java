package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class WarpShader extends AbstractShader {

	private static WarpShader instance;

	public UniformFloat power;

	private WarpShader() {
		super("postProcessing/simple", "postProcessing/warp", false);
	}

	public static WarpShader get() {
		if (instance == null)
			instance = new WarpShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		power = new UniformFloat("power");
		storeUniformLocations(power);
	}

}
