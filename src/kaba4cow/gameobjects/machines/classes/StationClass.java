package kaba4cow.gameobjects.machines.classes;

import java.util.ArrayList;
import java.util.List;

public enum StationClass implements MachineClass {

	STATION(0, 6.4f, 8.5f, "Station", "Terminal"), //
	TRADER(1, 7f, 9.2f, "Trade Ship", "Storage Facility"), //
	COLONY(2, 7.2f, 10f, "Colony", "Colonial Ship"), //
	PORT(3, 8f, 11.6f, "Starport", "Star Depot");

	private final String[] names;
	private final int rank;
	private final float minDestroyTime;
	private final float maxDestroyTime;

	private StationClass(int rank, float minDestroyTime, float maxDestroyTime,
			String... names) {
		this.names = names;
		this.rank = rank;
		this.minDestroyTime = minDestroyTime;
		this.maxDestroyTime = maxDestroyTime;
	}

	public static StationClass getClass(int rank) {
		StationClass[] values = values();
		for (int i = 0; i < values.length; i++) {
			int currentRank = values[i].rank;
			if (rank == currentRank)
				return values[i];
		}
		return STATION;
	}

	public static List<String> getStringList() {
		StationClass[] values = values();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; i++)
			list.add(values[i].toString());
		return list;
	}

	@Override
	public int getMachineClass(String name) {
		for (StationClass current : values())
			if (current.toString().equalsIgnoreCase(name))
				return current.rank;
		return 0;
	}

	@Override
	public float getMinDestroyTime() {
		return minDestroyTime;
	}

	@Override
	public float getMaxDestroyTime() {
		return maxDestroyTime;
	}

	@Override
	public String[] getNames() {
		return names;
	}

	@Override
	public int getRank() {
		return rank;
	}

}
