package kaba4cow.gameobjects.machinecontrollers.stationcontrollers;

import kaba4cow.gameobjects.machinecontrollers.MachineController;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.gameobjects.machines.Station;
import kaba4cow.gameobjects.projectiles.Projectile;

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
