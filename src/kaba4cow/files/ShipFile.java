package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.gameobjects.machines.classes.ShipClass;

public class ShipFile extends MachineFile {

	private static final Map<String, ShipFile> map = new HashMap<String, ShipFile>();
	private static final List<ShipFile> list = new ArrayList<ShipFile>();

	private float horSpeed;
	private float horThrust;
	private float horBrake;
	private float verSpeed;
	private float verThrust;
	private float verBrake;
	private float pitchSens;
	private float yawSens;
	private float rollSens;
	private float hyperSpeed;
	private float hyperThrust;
	private float aftPower;
	private float aftTime;
	private float aftCooldown;
	private float aftSmoothness;

	private int price;

	public ShipFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = null;

		node = data.node("controls").node("horizontal");
		horSpeed = node.node("speed").getFloat();
		horThrust = node.node("thrust").getFloat();
		horBrake = node.node("brake").getFloat();

		node = data.node("controls").node("vertical");
		verSpeed = node.node("speed").getFloat();
		verThrust = node.node("thrust").getFloat();
		verBrake = node.node("brake").getFloat();

		node = data.node("controls").node("rotation");
		pitchSens = node.node("pitch").getFloat();
		yawSens = node.node("yaw").getFloat();
		rollSens = node.node("roll").getFloat();

		node = data.node("controls").node("hyper");
		hyperSpeed = node.node("speed").getFloat();
		hyperThrust = node.node("thrust").getFloat();

		node = data.node("controls").node("afterburn");
		aftPower = node.node("power").getFloat();
		aftTime = node.node("time").getFloat();
		aftCooldown = node.node("cooldown").getFloat();
		aftSmoothness = node.node("smoothness").getFloat();

		super.prepareInit();

