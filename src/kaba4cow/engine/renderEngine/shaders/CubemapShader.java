package kaba4cow.engine.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;

public class CubemapShader extends AbstractShader {

	private static CubemapShader instance;

	public UniformMat4 projectionMatrix;
	public UniformMat4 viewMatrix;

	private CubemapShader() {
		super("skybox", false);
	}

	public static CubemapShader get() {
		if (instance == null)
			instance = new CubemapShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");

		projectionMatrix = new UniformMat4("projectionMatrix");
		viewMatrix = new UniformMat4("viewMatrix");

		storeUniformLocations(projectionMatrix, viewMatrix);
	}

}
