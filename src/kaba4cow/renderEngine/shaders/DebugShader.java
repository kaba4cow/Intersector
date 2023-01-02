package kaba4cow.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class DebugShader extends AbstractShader {

	private static DebugShader instance;

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformVec3 color;

	private DebugShader() {
		super("debug", true);
	}

	public static DebugShader get() {
		if (instance == null)
			instance = new DebugShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");

		transformationMatrix = new UniformMat4("transformationMatrix");
		viewMatrix = new UniformMat4("viewMatrix");
		projectionMatrix = new UniformMat4("projectionMatrix");

		color = new UniformVec3("color");

		storeUniformLocations(transformationMatrix, viewMatrix,
				projectionMatrix, color);
	}

	@Override
	public void connectTextureUnits() {

	}

}
