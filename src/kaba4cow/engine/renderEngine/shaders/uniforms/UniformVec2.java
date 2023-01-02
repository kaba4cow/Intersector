package kaba4cow.engine.renderEngine.shaders.uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector2f;

public class UniformVec2 extends Uniform {

	public UniformVec2(String name) {
		super(name);
	}

	public void loadValue(Vector2f value) {
		GL20.glUniform2f(getLocation(), value.x, value.y);
	}

	public void loadValue(float x, float y) {
		GL20.glUniform2f(getLocation(), x, y);
	}

}
