package kaba4cow.intersector.gameobjects.machinecontrollers.shipcontrollers;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import kaba4cow.engine.Input;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.gameobjects.cargo.CargoObject;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.Target;
import kaba4cow.intersector.gameobjects.targets.TargetMode;

public class ShipPlayerController extends ShipController {

	public ShipPlayerController() {
		super();
	}

	@Override
	public void update(float dt) {
		updateTargets(dt);

		if (Input.isKeyDown(Keyboard.KEY_J))
			hyperControl.switchEngage();

		if (getMachine().isHyperEngaged()) {
			if (Input.isKey(Keyboard.KEY_W))
				hyperControl.forward(dt);
			if (Input.isKey(Keyboard.KEY_S))
				hyperControl.reverse(dt);

			if (Input.isKey(Keyboard.KEY_R))
				hyperControl.shiftUp();
			if (Input.isKey(Keyboard.KEY_F))
				hyperControl.shiftDown();

			if (Input.isKey(Keyboard.KEY_LSHIFT))
				hyperControl.brake(dt);
			
			if (Input.isKeyDown(Keyboard.KEY_G))
				getMachine().jump();
		} else {
			if (Input.isKey(Keyboard.KEY_W))
				horizontalControl.forward(dt);
			if (Input.isKey(Keyboard.KEY_S))
				horizontalControl.reverse(dt);

			if (Input.isKey(Keyboard.KEY_R))
				verticalControl.forward(dt);
			if (Input.isKey(Keyboard.KEY_F))
				verticalControl.reverse(dt);

			if (Input.isKey(Keyboard.KEY_LSHIFT))
				horizontalControl.brake(dt);

			if (Input.isKey(Keyboard.KEY_TAB))
				afterburnerControl.engage();

			if (Input.isKey(Keyboard.KEY_L))
				getMachine().ejectShips();
			if (Input.isKey(Keyboard.KEY_K))
				getMachine().ejectContainers();
			if (Input.isKey(Keyboard.KEY_B))
				getMachine().pickCargoObject(targetCargo);
		}

		if (Input.isKey(Keyboard.KEY_E))
			rotationControl.yawUp(dt);
		if (Input.isKey(Keyboard.KEY_Q))
			rotationControl.yawDown(dt);

		if (Input.isKey(Keyboard.KEY_A))
			rotationControl.rollUp(dt);
		if (Input.isKey(Keyboard.KEY_D))
			rotationControl.rollDown(dt);

		if (Input.isKey(Keyboard.KEY_DOWN))
			rotationControl.pitchUp(dt);
		if (Input.isKey(Keyboard.KEY_UP))
			rotationControl.pitchDown(dt);

		if (Input.isKey(Keyboard.KEY_SPACE))
			getMachine().shootManual();
		if (Input.isKey(Keyboard.KEY_LCONTROL))
			getMachine().shootAutoAim();

		if (Input.isKeyDown(Keyboard.KEY_Z))
			getMachine().switchTurretsEnabled();
		if (Input.isKeyDown(Keyboard.KEY_C))
			getMachine().switchLaunchersEnabled();

		if (Input.isKey(Keyboard.KEY_T))
			selectTarget();

		if (Input.isKey(Keyboard.KEY_1))
			setMode(TargetMode.BATTLE);
		if (Input.isKey(Keyboard.KEY_2))
			setMode(TargetMode.SYSTEM);
		if (Input.isKey(Keyboard.KEY_3))
			setMode(TargetMode.GALACTIC);

		if (Input.isKeyDown(Keyboard.KEY_4))
			getMachine().switchHudEnabled();
		if (Input.isKeyDown(Keyboard.KEY_5))
			getMachine().switchHudInfoEnabled();

		if (Input.isButton(0))
			getMachine().smoothRotateTo(
					WindowUtils.toNormalizedX(Mouse.getX()),
					WindowUtils.toNormalizedY(Mouse.getY()), dt);

		if (Input.isKeyDown(Keyboard.KEY_N))
			getMachine().getVel().scale(16f);

		if (Input.isKey(Keyboard.KEY_P))
			getMachine().destroy(getMachine());
	}

	public void selectTarget() {
		List<Target> targetList = Target.getOnScreenObjects(getMachine(),
				mode.getTargets());
		float minDist = Float.POSITIVE_INFINITY;
		float maxSize = 0f;
		float mouseX = WindowUtils.toNormalizedX(Mouse.getX());
		float mouseY = WindowUtils.toNormalizedY(Mouse.getY());
		Target closest = null;
		Target current = null;
		for (int i = 0; i < targetList.size(); i++) {
			current = targetList.get(i);
			if (current.getScreenCoords().z > minDist
					|| current.getObject().getCollisionSize() < maxSize
					|| Maths.dist(mouseX, mouseY, current.getScreenCoords().x,
							current.getScreenCoords().y) > 0.1f)
				continue;
			minDist = current.getScreenCoords().z;
			maxSize = current.getObject().getCollisionSize();
			closest = current;
		}
		setTarget(closest);
	}

	private void setTarget(Target target) {
		setTargetEnemy(null);
		setTargetFriend(null);
		setTargetCargo(null);
		setTargetPlanet(null);
		if (target != null) {
			GameObject object = target.getObject();
			switch (target.getTargetType()) {
			case CARGO:
				setTargetCargo((CargoObject) object);
				break;
			case MACHINE:
				Machine machine = (Machine) object;
				if (mode == TargetMode.BATTLE)
					setTargetEnemy(machine);
				else
					setTargetFriend(machine);
				break;
			case PLANET:
				setTargetPlanet((Planet) object);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onDamage(Projectile proj) {

	}

}
