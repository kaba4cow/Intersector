package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.renderEngine.shaders.AbstractShader;

public class DownScaleEffect extends PostProcessingEffect {

	private Bypass bypass;

	public DownScaleEffect(int targetFboWidth, int targetFboHeight, int... ints) {
		super(targetFboWidth, targetFboHeight,
				FrameBufferObject.NEAREST_SAMPLING, null, ints);
	}

	@Override
	protected void init(int[] ints) {
		bypass = new Bypass(width / ints[0], height / ints[0],
				FrameBufferObject.NEAREST_SAMPLING);
	}

	@Override
	public void prepare() {

	}

	@Override
	public PostProcessingEffect render(int texture) {
		prepare();
		bypass.render(texture);
		return this;
	}

	@Override
	public int getOutputTexture() {
		return bypass.getOutputTexture();
	}

	@Override
	public AbstractShader getShader() {
		return null;
	}

}
