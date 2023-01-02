package kaba4cow.engine.renderEngine.shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class UniformSampler extends Uniform {

	public UniformSampler(String name) {
		super(name);
	}

	public void loadValue(int value) {
		GL20.glUniform1i(getLocation(), value);
	}

}
