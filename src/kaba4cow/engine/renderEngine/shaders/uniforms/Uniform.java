package kaba4cow.engine.renderEngine.shaders.uniforms;

import org.lwjgl.opengl.GL20;

public class Uniform {

	private String name;
	private int location;

	public Uniform(String name) {
		this.name = name;
	}

	public void storeLocation(int shader) {
		location = GL20.glGetUniformLocation(shader, name);
		if (location == -1)
			System.err.println("Uniform variable \"" + name + "\" not found");
	}

	public String getName() {
		return name;
	}

	public int getLocation() {
		return location;
	}

}
