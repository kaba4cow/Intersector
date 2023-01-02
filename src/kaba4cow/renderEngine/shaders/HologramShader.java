package kaba4cow.renderEngine.shaders;

import kaba4cow.engine.renderEngine.shaders.AbstractShader;
import kaba4cow.engine.renderEngine.shaders.consts.Const;
import kaba4cow.engine.renderEngine.shaders.consts.ConstFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformFloat;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformMat4;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformSampler;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec2;
import kaba4cow.engine.renderEngine.shaders.uniforms.UniformVec3;

public class HologramShader extends AbstractShader {

	private static HologramShader instance;

	public static final ConstFloat HEIGHT_SCALE = new ConstFloat(
			"HEIGHT_SCALE", 0.5f);

	public UniformMat4 transformationMatrix;
	public UniformMat4 viewMatrix;
	public UniformMat4 projectionMatrix;

	public UniformVec2 texOffset;
	public UniformFloat time;
	public UniformFloat scale;
	public UniformVec3 color;
	public UniformFloat brightness;

	public UniformFloat usesTexture;

	public UniformSampler colorTexture;
	public UniformSampler colorCube;
	public UniformSampler hologramTexture;

	private HologramShader() {
		super("hologram", true, HEIGHT_SCALE, Const.LUM);
	}

	public static HologramShader get() {
		if (instance == null)
			instance = new HologramShader();
		return instance;
	}

	@Override
	protected void init() {
		bindAttributes("position", "textureCoords");

		transformationMatrix = new UniformMat4("transformationMatrix");
		viewMatrix = new UniformMat4("viewMatrix");
		projectionMatrix = new UniformMat4("projectionMatrix");

		usesTexture = new UniformFloat("usesTexture");

		colorTexture = new UniformSampler("colorTexture");
		colorCube = new UniformSampler("colorCube");
		hologramTexture = new UniformSampler("hologramTexture");

		texOffset = new UniformVec2("texOffset");
		time = new UniformFloat("time");
		scale = new UniformFloat("scale");
		color = new UniformVec3("color");
		brightness = new UniformFloat("brightness");

		storeUniformLocations(transformationMatrix, viewMatrix,
				projectionMatrix, usesTexture, colorTexture, colorCube,
				hologramTexture, texOffset, time, scale, color, brightness);
	}

	@Override
	public void connectTextureUnits() {
		colorTexture.loadValue(0);
		colorCube.loadValue(1);
		hologramTexture.loadValue(2);
	}

}
