package kaba4cow.intersector.gameobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.noise.Noise;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.files.FractionFile;
import kaba4cow.intersector.files.ShipFile;
import kaba4cow.intersector.files.StationFile;
import kaba4cow.intersector.gameobjects.machines.Manufacturer;
import kaba4cow.intersector.gameobjects.machines.classes.ShipClass;
import kaba4cow.intersector.utils.GalaxyUtils;

public class Fraction {

	private static final Map<FractionFile, Fraction> map = new HashMap<FractionFile, Fraction>();
	private static final List<Fraction> list = new ArrayList<Fraction>();

	private final FractionFile file;

	private final Fraction[] friendly;
	private final Fraction[] enemy;
	private final Manufacturer[] manufacturers;

	private final List<ShipFile> ships;
	private final List<StationFile> stations;

	private final Noise densityNoise;
	private final float density;

	private Fraction(FractionFile file) {
		map.put(file, this);
		this.file = file;
		this.friendly = new Fraction[file.getFriendly().length];
		for (int i = 0; i < friendly.length; i++)
			friendly[i] = get(file.getFriendly(i));
		this.enemy = new Fraction[file.getEnemy().length];
		for (int i = 0; i < enemy.length; i++)
			enemy[i] = get(file.getEnemy(i));
		this.manufacturers = new Manufacturer[file.getManufacturers().length];
		for (int i = 0; i < manufacturers.length; i++)
			manufacturers[i] = Manufacturer.get(file.getManufacturer(i));

		this.ships = new ArrayList<ShipFile>();
		this.stations = new ArrayList<StationFile>();
		for (int i = 0; i < manufacturers.length; i++) {
			ships.addAll(manufacturers[i].getShips());
			stations.addAll(manufacturers[i].getStations());
		}

		this.densityNoise = new Noise(file.getDensitySeed());
		this.density = file.getDensity();
	}

	public static void init() {
		if (!list.isEmpty())
			return;
		List<FractionFile> fractions = FractionFile.getList();
		for (FractionFile fraction : fractions)
			get(fraction);
	}

	public static Fraction get(String name) {
		FractionFile file = FractionFile.get(name);
		if (file == null)
			return null;
		if (map.containsKey(file))
			return map.get(file);
		Fraction fraction = new Fraction(file);
		list.add(fraction);
		return fraction;
	}

	public static Fraction get(FractionFile file) {
		if (file == null)
			return null;
		if (map.containsKey(file))
			return map.get(file);
		Fraction fraction = new Fraction(file);
		list.add(fraction);
		return fraction;
	}

	public static Fraction getRandom() {
		return list.get(RNG.randomInt(list.size()));
	}

	public FractionFile getFractionFile() {
		return file;
	}

	public Manufacturer getRandomManufacturer() {
		return manufacturers[RNG.randomInt(manufacturers.length)];
	}

	public String getFractionName() {
		return file.getName();
	}

	public static Fraction getFraction(String capitalFile) {
		for (int i = 0; i < list.size(); i++)
			if (capitalFile.equalsIgnoreCase(list.get(i).getFractionFile().getCapital()))
				return list.get(i);
		return null;
	}

	public float getDensity(int posX, int posY, int posZ) {
		int centerX = GalaxyUtils.SECTOR_SIZE * file.getCenterX();
		int centerY = GalaxyUtils.SECTOR_SIZE * file.getCenterY();
		int centerZ = GalaxyUtils.SECTOR_SIZE * file.getCenterZ();
		if (posX == centerX && posY == centerY && posZ == centerZ)
			return 1f;
		float rangeSq = Maths.sqr(GalaxyUtils.SECTOR_SIZE * file.getRange());
		float distSq = Maths.distSq(posX, posY, posZ, centerX, centerY, centerZ);
		if (distSq >= rangeSq)
			return 0f;
		float d = 0.822f * (2f - density);
		float value = densityNoise.getNoiseValue(d * posX, d * posY, d * posZ);
		if (value > file.getDensity())
			return 0f;
		return value * (1f - distSq / rangeSq);
	}

	public boolean isFriendly(Fraction fraction) {
		if (fraction == this)
			return true;
		for (int i = 0; i < enemy.length; i++)
			if (enemy[i] == fraction)
				return false;
		for (int i = 0; i < friendly.length; i++)
			if (friendly[i] == fraction)
				return true;
		return false;
	}

	public boolean isEnemy(Fraction fraction) {
		if (fraction == this)
			return false;
		for (int i = 0; i < friendly.length; i++)
			if (friendly[i] == fraction)
				return false;
		for (int i = 0; i < enemy.length; i++)
			if (enemy[i] == fraction)
				return true;
		return false;
	}

	public ShipFile getRandomShip() {
		return ships.get(RNG.randomInt(ships.size()));
	}

	public ShipFile getRandomShip(float minSize, float maxSize) {
		List<ShipFile> list = new ArrayList<ShipFile>();
		for (int i = 0; i < ships.size(); i++) {
			ShipFile file = ships.get(i);
			if (file.getSize() >= minSize && file.getSize() <= maxSize)
				list.add(file);
		}
		if (list.isEmpty())
			return null;
		return list.get(RNG.randomInt(list.size()));
	}

	public ShipFile getRandomShip(ShipClass shipClass) {
		List<ShipFile> list = new ArrayList<ShipFile>();
		for (int i = 0; i < ships.size(); i++)
			if (shipClass == ships.get(i).getMachineClass())
				list.add(ships.get(i));
		if (list.isEmpty())
			return null;
		return list.get(RNG.randomInt(list.size()));
	}

	public boolean containsClass(ShipClass shipClass) {
		for (int i = 0; i < ships.size(); i++)
			if (shipClass == ships.get(i).getMachineClass())
				return true;
		return false;
	}

	public StationFile getRandomStation(RNG rng) {
		if (rng == null)
			return stations.get(RNG.randomInt(stations.size()));
		return stations.get(rng.nextInt(0, stations.size()));
	}

	public Vector3f getRandomColor(RNG rng) {
		if (rng == null)
			return file.getColors()[RNG.randomInt(0, file.getColors().length)];
		return file.getColors()[rng.nextInt(0, file.getColors().length)];
	}

	public static List<Fraction> getList() {
		return list;
	}

}