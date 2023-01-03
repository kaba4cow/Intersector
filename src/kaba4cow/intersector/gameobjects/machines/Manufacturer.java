package kaba4cow.intersector.gameobjects.machines;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.files.ManufacturerFile;
import kaba4cow.files.ShipFile;
import kaba4cow.files.StationFile;

public class Manufacturer {

	private static final Map<ManufacturerFile, Manufacturer> map = new HashMap<ManufacturerFile, Manufacturer>();

	private final ManufacturerFile file;

	private final List<ShipFile> ships;
	private final List<StationFile> stations;

	private Manufacturer(ManufacturerFile file) {
		map.put(file, this);
		this.file = file;
		this.ships = new ArrayList<ShipFile>();
		List<ShipFile> shipFiles = ShipFile.getList();
		for (ShipFile ship : shipFiles)
			if (ship.getManufacturer().equalsIgnoreCase(file.getFileName()))
				ships.add(ship);
		this.stations = new ArrayList<StationFile>();
		List<StationFile> stationFiles = StationFile.getList();
		for (StationFile station : stationFiles)
			if (station.getManufacturer().equalsIgnoreCase(file.getFileName()))
				stations.add(station);
	}

	public static Manufacturer get(String name) {
		ManufacturerFile file = ManufacturerFile.get(name);
		if (file == null)
			return null;
		if (map.containsKey(file))
			return map.get(file);
		return new Manufacturer(file);
	}

	public ManufacturerFile getManufacturerFile() {
		return file;
	}

	public String getShortName() {
		return file.getShortName();
	}

	public String getLongName() {
		return file.getLongName();
	}

	public String getRandomTextureSet() {
		return file.getRandomTextureSet();
	}

	public List<ShipFile> getShips() {
		return ships;
	}

	public List<StationFile> getStations() {
		return stations;
	}
}
