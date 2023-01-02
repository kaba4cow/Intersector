package kaba4cow.engine.renderEngine.shaders.uniforms;

import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;

public class UniformVec3 extends Uniform {

	public UniformVec3(String name) {
		super(name);
	}

	public void loadValue(Vector3f value) {
		GL20.glUniform3f(getLocation(), value.x, value.y, value.z);
	}

	public void loadValue(float x, float y, float z) {
		GL20.glUniform3f(getLocation(), x, y, z);
	}

}
