package kaba4cow.intersector.renderEngine;

import kaba4cow.engine.renderEngine.models.RawModel;

public class ThrustModel {

	private RawModel rawModel;
	private ThrustTexture texture;

	public ThrustModel(RawModel rawModel, ThrustTexture texture) {
		this.rawModel = rawModel;
		this.texture = texture;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public void setRawModel(RawModel rawModel) {
		this.rawModel = rawModel;
	}

	public ThrustTexture getTexture() {
		return texture;
	}

	public void setTexture(ThrustTexture texture) {
		this.texture = texture;
	}

}
