package kaba4cow.intersector.toolbox.flocking;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.machinecontrollers.shipcontrollers.ShipPlayerController;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.machines.Station;

public class Flock {

	private final List<Machine> list;
	private Machine leader;

	private final Vector3f averagePos;
	private float health;

	private boolean collectingCargo;

	public Flock(boolean independent) {
		this.list = new ArrayList<Machine>();
		this.leader = null;
		this.averagePos = new Vector3f();
		this.health = 0f;
		this.collectingCargo = false;
		if (!independent)
			FlockManager.add(this);
	}

	public void update(float dt) {
		averagePos.set(0f, 0f, 0f);
		int num = 0;
		health = 0f;
		collectingCargo = false;
		Machine current = null;
		for (int i = list.size() - 1; i >= 0; i--) {
			current = list.get(i);
			if (!GameObject.isAlive(current))
				list.remove(i);
			else {
				Vector3f.add(averagePos, current.getPos(), averagePos);
				num++;
				health += current.getHealth();
				if (current.getController().getTargetCargo() != null)
					collectingCargo = true;
			}
		}
		averagePos.scale(1f / (float) num);
		if (!GameObject.isAlive(leader) || leader.isDestroyed())
			leader = searchLeader();
		if (list.size() == 1)
			destroy();
	}

	private Machine searchLeader() {
		Machine newLeader = null;
		Machine current = null;
		float maxSize = Float.NEGATIVE_INFINITY;
		float size;
		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			if (current instanceof Station
					|| current.getController() instanceof ShipPlayerController)
				return current;
			size = current.getSize();
			if (size > maxSize) {
				maxSize = size;
				newLeader = current;
			}
		}
		return newLeader;
	}

	public void requestTarget(Machine machine) {
		Flock flock = machine.getFlock();
		if (flock == null)
			for (int i = 0; i < list.size(); i++)
				list.get(i).getController().requestTarget(machine);
		else
			for (int i = 0; i < list.size(); i++)
				list.get(i)
						.getController()
						.requestTarget(
								flock.list.get(RNG.randomInt(flock.list.size())));
	}

	public boolean isCollectingCargo() {
		return collectingCargo;
	}

	public boolean hasStations() {
		for (int i = 0; i < list.size(); i++)
			if (list.get(i) instanceof Station)
				return true;
		return false;
	}

	public void clear() {
		for (int i = 0; i < list.size(); i++)
			list.get(i).setFlock(null);
		list.clear();
	}

	public void add(Machine object) {
		if (GameObject.isAlive(object)
				&& (object.getFlock() == null || object.getFlock() == this)
				&& !list.contains(object)) {
			list.add(object);
			object.setFlock(this);
		}
	}

	public void destroy() {
		for (int i = 0; i < list.size(); i++)
			list.get(i).setFlock(null);
		list.clear();
	}

	public Machine getLeader() {
		return leader;
	}

	public Machine randomMachine() {
		return list.get(RNG.randomInt(list.size()));
	}

	public List<Machine> getList() {
		return list;
	}

	public Vector3f getAveragePos() {
		return averagePos;
	}

	public float getHealth() {
		return health;
	}

	public int size() {
		return list.size();
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

}
