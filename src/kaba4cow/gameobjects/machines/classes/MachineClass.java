package kaba4cow.gameobjects.machines.classes;

public interface MachineClass {

	public int getMachineClass(String name);

	public default int getNameIndex(String name) {
		for (int i = 0; i < getNames().length; i++)
			if (getNames()[i].equalsIgnoreCase(name))
				return i;
		return 0;
	}

	public default String getName(int name) {
		if (name < 0 || name >= getNames().length)
			name = 0;
		return getNames()[name];
	}

	public String[] getNames();

	public int getRank();

	public float getMinDestroyTime();

	public float getMaxDestroyTime();

}
