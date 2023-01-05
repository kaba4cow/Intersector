package kaba4cow.intersector.gameobjects.machines.controllers.stationcontrollers;

import java.util.List;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.gameobjects.Fraction;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.cargo.Container;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.machines.Station;
import kaba4cow.intersector.gameobjects.machines.controllers.MachineController;
import kaba4cow.intersector.gameobjects.objectcomponents.ContainerComponent;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;

public class StationAIController extends StationController {

	public StationAIController() {
		super();
	}

	@Override
	protected void updateTargets(float dt) {
		super.updateTargets(dt);
		targetPlanet = null;
		targetFriend = null;

		if (targetCargo != null && machine.allContainersOccupied())
			targetCargo = null;

		if (targetEnemy == null)
			setTargetEnemy(searchTargetEnemy());

		if (targetEnemy == null && targetCargo == null)
			setTargetCargo(searchTargetCargo());
	}

	@Override
	public void update(float dt) {
		updateTargets(dt);
		targetPlanet = null;

		if (targetCargo != null && machine.allContainersOccupied())
			targetCargo = null;

		if (targetEnemy == null)
			targetEnemy = searchTargetEnemy();

		if (targetEnemy == null && targetCargo == null)
			targetCargo = searchTargetCargo();

		if (targetEnemy == null) {
			machine.disableTurrets();
			machine.disableLaunchers();
		} else {
			machine.ejectShips();
			machine.enableTurrets();
			machine.enableLaunchers();
		}
	}

	private Machine searchTargetEnemy() {
		if (RNG.randomBoolean())
			return null;
		Station machine = getMachine();
		Fraction fraction = machine.getFraction();
		Machine target = null;
		List<GameObject> list = machine.getWorld().getOctTree().query(machine,
				MachineController.MAX_TARGET_DIST * machine.getSize());
		float minDistSq = Maths.sqr(MachineController.MAX_TARGET_DIST * machine.getSize());
		float minHealthDist = Float.POSITIVE_INFINITY;
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) instanceof Machine) {
				Machine current = (Machine) list.get(i);
				if (!current.isAlive() || current.isDestroyed() || fraction.isFriendly(current.getFraction()))
					continue;
				float distSq = Maths.distSq(machine.getPos(), current.getPos());
				float healthDist = Maths.dist(machine.getHealth(), current.getHealth());
				if (distSq < minDistSq && healthDist <= minHealthDist) {
					minDistSq = distSq;
					minHealthDist = healthDist;
					target = current;
				}
			}
		return target;
	}

	private Container searchTargetCargo() {
		if (RNG.randomBoolean())
			return null;
		Station machine = getMachine();
		if (machine.allContainersOccupied())
			return null;
		String cargoGroup = null;
		ContainerComponent[] cargos = machine.getCargos();
		for (int i = 0; i < cargos.length; i++)
			if (!cargos[i].occupied) {
				cargoGroup = cargos[i].containerGroupName;
				break;
			}
		if (cargoGroup == null)
			return null;
		Container target = null;
		List<GameObject> list = machine.getWorld().getOctTree().query(machine,
				MachineController.MAX_TARGET_DIST * machine.getSize());
		float minDistSq = Maths.sqr(Machine.MAX_CARGO_DIST * machine.getSize());
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) instanceof Container) {
				Container current = (Container) list.get(i);
				if (!current.isAlive() || current.getParent() != null || current.getParent() == machine
						|| !cargoGroup.equalsIgnoreCase(current.getGroupFile().getFileName()))
					continue;
				float distSq = Maths.distSq(machine.getPos(), current.getPos());
				if (distSq < minDistSq) {
					minDistSq = distSq;
					target = current;
				}
			}
		return target;
	}

	@Override
	public void onDamage(Projectile proj) {
		if (targetEnemy != null)
			return;
		Machine parent = proj.getParent();
		if (parent.getFraction() == getMachine().getFraction() || !GameObject.isAlive(parent) || parent.isDestroyed())
			return;
		searchTargetEnemy();
		if (targetEnemy != null)
			return;
		targetEnemy = proj.getParent();
		if (getMachine().getFlock() != null)
			getMachine().getFlock().requestTarget(targetEnemy);
	}

	@Override
	public void requestTarget(Machine machine) {
		if (machine.getFraction() == getMachine().getFraction() || !GameObject.isAlive(machine)
				|| machine.isDestroyed())
			return;
		setTargetEnemy(machine);
	}

}
