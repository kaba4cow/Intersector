package kaba4cow.gameobjects.parametercontrols;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.gameobjects.machines.Ship;

public class RotationControl extends ParameterControl {

	private float pitch;
	private float yaw;
	private float roll;

	private float pitchSensitivity;
	private float yawSensitivity;
	private float rollSensitivity;

	public RotationControl(Ship ship, float pitchSensitivity,
			float yawSensitivity, float rollSensitivity) {
		super(ship);
		this.pitch = 0f;
		this.yaw = 0f;
		this.roll = 0f;
		this.pitchSensitivity = pitchSensitivity;
		this.yawSensitivity = yawSensitivity;
		this.rollSensitivity = rollSensitivity;
	}

	@Override
	public void update(float dt) {
		pitch = Maths.limit(pitch * (1f - 2f * dt), -1f, 1f);
		yaw = Maths.limit(yaw * (1f - 2f * dt), -1f, 1f);
		roll = Maths.limit(roll * (1f - 2f * dt), -1f, 1f);
	}

	@Override
	public void reset() {
		pitch = 0f;
		yaw = 0f;
		roll = 0f;
	}

	public void pitchUp(float dt) {
		pitch += 0.1f * pitchSensitivity * dt;
	}

	public void pitchDown(float dt) {
		pitch -= 0.1f * pitchSensitivity * dt;
	}

	public void yawUp(float dt) {
		yaw += 0.1f * yawSensitivity * dt;
	}

	public void yawDown(float dt) {
		yaw -= 0.1f * yawSensitivity * dt;
	}

	public void rollUp(float dt) {
		roll += 0.1f * rollSensitivity * dt;
	}

	public void rollDown(float dt) {
		roll -= 0.1f * rollSensitivity * dt;
	}

	public float getPitch() {
		if (ship != null && ship.getHyperControl().isEngaged())
			return 0.5f * pitch;
		if (ship != null && ship.getAfterburnerControl().isEngaged())
			return ship.getAfterburnerControl().getRotationPower() * pitch;
		return pitch;
	}

	public float getYaw() {
		if (ship != null && ship.getHyperControl().isEngaged())
			return 0.5f * yaw;
		if (ship != null && ship.getAfterburnerControl().isEngaged())
			return ship.getAfterburnerControl().getRotationPower() * yaw;
		return yaw;
	}

	public float getRoll() {
		if (ship != null && ship.getHyperControl().isEngaged())
			return 0.5f * roll;
		if (ship != null && ship.getAfterburnerControl().isEngaged())
			return ship.getAfterburnerControl().getRotationPower() * roll;
		return roll;
	}

	public float getPitchSensitivity() {
		return pitchSensitivity;
	}

	public void setPitchSensitivity(float pitchSensitivity) {
		this.pitchSensitivity = pitchSensitivity;
	}

	public float getYawSensitivity() {
		return yawSensitivity;
	}

	public void setYawSensitivity(float yawSensitivity) {
		this.yawSensitivity = yawSensitivity;
	}

	public float getRollSensitivity() {
		return rollSensitivity;
	}

	public void setRollSensitivity(float rollSensitivity) {
		this.rollSensitivity = rollSensitivity;
	}

}
