package kaba4cow.engine.renderEngine.shaders.postProcessing;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;

public class BypassShader extends AbstractShader {

	private static BypassShader instance;

	private BypassShader() {
		super("postProcessing/simple", false);
	}

	public static BypassShader get() {
		if (instance == null)
			instance = new BypassShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position");
	}

}
