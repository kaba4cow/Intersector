package kaba4cow.intersector.gameobjects.machinecontrollers.shipcontrollers;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.intersector.gameobjects.Fraction;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.gameobjects.cargo.CargoObject;
import kaba4cow.intersector.gameobjects.cargo.Container;
import kaba4cow.intersector.gameobjects.machinecontrollers.MachineController;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.machines.Ship;
import kaba4cow.intersector.gameobjects.machines.Station;
import kaba4cow.intersector.gameobjects.objectcomponents.ContainerComponent;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetMode;
import kaba4cow.intersector.toolbox.flocking.Flock;
import kaba4cow.intersector.utils.GameUtils;

public class ShipAIController extends ShipController {

	public ShipAIController() {
		super();
	}

	@Override
	protected void updateTargets(float dt) {
		super.updateTargets(dt);

		if (targetFriend != null && targetFriend.allPortsOccupied(getMachine().getCollisionSize()))
			targetFriend = null;

		if (targetCargo != null && machine.allContainersOccupied())
			targetCargo = null;

		if (targetEnemy == null) {
			if (machine.getFlock() != null) {
				Machine leader = machine.getFlock().getLeader();
				if (leader == null || Maths.distSq(machine.getPos(), leader.getPos()) < Maths
						.sqr(MAX_TARGET_DIST * leader.getSize()))
					setTargetEnemy(searchTargetEnemy());
				else
					setTargetEnemy(null);
			} else
				setTargetEnemy(searchTargetEnemy());
		}

		if (targetEnemy == null && targetCargo == null)
			setTargetCargo(searchTargetCargo());

		if (RNG.chance(0.01f) && targetEnemy == null && targetCargo == null && targetFriend == null)
			setTargetFriend(searchTargetFriend());
	}

	@Override
	public void update(float dt) {
		updateTargets(dt);

		Renderer renderer = GameUtils.getAiPov();
		Camera camera = renderer.getCamera();
		camera.orbit(machine.getPos(), machine.getSize(), 0f, 0f, machine.getDirection());
		renderer.updateViewMatrix();

		if (targetEnemy != null)
			processTargetEnemy(renderer, dt);
		else if (targetCargo != null)
			processTargetCargo(renderer, dt);
		else if (targetPlanet != null)
			processTargetPlanet(renderer, dt);
		else if (targetFriend != null)
			processTargetFriend(renderer, dt);
		else
			processFlocking(renderer, dt);

		if (targetEnemy == null) {
			machine.disableTurrets();
			machine.disableLaunchers();
		} else {
			machine.enableTurrets();
			machine.enableLaunchers();
		}
	}

