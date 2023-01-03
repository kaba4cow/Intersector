package kaba4cow.intersector.renderEngine.models;

import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.textures.TextureAtlas;

public class LaserModel extends AbstractModel {

	private TextureAtlas texture;

	public LaserModel(RawModel rawModel, TextureAtlas texture) {
		super(rawModel);
		this.texture = texture;
	}

	public TextureAtlas getTexture() {
		return texture;
	}

	public void setTexture(TextureAtlas texture) {
		this.texture = texture;
	}

}
