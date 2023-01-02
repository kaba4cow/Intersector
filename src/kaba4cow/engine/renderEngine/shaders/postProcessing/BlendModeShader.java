package kaba4cow.engine.renderEngine.shaders.postProcessing;

import java.util.HashMap;
import java.util.Map;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.consts.Const;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class BlendModeShader extends AbstractShader {

	private static Map<String, BlendModeShader> instances = new HashMap<String, BlendModeShader>();

	public UniformFloat blendFactor;
	public UniformVec3 color;

	public UniformSampler colorTexture;

	private BlendModeShader(String blendMode) {
		super("postProcessing/simple",
				"postProcessing/blendmodes/" + blendMode, false, Const.LUM);
	}

	public static BlendModeShader get(String blendMode) {
		if (!instances.containsKey(blendMode)) {
			BlendModeShader shader = new BlendModeShader(blendMode);
			instances.put(blendMode, shader);
			return shader;
		}
		return instances.get(blendMode);
	}

	@Override
	protected void init() {
		bindAttributes("position");

		blendFactor = new UniformFloat("blendFactor");
		color = new UniformVec3("color");

		colorTexture = new UniformSampler("colorTexture");

		storeUniformLocations(blendFactor, color, colorTexture);
	}

	public void connectTextureUnits() {
		colorTexture.loadValue(0);
	}

}
