package kaba4cow.intersector.gameobjects.parametercontrols;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.intersector.gameobjects.machines.Ship;

public class AfterburnerControl extends ParameterControl {

	private float power;
	private float time;
	private float cooldown;

	private final float maxPower;
	private final float maxTime;
	private final float maxCooldown;
	private final float smoothness;

	public AfterburnerControl(Ship ship, float power, float time,
			float cooldown, float smoothness) {
		super(ship);
		this.maxPower = power;
		this.maxTime = time;
		this.maxCooldown = cooldown + time;
		this.power = 0f;
		this.time = time;
		this.cooldown = maxCooldown;
		this.smoothness = smoothness;
	}

	@Override
	public void update(float dt) {
		time += dt;
		if (time > maxTime) {
			cooldown += dt;
			power = 0f;
		} else {
			float normTime = time / maxTime;
			power = Maths.limit(1f - Maths.pow(Maths.abs(2f * normTime - 1f),
					smoothness));
		}
	}

	public void engage() {
		if (!isEngageable())
			return;
		time = 0f;
		cooldown = 0f;
	}

	@Override
	public void reset() {
		power = 0f;
		time = maxTime;
		cooldown = maxCooldown;
	}

	public boolean isEngaged() {
		return time <= maxTime;
	}

	public boolean isEngageable() {
		return !ship.getHyperControl().isEngaged() && cooldown >= maxCooldown;
	}

	public float getPower() {
		return 1f + power;
	}

	public float getThrustPower() {
		return 1f + power * maxPower;
	}

	public float getRotationPower() {
		return 1f + power * maxPower / (4f * Maths.PI);
	}

	public float getCooldownProgress() {
		if (cooldown >= maxCooldown)
			return 1f;
		return cooldown / maxCooldown;
	}

}
