package kaba4cow.engine.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class TextShader extends AbstractShader {

	private static TextShader instance;

	public UniformVec3 color;

	public UniformVec2 translation;

	private TextShader() {
		super("text", false);
	}

	public static TextShader get() {
		if (instance == null)
			instance = new TextShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords");

		color = new UniformVec3("color");
		translation = new UniformVec2("translation");

		storeUniformLocations(color, translation);
	}

}
