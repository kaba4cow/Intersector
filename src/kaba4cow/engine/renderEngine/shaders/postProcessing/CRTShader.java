package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;

public class CRTShader extends AbstractShader {

	private static CRTShader instance;

	public UniformFloat targetWidth;
	public UniformFloat targetHeight;
	public UniformFloat curvature;
	public UniformFloat rayFrequency;
	public UniformFloat time;

	private CRTShader() {
		super("postProcessing/simple", "postProcessing/crt", false);
	}

	public static CRTShader get() {
		if (instance == null)
			instance = new CRTShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		targetWidth = new UniformFloat("targetWidth");
		targetHeight = new UniformFloat("targetHeight");
		curvature = new UniformFloat("curvature");
		rayFrequency = new UniformFloat("rayFrequency");
		time = new UniformFloat("time");
		storeUniformLocations(targetWidth, targetHeight, curvature,
				rayFrequency, time);
	}

}
