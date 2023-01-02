package kaba4cow.engine.toolbox.particles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import kaba4cow.engine.renderEngine.renderers.ParticleRenderer;
import kaba4cow.engine.renderEngine.textures.ParticleTexture;
import kaba4cow.engine.toolbox.maths.Easing;
import kaba4cow.engine.toolbox.maths.Maths;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class ParticleSystem {

	private float pps;
	private float averageSpeed;
	private float averageLifeLength;
	private float averageScale;

	private float speedError;
	private float lifeError;
	private float scaleError;
	private float dScale;
	private float dRotation;
	private boolean randomRotation;
	private Vector3f direction;
	private float directionDeviation;

	private ParticleTexture particleTexture;
	private Easing brightnessEasing;

	private List<Particle> list;
	private List<Particle> queue;

	private final Random random;

	public ParticleSystem(ParticleTexture particleTexture,
			Easing brightnessEasing, float pps, float speed, float lifeLength,
			float scale, float dScale, float dRotation) {
		this.list = new ArrayList<Particle>();
		this.queue = new ArrayList<Particle>();
		this.particleTexture = particleTexture;
		this.brightnessEasing = brightnessEasing;
		this.pps = pps;
		this.averageSpeed = speed;
		this.averageLifeLength = lifeLength;
		this.averageScale = scale;
		this.dScale = dScale;
		this.dRotation = dRotation;
		this.speedError = 0f;
		this.lifeError = 0f;
		this.scaleError = 0f;
		this.direction = null;
		this.directionDeviation = 0f;
		this.randomRotation = false;
		this.random = new Random();
	}

	public void update(float dt) {
		updateQueue();
		for (int i = list.size() - 1; i >= 0; i--)
			if (!list.get(i).update(dt))
				list.remove(i);
	}

	private void updateQueue() {
		for (int i = 0; i < queue.size(); i++)
			list.add(queue.get(i));
		queue.clear();
	}

	public void setTint(Vector3f tint, float power) {
		for (int i = 0; i < list.size(); i++)
			list.get(i).setTint(tint, power);
	}

	public void move(Vector3f off) {
		for (int i = 0; i < list.size(); i++)
			Vector3f.add(list.get(i).getPos(), off, list.get(i).getPos());
	}

	public void render(ParticleRenderer renderer) {
		for (int i = 0; i < list.size(); i++)
			renderer.render(list.get(i));
	}

	public void add(Particle particle) {
		if (particle != null && !list.contains(particle))
			queue.add(particle);
	}

	public void remove(Particle particle) {
		if (list.contains(particle))
			list.remove(particle);
		if (queue.contains(particle))
			queue.remove(particle);
	}

	public void clear() {
		list.clear();
		queue.clear();
	}

	public Easing getBrightnessEasing() {
		return brightnessEasing;
	}

	public ParticleSystem setBrightnessEasing(Easing brightnessEasing) {
		this.brightnessEasing = brightnessEasing;
		return this;
	}

	public float getPps() {
		return pps;
	}

	public ParticleSystem setPps(float pps) {
		this.pps = pps;
		return this;
	}

	public float getAverageSpeed() {
		return averageSpeed;
	}

	public ParticleSystem setAverageSpeed(float averageSpeed) {
		this.averageSpeed = averageSpeed;
		return this;
	}

	public float getAverageLifeLength() {
		return averageLifeLength;
	}

	public ParticleSystem setAverageLifeLength(float averageLifeLength) {
		this.averageLifeLength = averageLifeLength;
		return this;
	}

	public float getAverageScale() {
		return averageScale;
	}

	public ParticleSystem setAverageScale(float averageScale) {
		this.averageScale = averageScale;
		return this;
	}

	public float getDScale() {
		return dScale;
	}

	public ParticleSystem setDScale(float dScale) {
		this.dScale = dScale;
		return this;
	}

	public float getDRotation() {
		return dRotation;
	}

	public ParticleSystem setDRotation(float dRotation) {
		this.dRotation = dRotation;
		return this;
	}

	public Vector3f getDirection() {
		return direction;
	}

	public ParticleSystem setDirection(Vector3f direction) {
		this.direction = direction;
		return this;
	}

	public float getDirectionDeviation() {
		return directionDeviation;
	}

	public ParticleSystem setDirectionDeviation(float directionDeviation) {
		this.directionDeviation = directionDeviation;
		return this;
	}

	public float getSpeedError() {
		return speedError;
	}

	public ParticleSystem setSpeedError(float error) {
		this.speedError = error;
		return this;
	}

	public float getLifeError() {
		return lifeError;
	}

	public ParticleSystem setLifeError(float error) {
		this.lifeError = error;
		return this;
	}

	public float getScaleError() {
		return scaleError;
	}

	public ParticleSystem setScaleError(float error) {
		this.scaleError = error;
		return this;
	}

	public ParticleSystem setDirection(Vector3f direction, float deviation) {
		this.direction = new Vector3f(direction);
		this.directionDeviation = deviation * Maths.PI;
		return this;
	}

	public ParticleSystem randomizeRotation() {
		this.randomRotation = true;
		return this;
	}

	public void generateParticles(Vector3f systemCenter, float dt) {
		float particlesToCreate = pps * dt;
		int count = (int) particlesToCreate;
		float partialParticle = particlesToCreate - count;
		for (int i = 0; i < count; i++)
			emitParticle(systemCenter);
		if (Math.random() < partialParticle)
			emitParticle(systemCenter);
	}

	public void generateParticles(Vector3f systemCenter, Vector3f velocity,
			float dt) {
		float particlesToCreate = pps * dt;
		int count = (int) particlesToCreate;
		float partialParticle = particlesToCreate - count;
		for (int i = 0; i < count; i++)
			emitParticle(systemCenter, velocity);
		if (Math.random() < partialParticle)
			emitParticle(systemCenter, velocity);
	}

	public void generateParticles(Vector3f systemCenter, Vector3f velocity,
			float pps, float dt) {
		float particlesToCreate = pps * dt;
		int count = (int) particlesToCreate;
		float partialParticle = particlesToCreate - count;
		for (int i = 0; i < count; i++)
			emitParticle(systemCenter, velocity);
		if (Math.random() < partialParticle)
			emitParticle(systemCenter, velocity);
	}

	public void emitParticle(Vector3f center) {
		Vector3f velocity = null;
		if (direction != null)
			velocity = generateRandomUnitVectorWithinCone(direction,
					directionDeviation);
		else
			velocity = generateRandomUnitVector();
		velocity.normalise();
		velocity.scale(generateValue(averageSpeed, speedError));
		float scale = generateValue(averageScale, scaleError * averageScale);
		float lifeLength = generateValue(averageLifeLength, lifeError
				* averageLifeLength);
		new Particle(this, new Vector3f(center), velocity, lifeLength,
				generateRotation(), scale);
	}

	public void emitParticle(Vector3f center, Vector3f velocity) {
		float scale = generateValue(averageScale, scaleError * averageScale);
		float lifeLength = generateValue(averageLifeLength, lifeError
				* averageLifeLength);
		new Particle(this, new Vector3f(center), new Vector3f(velocity),
				lifeLength, generateRotation(), scale);
	}

	private float generateValue(float average, float errorMargin) {
		float offset = (random.nextFloat() - 0.5f) * 2f * errorMargin;
		return average + offset;
	}

	private float generateRotation() {
		if (randomRotation)
			return random.nextFloat() * Maths.TWO_PI;
		else
			return 0f;
	}

	private static Vector3f generateRandomUnitVectorWithinCone(
			Vector3f coneDirection, float angle) {
		float cosAngle = Maths.cos(angle);
		Random random = new Random();
		float theta = random.nextFloat() * Maths.TWO_PI;
		float z = cosAngle + (random.nextFloat() * (1f - cosAngle));
		float rootOneMinusZSquared = Maths.sqrt(1f - z * z);
		float x = rootOneMinusZSquared * Maths.cos(theta);
		float y = rootOneMinusZSquared * Maths.sin(theta);

		Vector4f direction = new Vector4f(x, y, z, 1f);
		if (coneDirection.x != 0f || coneDirection.y != 0f
				|| (coneDirection.z != 1f && coneDirection.z != -1f)) {
			Vector3f rotateAxis = Vector3f.cross(coneDirection, new Vector3f(
					0f, 0f, 1f), null);
			rotateAxis.normalise();
			float rotateAngle = (float) Math.acos(Vector3f.dot(coneDirection,
					new Vector3f(0f, 0f, 1f)));
			Matrix4f rotationMatrix = new Matrix4f();
			rotationMatrix.rotate(-rotateAngle, rotateAxis);
			Matrix4f.transform(rotationMatrix, direction, direction);
		} else if (coneDirection.z == -1f)
			direction.z *= -1f;
		return new Vector3f(direction);
	}

	private Vector3f generateRandomUnitVector() {
		float theta = random.nextFloat() * Maths.TWO_PI;
		float z = 2f * random.nextFloat() - 1f;
		float rootOneMinusZSquared = Maths.sqrt(1f - z * z);
		float x = rootOneMinusZSquared * Maths.cos(theta);
		float y = rootOneMinusZSquared * Maths.sin(theta);
		return new Vector3f(x, y, z);
	}

	public List<Particle> getParticles() {
		return list;
	}

	public int size() {
		return list.size();
	}

	public ParticleTexture getParticleTexture() {
		return particleTexture;
	}

	public void setParticleTexture(ParticleTexture particleTexture) {
		this.particleTexture = particleTexture;
	}

}