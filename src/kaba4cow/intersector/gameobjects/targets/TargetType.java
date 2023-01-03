package kaba4cow.intersector.gameobjects.targets;

public enum TargetType {

	SYSTEM("System"), PLANET("Space body"), CARGO("Cargo"), MACHINE("Machine");

	private final String name;

	private TargetType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
