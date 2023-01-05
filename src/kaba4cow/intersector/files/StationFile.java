package kaba4cow.intersector.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.intersector.gameobjects.machines.classes.StationClass;

public class StationFile extends MachineFile {

	private static final Map<String, StationFile> map = new HashMap<String, StationFile>();
	private static final List<StationFile> list = new ArrayList<StationFile>();

	public StationFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		super.prepareInit();
	}

	@Override
	public void preparePostInit() {
		super.preparePostInit();
	}

	@Override
	public void setMachineClassInfo(String machineClass, String machineClassName) {
		StationClass shipClass = StationClass.valueOf(machineClass);
		setClassRank(shipClass.getRank());
		setClassName(shipClass.getNameIndex(machineClassName));
	}

	@Override
	public StationClass getMachineClass() {
		return StationClass.getClass(classRank);
	}

	public static StationFile load(String name) {
		StationFile file = new StationFile(name);
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

	public static StationFile get(String name) {
		StationFile file = (StationFile) MachineFile.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<StationFile> getList() {
		return list;
	}

}
