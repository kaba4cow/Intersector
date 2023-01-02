package kaba4cow.engine.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;

public class GUIShader extends AbstractShader {

	private static GUIShader instance;

	public UniformMat4 transformationMatrix;
	public UniformFloat progress;

	private GUIShader() {
		super("gui", false);
	}

	public static GUIShader get() {
		if (instance == null)
			instance = new GUIShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		transformationMatrix = new UniformMat4("transformationMatrix");
		progress = new UniformFloat("progress");
		storeUniformLocations(transformationMatrix, progress);
	}

}
