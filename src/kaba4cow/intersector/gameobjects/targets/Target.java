package kaba4cow.intersector.gameobjects.targets;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.intersector.galaxyengine.objects.GalacticObject;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.gameobjects.machines.Machine;
import kaba4cow.intersector.gameobjects.machines.Ship;
import kaba4cow.intersector.gameobjects.machines.controllers.MachineController;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.utils.GameUtils;

public class Target {

	private final GameObject object;

	private final Vector3f screenCoords;
	private final Vector3f nextScreenCoords;

	public Target(GameObject object, Vector3f screenCoords, Vector3f nextScreenCoords) {
		this.object = object;
		this.screenCoords = new Vector3f(screenCoords);
		this.nextScreenCoords = new Vector3f(nextScreenCoords);
	}

	public static Target getDirectionTarget(Ship ship) {
		Vector3f pos = Vectors.addScaled(ship.getPos(), ship.getDirection().getForward(),
				512f + 8f * ship.getCollisionSize(), null);
		Vector3f targetCoords = WindowUtils.calculateScreenCoords(pos, GameUtils.getPlayerPov(), null);
		if (targetCoords.z > 0f)
			return new Target(null, targetCoords, targetCoords);
		else
			return null;
	}

	public static Target getSystemTarget(Ship ship) {
		SystemObject target = ship.getController().getTargetSystem();
		if (target == null)
			return null;
		SystemObject home = ship.getWorld().getSystem();
		Vector3f direction = Maths.direction(home.getPos(), target.getPos());
		Vector3f pos = Vectors.addScaled(ship.getPos(), direction, 256f * ship.getCollisionSize(), null);
		Vector3f targetCoords = WindowUtils.calculateScreenCoords(pos, GameUtils.getPlayerPov(), null);
		if (targetCoords.z > 0f)
			return new Target(null, targetCoords, targetCoords);
		else
			return null;
	}

	public static List<Target> getOnScreenObjects(Machine machine, TargetType... filters) {
		List<GameObject> list = machine.getWorld().getList();
		List<Target> targetList = new ArrayList<Target>();
		Vector3f pos = machine.getPos();
		float maxTargetDistSq = Maths.sqr(MachineController.MAX_TARGET_DIST * machine.getSize());
		Renderer renderer = GameUtils.getPlayerPov();
		Vector3f targetCoords = new Vector3f();
		Vector3f nextTargetCoords = new Vector3f();
		GameObject current = null;
		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			if (current == machine || !current.isTargetable() || filter(current, filters) || !current.isFarTargetable()
					&& !GameObject.isEnvironment(current) && Maths.distSq(pos, current.getPos()) > maxTargetDistSq)
				continue;
			WindowUtils.calculateScreenCoords(current.getPos(), renderer, targetCoords);
			WindowUtils.calculateScreenCoords(Projectile.getNextTargetPos(machine, current), renderer,
					nextTargetCoords);
			if (targetCoords.z > 0f && Maths.abs(targetCoords.x) <= 1f && Maths.abs(targetCoords.y) <= 1f
					|| nextTargetCoords.z > 0f && Maths.abs(nextTargetCoords.x) <= 1f
							&& Maths.abs(nextTargetCoords.y) <= 1f)
				targetList.add(new Target(current, targetCoords, nextTargetCoords));
		}
		return targetList;
	}

	public static List<Target> getOnGalMapObjects(List<? extends GalacticObject> list, Renderer renderer) {
		List<Target> targetList = new ArrayList<Target>();
		Vector3f targetCoords = new Vector3f();
		GalacticObject current = null;
		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			WindowUtils.calculateScreenCoords(current.getPos(), renderer, targetCoords);
			if (targetCoords.z > 0f && Maths.abs(targetCoords.x) <= 1f && Maths.abs(targetCoords.y) <= 1f)
				targetList.add(new Target(current, targetCoords, targetCoords));
		}
		return targetList;
	}

	public static List<Target> getOnSysMapObjects(List<Planet> list, Renderer renderer) {
		List<Target> targetList = new ArrayList<Target>();
		Vector3f targetCoords = new Vector3f();
		Planet current = null;
		for (int i = 0; i < list.size(); i++) {
			current = list.get(i);
			WindowUtils.calculateScreenCoords(current.getPlanetObject().getPos(), renderer, targetCoords);
			if (targetCoords.z > 0f && Maths.abs(targetCoords.x) <= 1f && Maths.abs(targetCoords.y) <= 1f)
				targetList.add(new Target(current, targetCoords, targetCoords));
		}
		return targetList;
	}

	private static boolean filter(GameObject object, TargetType... filters) {
		if (filters == null || filters.length == 0)
			return false;
		for (int i = 0; i < filters.length; i++)
			if (object.getTargetType() == filters[i])
				return false;
		return true;
	}

	public GameObject getObject() {
		return object;
	}

	public TargetType getTargetType() {
		return object.getTargetType();
	}

	public Vector3f getScreenCoords() {
		return screenCoords;
	}

	public Vector3f getNextScreenCoords() {
		return nextScreenCoords;
	}

}
