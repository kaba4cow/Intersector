package kaba4cow.engine.renderEngine;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Vectors;

public class Light {

	public static final Light INVALID = new Light(Vectors.INIT3, Vectors.INIT3,
			Vectors.INIT3);

	private Vector3f pos;
	private Vector3f color;
	private Vector3f attenuation;

	public Light(Vector3f pos, Vector3f color, Vector3f attenuation) {
		this.pos = pos;
		this.color = color;
		this.attenuation = attenuation;
	}

	public Light(Vector3f position, Vector3f color) {
		this(position, color, new Vector3f(1f, 0f, 0f));
	}

	public Light(Light light) {
		this(new Vector3f(light.pos), new Vector3f(light.color), new Vector3f(
				light.attenuation));
	}

	public Vector3f getPos() {
		return pos;
	}

	public Light setPos(Vector3f position) {
		this.pos = position;
		return this;
	}

	public Vector3f getColor() {
		return color;
	}

	public Light setColor(Vector3f color) {
		this.color = color;
		return this;
	}

	public Vector3f getAttenuation() {
		return attenuation;
	}

	public Light setAttenuation(Vector3f attenuation) {
		this.attenuation = attenuation;
		return this;
	}

}
