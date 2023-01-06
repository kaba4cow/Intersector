package kaba4cow.intersector.gameobjects.machines;

import java.util.ArrayList;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.files.ShipFile;
import kaba4cow.intersector.files.StationFile;
import kaba4cow.intersector.galaxyengine.objects.StationObject;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.machines.controllers.shipcontrollers.ShipAIController;
import kaba4cow.intersector.gameobjects.machines.controllers.stationcontrollers.StationAIController;
import kaba4cow.intersector.gameobjects.machines.controllers.stationcontrollers.StationController;
import kaba4cow.intersector.toolbox.flocking.Flock;

public class Station extends Machine {

	private final StationController controller;

	private final StationObject stationObject;

	private final ArrayList<Ship> shipList;

	public Station(World world, StationObject stationObject) {
		super(world, stationObject.getFraction(), stationObject.file, stationObject.worldPosition);

		this.stationObject = stationObject;

		this.controller = new StationAIController().setMachine(this);

		this.shipList = new ArrayList<Ship>();

		float portDensity = RNG.randomFloat(0.2f);
		for (int i = 0; i < ports.length; i++)
			if (RNG.chance(portDensity)) {
				ShipFile shipFile = fraction.getRandomShip(ports[i].min, ports[i].max);
				if (shipFile != null)
					addShip(new Ship(world, fraction, shipFile, new Vector3f(), new ShipAIController()), i);
			}
	}

	@Override
	public void update(float dt) {
		if (isDestroyed()) {
			disableTurrets();
			disableLaunchers();
		} else
			controller.update(dt);
		rotate(direction.getUp(), stationObject.rotationSpeed * dt);

		Maths.blend(Vectors.INIT3, vel, dt, vel);

		for (int i = shipList.size() - 1; i >= 0; i--)
			if (!GameObject.isAlive(shipList.get(i)))
				shipList.remove(i);

		super.update(dt);
	}

	@Override
	public void rotate(Vector3f axis, float angle) {
		super.rotate(axis, angle);
		for (int i = 0; i < shipList.size(); i++)
			shipList.get(i).rotate(axis, angle);
	}

	public boolean canPickShip(Ship ship) {
		if (shipPickTime > 0f)
			return false;
		float shipSize = ship.getCollisionSize();
		for (int i = 0; i < ports.length; i++)
			if (!ports[i].occupied && shipSize >= ports[i].min && shipSize <= ports[i].max)
				return true;
		return false;
	}

	public Machine ejectShips() {
		if (shipEjectTime > 0f || shipList.isEmpty())
			return this;
		removeShip(shipList.get(0));
		return this;
	}

	public void requestPickShip(Ship ship) {
		if (shipList.contains(ship))
			return;
		addShip(ship);
	}

	public void requestEjectShip(Ship ship) {
		if (shipEjectTime > 0f || !shipList.contains(ship))
			return;
		removeShip(ship);
	}

	public boolean isPickable(Ship ship) {
		if (shipPickTime > 0f || ship == null || ship.getParent() != null)
			return false;
		for (int i = 0; i < ports.length; i++)
			if (isPickable(ship, i))
				return true;
		return false;
	}

	public boolean isPickable(Ship ship, int index) {
		if (shipPickTime > 0f || ship == null || ship.getParent() != null)
			return false;
		if (ports[index].occupied || ship.getCollisionSize() < ports[index].min
				|| ship.getCollisionSize() > ports[index].max)
			return false;
		return ship.getParent() == null && Maths.distSq(ship.getPos(), pos) < Maths.sqr(MAX_SHIP_DIST * size);
	}

	public Machine addShip(Ship ship) {
		if (ship.getParent() != null && ship.getParent() != this)
			return this;
		for (int i = 0; i < ports.length; i++)
			if (isPickable(ship, i)) {
				setShip(i, ship);
				return this;
			}
		return this;
	}

	public Machine addShip(Ship ship, int index) {
		if (ship.getParent() != null && ship.getParent() != this)
			return this;
		if (ports[index].occupied)
			addShip(ship);
		else
			setShip(index, ship);
		return this;
	}

	private Machine setShip(int index, Ship ship) {
		ship.setParent(this, ports[index]);
		shipList.add(ship);
		shipPickTime = MAX_SHIP_PICK_TIME;
		return this;
	}

	public Machine removeShip(Ship ship) {
		if (shipEjectTime > 0f || ship == null || ship.getParent() != null && ship.getParent() != this)
			return this;
		ship.onEject();
		shipList.remove(ship);
		shipEjectTime = MAX_SHIP_EJECT_TIME;
		return this;
	}

	public Machine removeShips() {
		if (!shipList.isEmpty())
			removeShip(shipList.get(0));
		return this;
	}

	@Override
	public Station setFlock(Flock flock) {
		super.setFlock(flock);
		for (int i = 0; i < shipList.size(); i++)
			shipList.get(i).setFlock(flock);
		return this;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		for (int i = 0; i < shipList.size(); i++)
			shipList.get(i).destroy();
	}

	@Override
	protected void onFinalDestroy() {
		super.onFinalDestroy();
		for (int i = 0; i < shipList.size(); i++)
			shipList.get(i).onParentDestroy();
	}

	@Override
	public boolean isFarTargetable() {
		return true;
	}

	@Override
	public StationFile getFile() {
		return (StationFile) file;
	}

	public StationObject getStationObject() {
		return stationObject;
	}

	@Override
	public StationController getController() {
		return controller;
	}

}
