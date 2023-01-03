package kaba4cow.intersector.renderEngine.shaders.menu;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class StaticFrameShader extends AbstractShader {

	private static StaticFrameShader instance;

	public UniformMat4 transformationMatrix;
	public UniformVec3 color;

	public UniformVec2 mouse;

	private StaticFrameShader() {
		super("menu/staticFrame", true);
	}

	public static StaticFrameShader get() {
		if (instance == null)
			instance = new StaticFrameShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		transformationMatrix = new UniformMat4("transformationMatrix");
		color = new UniformVec3("color");
		mouse = new UniformVec2("mouse");
		storeUniformLocations(transformationMatrix, color, mouse);
	}

}
