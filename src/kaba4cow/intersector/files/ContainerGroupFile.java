package kaba4cow.intersector.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.rng.RNG;

public class ContainerGroupFile extends GameFile {

	private static final String LOCATION = "containergroups/";

	private static final Map<String, ContainerGroupFile> map = new HashMap<String, ContainerGroupFile>();
	private static final List<ContainerGroupFile> list = new ArrayList<ContainerGroupFile>();

	private int mass;
	private float size;
	private float health;
	private List<String> containers;

	private ContainerGroupFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		mass = node.node("mass").getInt();
		size = node.node("size").getFloat();
		health = node.node("health").getFloat();

		node = data.node("containers");
		containers = new ArrayList<String>();
		for (int i = 0; i < node.contentSize(); i++)
			containers.add(node.getString(i));
	}

	@Override
	public void preparePostInit() {

	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("mass").setInt(mass);
		node.node("size").setFloat(size);
		node.node("health").setFloat(health);

		for (int i = 0; i < containers.size(); i++)
			node.node("containers").setString(containers.get(i));

		super.save();
	}

	public int getMass() {
		return mass;
	}

	public void setMass(int mass) {
		this.mass = mass;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public List<String> getContainers() {
		return containers;
	}

	public String getContainer(int index) {
		return containers.get(index % containers.size());
	}

	public String getRandomContainer() {
		return getContainer(RNG.randomInt());
	}

	public void addContainer(String container) {
		containers.add(container);
	}

	public void removeContainer(int index) {
		if (index >= 0 && index < containers.size())
			containers.remove(index);
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public static ContainerGroupFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		ContainerGroupFile file = new ContainerGroupFile(name);
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

	public static ContainerGroupFile get(String name) {
		ContainerGroupFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ContainerGroupFile> getList() {
		return list;
	}

}
