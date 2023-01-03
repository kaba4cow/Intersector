package kaba4cow.intersector.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;

public class ShieldShader extends AbstractShader {

	private static ShieldShader instance;

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformSampler diffuseMap;

	public UniformFloat texOffset;
	public UniformFloat brightness;

	private ShieldShader() {
		super("shield", true);
	}

	public static ShieldShader get() {
		if (instance == null)
			instance = new ShieldShader();
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
