package kaba4cow.engine.renderEngine.shaders.consts;

public class ConstInt extends Const {

	private final int value;

	public ConstInt(String name, int value) {
		super(name, "int");
		this.value = value;
	}

	@Override
	protected String getDeclaration() {
		return Integer.toString(value);
	}

	public int getValue() {
		return value;
	}

}
