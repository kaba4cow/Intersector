package kaba4cow.engine.renderEngine.shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class UniformFloat extends Uniform {

	public UniformFloat(String name) {
		super(name);
	}

	public void loadValue(float value) {
		GL20.glUniform1f(getLocation(), value);
	}

}
