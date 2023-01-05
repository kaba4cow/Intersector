package kaba4cow.intersector.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.assets.Loaders;
import kaba4cow.engine.renderEngine.textures.ParticleTexture;
import kaba4cow.engine.toolbox.files.DataFile;

public class ParticleTextureFile extends GameFile {

	private static final String LOCATION = "particletextures/";

	private static final Map<String, ParticleTextureFile> map = new HashMap<String, ParticleTextureFile>();
	private static final List<ParticleTextureFile> list = new ArrayList<ParticleTextureFile>();

	private String name;
	private int rows;
	private boolean additive;

	private ParticleTexture particleTexture;

	public ParticleTextureFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		name = data.node("name").getString();
		rows = data.node("rows").getInt();
		additive = data.node("additive").getBoolean();
	}

	@Override
	public void preparePostInit() {
		particleTexture = new ParticleTexture(Loaders.loadTexture("textures/"
				+ name, true));
		particleTexture.setNumberOfRows(rows);
		particleTexture.setAdditive(additive);
	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("rows").setInt(rows);
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

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public boolean isAdditive() {
		return additive;
	}

	public void setAdditive(boolean additive) {
		this.additive = additive;
	}

	public ParticleTexture get() {
		return particleTexture;
	}

	public static ParticleTextureFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		ParticleTextureFile file = new ParticleTextureFile(name);
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

	public static ParticleTextureFile get(String name) {
		ParticleTextureFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ParticleTextureFile> getList() {
		return list;
	}

}
