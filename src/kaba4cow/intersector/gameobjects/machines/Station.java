package kaba4cow.intersector.gameobjects.machines;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.intersector.files.StationFile;
import kaba4cow.intersector.galaxyengine.objects.StationObject;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.machines.controllers.stationcontrollers.StationAIController;
import kaba4cow.intersector.gameobjects.machines.controllers.stationcontrollers.StationController;

public class Station extends Machine {

	private final StationController controller;

	private final StationObject stationObject;

	public Station(World world, StationObject stationObject) {
		super(world, stationObject.getFraction(), stationObject.file, stationObject.worldPosition);

		this.stationObject = stationObject;

		this.controller = new StationAIController().setMachine(this);
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

		super.update(dt);
	}

	@Override
	public void onSpawn() {

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
