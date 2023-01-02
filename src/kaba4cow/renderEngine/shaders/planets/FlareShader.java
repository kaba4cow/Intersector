package kaba4cow.renderEngine.shaders.planets;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class FlareShader extends AbstractShader {

	private static FlareShader instance;

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformSampler diffuseMap;

	public UniformVec3 texOffset;

	public UniformVec3 color;
	public UniformFloat brightness;

	private FlareShader() {
		super("planets/flare", true);
	}

	public static FlareShader get() {
		if (instance == null)
			instance = new FlareShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords");

		transformationMatrix = new UniformMat4("transformationMatrix");
		viewMatrix = new UniformMat4("viewMatrix");
		projectionMatrix = new UniformMat4("projectionMatrix");

		diffuseMap = new UniformSampler("diffuseMap");

		texOffset = new UniformVec3("texOffset");

		color = new UniformVec3("color");
		brightness = new UniformFloat("brightness");

		storeUniformLocations(transformationMatrix, viewMatrix,
				projectionMatrix, diffuseMap, texOffset, color, brightness);
	}

	@Override
	public void connectTextureUnits() {
		diffuseMap.loadValue(0);
	}

}
