package kaba4cow.gameobjects.parametercontrols;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.gameobjects.machines.Ship;

public class HyperControl extends ParameterControl {

	public static final float[] SHIFTS = { 0.00001f, 0.0002f, 0.004f, 0.08f, 1f };

	private int shift;
	private float thrust;

	private float dThrust;

	private final float thrustSensitivity;

	private boolean engaged;

	private float cooldown;
	private final float maxCooldown;

	public HyperControl(Ship ship, float speed, float thrustSensitivity) {
		super(ship);
		this.shift = 0;
		this.thrust = 0f;
		this.dThrust = 0f;
		this.thrustSensitivity = thrustSensitivity;
		this.maxCooldown = 12.18f / speed;
		this.cooldown = maxCooldown;
		this.engaged = false;
	}

	@Override
	public void update(float dt) {
		if (!engaged) {
			cooldown = Maths.limit(cooldown + dt, 0f, maxCooldown);
			dThrust = 0f;
			thrust = 0f;
			return;
		}
		cooldown = 0f;
		dThrust = Maths.limit(dThrust * (1f - 2f * dt), -1f, 1f);
		thrust += dThrust * dt;
		thrust = Maths.limit(thrust, 0f, 1f);
	}

	@Override
	public void reset() {
		shift = 0;
		thrust = 0f;
		dThrust = 0f;
		engaged = false;
	}
	
	public void jump() {
		shift = 0;
		thrust = 0f;
		dThrust = 0f;
	}

	public void shiftUp() {
		if (shift >= SHIFTS.length - 2 || thrust < 1f)
			return;
		shift++;
		thrust = 0f;
		dThrust = 0f;
	}

	public void shiftDown() {
		if (shift <= 0 || thrust > 0f)
			return;
		shift--;
		thrust = 1f;
		dThrust = 0f;
	}

	public void switchEngage() {
		if (engaged)
			disengage();
		else
			engage();
	}

	public void engage() {
		if (isEngageable())
			engaged = true;
	}

	public void disengage() {
		if (shift == 0 && thrust <= 0f)
			engaged = false;
	}

	public boolean isEngageable() {
		return !ship.getAfterburnerControl().isEngaged()
				&& cooldown >= maxCooldown
				&& (int) Maths.abs(ship.getHorizontalControl().getThrust()) == 0;
	}

	public void brake(float dt) {
		reverse(dt);
	}

	public void forward(float dt) {
		dThrust += thrustSensitivity * dt;
	}

	public void reverse(float dt) {
		dThrust -= thrustSensitivity * dt;
	}

	public float getCooldownProgress() {
		return cooldown / maxCooldown;
	}

	public boolean isEngaged() {
		return engaged;
	}

	public float getShift() {
		return (shift + thrust) / (SHIFTS.length - 1f);
	}

	public float getShiftThrust() {
		return thrust;
	}

	public float getThrust() {
		return Maths.map(thrust, 0f, 1f, SHIFTS[shift], SHIFTS[shift + 1]);
	}

	public float getThrustSensitivity() {
		return thrustSensitivity;
	}

}
