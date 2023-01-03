package kaba4cow.intersector.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;

public class LaserShader extends AbstractShader {

	private static LaserShader instance;

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformSampler diffuseMap;

	public UniformVec2 texOffset;
	public UniformFloat brightness;

	private LaserShader() {
		super("laser", true);
	}

	public static LaserShader get() {
		if (instance == null)
			instance = new LaserShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords");

		transformationMatrix = new UniformMat4("transformationMatrix");
		viewMatrix = new UniformMat4("viewMatrix");
		projectionMatrix = new UniformMat4("projectionMatrix");

		diffuseMap = new UniformSampler("diffuseMap");

		texOffset = new UniformVec2("texOffset");
		brightness = new UniformFloat("brightness");

		storeUniformLocations(transformationMatrix, viewMatrix,
				projectionMatrix, diffuseMap, texOffset, brightness);
	}

	@Override
	public void connectTextureUnits() {
		diffuseMap.loadValue(0);
	}

}
