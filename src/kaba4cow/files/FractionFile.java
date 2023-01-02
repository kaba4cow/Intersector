package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.files.DataFile;

public class FractionFile extends GameFile {

	private static final String LOCATION = "fractions/";

	private static final Map<String, FractionFile> map = new HashMap<String, FractionFile>();
	private static final List<FractionFile> list = new ArrayList<FractionFile>();

	private String name;
	private String capital;
	private int centerX;
	private int centerY;
	private int centerZ;
	private float range;
	private long densitySeed;
	private float density;
	private Vector3f mainColor;
	private Vector3f[] colors;
	private String[] friendly;
	private String[] enemy;
	private String[] manufacturers;

	private FractionFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		name = node.node("name").getString();
		capital = node.node("capital").getString();

		friendly = node.node("friendly").toStringArray();
		enemy = node.node("enemy").toStringArray();
		manufacturers = node.node("manufacturers").toStringArray();

		mainColor = node.node("maincolor").getVector3();
		colors = node.node("colors").toVector3Array();

		centerX = node.node("center").getInt(0);
		centerY = node.node("center").getInt(1);
		centerZ = node.node("center").getInt(2);
		range = node.node("range").getFloat();

		densitySeed = node.node("densityseed").getLong();
		density = node.node("density").getFloat();
	}

	@Override
	public void preparePostInit() {

	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("capital").setString(capital);

		for (int i = 0; i < friendly.length; i++)
			node.node("friendly").setString(friendly[i]);
		for (int i = 0; i < enemy.length; i++)
			node.node("enemy").setString(enemy[i]);
		for (int i = 0; i < manufacturers.length; i++)
			node.node("manufacturers").setString(manufacturers[i]);

		node.node("maincolor").setVector3(mainColor);
		for (int i = 0; i < colors.length; i++)
			node.node("colors").node(i + "").setVector3(colors[i]);

		node.node("center").setInt(centerX).setInt(centerY).setInt(centerZ);
		node.node("range").setFloat(range);

		node.node("densityseed").setLong(densitySeed);
		node.node("density").setFloat(density);

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

	public String getCapital() {
		return capital;
	}

	public void setCapital(String capital) {
		this.capital = capital;
	}

	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public int getCenterZ() {
		return centerZ;
	}

	public void setCenterZ(int centerZ) {
		this.centerZ = centerZ;
	}

	public float getRange() {
		return range;
	}

	public void setRange(float range) {
		this.range = range;
	}

	public long getDensitySeed() {
		return densitySeed;
	}

	public void setDensitySeed(long densitySeed) {
		this.densitySeed = densitySeed;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	public void setMainColor(Vector3f mainColor) {
		this.mainColor = mainColor;
	}

	public Vector3f[] getColors() {
		return colors;
	}

	public Vector3f getMainColor() {
		return mainColor;
	}

	public String[] getFriendly() {
		return friendly;
	}

	public String getFriendly(int index) {
		return friendly[index % friendly.length];
	}

	public String[] getEnemy() {
		return enemy;
	}

	public String getEnemy(int index) {
		return enemy[index % enemy.length];
	}

	public String[] getManufacturers() {
		return manufacturers;
	}

	public String getManufacturer(int index) {
		return manufacturers[index % manufacturers.length];
	}

	public static FractionFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		FractionFile file = new FractionFile(name);
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

	public static FractionFile get(String name) {
		FractionFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<FractionFile> getList() {
		return list;
	}

}
