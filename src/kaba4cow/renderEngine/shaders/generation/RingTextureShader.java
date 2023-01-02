package kaba4cow.renderEngine.shaders.generation;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class RingTextureShader extends AbstractShader {

	private static RingTextureShader instance;

	public UniformFloat seed;
	public UniformVec3 info;
	public UniformVec3 scale;
	public UniformFloat emission;

	private RingTextureShader() {
		super("generation/ringTexture", true);
	}

	public static RingTextureShader get() {
		if (instance == null)
			instance = new RingTextureShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords");

		seed = new UniformFloat("seed");
		info = new UniformVec3("info");
		scale = new UniformVec3("scale");
		emission = new UniformFloat("emission");

		storeUniformLocations(seed, info, scale, emission);
	}

}
