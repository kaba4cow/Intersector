package kaba4cow.gameobjects.machinecontrollers.shipcontrollers;

import kaba4cow.gameobjects.projectiles.Projectile;

public class ShipStaticController extends ShipController {

	public ShipStaticController() {
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
