package kaba4cow.gameobjects.parametercontrols;

import kaba4cow.gameobjects.machines.Ship;

public abstract class ParameterControl {

	protected Ship ship;

	protected ParameterControl(Ship ship) {
		this.ship = ship;
	}

	public abstract void update(float dt);

	public abstract void reset();

	public Ship getShip() {
		return ship;
	}

}
