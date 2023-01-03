package kaba4cow.intersector.renderEngine.shaders.generation;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.consts.ConstInt;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;
import kaba4cow.intersector.toolbox.ColorRamp;

public class TerrainTextureShader extends AbstractShader {

	private static TerrainTextureShader instance;

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformVec3 scale;
	public UniformFloat seed;
	public UniformFloat generation;
	public UniformVec3 info;

	public UniformVec3 elementColor[];
	public UniformVec2 elementInfo[];

	private TerrainTextureShader() {
		super("generation/terrainTexture", true, new ConstInt("ELEMENTS", ColorRamp.MAX_NUM));
	}

	public static TerrainTextureShader get() {
		if (instance == null)
			instance = new TerrainTextureShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords", "normal");

		transformationMatrix = new UniformMat4("transformationMatrix");
		viewMatrix = new UniformMat4("viewMatrix");
		projectionMatrix = new UniformMat4("projectionMatrix");

		scale = new UniformVec3("scale");
		seed = new UniformFloat("seed");
		generation = new UniformFloat("generation");
		info = new UniformVec3("info");

		elementColor = new UniformVec3[ColorRamp.MAX_NUM];
		elementInfo = new UniformVec2[ColorRamp.MAX_NUM];
		for (int i = 0; i < ColorRamp.MAX_NUM; i++) {
			elementColor[i] = new UniformVec3("elementColor[" + i + "]");
			elementInfo[i] = new UniformVec2("elementInfo[" + i + "]");
		}

		storeUniformLocations(transformationMatrix, viewMatrix,
				projectionMatrix, scale, seed, generation, info);
		storeUniformLocations(elementColor, elementInfo);
	}

	@Override
	public void connectTextureUnits() {

	}

}
