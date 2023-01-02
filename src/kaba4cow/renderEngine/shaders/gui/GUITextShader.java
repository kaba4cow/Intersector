package kaba4cow.renderEngine.shaders.gui;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class GUITextShader extends AbstractShader {

	private static GUITextShader instance;

	public UniformVec3 color;

	public UniformVec2 translation;

	public UniformFloat time;

	private GUITextShader() {
		super("gui/text", true);
	}

	public static GUITextShader get() {
		if (instance == null)
			instance = new GUITextShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords");
		color = new UniformVec3("color");
		translation = new UniformVec2("translation");
		time = new UniformFloat("time");
		storeUniformLocations(color, translation, time);
	}

}
