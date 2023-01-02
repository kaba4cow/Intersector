package kaba4cow.engine.toolbox;

import kaba4cow.engine.renderEngine.shaders.consts.ConstInt;

public class Shaders {

	private static ConstInt constLights;
	private static int constLightsValue;

	public static void setLights(int lights) {
		constLights = new ConstInt("LIGHTS", lights);
		constLightsValue = lights;
	}

	public static ConstInt getConstLights() {
		return constLights;
	}

	public static int getLights() {
		return constLightsValue;
	}

}
