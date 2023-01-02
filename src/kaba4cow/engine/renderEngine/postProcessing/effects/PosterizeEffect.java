package kaba4cow.engine.renderEngine.postProcessing.effects;

import kaba4cow.engine.renderEngine.shaders.postProcessing.PosterizeShader;

public class PosterizeEffect extends PostProcessingEffect {

	private float levels = 0f;

	public PosterizeEffect(int targetFboWidth, int targetFboHeight, int levels) {
		super(targetFboWidth, targetFboHeight, PosterizeShader.get(),
				levels);
	}

	@Override
	protected void init(int[] ints) {
		setLevels(ints[0]);
	}

	@Override
	public void prepare() {
		getShader().levels.loadValue(levels);
	}

	@Override
	public PosterizeShader getShader() {
		return (PosterizeShader) shader;
	}

	public PosterizeEffect setLevels(float levels) {
		this.levels = levels;
		return this;
	}

}
