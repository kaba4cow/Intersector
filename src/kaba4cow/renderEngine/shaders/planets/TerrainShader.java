package kaba4cow.renderEngine.shaders.planets;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;
import kaba4cow.engine.toolbox.Shaders;

public class TerrainShader extends AbstractShader {

	private static TerrainShader instance;

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformVec3 lightPosition[];
	public UniformVec3 lightColor[];
	public UniformVec3 lightAttenuation[];
	public UniformVec3 ambientLighting;
	public UniformFloat emission;

	public UniformSampler cubeMap;

	private TerrainShader() {
		super("planets/terrain", true, Shaders.getConstLights());
	}

	public static TerrainShader get() {
		if (instance == null)
			instance = new TerrainShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords", "normal");

		transformationMatrix = new UniformMat4("transformationMatrix");
		viewMatrix = new UniformMat4("viewMatrix");
		projectionMatrix = new UniformMat4("projectionMatrix");

		ambientLighting = new UniformVec3("ambientLighting");
		emission = new UniformFloat("emission");

		cubeMap = new UniformSampler("cubeMap");

		lightPosition = new UniformVec3[Shaders.getLights()];
		lightColor = new UniformVec3[Shaders.getLights()];
		lightAttenuation = new UniformVec3[Shaders.getLights()];
		for (int i = 0; i < Shaders.getLights(); i++) {
			lightPosition[i] = new UniformVec3("lightPosition[" + i + "]");
			lightColor[i] = new UniformVec3("lightColor[" + i + "]");
			lightAttenuation[i] = new UniformVec3("lightAttenuation[" + i + "]");
		}

		storeUniformLocations(transformationMatrix, viewMatrix,
				projectionMatrix, ambientLighting, emission, cubeMap);
		storeUniformLocations(lightPosition, lightColor, lightAttenuation);
	}

	@Override
	public void connectTextureUnits() {
		cubeMap.loadValue(0);
	}

}
