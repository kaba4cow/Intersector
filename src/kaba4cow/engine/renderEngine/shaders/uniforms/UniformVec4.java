package kaba4cow.engine.renderEngine.shaders.uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector4f;

public class UniformVec4 extends Uniform {

	public UniformVec4(String name) {
		super(name);
	}

	public void loadValue(Vector4f value) {
		GL20.glUniform4f(getLocation(), value.x, value.y, value.z, value.w);
	}

	public void loadValue(float x, float y, float z, float w) {
		GL20.glUniform4f(getLocation(), x, y, z, w);
	}

}
