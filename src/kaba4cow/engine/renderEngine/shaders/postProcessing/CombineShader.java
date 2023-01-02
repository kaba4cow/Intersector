package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;

public class CombineShader extends AbstractShader {

	private static CombineShader instance;

	public UniformFloat intensity;

	public UniformSampler colorTexture1;
	public UniformSampler colorTexture2;

	private CombineShader() {
		super("postProcessing/simple", "postProcessing/filters/combine", false);
	}

	public static CombineShader get() {
		if (instance == null)
			instance = new CombineShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");

		intensity = new UniformFloat("intensity");

		colorTexture1 = new UniformSampler("colorTexture1");
		colorTexture2 = new UniformSampler("colorTexture2");

		storeUniformLocations(intensity, colorTexture1, colorTexture2);
	}

	public void connectTextureUnits() {
		colorTexture1.loadValue(0);
		colorTexture2.loadValue(1);
	}

}
