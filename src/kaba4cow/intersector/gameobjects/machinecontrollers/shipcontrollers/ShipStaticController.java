package kaba4cow.intersector.gameobjects.machinecontrollers.shipcontrollers;

import kaba4cow.intersector.gameobjects.projectiles.Projectile;

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
