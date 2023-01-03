package kaba4cow.intersector.renderEngine.shaders.menu;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class SliderFrameShader extends AbstractShader {

	private static SliderFrameShader instance;

	public UniformMat4 transformationMatrix;
	public UniformVec3 color;

	public UniformFloat time;
	public UniformVec2 mouse;
	public UniformFloat slider;

	private SliderFrameShader() {
		super("menu/sliderFrame", true);
	}

	public static SliderFrameShader get() {
		if (instance == null)
			instance = new SliderFrameShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
		transformationMatrix = new UniformMat4("transformationMatrix");
		color = new UniformVec3("color");
		time = new UniformFloat("time");
		mouse = new UniformVec2("mouse");
		slider = new UniformFloat("slider");
		storeUniformLocations(transformationMatrix, color, time, mouse, slider);
	}

}
