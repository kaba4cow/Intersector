package kaba4cow.intersector.gameobjects.projectiles;

import kaba4cow.intersector.gameobjects.machines.Machine;

public class Ray {

	public static final int LENGTH = 300;
	public static final float STEP = 1f / (float) LENGTH;

	private final Subray[] subrays;

	public Ray(Machine parent, Machine targetShip, ProjectileInfo projInfo) {
		subrays = new Subray[LENGTH];
		for (int i = 0; i < LENGTH; i++)
			subrays[i] = new Subray(parent, targetShip, projInfo, this, i);
	}

	public void collide(Subray subray) {
		int index = subray.getIndex();
		for (int i = index; i < LENGTH; i++)
			subrays[i].setActive(false);
	}

}
