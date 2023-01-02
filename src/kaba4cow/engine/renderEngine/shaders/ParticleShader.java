package kaba4cow.engine.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class ParticleShader extends AbstractShader {

	private static ParticleShader instance;

	public UniformMat4 projectionMatrix;
	public UniformMat4 modelViewMatrix;

	public UniformVec2 texOffset1;
	public UniformVec2 texOffset2;
	public UniformVec3 texInfo;
	public UniformVec3 tint;

	private ParticleShader() {
		super("particle", false);
	}

	public static ParticleShader get() {
		if (instance == null)
			instance = new ParticleShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");

		projectionMatrix = new UniformMat4("projectionMatrix");
		modelViewMatrix = new UniformMat4("modelViewMatrix");

		texOffset1 = new UniformVec2("texOffset1");
		texOffset2 = new UniformVec2("texOffset2");
		texInfo = new UniformVec3("texInfo");
		tint = new UniformVec3("tint");

		storeUniformLocations(projectionMatrix, modelViewMatrix, texOffset1,
				texOffset2, texInfo, tint);
	}

}