	private Machine searchTargetEnemy() {
		if (RNG.randomBoolean())
			return null;
		Ship machine = getMachine();
		Fraction fraction = machine.getFraction();
		Machine target = null;
		List<GameObject> list = machine.getWorld().getOctTree().query(machine,
				MachineController.MAX_TARGET_DIST * machine.getSize());
		float minDistSq = Maths.sqr(MachineController.MAX_TARGET_DIST * machine.getSize());
		float minHealthDist = Float.POSITIVE_INFINITY;
		Machine current = null;
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) instanceof Machine && list.get(i) != machine) {
				current = (Machine) list.get(i);
				if (!current.isAlive() || current.isDestroyed() || !fraction.isEnemy(current.getFraction()))
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

	private Machine searchTargetFriend() {
		if (RNG.randomBoolean())
			return null;
		Ship machine = getMachine();
		Fraction fraction = machine.getFraction();
		Flock flock = machine.getFlock();
		Machine target = null;
		List<GameObject> list = machine.getWorld().getOctTree().query(machine,
				MachineController.MAX_TARGET_DIST * machine.getSize());
		float minDistSq = Maths.sqr(MachineController.MAX_TARGET_DIST * machine.getSize());
		Machine current = null;
		boolean ignoreFlock = flock == null;
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) instanceof Machine && list.get(i) != machine) {
				current = (Machine) list.get(i);
				if (!current.isAlive() || current.isDestroyed() || !current.canPickShip(machine))
					continue;
				if (ignoreFlock ? fraction.isFriendly(current.getFraction()) : flock == current.getFlock()) {
					float distSq = Maths.distSq(machine.getPos(), current.getPos());
					if (distSq < minDistSq) {
						minDistSq = distSq;
						target = current;
					}
				}
			}
		return target;
	}

	private Container searchTargetCargo() {
		if (RNG.randomBoolean())
			return null;
		Ship machine = getMachine();
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
		float minDistSq = Maths.sqr(MachineController.MAX_TARGET_DIST * machine.getSize());
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

	private void processFlocking(Renderer renderer, float dt) {
		Ship machine = getMachine();

		if (machine.getFlock() != null) {
			Flock flock = machine.getFlock();
			float minSpeed = flock.hasStations() ? getStationTargetInfo(tempTargetPos)
					: getFlockingTargetInfo(tempTargetPos);
			if (flock.getLeader() != null) {
				Direction leaderDirection = flock.getLeader().getDirection();
				Direction machineDirection = machine.getDirection();
				float dot = Vector3f.dot(leaderDirection.getUp(), machineDirection.getUp());
				rotationControl.rollUp(Maths.sqr(Maths.map(dot, -1f, 1f, 1f, 0f)) * dt);
				if (Maths.distSq(machine.getPos(), flock.getLeader().getPos()) < Maths.sqr(16f * machine.getSize()))
					afterburnerControl.engage();
			}
			if (minSpeed == 0f || machine.getMaxSpeed() == minSpeed || machine.getVel().length() <= minSpeed)
				horizontalControl.forward(dt);
			else
				horizontalControl.brake(dt);
		} else {
			Vector3f forward = machine.getDirection().getForward();
			Vectors.addScaled(machine.getPos(), forward, 256f, tempTargetPos);
			horizontalControl.forward(dt);
		}

		WindowUtils.calculateScreenCoords(tempTargetPos, renderer, tempTargetCoords);

		if (tempTargetCoords.z >= 0f) {
			machine.rotateTo(tempTargetCoords.x, tempTargetCoords.y, false, false, dt);
			if (RNG.chance(0.05f) && horizontalControl.getThrust() < 0.25f)
				afterburnerControl.engage();
		} else {
			if (horizontalControl.getThrust() > 0.75f)
				horizontalControl.brake(dt);
			verticalControl.reverse(dt);
			if (RNG.chance(0.1f))
				afterburnerControl.engage();
			if (RNG.chance(0.4f))
				machine.rotateTo(RNG.chance(0.1f) ? -1f : 1f, RNG.randomFloat(-0.25f, 0.25f), true, true, dt);
			else
				machine.rotateTo(RNG.randomFloat(-0.25f, 0.25f), RNG.chance(0.1f) ? -1f : 1f, false, true, dt);
		}
	}

	private void processTargetEnemy(Renderer renderer, float dt) {
		Ship machine = getMachine();

		boolean flee = getFlockHealth(machine) < 0.05f * getFlockHealth(targetEnemy);

		Vector3f nextTargetPos = Projectile.getNextTargetPos(machine, targetEnemy);

		if (flee) {
			Vector3f avgPos = getFlockAvgPos(targetEnemy);
			Vectors.subScaled(machine.getPos(), avgPos, 16f, tempTargetPos);
			afterburnerControl.engage();
		} else {
			float dist = Maths.dist(targetEnemy.getPos(), machine.getPos());
			float blendFactor = Maths.mapLimit((dist - targetEnemy.getSize()) / machine.getSize(), 4f, 32f, 0f, 1f);
			Vector3f direction = Maths.direction(targetEnemy.getPos(), machine.getPos());
			Vector3f.cross(direction, targetEnemy.getDirection().getRight(), tempDirection);
			Vectors.addScaled(direction, Maths.direction(targetEnemy.getPos(), machine.getPos()), 3f, tempDirection);
			Vectors.addScaled(targetEnemy.getPos(), tempDirection, machine.getSize(), tempTargetPos);
			Maths.blend(nextTargetPos, tempTargetPos, blendFactor, tempTargetPos);
			machine.ejectShips();
		}

		WindowUtils.calculateScreenCoords(tempTargetPos, renderer, tempTargetCoords);

		if (tempTargetCoords.z >= 0f) {
			if (tempTargetCoords.y < -0.25f)
				verticalControl.forward(dt);
			else if (tempTargetCoords.y > 0.25f)
				verticalControl.reverse(dt);
			float dist = tempTargetCoords.z / machine.getSize();
			if (dist > 16f)
				afterburnerControl.engage();
			if (dist > 2f)
				machine.rotateTo(tempTargetCoords.x, tempTargetCoords.y, true, true, dt);
			else {
				if (horizontalControl.getThrust() > 0.5f && RNG.randomBoolean())
					horizontalControl.brake(dt);
				machine.rotateTo(RNG.randomFloat(-1f, 1f), 1f, true, true, dt);
			}
			if (RNG.randomBoolean())
				rotationControl.rollDown(dt);
			else if (dist > 8f)
				horizontalControl.forward(dt);
			else if (dist > 2f)
				horizontalControl.brake(dt / dist);
			if (dist < 8f && Maths.abs(tempTargetCoords.y) > 0.5f)
				horizontalControl.reverse(dt);
		} else {
			if (horizontalControl.getThrust() > 0.75f)
				horizontalControl.brake(dt);
			verticalControl.reverse(dt);
			afterburnerControl.engage();
			if (RNG.chance(0.2f))
				machine.rotateTo(RNG.chance(0.1f) ? -1f : 1f, RNG.randomFloat(-0.25f, 0.25f), true, true, dt);
			else
				machine.rotateTo(RNG.randomFloat(-0.25f, 0.25f), RNG.chance(0.1f) ? -1f : 1f, false, true, dt);
		}

		if (horizontalControl.getThrust() < 0.25f)
			horizontalControl.forward(dt);

		WindowUtils.calculateScreenCoords(nextTargetPos, renderer, tempTargetCoords);
		if (tempTargetCoords.z > 0f && RNG.chance(0.7f) && Maths.abs(tempTargetCoords.x) < 0.1f
				&& Maths.abs(tempTargetCoords.y) < 0.1f)
			machine.shootManual();
		else {
			WindowUtils.calculateScreenCoords(targetEnemy.getPos(), renderer, tempTargetCoords);
			if (tempTargetCoords.z > 0f && RNG.chance(0.7f) && Maths.abs(tempTargetCoords.x) < 0.1f
					&& Maths.abs(tempTargetCoords.y) < 0.1f)
				machine.shootManual();
		}
		if (RNG.chance(0.6f))
			machine.shootAutoAim();
	}

	private void processTargetFriend(Renderer renderer, float dt) {
		Ship machine = getMachine();

		tempTargetPos.set(targetFriend.getPos());
		Vectors.addScaled(tempTargetPos, targetFriend.getVel(), dt, tempTargetPos);

		WindowUtils.calculateScreenCoords(tempTargetPos, renderer, tempTargetCoords);

		if (tempTargetCoords.z >= 0f) {
			if (tempTargetCoords.y < -0.25f)
				verticalControl.forward(dt);
			else if (tempTargetCoords.y > 0.25f)
				verticalControl.reverse(dt);
			float dist = tempTargetCoords.z / machine.getSize();
			if (dist > 32f)
				afterburnerControl.engage();
			if (dist > 2f)
				machine.rotateTo(tempTargetCoords.x, tempTargetCoords.y, true, true, dt);
			else {
				if (horizontalControl.getThrust() > 0.5f && RNG.randomBoolean())
					horizontalControl.brake(dt);
				machine.rotateTo(RNG.randomFloat(-1f, 1f), 1f, true, true, dt);
			}
			if (RNG.chance(0.1f))
				rotationControl.rollDown(dt);
			else if (dist > 8f)
				horizontalControl.forward(dt);
			else if (dist > 2f)
				horizontalControl.brake(dt / dist);
			if (dist < 8f && Maths.abs(tempTargetCoords.y) > 0.5f)
				horizontalControl.reverse(dt);
		} else {
			if (horizontalControl.getThrust() > 0.75f)
				horizontalControl.brake(dt);
			verticalControl.reverse(dt);
			if (RNG.chance(0.1f))
				machine.rotateTo(RNG.chance(0.1f) ? -1f : 1f, RNG.randomFloat(-0.25f, 0.25f), true, true, dt);
			else
				machine.rotateTo(RNG.randomFloat(-0.25f, 0.25f), RNG.chance(0.1f) ? -1f : 1f, false, true, dt);
		}

		if (horizontalControl.getThrust() < 0.1f)
			horizontalControl.forward(dt);

		targetFriend.requestPickShip(machine);
	}

	private void processTargetCargo(Renderer renderer, float dt) {
		Ship machine = getMachine();

		tempTargetPos.set(targetCargo.getPos());

		WindowUtils.calculateScreenCoords(tempTargetPos, renderer, tempTargetCoords);

		if (Maths.distSq(machine.getPos(), targetCargo.getPos()) < Maths
				.sqr(Machine.MAX_CARGO_DIST * machine.getSize()))
			machine.pickCargoObject(targetCargo);

		if (tempTargetCoords.z >= 0f) {
			machine.rotateTo(tempTargetCoords.x, tempTargetCoords.y, false, false, dt);
		} else {
			horizontalControl.reverse(dt);
			machine.rotateTo(1f, RNG.randomFloat(-0.1f, 0.1f), false, true, dt);
		}

		float dot = Vector3f.dot(machine.getDirection().getForward(),
				Maths.direction(machine.getPos(), targetCargo.getPos()));
		float maxThrust = dot * Maths.mapLimit(Maths.dist(machine.getPos(), targetCargo.getPos()) / machine.getSize(),
				1f, 16f, 0.2f, 1f);
		if (horizontalControl.getThrust() > maxThrust)
			horizontalControl.reverse(dt);
		else
			horizontalControl.forward(dt);
	}

	private void processTargetPlanet(Renderer renderer, float dt) {
		Ship machine = getMachine();

		tempTargetPos.set(targetPlanet.getPos());

		Vector3f direction = Maths.direction(targetPlanet.getPos(), machine.getPos());
		Vector3f.cross(direction, targetPlanet.getDirection().getUp(), direction);
		Vectors.addScaled(tempTargetPos, direction, 2f * (targetPlanet.getSize() + machine.getSize()), tempTargetPos);

		WindowUtils.calculateScreenCoords(tempTargetPos, renderer, tempTargetCoords);

		if (tempTargetCoords.z >= 0f) {
			machine.rotateTo(tempTargetCoords.x, tempTargetCoords.y, false, false, dt);
			horizontalControl.forward(dt);
		} else {
			if (horizontalControl.getThrust() > 0.75f)
				horizontalControl.brake(dt);
			verticalControl.reverse(dt);
			afterburnerControl.engage();
			if (RNG.chance(0.2f))
				machine.rotateTo(RNG.chance(0.1f) ? -1f : 1f, RNG.randomFloat(-0.25f, 0.25f), true, true, dt);
			else
				machine.rotateTo(RNG.randomFloat(-0.25f, 0.25f), RNG.chance(0.1f) ? -1f : 1f, false, true, dt);
		}
	}

	private static final Vector3f tempTargetPos = new Vector3f();
	private static final Vector3f tempTargetCoords = new Vector3f();
	private static final Vector3f tempShipPos = new Vector3f();
	private static final Vector3f tempCurrentPos = new Vector3f();
	private static final Vector3f tempCurrentVel = new Vector3f();
	private static final Vector3f tempOrbiting = new Vector3f();
	private static final Vector3f tempAlignment = new Vector3f();
	private static final Vector3f tempCentralization = new Vector3f();
	private static final Vector3f tempCohesion = new Vector3f();
	private static final Vector3f tempSeparation = new Vector3f();
	private static final Vector3f tempDirection = new Vector3f();
	private static final Vector3f tempCenter = new Vector3f();
	private static final Vector3f tempSum = new Vector3f();

	private float getStationTargetInfo(Vector3f dest) {
		Ship machine = getMachine();
		List<Machine> list = machine.getFlock().getList();

		float shipSize = getMachine().getSize();
		float invShipSize = 1f / getMachine().getSize();
		tempShipPos.set(getMachine().getPos());
		float currentSize = 0f;

		float minDistSq = Float.POSITIVE_INFINITY;
		float distToStation = 0f;
		float stationSize = 0f;

		tempOrbiting.set(0f, 0f, 0f);
		tempCohesion.set(0f, 0f, 0f);
		tempSeparation.set(0f, 0f, 0f);

		Machine current = null;
		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			if (current == machine)
				continue;
			currentSize = current.getSize();
			tempCurrentPos.set(current.getPos());
			tempCurrentVel.set(current.getVel());
			tempCurrentVel.scale(currentSize);
			float distSq = Maths.distSq(tempShipPos, tempCurrentPos) / (shipSize + currentSize);
			if (distSq < minDistSq)
				minDistSq = distSq;
			tempDirection.set(Maths.direction(tempShipPos, tempCurrentPos));
			Vectors.addScaled(tempCohesion, tempDirection, currentSize, tempCohesion);
			Vectors.addScaled(tempSeparation, tempDirection, -currentSize * invShipSize / distSq, tempSeparation);
			if (current instanceof Station) {
				distToStation = Maths.dist(tempShipPos, tempCurrentPos);
				stationSize = currentSize;
				tempDirection.set(Maths.direction(tempCurrentPos, tempShipPos));
				Vector3f.cross(tempDirection, current.getDirection().getUp(), tempDirection);
				tempDirection.scale(Maths.SQRT2 * currentSize + shipSize);
				Vector3f.add(tempCurrentPos, tempDirection, tempDirection);
				tempDirection.set(Maths.direction(tempShipPos, tempDirection));
				Vectors.addScaled(tempOrbiting, tempDirection, currentSize, tempOrbiting);

				Vectors.addScaled(tempCohesion, tempDirection, currentSize * currentSize, tempCohesion);
			}
		}
		tempOrbiting.normalise(tempOrbiting).scale(16f);
		tempCohesion.normalise(tempCohesion).scale(4f);
		tempSeparation.normalise(tempSeparation).scale(2f);

		Vectors.sum(tempOrbiting, tempCohesion, tempSeparation).normalise(tempSum);
		tempSum.scale(64f * machine.getMaxSpeed() * shipSize);
		Vector3f.add(tempShipPos, tempSum, dest);

		// RenderUtils.renderDebugPoints(0.5f * shipSize, getMachine()
		// .getDirection().getMatrix(shipPos, true), Game.game
		// .getRenderers(), sum);

		if (distToStation > 4f * stationSize)
			return machine.getMaxSpeed();
		return Maths.mapLimit(distToStation, stationSize, 4f * stationSize, 0.25f, 1f) * machine.getMaxSpeed();
	}

	private float getFlockingTargetInfo(Vector3f dest) {
		Ship machine = getMachine();
		Flock flock = machine.getFlock();
		List<Machine> list = flock.getList();
		Machine leader = flock.getLeader();
		boolean isLeader = machine == leader;

		boolean collectingCargo = flock.isCollectingCargo();
		if (collectingCargo) {
			leader = null;
			isLeader = false;
		}

		float shipSize = machine.getSize();
		float invShipSize = 1f / machine.getSize();
		tempShipPos.set(getMachine().getPos());
		float currentSize = 0f;
		float weight = 0f;

		tempAlignment.set(machine.getDirection().getForward());
		tempCohesion.set(tempAlignment);
		tempSeparation.set(tempAlignment);
		tempCentralization.set(0f, 0f, 0f);

		tempCenter.set(tempShipPos);
		float minSpeed = machine.getMaxSpeed();
		float minDistSq = Float.POSITIVE_INFINITY;
		Ship current = null;
		for (int i = 0; i < list.size(); i++) {
			current = (Ship) list.get(i);
			if (current == machine || current.getParent() != null)
				continue;
			if (isLeader)
				weight = 0f;
			else if (current == leader)
				weight = 25f;
			else
				weight = 1f;
			if (current instanceof Ship) {
				float speed = ((Ship) current).getMaxSpeed();
				if (speed < minSpeed)
					minSpeed = speed;
			}
			if (weight == 0f)
				continue;
			currentSize = current.getSize();
			tempCurrentPos.set(current.getPos());
			tempCurrentVel.set(current.getDirection().getForward());
			tempCurrentVel.scale(currentSize);
			float distSq = Maths.distSq(tempShipPos, tempCurrentPos) / (shipSize + currentSize);
			if (distSq < minDistSq)
				minDistSq = distSq;

			Vectors.addScaled(tempAlignment, tempCurrentVel, weight * shipSize, tempAlignment);
			tempDirection.set(Maths.direction(tempShipPos, tempCurrentPos));
			Vectors.addScaled(tempCohesion, tempDirection, weight * currentSize, tempCohesion);
			Vectors.addScaled(tempCenter, tempCurrentPos, 1f, tempCenter);
			Vectors.addScaled(tempSeparation, tempDirection, -weight * currentSize * invShipSize / distSq,
					tempSeparation);
		}
		tempCentralization.set(Maths.direction(tempShipPos, tempCenter));

		if (collectingCargo) {
			tempAlignment.normalise(tempAlignment).scale(2f);
			tempCohesion.normalise(tempCohesion).scale(16f);
			tempCentralization.normalise(tempCentralization).scale(8f);
			tempSeparation.normalise(tempSeparation).scale(32f);
		} else {
			tempAlignment.normalise(tempAlignment).scale(32f);
			tempCohesion.normalise(tempCohesion).scale(12f);
			tempCentralization.normalise(tempCentralization).scale(6f);
			tempSeparation.normalise(tempSeparation).scale(32f);
		}

		Vector3f sum = Vectors.sum(tempAlignment, tempCohesion, tempCentralization, tempSeparation).normalise(null);
		sum.scale(64f * machine.getMaxSpeed() * shipSize);
		Vector3f.add(tempShipPos, sum, dest);

		// RenderUtils.renderDebugPoints(0.5f * shipSize, getMachine()
		// .getDirection().getMatrix(shipPos, true), Game.game
		// .getRenderers(), sum);

		if (collectingCargo)
			return 0.25f * minSpeed;
		if (isLeader)
			return 0.8f * minSpeed;
		if (leader == null)
			return 0.7f * minSpeed;
		float distToLeader = Maths.dist(leader.getPos(), tempShipPos);
		float leaderSize = leader.getSize();
		return Maths.mapLimit(distToLeader, 1f * leaderSize, 4f * leaderSize, 0.5f * minSpeed, 1.5f * minSpeed);
	}

	@Override
	public void onDamage(Projectile proj) {
		if (targetEnemy != null)
			return;
		Machine parent = proj.getParent();
		if (parent.getFraction() == getMachine().getFraction() || !GameObject.isAlive(parent) || parent.isDestroyed())
			return;
		Machine targetEnemy = searchTargetEnemy();
		if (targetEnemy != null)
			return;
		targetEnemy = proj.getParent();
		setTargetEnemy(targetEnemy);
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

	@Override
	public void setTargetFriend(Machine targetFriend) {
		setMode(TargetMode.SYSTEM);
		super.setTargetFriend(targetFriend);
	}

	@Override
	public void setTargetEnemy(Machine targetEnemy) {
		setMode(TargetMode.BATTLE);
		super.setTargetEnemy(targetEnemy);
	}

	@Override
	public void setTargetCargo(CargoObject targetCargo) {
		setMode(TargetMode.SYSTEM);
		super.setTargetCargo(targetCargo);
	}

	@Override
	public void setTargetPlanet(Planet targetPlanet) {
		setMode(TargetMode.GALACTIC);
		super.setTargetPlanet(targetPlanet);
	}

	private static float getFlockHealth(Machine machine) {
		if (machine.getFlock() == null)
			return machine.getHealth();
		return machine.getFlock().getHealth();
	}

	private static Vector3f getFlockAvgPos(Machine machine) {
		if (machine.getFlock() == null)
			return machine.getPos();
		return machine.getFlock().getAveragePos();
	}

}
