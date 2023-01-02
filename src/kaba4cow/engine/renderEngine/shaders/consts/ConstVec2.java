package kaba4cow.engine.renderEngine.shaders.consts;

import org.lwjgl.util.vector.Vector2f;

public class ConstVec2 extends Const {

	private final Vector2f value;

	public ConstVec2(String name, Vector2f value) {
		super(name, "vec2");
		this.value = value;
	}

	@Override
	protected String getDeclaration() {
		return type + "(" + Float.toString(value.x) + ", " + Float.toString(value.y) + ")";
	}

	public Vector2f getValue() {
		return value;
	}

}
