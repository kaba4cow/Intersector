package kaba4cow.renderEngine.shaders.gui;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class FrameShader extends AbstractShader {

	private static FrameShader instance;

	public UniformMat4 transformationMatrix;
	public UniformVec3 color;

	public UniformFloat time;

	private FrameShader() {
		super("gui/frame", true);
	}

	public static FrameShader get() {
		if (instance == null)
			instance = new FrameShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		transformationMatrix = new UniformMat4("transformationMatrix");
		color = new UniformVec3("color");
		time = new UniformFloat("time");
		storeUniformLocations(transformationMatrix, color, time);
	}

}
