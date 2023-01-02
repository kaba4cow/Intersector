package kaba4cow.renderEngine.models;

import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.renderEngine.textures.ThrustTexture;

public class ThrustModel extends AbstractModel {

	private ThrustTexture texture;

	public ThrustModel(RawModel rawModel, ThrustTexture texture) {
		super(rawModel);
		this.texture = texture;
	}

	public ThrustTexture getTexture() {
		return texture;
	}

	public void setTexture(ThrustTexture texture) {
		this.texture = texture;
	}

}
