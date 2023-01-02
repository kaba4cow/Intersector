package kaba4cow.engine.renderEngine.textures;

import org.lwjgl.util.vector.Vector2f;

public class GUITexture extends TextureAtlas {

	private Vector2f position;
	private Vector2f scale;

	private float progress;

	private boolean centered;

	public GUITexture(int textureID, Vector2f position, Vector2f scale,
			boolean centered) {
		super(textureID);
		this.position = position;
		this.scale = scale == null ? new Vector2f(1f, 1f) : scale;
		this.centered = centered;
		this.progress = 1f;
	}

	public GUITexture(int textureID, Vector2f position, Vector2f scale) {
		this(textureID, position, scale, false);
	}

	public Vector2f getPosition() {
		return position;
	}

	public GUITexture setPosition(Vector2f position) {
		this.position = position;
		return this;
	}

	public Vector2f getScale() {
		return scale;
	}

	public GUITexture setScale(Vector2f scale) {
		this.scale = scale == null ? new Vector2f(1f, 1f) : scale;
		return this;
	}

	public boolean isCentered() {
		return centered;
	}

	public GUITexture setCentered(boolean centered) {
		this.centered = centered;
		return this;
	}

	public float getProgress() {
		return progress;
	}

	public GUITexture setProgress(float progress) {
		this.progress = progress;
		return this;
	}

}
