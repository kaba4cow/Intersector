package kaba4cow.engine.renderEngine.shaders.consts;

public class ConstFloat extends Const {

	private final float value;

	public ConstFloat(String name, float value) {
		super(name, "float");
		this.value = value;
	}

	@Override
	protected String getDeclaration() {
		return Float.toString(value);
	}

	public float getValue() {
		return value;
	}

}
