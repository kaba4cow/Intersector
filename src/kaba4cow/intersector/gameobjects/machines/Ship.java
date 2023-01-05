package kaba4cow.intersector.gameobjects.machines;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.files.ShipFile;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.gameobjects.Fraction;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.machines.controllers.shipcontrollers.ShipController;
import kaba4cow.intersector.gameobjects.objectcomponents.PortComponent;
import kaba4cow.intersector.gameobjects.parametercontrols.AfterburnerControl;
import kaba4cow.intersector.gameobjects.parametercontrols.HyperControl;
import kaba4cow.intersector.gameobjects.parametercontrols.RotationControl;
import kaba4cow.intersector.gameobjects.parametercontrols.ThrustControl;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.toolbox.Measures;
import kaba4cow.intersector.utils.GalaxyUtils;
import kaba4cow.intersector.utils.RenderUtils;

public class Ship extends Machine {

	public static final float HYPER_SPEED = 23.17f * Measures.LIGHT_SECOND;
	public static final float JUMP_RANGE = 45f;

	private final ThrustControl horizontalControl;
	private final ThrustControl verticalControl;
	private final HyperControl hyperControl;
	private final AfterburnerControl afterburnerControl;
	private final RotationControl rotationControl;

	private final String thrustSound;

	private final ShipController controller;
	private boolean hudEnabled;
	private boolean hudInfoEnabled;
	private float hudElapsedTime;

	private Machine parent;
	private PortComponent component;

	private DestroyScenario destroyScenario;

	public Ship(World world, Fraction fraction, ShipFile file, Vector3f pos, ShipController controller) {
		super(world, fraction, file, pos);

		this.parent = null;
		this.component = null;

		this.horizontalControl = new ThrustControl(this, file.getHorThrust(), file.getHorBrake());
		this.verticalControl = new ThrustControl(this, file.getVerThrust(), file.getVerBrake());
		this.hyperControl = new HyperControl(this, file.getHyperSpeed(), file.getHyperThrust());
		this.afterburnerControl = new AfterburnerControl(this, file.getAftPower(), file.getAftTime(),
				file.getAftCooldown(), file.getAftSmoothness());
		this.rotationControl = new RotationControl(this, file.getPitchSens(), file.getYawSens(), file.getRollSens());

		this.controller = controller.setMachine(this);
		this.hudEnabled = true;
		this.hudInfoEnabled = false;
		this.hudElapsedTime = 0f;

		this.destroyScenario = DestroyScenario.getRandom();

		this.thrustSound = file.getThrustModel().getTexture().getSound();
		playSound(thrustSound).setGain(0f);
	}

	@Override
	public void update(float dt) {
		loopSound(thrustSound).setGain(Maths.abs(horizontalControl.getThrust()));

		hudElapsedTime += dt;
		if (parent != null) {
			afterburnerControl.reset();
			horizontalControl.reset();
			hyperControl.reset();
			verticalControl.reset();
			rotationControl.reset();
			vel.set(parent.getVel());
			rotationVel.set(0f, 0f, 0f);
			Vector3f.add(component.translated, parent.getPos(), pos);
		} else {
			if (!isDestroyed())
				controller.update(dt);
			verticalControl.brake(0.5f * dt);
			afterburnerControl.update(dt);
			horizontalControl.update(dt);
			verticalControl.update(dt);
			hyperControl.update(dt);
			rotationControl.update(dt);

			rotate(direction.getRight(), rotationControl.getPitch());
			rotate(direction.getUp(), rotationControl.getYaw());
			rotate(direction.getForward(), rotationControl.getRoll());

			Vector3f newVel = new Vector3f();

			if (!hyperControl.isEngaged()) {
				Vector3f horizontalDirection = direction.getForward();
				if (afterburnerControl.isEngaged())
					Vectors.addScaled(newVel, horizontalDirection,
							afterburnerControl.getThrustPower() * getHorSpeed() * horizontalControl.getThrust(),
							newVel);
				else
					Vectors.addScaled(newVel, horizontalDirection, getHorSpeed() * horizontalControl.getThrust(),
							newVel);

				Vector3f verticalDirection = direction.getUp();
				Vectors.addScaled(newVel, verticalDirection, getVerSpeed() * verticalControl.getThrust(), newVel);

				newVel.scale(getMassDivider(mass));
			} else {
				Vector3f horizontalDirection = direction.getForward();
				Vectors.addScaled(newVel, horizontalDirection, getHyperSpeed() * hyperControl.getThrust(), newVel);
			}

			Maths.blend(newVel, vel, dt, vel);
			Maths.blend(Vectors.INIT3, rotationVel, dt, rotationVel);
		}

		if (parent != null || isDestroyed()) {
			disableTurrets();
			disableLaunchers();
		}

		super.update(dt);
	}

