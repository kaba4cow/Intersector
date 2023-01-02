package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.Loaders;
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.renderEngine.textures.ThrustTexture;

public class ThrustTextureFile extends GameFile {

	private static final String LOCATION = "thrusttextures/";

	public static final Map<String, ThrustTextureFile> map = new HashMap<String, ThrustTextureFile>();
	private static final List<ThrustTextureFile> list = new ArrayList<ThrustTextureFile>();

	private String name;
	private String sound;
	private float speed;

	private ThrustTexture thrustTexture;

	private ThrustTextureFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		name = data.node("name").getString();
		sound = data.node("sound").getString();
		speed = data.node("speed").getFloat();
	}

	@Override
	public void preparePostInit() {
		thrustTexture = new ThrustTexture(Loaders.loadTexture("textures/"
				+ name, true));
		thrustTexture.setSpeed(speed);
		thrustTexture.setSound(sound);
	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("sound").setString(sound);
		node.node("speed").setFloat(speed);

		super.save();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public static ThrustTextureFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		ThrustTextureFile file = new ThrustTextureFile(name);
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

	public static ThrustTextureFile get(String name) {
		ThrustTextureFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ThrustTextureFile> getList() {
		return list;
	}

	public ThrustTexture get() {
		return thrustTexture;
	}

}
