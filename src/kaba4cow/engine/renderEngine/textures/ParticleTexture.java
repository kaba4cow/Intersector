package kaba4cow.engine.renderEngine.textures;

public class ParticleTexture extends TextureAtlas {

	public ParticleTexture(int textureID, int numberOfRows) {
		super(textureID, numberOfRows);
		this.additive = true;
	}

	public ParticleTexture(int textureID) {
		this(textureID, 1);
	}

}
