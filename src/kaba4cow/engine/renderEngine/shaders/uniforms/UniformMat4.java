package kaba4cow.engine.renderEngine.shaders.uniforms;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;

public class UniformMat4 extends Uniform {

	private static FloatBuffer buffer = BufferUtils.createFloatBuffer(16);

	public UniformMat4(String name) {
		super(name);
	}

	public void loadValue(Matrix4f value) {
		value.store(buffer);
		buffer.flip();
		GL20.glUniformMatrix4(getLocation(), false, buffer);
	}

}
