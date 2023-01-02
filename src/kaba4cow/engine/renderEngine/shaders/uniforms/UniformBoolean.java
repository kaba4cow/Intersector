package kaba4cow.engine.renderEngine.shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class UniformBoolean extends Uniform {

	public UniformBoolean(String name) {
		super(name);
	}

	public void loadValue(boolean value) {
		GL20.glUniform1f(getLocation(), value ? 1f : 0f);
	}

}