		getPrice();
	}

	public int getPrice() {
		if (price == 0) {
			float infos = 0f;
			infos += 1.6f * size;
			infos += 14.8f * health;
			infos += 10.6f * shield;
			infos -= 7.3f * mass;

			float cargo = 0f;
			cargo += 4.5f * maxCargo;
			cargo += 8.2f * containerComponents.size();

			float horizontal = 0f;
			horizontal += 4.9f * horSpeed;
			horizontal += 2.3f * horThrust;
			horizontal += 0.9f * horBrake;

			float vertical = 0f;
			vertical += 2.2f * verSpeed;
			vertical += 1.8f * verThrust;
			vertical += 0.2f * verBrake;

			float hyper = 0f;
			hyper += 2.6f * hyperSpeed;
			hyper += 3.9f * hyperThrust;

			float rotations = 0f;
			rotations += 1.3f * pitchSens;
			rotations += 1.4f * yawSens;
			rotations += 1.2f * rollSens;

			float afterburner = 0f;
			afterburner += 1.8f * aftPower;
			afterburner += 2.3f * aftTime;
			afterburner -= 2.4f * aftCooldown;
			afterburner += 1.4f * aftSmoothness;

			float weapons = 0f;
			for (int i = 0; i < weaponComponents.size(); i++) {
				WeaponFile weapon = weaponComponents.get(i).weaponFile;

				float firePoints = 2.6f * weapon.getFirePoints();

				weapons += 1.7f * firePoints * weapon.getRepeat();
				weapons += 6.8f * firePoints * weapon.getDamage();
				weapons += 2.4f * weapon.getDamageDeviation();

				weapons -= 2.3f * weapon.getCooldown();
				weapons -= 1.3f * weapon.getReload();

				weapons += 2.2f * weapon.getRotationSpeed();
			}

			float total = 1.93f * infos + 0.92f * cargo + 0.61f * horizontal
					+ 0.44f * vertical + 0.52f * hyper + 1.07f * rotations
					+ 0.65f * afterburner + 0.32f * weapons;

			total *= ManufacturerFile.get(manufacturer).getOverprice();
			total *= getMachineClass().getPrice();

			price = (int) total;

			int length = 0;
			int number = price;
			while (number != 0) {
				length++;
				number /= 10;
			}
			length -= 4;
			for (int i = 0; i < length; i++)
				price /= 10;
			for (int i = 0; i < length; i++)
				price *= 10;

			// Printer.println(fileName, ManufacturerFile.get(manufacturer)
			// .getShortName(), price);
		}
		return price;
	}

	@Override
	public void preparePostInit() {
		super.preparePostInit();
	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node = data.node("controls").node("horizontal");
		node.node("speed").setFloat(horSpeed);
		node.node("thrust").setFloat(horThrust);
		node.node("brake").setFloat(horBrake);

		node = data.node("controls").node("vertical");
		node.node("speed").setFloat(verSpeed);
		node.node("thrust").setFloat(verThrust);
		node.node("brake").setFloat(verBrake);

		node = data.node("controls").node("rotation");
		node.node("pitch").setFloat(pitchSens);
		node.node("yaw").setFloat(yawSens);
		node.node("roll").setFloat(rollSens);

		node = data.node("controls").node("hyper");
		node.node("speed").setFloat(hyperSpeed);
		node.node("thrust").setFloat(hyperThrust);

		node = data.node("controls").node("afterburn");
		node.node("power").setFloat(aftPower);
		node.node("time").setFloat(aftTime);
		node.node("cooldown").setFloat(aftCooldown);
		node.node("smoothness").setFloat(aftSmoothness);

		super.save();
	}

	@Override
	public void setMachineClassInfo(String machineClass, String machineClassName) {
		ShipClass shipClass = ShipClass.valueOf(machineClass);
		setClassRank(shipClass.getRank());
		setClassName(shipClass.getNameIndex(machineClassName));
	}

	@Override
	public ShipClass getMachineClass() {
		return ShipClass.getClass(classRank);
	}

	public float getHyperSpeed() {
		return hyperSpeed;
	}

	public void setHyperSpeed(float hyperSpeed) {
		this.hyperSpeed = hyperSpeed;
	}

	public float getHyperThrust() {
		return hyperThrust;
	}

	public void setHyperThrust(float hyperThrust) {
		this.hyperThrust = hyperThrust;
	}

	public float getHorSpeed() {
		return horSpeed;
	}

	public void setHorSpeed(float horSpeed) {
		this.horSpeed = horSpeed;
	}

	public float getVerSpeed() {
		return verSpeed;
	}

	public void setVerSpeed(float verSpeed) {
		this.verSpeed = verSpeed;
	}

	public float getHorThrust() {
		return horThrust;
	}

	public void setHorThrust(float horThrust) {
		this.horThrust = horThrust;
	}

	public float getHorBrake() {
		return horBrake;
	}

	public void setHorBrake(float horBrake) {
		this.horBrake = horBrake;
	}

	public float getVerThrust() {
		return verThrust;
	}

	public void setVerThrust(float verThrust) {
		this.verThrust = verThrust;
	}

	public float getVerBrake() {
		return verBrake;
	}

	public void setVerBrake(float verBrake) {
		this.verBrake = verBrake;
	}

	public float getAftPower() {
		return aftPower;
	}

	public void setAftPower(float aftPower) {
		this.aftPower = aftPower;
	}

	public float getAftTime() {
		return aftTime;
	}

	public void setAftTime(float aftTime) {
		this.aftTime = aftTime;
	}

	public float getAftCooldown() {
		return aftCooldown;
	}

	public void setAftCooldown(float aftCooldown) {
		this.aftCooldown = aftCooldown;
	}

	public float getAftSmoothness() {
		return aftSmoothness;
	}

	public void setAftSmoothness(float aftSmoothness) {
		this.aftSmoothness = aftSmoothness;
	}

	public float getPitchSens() {
		return pitchSens;
	}

	public void setPitchSens(float pitchSens) {
		this.pitchSens = pitchSens;
	}

	public float getYawSens() {
		return yawSens;
	}

	public void setYawSens(float yawSens) {
		this.yawSens = yawSens;
	}

	public float getRollSens() {
		return rollSens;
	}

	public void setRollSens(float rollSens) {
		this.rollSens = rollSens;
	}

	public static ShipFile load(String name) {
		ShipFile file = new ShipFile(name);
		load(file);
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

	public static ShipFile get(String name) {
		ShipFile file = (ShipFile) MachineFile.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ShipFile> getList() {
		return list;
	}

	public static List<ShipFile> getList(float minSize, float maxSize) {
		List<ShipFile> newList = new ArrayList<ShipFile>();
		for (int i = 0; i < list.size(); i++) {
			ShipFile file = list.get(i);
			float size = file.data.node("info").node("size").getFloat(0)
					* file.data.node("info").node("collisionsize").getFloat(0);
			if (size >= minSize && size <= maxSize)
				newList.add(file);
		}
		return newList;
	}

}
