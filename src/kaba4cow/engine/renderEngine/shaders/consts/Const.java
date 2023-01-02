package kaba4cow.engine.renderEngine.shaders.consts;

import org.lwjgl.util.vector.Vector3f;

public abstract class Const {

	public static ConstVec3 LUM = new ConstVec3("LUM", new Vector3f(0.299f,
			0.587f, 0.114f));

	protected final String type;

	private final String code;

	public Const(String name, String type) {
		this.type = type;
		this.code = "const " + type + " " + name + " = ";
	}

	protected abstract String getDeclaration();

	public String getString() {
		return code + getDeclaration() + ";";
	}

	public String getType() {
		return type;
	}

}
