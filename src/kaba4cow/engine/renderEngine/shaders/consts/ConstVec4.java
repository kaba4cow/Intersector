package kaba4cow.engine.renderEngine.shaders.consts;

import org.lwjgl.util.vector.Vector4f;

public class ConstVec4 extends Const {

	private final Vector4f value;

	public ConstVec4(String name, Vector4f value) {
		super(name, "vec4");
		this.value = value;
	}

	@Override
	protected String getDeclaration() {
		return type + "(" + Float.toString(value.x) + ", " + Float.toString(value.y) + ", " + Float.toString(value.z)
				+ ", " + Float.toString(value.w) + ")";
	}

	public Vector4f getValue() {
		return value;
	}

}
