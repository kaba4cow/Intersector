package kaba4cow.gameobjects.parametercontrols;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.gameobjects.machines.Ship;

public class ThrustControl extends ParameterControl {

	private float thrust;

	private float dThrust;

	private final float thrustSensitivity;
	private final float brakeSensitivity;

	public ThrustControl(Ship ship, float thrustSensitivity,
			float brakeSensitivity) {
		super(ship);
		this.thrust = 0f;
		this.dThrust = 0f;
		this.thrustSensitivity = thrustSensitivity;
		this.brakeSensitivity = brakeSensitivity;
	}

	@Override
	public void update(float dt) {
		dThrust = Maths.limit(dThrust * (1f - 2f * dt), -1f, 1f);
		if (ship.getAfterburnerControl().isEngaged())
			thrust += ship.getAfterburnerControl().getThrustPower() * dThrust
					* dt;
		else
			thrust += dThrust * dt;
		thrust = Maths.limit(thrust, -1f, 1f);
	}

	@Override
	public void reset() {
		thrust = 0f;
		dThrust = 0f;
	}

	public void brake(float dt) {
		dThrust -= thrust * 4f * dt;
		thrust = Maths.blend(0f, thrust, brakeSensitivity * dt);
	}

	public void forward(float dt) {
		dThrust += thrustSensitivity * dt;
	}

	public void reverse(float dt) {
		dThrust -= thrustSensitivity * dt;
	}

	public float getThrust() {
		return thrust;
	}

	public float getThrustSensitivity() {
		return thrustSensitivity;
	}

	public float getBrakeSensitivity() {
		return brakeSensitivity;
	}

}
