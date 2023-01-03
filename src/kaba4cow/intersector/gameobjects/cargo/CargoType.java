package kaba4cow.intersector.gameobjects.cargo;

import kaba4cow.engine.toolbox.rng.RNG;

public enum CargoType {

	LIGHT_ALLOYS(CargoCategory.METALS, 0.73f, 1f, 0.81f), //
	HEAVY_ALLOYS(CargoCategory.METALS, 0.68f, 1f, 0.89f), //
	ALUMINIUM(CargoCategory.METALS, 0.76f, 1f, 0.98f), //
	GOLD(CargoCategory.METALS, 0.84f, 1f, 0.54f), //
	SILVER(CargoCategory.METALS, 0.81f, 1f, 0.61f), //
	STEEL(CargoCategory.METALS, 0.64f, 1f, 0.78f), //
	//
	MINERALS(CargoCategory.ORES, 0.88f, 1f, 0.62f), //
	CRYSTALS(CargoCategory.ORES, 0.82f, 1f, 0.56f), //
	//
	FUEL(CargoCategory.FUEL, 0.54f, 1f, 0.94f), //
	HYDROGEN_FUEL(CargoCategory.FUEL, 0.78f, 0.99f, 0.92f), //
	ANTIMATTER_FUEL(CargoCategory.FUEL, 0.89f, 0.88f, 0.73f), //
	//
	WEAPONS(CargoCategory.WEAPONRY, 0.85f, 0.71f, 0.64f), //
	AMMO(CargoCategory.WEAPONRY, 0.47f, 0.77f, 0.76f), //
	ARMOR(CargoCategory.WEAPONRY, 0.79f, 0.79f, 0.66f), //
	//
	COMPUTERS(CargoCategory.TECHNOLOGIES, 0.66f, 1f, 0.82f), //
	NANO_COMPUTERS(CargoCategory.TECHNOLOGIES, 0.79f, 1f, 0.7f), //
	ROBOTS(CargoCategory.TECHNOLOGIES, 0.74f, 0.97f, 0.46f), //
	INTELLIGENT_ROBOTS(CargoCategory.TECHNOLOGIES, 0.87f, 0.71f, 0.38f), //
	NANO_CHIPS(CargoCategory.TECHNOLOGIES, 0.77f, 1f, 0.34f), //
	//
	SLAVES(CargoCategory.CREATURES, 0.93f, 0.03f, 0.32f), //
	ANIMALS(CargoCategory.CREATURES, 0.54f, 0.87f, 0.82f), //
	EXOTIC_ANIMALS(CargoCategory.CREATURES, 0.78f, 0.53f, 0.39f), //
	ALIEN_ANIMALS(CargoCategory.CREATURES, 0.96f, 0.31f, 0.16f), //
	//
	FOOD(CargoCategory.OTHER, 0.66f, 1f, 0.95f), //
	MEDICATIONS(CargoCategory.OTHER, 0.79f, 1f, 0.48f), //
	CHEMICALS(CargoCategory.OTHER, 0.73f, 0.89f, 0.53f), //
	DRUGS(CargoCategory.OTHER, 0.91f, 0.14f, 0.34f), //
	LIQUID_WATER(CargoCategory.OTHER, 0.59f, 1f, 0.95f), //
	ICE(CargoCategory.OTHER, 0.61f, 1f, 0.92f), //
	CLOTHES(CargoCategory.OTHER, 0.31f, 1f, 1f), //
	PLASTIC(CargoCategory.OTHER, 0.61f, 0.93f, 0.75f), //
	//
	METAL_SCRAP(CargoCategory.WASTE, -0.08f, 1f, 1f), //
	WASTE(CargoCategory.WASTE, -0.12f, 1f, 1f), //
	BIO_WASTE(CargoCategory.WASTE, -0.19f, 1f, 0.81f), //
	NUCLEAR_WASTE(CargoCategory.WASTE, -0.23f, 0.82f, 0.47f);

	private final CargoCategory category;
	private final float value;
	private final float legality;
	private final float chance;

	private CargoType(CargoCategory category, float value, float legality,
			float chance) {
		this.category = category;
		this.value = value;
		this.legality = legality;
		this.chance = chance;
	}

	public static CargoType getRandom() {
		return values()[RNG.randomInt(values().length)];
	}

	public CargoCategory getCategory() {
		return category;
	}

	public float getValue() {
		return value;
	}

	public float getLegality() {
		return legality;
	}

	public float getChance() {
		return chance;
	}

}