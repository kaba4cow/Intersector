package kaba4cow.gameobjects.machines;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.files.ShipFile;
import kaba4cow.files.StationFile;
import kaba4cow.gameobjects.Fraction;
import kaba4cow.gameobjects.World;
import kaba4cow.gameobjects.machinecontrollers.stationcontrollers.StationController;

public class Station extends Machine {

	private final StationController controller;

	public Station(World world, Fraction fraction, StationFile file, Vector3f pos, StationController controller) {
		super(world, fraction, file, pos);

		this.controller = controller.setMachine(this);
	}

	public Station(World world, StationFile file, Vector3f pos, StationController controller) {
		this(world, Fraction.get("CIVILIAN"), file, pos, controller);
	}

	@Override
	public void update(float dt) {
		if (isDestroyed()) {
			disableTurrets();
			disableLaunchers();
		} else
			controller.update(dt);
		rotate(direction.getUp(), 0.01f * dt);

		Maths.blend(Vectors.INIT3, vel, dt, vel);

		super.update(dt);
	}

	@Override
	public void onSpawn() {
		rotate(Vectors.randomize(-1f, 1f, (Vector3f) null).normalise(null), RNG.randomFloat(-Maths.PI, Maths.PI));
	}

	public float getMaxSpeed() {
		return getFile().getHorSpeed();
	}

	@Override
	public ShipFile getFile() {
		return (ShipFile) file;
	}

	@Override
	public StationController getController() {
		return controller;
	}

}
