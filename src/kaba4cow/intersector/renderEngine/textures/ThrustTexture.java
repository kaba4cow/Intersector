package kaba4cow.intersector.renderEngine.textures;

import kaba4cow.engine.renderEngine.textures.TextureAtlas;

public class ThrustTexture extends TextureAtlas {

	private float speed;
	private String sound;

	public ThrustTexture(int textureID) {
		super(textureID);
		this.transparent = true;
		this.speed = 0.5f;
		this.sound = "";
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

}
