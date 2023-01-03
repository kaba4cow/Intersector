package kaba4cow.intersector.renderEngine.shaders.planets;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;
import kaba4cow.engine.toolbox.Shaders;

public class RingShader extends AbstractShader {

	private static RingShader instance;

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 invViewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformVec3 lightPosition[];
	public UniformVec3 lightColor[];
	public UniformVec3 lightAttenuation[];
	public UniformVec3 ambientLighting;

	public UniformVec3 cameraPosition;

	public UniformSampler diffuseMap;

	public UniformVec3 color;
	public UniformVec2 texInfo;

	private RingShader() {
		super("planets/ring", true, Shaders.getConstLights());
	}

	public static RingShader get() {
		if (instance == null)
			instance = new RingShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords", "normal");

		transformationMatrix = new UniformMat4("transformationMatrix");
		viewMatrix = new UniformMat4("viewMatrix");
		invViewMatrix = new UniformMat4("invViewMatrix");
		projectionMatrix = new UniformMat4("projectionMatrix");

		ambientLighting = new UniformVec3("ambientLighting");

		cameraPosition = new UniformVec3("cameraPosition");

		diffuseMap = new UniformSampler("diffuseMap");

		color = new UniformVec3("color");
		texInfo = new UniformVec2("texInfo");

		lightPosition = new UniformVec3[Shaders.getLights()];
		lightColor = new UniformVec3[Shaders.getLights()];
		lightAttenuation = new UniformVec3[Shaders.getLights()];
		for (int i = 0; i < Shaders.getLights(); i++) {
			lightPosition[i] = new UniformVec3("lightPosition[" + i + "]");
			lightColor[i] = new UniformVec3("lightColor[" + i + "]");
			lightAttenuation[i] = new UniformVec3("lightAttenuation[" + i + "]");
		}

		storeUniformLocations(transformationMatrix, viewMatrix, invViewMatrix,
				projectionMatrix, ambientLighting, cameraPosition, diffuseMap,
				color, texInfo);
		storeUniformLocations(lightPosition, lightColor, lightAttenuation);
	}

	@Override
	public void connectTextureUnits() {
		diffuseMap.loadValue(0);
	}

}
