package kaba4cow.intersector.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.consts.ConstFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;

public class ThrustShader extends AbstractShader {

	private static ThrustShader instance;

	public static final ConstFloat HEIGHT_SCALE = new ConstFloat(
			"HEIGHT_SCALE", 8f);

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformSampler diffuseMap;

	public UniformFloat texOffset;
	public UniformFloat brightness;

	private ThrustShader() {
		super("thrust", true, HEIGHT_SCALE);
	}

	public static ThrustShader get() {
		if (instance == null)
			instance = new ThrustShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords");

		transformationMatrix = new UniformMat4("transformationMatrix");
		viewMatrix = new UniformMat4("viewMatrix");
		projectionMatrix = new UniformMat4("projectionMatrix");

		diffuseMap = new UniformSampler("diffuseMap");

		texOffset = new UniformFloat("texOffset");
		brightness = new UniformFloat("brightness");

		storeUniformLocations(transformationMatrix, viewMatrix,
				projectionMatrix, diffuseMap, texOffset, brightness);
	}

	@Override
	public void connectTextureUnits() {
		diffuseMap.loadValue(0);
	}

}
