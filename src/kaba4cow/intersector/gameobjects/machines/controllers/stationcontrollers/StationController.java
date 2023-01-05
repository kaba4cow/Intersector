package kaba4cow.intersector.gameobjects.machines.controllers.stationcontrollers;

import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.machines.Station;
import kaba4cow.intersector.gameobjects.machines.controllers.MachineController;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;

public abstract class StationController extends MachineController {

	public StationController() {
		super();
	}

	public abstract void update(float dt);

	public abstract void onDamage(Projectile proj);

	@Override
	public Station getMachine() {
		return (Station) machine;
	}

	@Override
	public StationController setMachine(Machine machine) {
		super.setMachine(machine);
		return this;
	}

}
