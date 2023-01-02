package kaba4cow.engine.renderEngine.textures;

import org.lwjgl.util.vector.Vector2f;

public class TextureAtlas {

	protected int texture;

	protected int numberOfRows;
	protected boolean transparent;
	protected boolean additive;

	public TextureAtlas(int texture, int numberOfRows) {
		this.texture = texture;
		this.numberOfRows = numberOfRows;
		this.transparent = false;
		this.additive = false;
	}

	public TextureAtlas(int textureID) {
		this(textureID, 1);
	}

	public Vector2f calculateTextureOffset(int index, Vector2f dest) {
		if (dest == null)
			dest = new Vector2f();
		int col = index % numberOfRows;
		int row = index / numberOfRows;
		dest.x = (float) col / (float) numberOfRows;
		dest.y = (float) row / (float) numberOfRows;
		return dest;
	}

	public int getTexture() {
		return texture;
	}

	public TextureAtlas setTexture(int texture) {
		this.texture = texture;
		return this;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public TextureAtlas setTransparent(boolean transparent) {
		this.transparent = transparent;
		return this;
	}

	public boolean isAdditive() {
		return additive;
	}

	public TextureAtlas setAdditive(boolean additive) {
		this.additive = additive;
		return this;
	}

	public int getNumberOfRows() {
		return numberOfRows;
	}

	public TextureAtlas setNumberOfRows(int numberOfRows) {
		this.numberOfRows = numberOfRows;
		return this;
	}

}
