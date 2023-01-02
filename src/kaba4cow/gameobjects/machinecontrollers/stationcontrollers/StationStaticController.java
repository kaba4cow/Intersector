package kaba4cow.gameobjects.machinecontrollers.stationcontrollers;

import kaba4cow.gameobjects.projectiles.Projectile;

public class StationStaticController extends StationController {

	public StationStaticController() {
		super();
	}

	@Override
	public void update(float dt) {
		getMachine().disableTurrets();
		getMachine().disableLaunchers();
	}

	@Override
	public void onDamage(Projectile proj) {

	}

}
