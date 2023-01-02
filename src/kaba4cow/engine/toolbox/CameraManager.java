package kaba4cow.engine.toolbox;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Maths;

public class CameraManager {

	private Vector3f point;
	private Vector3f newPoint;
	private float dist;
	private float newDist;
	private float pitch;
	private float newPitch;
	private float yaw;
	private float newYaw;

	private float pointChangeRate;

	private float minDist;
	private float maxDist;
	private float distSensitivity;
	private float distChangeRate;

	private float minPitch;
	private float maxPitch;
	private float pitchSensitivity;
	private float pitchChangeRate;

	private float yawSensitivity;
	private float yawChangeRate;

	private float initDist;
	private float initPitch;
	private float initYaw;

	private int initKey;

	public CameraManager() {
		this.point = new Vector3f();
		this.newPoint = new Vector3f();
		this.initKey = Keyboard.KEY_C;
	}

	public void update(boolean rotate, float dt) {
		if (rotate) {
			if (Mouse.isButtonDown(2)) {
				newPitch -= pitchSensitivity * Mouse.getDY() * dt;
				newYaw += yawSensitivity * Mouse.getDX() * dt;
			}
			newDist -= distSensitivity * Mouse.getDWheel();
		}

		if (initKey != -1 && Keyboard.isKeyDown(initKey))
			reset();

		Maths.blend(newPoint, point, pointChangeRate * dt, point);
		dist = Maths.blend(newDist, dist, distChangeRate * dt);

		pitch = Maths.blend(newPitch, pitch, pitchChangeRate * dt);
		yaw = Maths.blend(newYaw, yaw, yawChangeRate * dt);

		constrainParameters();
	}

	private void constrainParameters() {
		if (newDist < minDist)
			newDist = minDist;
		else if (newDist >= maxDist)
			newDist = maxDist;
		if (dist < minDist)
			dist = minDist;
		else if (dist >= maxDist)
			dist = maxDist;
		if (newPitch < minPitch)
			newPitch = minPitch;
		else if (newPitch >= maxPitch)
			newPitch = maxPitch;
		if (pitch < minPitch)
			pitch = minPitch;
		else if (pitch >= maxPitch)
			pitch = maxPitch;
	}

	public CameraManager reset() {
		setDist(initDist);
		setPitch(initPitch);
		setYaw(initYaw);
		return this;
	}

	public CameraManager setInitParameters(float initDist, float initPitch,
			float initYaw) {
		this.initDist = initDist;
		this.initPitch = initPitch;
		this.initYaw = initYaw;
		return this;
	}

	public CameraManager resetParameters() {
		point = new Vector3f(newPoint);
		dist = newDist;
		pitch = newPitch;
		yaw = newYaw;
		return this;
	}

	public CameraManager moveTo(Vector3f newPoint) {
		this.newPoint.set(newPoint);
		return this;
	}

	public CameraManager rotate(float pitch, float yaw) {
		newPitch += pitch;
		newYaw += yaw;
		return this;
	}

	public CameraManager setPointParameters(float pointChangeRate) {
		this.pointChangeRate = pointChangeRate;
		return this;
	}

	public CameraManager setDistParameters(float minDist, float maxDist,
			float distSensitivity, float distChangeRate) {
		this.minDist = minDist;
		this.maxDist = maxDist;
		this.distSensitivity = distSensitivity;
		this.distChangeRate = distChangeRate;
		return this;
	}

	public CameraManager setDistParameters(float minDist, float maxDist) {
		return setDistParameters(minDist, maxDist, distSensitivity,
				distChangeRate);
	}

	public CameraManager setPitchParameters(float minPitch, float maxPitch,
			float pitchSensitivity, float pitchChangeRate) {
		this.minPitch = minPitch;
		this.maxPitch = maxPitch;
		this.pitchSensitivity = pitchSensitivity;
		this.pitchChangeRate = pitchChangeRate;
		return this;
	}

	public CameraManager setPitchParameters() {
		return setPitchParameters(minPitch, maxPitch, pitchSensitivity,
				pitchChangeRate);
	}

	public CameraManager setYawParameters(float yawSensitivity,
			float yawChangeRate) {
		this.yawSensitivity = yawSensitivity;
		this.yawChangeRate = yawChangeRate;
		return this;
	}

	public int getInitKey() {
		return initKey;
	}

	public void setInitKey(int initKey) {
		this.initKey = initKey;
	}

	public Vector3f getPoint() {
		return point;
	}

	public CameraManager setPoint(Vector3f point) {
		this.newPoint = point;
		return this;
	}

	public float getDist() {
		return dist;
	}

	public CameraManager setDist(float dist) {
		this.newDist = dist;
		return this;
	}

	public float getPitch() {
		return pitch;
	}

	public CameraManager setPitch(float pitch) {
		this.newPitch = pitch % Maths.TWO_PI;
		this.pitch %= Maths.TWO_PI;
		return this;
	}

	public float getYaw() {
		return yaw;
	}

	public CameraManager setYaw(float yaw) {
		this.newYaw = yaw % Maths.TWO_PI;
		this.yaw %= Maths.TWO_PI;
		return this;
	}

	public float getPointChangeRate() {
		return pointChangeRate;
	}

	public float getMinDist() {
		return minDist;
	}

	public float getMaxDist() {
		return maxDist;
	}

	public float getDistSensitivity() {
		return distSensitivity;
	}

	public float getDistChangeRate() {
		return distChangeRate;
	}

	public float getMinPitch() {
		return minPitch;
	}

	public float getMaxPitch() {
		return maxPitch;
	}

	public float getPitchSensitivity() {
		return pitchSensitivity;
	}

	public float getPitchChangeRate() {
		return pitchChangeRate;
	}

	public float getYawSensitivity() {
		return yawSensitivity;
	}

	public float getYawChangeRate() {
		return yawChangeRate;
	}

}
