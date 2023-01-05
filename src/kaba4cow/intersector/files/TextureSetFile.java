package kaba4cow.intersector.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.files.DataFile;

public class TextureSetFile extends GameFile {

	protected static final String LOCATION = "texturesets/";

	private static final Map<String, TextureSetFile> map = new HashMap<String, TextureSetFile>();
	private static final List<TextureSetFile> list = new ArrayList<TextureSetFile>();

	private String metalTexture;
	private String glassTexture;
	private String lightTexture;

	private TextureSetFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		metalTexture = data.node("metal").getString();
		glassTexture = data.node("glass").getString();
		lightTexture = data.node("light").getString();
	}

	@Override
	public void preparePostInit() {

	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("metal").setString(metalTexture);
		node.node("glass").setString(glassTexture);
		node.node("light").setString(lightTexture);

		super.save();
	}

	public String getMetalTexture() {
		return metalTexture;
	}

	public void setMetalTexture(String metalTexture) {
		this.metalTexture = metalTexture;
	}

	public String getGlassTexture() {
		return glassTexture;
	}

	public void setGlassTexture(String glassTexture) {
		this.glassTexture = glassTexture;
	}

	public String getLightTexture() {
		return lightTexture;
	}

	public void setLightTexture(String lightTexture) {
		this.lightTexture = lightTexture;
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public static TextureSetFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		TextureSetFile file = new TextureSetFile(name);
		map.put(name, file);
		list.add(file);
		return file;
	}

	public static void load(List<String> names) {
		for (int i = 0; i < names.size(); i++)
			load(names.get(i));
	}

	public static void load(String... names) {
		if (names != null)
			for (int i = 0; i < names.length; i++)
				load(names[i]);
	}

	public static TextureSetFile get(String name) {
		TextureSetFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<TextureSetFile> getList() {
		return list;
	}

}
