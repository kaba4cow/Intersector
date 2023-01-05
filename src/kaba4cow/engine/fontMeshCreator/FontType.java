package kaba4cow.engine.fontMeshCreator;

import kaba4cow.engine.assets.Loaders;
import kaba4cow.engine.renderEngine.models.GUIText;

public class FontType {

	private final String name;

	private final int textureAtlas;
	private final TextMeshLoader loader;

	public FontType(String name) {
		this.name = name;
		this.textureAtlas = Loaders.loadFontTexture(name);
		this.loader = new TextMeshLoader(name);
	}

	public TextMeshData loadText(GUIText text) {
		return loader.createTextMesh(text);
	}

	public int getTextureAtlas() {
		return textureAtlas;
	}

	public String getName() {
		return name;
	}

}
