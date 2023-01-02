package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.rng.RNG;

public class ManufacturerFile extends GameFile {

	private static final String LOCATION = "manufacturers/";

	private static final Map<String, ManufacturerFile> map = new HashMap<String, ManufacturerFile>();
	private static final List<ManufacturerFile> list = new ArrayList<ManufacturerFile>();

	private String shortName;
	private String longName;
	private float overprice;

	private String[] textureSets;

	private ManufacturerFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		overprice = node.node("overprice").getFloat();
		textureSets = node.node("texturesets").toStringArray();

		node = data.node("name");
		shortName = node.node("short").getString();
		longName = node.node("long").getString();
	}

	@Override
	public void preparePostInit() {

	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("overprice").setFloat(overprice);
		for (int i = 0; i < textureSets.length; i++)
			node.node("texturesets").setString(textureSets[i]);

		node = data.node("name");
		node.node("short").setString(shortName);
		node.node("long").setString(longName);

		super.save();
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getLongName() {
		return longName;
	}

	public void setLongName(String longName) {
		this.longName = longName;
	}

	public float getOverprice() {
		return overprice;
	}

	public void setOverprice(float overprice) {
		this.overprice = overprice;
	}

	public String getTextureSet(int index) {
		return textureSets[index % textureSets.length];
	}

	public String getRandomTextureSet() {
		return textureSets[RNG.randomInt(textureSets.length)];
	}

	public static ManufacturerFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		ManufacturerFile file = new ManufacturerFile(name);
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

	public static ManufacturerFile get(String name) {
		ManufacturerFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ManufacturerFile> getList() {
		return list;
	}

}
