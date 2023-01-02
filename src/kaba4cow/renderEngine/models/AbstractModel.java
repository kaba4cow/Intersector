package kaba4cow.renderEngine.models;

import kaba4cow.engine.renderEngine.models.RawModel;

public abstract class AbstractModel {

	private RawModel rawModel;

	public AbstractModel(RawModel rawModel) {
		this.rawModel = rawModel;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public void setRawModel(RawModel rawModel) {
		this.rawModel = rawModel;
	}

}
