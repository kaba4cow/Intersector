package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.Loaders;
import kaba4cow.engine.toolbox.files.DataFile;

public class ModelTextureFile extends GameFile {

	private static final String LOCATION = "modeltextures/";

	private static final Map<String, ModelTextureFile> map = new HashMap<String, ModelTextureFile>();
	private static final List<ModelTextureFile> list = new ArrayList<ModelTextureFile>();

	private String name;
	private boolean linear;
	private float shininess;
	private float shineDamper;
	private float reflectivity;
	private float emission;
	private boolean transparent;
	private boolean additive;

	private ModelTexture modelTexture;

	private ModelTextureFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		name = node.node("name").getString();
		linear = node.node("linear").getBoolean();
		shininess = node.node("shininess").getFloat();
		shineDamper = node.node("shinedamper").getFloat();
		reflectivity = node.node("reflectivity").getFloat();
		emission = node.node("emission").getFloat();
		transparent = node.node("transparent").getBoolean();
		additive = node.node("additive").getBoolean();
	}

	@Override
	public void preparePostInit() {
		modelTexture = new ModelTexture(Loaders.loadTexture("textures/" + name,
				linear));
		modelTexture.setShininess(shininess);
		modelTexture.setShineDamper(shineDamper);
		modelTexture.setReflectivity(reflectivity);
		modelTexture.setEmission(emission);
		modelTexture.setTransparent(transparent);
		modelTexture.setAdditive(additive);
	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("linear").setBoolean(linear);
		node.node("shininess").setFloat(shininess);
		node.node("shinedamper").setFloat(shineDamper);
		node.node("reflectivity").setFloat(reflectivity);
		node.node("emission").setFloat(emission);
		node.node("transparent").setBoolean(transparent);
		node.node("additive").setBoolean(additive);

		super.save();
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLinear() {
		return linear;
	}

	public void setLinear(boolean linear) {
		this.linear = linear;
	}

	public float getShininess() {
		return shininess;
	}

	public void setShininess(float shininess) {
		this.shininess = shininess;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public void setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public void setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
	}

	public float getEmission() {
		return emission;
	}

	public void setEmission(float emission) {
		this.emission = emission;
	}

	public boolean isTransparent() {
		return transparent;
	}

	public void setTransparent(boolean transparent) {
		this.transparent = transparent;
	}

	public boolean isAdditive() {
		return additive;
	}

	public void setAdditive(boolean additive) {
		this.additive = additive;
	}

	public static ModelTextureFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		ModelTextureFile file = new ModelTextureFile(name);
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

	public static ModelTextureFile get(String name) {
		ModelTextureFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ModelTextureFile> getList() {
		return list;
	}

	public ModelTexture get() {
		return modelTexture;
	}

}