	@Override
	public void render(RendererContainer renderers) {
		if (!isVisible(renderers.getRenderer()))
			return;
		super.render(renderers);

		Matrix4f matrix = direction.getMatrix(pos, true, size);
		RenderUtils.renderThrusts(thrustModel, horizontalControl.getThrust(), afterburnerControl.getPower(), size,
				matrix, renderers, thrusts);
	}

	public void jump() {
		SystemObject system = controller.getTargetSystem();
		if (system == null || system.equals(world.getSystem()))
			return;
		float distance = GalaxyUtils.lightYears(Maths.dist(world.getSystem().getPos(), system.getPos()));
		if (distance > JUMP_RANGE)
			return;
		Vector3f direction = Maths.direction(world.getSystem().getPos(), system.getPos());
		float alignment = Vector3f.dot(getDirection().getForward(), direction);
		if (alignment < 0.95f)
			return;
		world.jump(this, system);
		hyperControl.jump();
	}

	@Override
	public boolean isVisible(Renderer renderer) {
		return super.isVisible(renderer) && (parent == null || component.visible);
	}

	@Override
	public boolean isCollidable() {
		if (parent != null && !component.visible)
			return false;
		return super.isCollidable();
	}

	@Override
	public TargetType getTargetType() {
		if (parent != null)
			return null;
		return super.getTargetType();
	}

	@Override
	protected void collide(GameObject obj, float dt) {
		if (obj == parent || obj instanceof Ship && parent != null && ((Ship) obj).parent == parent)
			return;
		super.collide(obj, dt);
	}

	public Ship rotateTo(float targetX, float targetY, boolean addRoll, boolean limit, float dt) {
		float minValue = limit ? 0.2f : 0.1f;
		float dPitch = Maths.signum(targetY) * Maths.limit(100f * targetY * targetY + minValue);
		rotationControl.pitchUp(dt * dPitch);
		float dYaw = Maths.signum(targetX) * Maths.limit(100f * targetX * targetX + minValue);
		rotationControl.yawUp(dt * dYaw);
		if (addRoll) {
			float dRoll = 0.25f * Maths.signum(targetX) * Maths.limit(100f * targetX * targetX + minValue);
			rotationControl.rollDown(dt * dRoll);
		}
		return this;
	}

	public Ship smoothRotateTo(float targetX, float targetY, float dt) {
		float dPitch = targetY;
		rotationControl.pitchUp(dt * dPitch);
		float dYaw = targetX;
		rotationControl.yawUp(dt * dYaw);
		return this;
	}

	@Override
	public void onSpawn() {
		// if (parent == null)
		// rotate(Vectors.randomize(-1f, 1f, (Vector3f) null).normalise(null),
		// RNG.randomFloat(-Maths.PI, Maths.PI));
	}

	public void onParentDestroy() {
		if (parent == null || parent != null && parent.isAlive())
			return;
		Vector3f dir = Maths.direction(parent.getPos(), pos);
		Vectors.rotateRandom(-0.4f, 0.4f, dir, dir);
		removeParent();
	}

