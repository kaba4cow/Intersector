package kaba4cow.intersector.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.files.DataFile;

public class SystemFile extends GameFile {

	private static final String LOCATION = "systems/";

	private static final Map<String, SystemFile> map = new HashMap<String, SystemFile>();
	private static final List<SystemFile> list = new ArrayList<SystemFile>();

	private String name;
	private String star;
	private long seed;
	private int planets;
	private int stations;
	private int posX;
	private int posY;
	private int posZ;

	private SystemFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		name = data.node("name").getString();
		star = data.node("star").getString();
		seed = data.node("seed").getLong();
		planets = data.node("planets").getInt();
		stations = data.node("stations").getInt();

		posX = data.node("pos").getInt(0);
		posY = data.node("pos").getInt(1);
		posZ = data.node("pos").getInt(2);
	}

	@Override
	public void preparePostInit() {

	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("star").setString(star);
		node.node("seed").setLong(seed);
		node.node("planets").setInt(planets);
		node.node("stations").setInt(stations);

		node.node("pos").setInt(posX, 0);
		node.node("pos").setInt(posY, 1);
		node.node("pos").setInt(posZ, 2);

		super.save();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
	}

	public int getPlanets() {
		return planets;
	}

	public void setPlanets(int planets) {
		this.planets = planets;
	}

	public int getStations() {
		return stations;
	}

	public void setStations(int stations) {
		this.stations = stations;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public int getPosZ() {
		return posZ;
	}

	public void setPosZ(int posZ) {
		this.posZ = posZ;
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public static SystemFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		SystemFile file = new SystemFile(name);
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

	public static SystemFile get(String name) {
		SystemFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<SystemFile> getList() {
		return list;
	}

}
