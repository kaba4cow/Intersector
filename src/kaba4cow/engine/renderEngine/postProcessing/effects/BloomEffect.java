package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.postProcessing.effects.blur.FastBlur;
import kaba4cow.engine.renderEngine.postProcessing.effects.blur.GaussianBlur;
import kaba4cow.engine.renderEngine.postProcessing.effects.filters.CombineFilter;
import kaba4cow.engine.renderEngine.shaders.AbstractShader;

public class BloomEffect extends PostProcessingEffect {

	private BlendMode greyscale;
	private BlendMode brightFilter1;
	private BlendMode brightFilter2;
	private FastBlur blur1;
	private FastBlur blur2;
	private FastBlur blur3;
	private GaussianBlur blur4;
	private CombineFilter combineFilter;

	public BloomEffect(int targetFboWidth, int targetFboHeight, int divisor) {
		super(targetFboWidth, targetFboHeight, null, divisor);
	}

	@Override
	protected void init(int[] ints) {
		int div = ints[0];
		brightFilter1 = new BlendMode(width / div, height / div, "colorburn")
				.setColor(null);
		brightFilter2 = new BlendMode(width / div, height / div, "multiply")
				.setColor(null);
		blur1 = new FastBlur(width, height, 1 * div).setIntensity(1f);
		blur2 = new FastBlur(width, height, 2 * div).setIntensity(1f);
		blur3 = new FastBlur(width, height, 4 * div).setIntensity(1f);
		greyscale = new BlendMode(width / 4 * div, height / 4 * div,
				"greyscale").setColor(null).setBlendFactor(0.25f);
		blur4 = new GaussianBlur(width, height, 1 * div);
		combineFilter = new CombineFilter(width, height);
	}

	@Override
	public void prepare() {

	}

	@Override
	public PostProcessingEffect render(int texture) {
		brightFilter1.render(texture);
		brightFilter2.render(brightFilter1.getOutputTexture());
		blur1.render(brightFilter2.getOutputTexture());
		blur2.render(blur1.getOutputTexture());
		blur3.render(blur2.getOutputTexture());
		greyscale.render(blur3.getOutputTexture());
		blur4.render(greyscale.getOutputTexture());
		combineFilter.render(texture, blur4.getOutputTexture());
		return this;
	}

	public BloomEffect setBrightFiltering(float filtering) {
		brightFilter1.setBlendFactor(filtering);
		brightFilter2.setBlendFactor(filtering);
		return this;
	}

	public BloomEffect setIntensity(float intensity) {
		combineFilter.setIntensity(intensity);
		return this;
	}

	@Override
	public int getOutputTexture() {
		return combineFilter.getOutputTexture();
	}

	@Override
	public AbstractShader getShader() {
		return null;
	}
}