	public void onEject() {
		if (parent == null || parent == null || !parent.isAlive())
			return;
		Vector3f dir = Maths.direction(parent.getPos(), pos);
		Vectors.rotateRandom(-0.1f, 0.1f, dir, dir);
		Vector3f.add(vel, parent.getVel(), vel);
		removeParent();
	}

	public float getMaxSpeed() {
		return getFile().getHorSpeed();
	}

	@Override
	protected void destroy(float dt) {
		super.destroy(dt);
		if (RNG.randomFloat(1f) < 0.1f)
			afterburnerControl.engage();
		horizontalControl.forward(destroyScenario.thrust * dt);
		rotationControl.pitchUp(destroyScenario.pitch * dt);
		rotationControl.yawUp(destroyScenario.yaw * dt);
		rotationControl.rollUp(destroyScenario.thrust * dt);
		rotateTo(RNG.randomFloat(-1f, 1f), RNG.randomFloat(-1f, 1f), true, false, dt);
	}

	@Override
	public ShipFile getFile() {
		return (ShipFile) file;
	}

	public Machine getParent() {
		return parent;
	}

	public void setParent(Machine parent, PortComponent component) {
		this.parent = parent;
		this.parent.addMass(mass);
		this.component = component;
		this.component.occupied = true;
		this.resetDirection();
		this.rotate(parent.getDirection().getUp(), component.rotation);
	}

	public void removeParent() {
		if (parent != null)
			parent.addMass(-mass);
		parent = null;
		if (component != null)
			component.occupied = false;
		component = null;
	}

	@Override
	public void addMass(float mass) {
		super.addMass(mass);
		if (parent != null)
			parent.addMass(mass);
	}

	public PortComponent getComponent() {
		return component;
	}

	public boolean isAfterburnerEngaged() {
		return afterburnerControl.isEngaged();
	}

	public boolean isHyperEngaged() {
		return hyperControl.isEngaged();
	}

	public float getHorSpeed() {
		return getFile().getHorSpeed();
	}

	public float getVerSpeed() {
		return getFile().getVerSpeed();
	}

	public float getHyperSpeed() {
		return HYPER_SPEED * getFile().getHyperSpeed();
	}

	@Override
	public Machine shootManualWeapon() {
		if (isHyperEngaged())
			return this;
		else
			return super.shootManualWeapon();
	}

	@Override
	public Machine shootAutomaticWeapon() {
		if (isHyperEngaged())
			return this;
		else
			return super.shootAutomaticWeapon();
	}

	public void switchHudEnabled() {
		hudEnabled = !hudEnabled;
		hudElapsedTime = 0f;
	}

	public boolean isHudEnabled() {
		return hudEnabled;
	}

	public void switchHudInfoEnabled() {
		hudInfoEnabled = !hudInfoEnabled;
	}

	public boolean isHudInfoEnabled() {
		return hudInfoEnabled;
	}

	public float getHudElapsedTime() {
		return hudElapsedTime;
	}

	@Override
	public ShipController getController() {
		return controller;
	}

	public ThrustControl getHorizontalControl() {
		return horizontalControl;
	}

	public ThrustControl getVerticalControl() {
		return verticalControl;
	}

	public HyperControl getHyperControl() {
		return hyperControl;
	}

	public AfterburnerControl getAfterburnerControl() {
		return afterburnerControl;
	}

	public RotationControl getRotationControl() {
		return rotationControl;
	}

	public static enum DestroyScenario {

		DEFAULT(0f, 0f, 0f, 0f), ROLL(0f, 0f, 0.1f, 1f), ROLL_FORWARD(1f, 0f, 0f, 1f), PITCH(0f, 1f, 0f, 0.2f),
		PITCH_FORWARD(1f, 1f, 0f, 0f);

		public final float thrust;
		public final float pitch;
		public final float yaw;
		public final float roll;

		private DestroyScenario(float thrust, float pitch, float yaw, float roll) {
			this.thrust = thrust;
			this.yaw = yaw;
			this.pitch = pitch;
			this.roll = roll;
		}

		public static DestroyScenario getRandom() {
			return values()[RNG.randomInt(values().length)];
		}

	}

}
