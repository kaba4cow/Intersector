package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.toolbox.RawModelContainer;

public class ContainerFile extends GameFile {

	private static final String LOCATION = "containers/";

	private static final Map<String, ContainerFile> map = new HashMap<String, ContainerFile>();
	private static final List<ContainerFile> list = new ArrayList<ContainerFile>();

	private String model;
	private String[] textures;

	private TexturedModel[] models;

	private ContainerFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		model = node.node("model").getString();
		textures = node.node("textures").toStringArray();
	}
	
	@Override
	public void preparePostInit() {
		models = new TexturedModel[textures.length];
		for (int i = 0; i < models.length; i++)
			models[i] = createTexturedModel(i);
	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("model").setString(model);
		for (int i = 0; i < textures.length; i++)
			node.node("textures").setString(textures[i]);

		super.save();
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String[] getTextures() {
		return textures;
	}

	public void setTextures(String[] textures) {
		this.textures = textures;
	}

	public TexturedModel createTexturedModel(int index) {
		String texture = textures[index];
		if (isNull(model) || isNull(texture))
			return null;
		RawModel rawModel = RawModelContainer.get(model);
		ModelTextureFile textureFile = ModelTextureFile.get(texture);
		if (rawModel == null || textureFile == null)
			return null;
		return new TexturedModel(rawModel, textureFile.get());
	}

	public TexturedModel getTexturedModel(int index) {
		return models[index % models.length];
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public static ContainerFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		ContainerFile file = new ContainerFile(name);
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

	public static ContainerFile get(String name) {
		ContainerFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ContainerFile> getList() {
		return list;
	}

}
