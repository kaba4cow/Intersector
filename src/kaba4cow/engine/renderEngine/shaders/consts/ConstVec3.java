package kaba4cow.engine.renderEngine.shaders.consts;

import org.lwjgl.util.vector.Vector3f;

public class ConstVec3 extends Const {

	private final Vector3f value;

	public ConstVec3(String name, Vector3f value) {
		super(name, "vec3");
		this.value = value;
	}

	@Override
	protected String getDeclaration() {
		return type + "(" + Float.toString(value.x) + ", " + Float.toString(value.y) + ", " + Float.toString(value.z)
				+ ")";
	}

	public Vector3f getValue() {
		return value;
	}

}
