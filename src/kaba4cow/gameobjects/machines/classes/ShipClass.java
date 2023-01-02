package kaba4cow.gameobjects.machines.classes;

import java.util.ArrayList;
import java.util.List;

public enum ShipClass implements MachineClass {

	SHUTTLE(true, -1, 1f, 0.2f, 1.3f, "Shuttle"), //
	FREIGHTER(true, -2, 1.19f, 0.7f, 2.2f, "Freighter", "Hauler"), //
	HEAVYFREIGHTER(false, -3, 2.89f, 3.3f, 4.8f, "Heavy Freighter",
			"Heavy Hauler", "Container Vessel"), //
	ATTACKSHUTTLE(true, 0, 1.28f, 0.3f, 1.4f, "Attack Shuttle",
			"Assault Shuttle"), //
	FIGHTER(true, 1, 1.63f, 0f, 0.7f, "Fighter", "Heavy Fighter", "Bomber"), //
	CORVETTE(true, 2, 2.24f, 1.7f, 2.6f, "Corvette", "Gunship", "Cutter"), //
	FRIGATE(false, 3, 6.47f, 2.2f, 3.9f, "Frigate", "Light Cruiser"), //
	CRUISER(false, 4, 12.11f, 3f, 5.1f, "Cruiser"), //
	HEAVYCRUISER(false, 5, 14.66f, 3.3f, 5.8f, "Heavy Cruiser", "Battleship"), //
	CARRIER(false, 6, 8.87f, 2.5f, 4.5f, "Carrier");

	private final String[] names;
	private final int rank;
	private final boolean battle;
	private final boolean playable;
	private final float price;
	private final float minDestroyTime;
	private final float maxDestroyTime;

	private ShipClass(boolean playable, int rank, float price,
			float minDestroyTime, float maxDestroyTime, String... names) {
		this.names = names;
		this.rank = rank;
		this.battle = rank > 0;
		this.playable = playable;
		this.price = price;
		this.minDestroyTime = minDestroyTime;
		this.maxDestroyTime = maxDestroyTime;
	}

	public static ShipClass getClass(int rank) {
		ShipClass[] values = values();
		for (int i = 0; i < values.length; i++) {
			int currentRank = values[i].rank;
			if (rank == currentRank)
				return values[i];
		}
		return SHUTTLE;
	}

	public static List<String> getStringList() {
		ShipClass[] values = values();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; i++)
			list.add(values[i].toString());
		return list;
	}

	@Override
	public int getMachineClass(String name) {
		for (ShipClass current : values())
			if (current.toString().equalsIgnoreCase(name))
				return current.rank;
		return 0;
	}

	public float getPrice() {
		return price;
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

	public boolean isBattle() {
		return battle;
	}

	public boolean isCivilian() {
		return !battle;
	}

	public boolean isPlayable() {
		return playable;
	}

}
