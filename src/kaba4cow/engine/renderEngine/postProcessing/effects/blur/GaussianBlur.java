package kaba4cow.engine.renderEngine.postProcessing.effects.blur;

import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.renderEngine.shaders.AbstractShader;

public class GaussianBlur extends PostProcessingEffect {

	private GaussHorizontalBlur horizontalBlur;
	private GaussVerticalBlur verticalBlur;

	public GaussianBlur(int targetFboWidth, int targetFboHeight, int divisor) {
		super(targetFboWidth, targetFboHeight, null, divisor);
	}

	public GaussianBlur(int targetFboWidth, int targetFboHeight) {
		this(targetFboWidth, targetFboHeight, 1);
	}

	@Override
	protected void init(int[] ints) {
		horizontalBlur = new GaussHorizontalBlur(width / ints[0], height
				/ ints[0]);
		verticalBlur = new GaussVerticalBlur(width / ints[0], height / ints[0]);
	}

	@Override
	public void prepare() {

	}

	@Override
	public PostProcessingEffect render(int texture) {
		prepare();
		horizontalBlur.render(texture);
		verticalBlur.render(horizontalBlur.getOutputTexture());
		return this;
	}

	@Override
	public int getOutputTexture() {
		return verticalBlur.getOutputTexture();
	}

	@Override
	public AbstractShader getShader() {
		return null;
	}

}
