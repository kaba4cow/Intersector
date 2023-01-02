package kaba4cow.gameobjects.machinecontrollers.shipcontrollers;

import kaba4cow.gameobjects.machinecontrollers.MachineController;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.gameobjects.machines.Ship;
import kaba4cow.gameobjects.parametercontrols.AfterburnerControl;
import kaba4cow.gameobjects.parametercontrols.HyperControl;
import kaba4cow.gameobjects.parametercontrols.RotationControl;
import kaba4cow.gameobjects.parametercontrols.ThrustControl;
import kaba4cow.gameobjects.projectiles.Projectile;
import kaba4cow.gameobjects.targets.TargetMode;

public abstract class ShipController extends MachineController {

	protected ThrustControl horizontalControl;
	protected ThrustControl verticalControl;
	protected HyperControl hyperControl;
	protected AfterburnerControl afterburnerControl;
	protected RotationControl rotationControl;

	protected TargetMode mode;

	public ShipController() {
		super();
		this.setMode(TargetMode.SYSTEM);
	}

	public abstract void onDamage(Projectile proj);

	public TargetMode getMode() {
		return mode;
	}

	public void setMode(TargetMode mode) {
		if (this.mode == mode)
			return;
		this.mode = mode;
		this.resetTargets();
	}

	@Override
	public Ship getMachine() {
		return (Ship) machine;
	}

	@Override
	public ShipController setMachine(Machine machine) {
		super.setMachine(machine);
		Ship ship = (Ship) machine;
		this.horizontalControl = ship.getHorizontalControl();
		this.verticalControl = ship.getVerticalControl();
		this.hyperControl = ship.getHyperControl();
		this.afterburnerControl = ship.getAfterburnerControl();
		this.rotationControl = ship.getRotationControl();
		return this;
	}

}
