package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.consts.Const;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class SaturationShader extends AbstractShader {

	private static SaturationShader instance;

	public UniformVec3 saturation;

	private SaturationShader() {
		super("postProcessing/simple", "postProcessing/filters/saturation",
				false, Const.LUM);
	}

	public static SaturationShader get() {
		if (instance == null)
			instance = new SaturationShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		saturation = new UniformVec3("saturation");
		storeUniformLocations(saturation);
	}

}
