package kaba4cow.engine.renderEngine.models;

import kaba4cow.engine.renderEngine.textures.ModelTexture;

public class TexturedModel implements ModelContainer {

	private RawModel rawModel;
	private ModelTexture texture;

	public TexturedModel(RawModel rawModel, ModelTexture texture) {
		this.rawModel = rawModel;
		this.texture = texture;
	}

	@Override
	public RawModel getRawModel() {
		return rawModel;
	}

	@Override
	public void setRawModel(RawModel rawModel) {
		this.rawModel = rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	public void setTexture(ModelTexture texture) {
		this.texture = texture;
	}

}
