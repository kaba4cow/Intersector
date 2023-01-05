package kaba4cow.intersector.gameobjects.machines.controllers;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.gameobjects.cargo.CargoObject;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;

public abstract class MachineController {

	public static final float MAX_TARGET_DIST = 300f;

	protected Machine machine;

	protected Machine targetFriend;
	protected Machine targetEnemy;
	protected CargoObject targetCargo;
	protected Planet targetPlanet;
	protected SystemObject targetSystem;

	private boolean lostTarget;
	private float lostTargetTime;

	public MachineController() {
		this.resetTargets();
		this.lostTarget = false;
		this.lostTargetTime = 0f;
	}

	public abstract void update(float dt);

	protected void updateTargets(float dt) {
		Vector3f pos = getMachine().getPos();
		float maxDistSq = Maths.sqr(MAX_TARGET_DIST * getMachine().getSize());

		if (lostTarget) {
			lostTargetTime += dt;
			if (lostTargetTime > 3f || targetFriend != null || targetEnemy != null || targetCargo != null
					|| targetPlanet != null) {
				lostTarget = false;
				lostTargetTime = 0f;
			}
		}

		if (targetFriend != null)
			if (!GameObject.isAlive(targetFriend) || targetFriend.isDestroyed()
					|| !targetFriend.isFarTargetable() && Maths.distSq(pos, targetFriend.getPos()) > maxDistSq) {
				targetFriend = null;
				loseTarget();
			}

		if (targetEnemy != null)
			if (!GameObject.isAlive(targetEnemy) || targetEnemy.isDestroyed()
					|| !targetEnemy.isFarTargetable() && Maths.distSq(pos, targetEnemy.getPos()) > maxDistSq) {
				targetEnemy = null;
				loseTarget();
			}

		if (targetCargo != null)
			if (!GameObject.isAlive(targetCargo) || targetCargo.getParent() != null
					|| Maths.distSq(pos, targetCargo.getPos()) > maxDistSq) {
				targetCargo = null;
				loseTarget();
			}

		if (targetSystem != null) {
			if (targetSystem.equals(machine.getWorld().getSystem()))
				targetSystem = null;
		}
	}

	public void resetTargets() {
		targetFriend = null;
		targetEnemy = null;
		targetCargo = null;
		targetPlanet = null;
	}

	private void loseTarget() {
		lostTarget = true;
		lostTargetTime = 0f;
	}

	public boolean hasLostTarget() {
		return lostTarget;
	}

	public abstract void onDamage(Projectile proj);

	public void requestTarget(Machine machine) {

	}

	public Machine getTargetFriend() {
		return targetFriend;
	}

	public void setTargetFriend(Machine targetFriend) {
		if (targetFriend == null || !targetFriend.isDestroyed())
			this.targetFriend = targetFriend;
	}

	public Machine getTargetEnemy() {
		return targetEnemy;
	}

	public void setTargetEnemy(Machine targetEnemy) {
		if (targetEnemy == null || !targetEnemy.isDestroyed())
			this.targetEnemy = targetEnemy;
	}

	public CargoObject getTargetCargo() {
		return targetCargo;
	}

	public void setTargetCargo(CargoObject targetCargo) {
		if (targetCargo == null || targetCargo.getParent() == null)
			this.targetCargo = targetCargo;
	}

	public Planet getTargetPlanet() {
		return targetPlanet;
	}

	public void setTargetPlanet(Planet targetPlanet) {
		this.targetPlanet = targetPlanet;
	}

	public SystemObject getTargetSystem() {
		return targetSystem;
	}

	public void setTargetSystem(SystemObject targetSystem) {
		this.targetSystem = targetSystem;
	}

	public Machine getMachine() {
		return machine;
	}

	public MachineController setMachine(Machine machine) {
		this.machine = machine;
		this.resetTargets();
		return this;
	}

}
