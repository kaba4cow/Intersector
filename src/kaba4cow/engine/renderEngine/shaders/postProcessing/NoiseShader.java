package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class NoiseShader extends AbstractShader {

	private static NoiseShader instance;

	public UniformFloat time;
	public UniformFloat power;

	private NoiseShader() {
		super("postProcessing/simple", "postProcessing/noise", false);
	}

	public static NoiseShader get() {
		if (instance == null)
			instance = new NoiseShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		time = new UniformFloat("time");
		power = new UniformFloat("power");
		storeUniformLocations(time, power);
	}

}
