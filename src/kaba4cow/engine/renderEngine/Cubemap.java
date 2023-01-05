package kaba4cow.engine.renderEngine;

import kaba4cow.engine.assets.Loaders;

public class Cubemap {

	private static final String[] FILE_NAMES = { "right", "left", "top",
			"bottom", "back", "front" };

	public static final float SCALE = 256f;

	private int texture;
	private int size;

	public Cubemap(int size) {
		this.texture = Loaders.createCubemapTexture(size);
		this.size = size;
	}

	public Cubemap(String name) {
		String[] files = new String[FILE_NAMES.length];
		for (int i = 0; i < files.length; i++)
			files[i] = name + "/" + FILE_NAMES[i];
		this.texture = Loaders.loadCubemapTexture(files);
	}

	public Cubemap(String name, int faces) {
		String[] files = new String[faces];
		for (int i = 0; i < files.length; i++)
			files[i] = name + "/" + i;
		this.texture = Loaders.loadCubemapTexture(files);
	}

	public int getTexture() {
		return texture;
	}

	public void setTexture(int texture) {
		this.texture = texture;
	}

	public int getSize() {
		return size;
	}

}
