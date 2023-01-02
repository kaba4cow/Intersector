package kaba4cow.gameobjects.objectcomponents;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.files.ParticleSystemFile;
import kaba4cow.files.WeaponFile;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.gameobjects.machinecontrollers.MachineController;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.gameobjects.projectiles.Projectile;
import kaba4cow.gameobjects.projectiles.ProjectileInfo;
import kaba4cow.gameobjects.projectiles.ProjectileType;
import kaba4cow.utils.GameUtils;

public class WeaponComponent extends ObjectComponent {

	public WeaponFile weaponFile;
	public Vector3f translated;
	public Vector3f origin;
	public String weaponName;
	public float pitch;
	public float yaw;
	public Direction fireDirection;
	public final float[] reload;
	public float cooldown;
	public int currentFirePoint;
	public boolean enabled;
	public boolean copyTarget;

	public TexturedModel staticModel;
	public TexturedModel yawModel;
	public TexturedModel pitchModel;

	private Machine targetShip;

	public WeaponComponent(float x, float y, float z, Direction direction,
			float pitch, float yaw, boolean copyTarget, String weaponName,
			boolean updateFile) {
		super(x, y, z, direction, 1f);
		this.weaponName = weaponName;
		this.weaponFile = WeaponFile.get(weaponName);
		if (updateFile)
			this.weaponFile.update();
		this.pitch = pitch;
		this.yaw = yaw;
		this.fireDirection = direction.copy();
		this.translated = new Vector3f(Vectors.FORWARD);
		this.reload = new float[weaponFile.getFirePoints()];
		for (int i = 0; i < reload.length; i++)
			reload[i] = weaponFile.getReload();
		this.cooldown = weaponFile.getCooldown();
		this.currentFirePoint = 0;
		this.enabled = true;
		this.copyTarget = copyTarget;
		this.targetShip = null;
	}

	public WeaponComponent(String weaponName, boolean updateFile) {
		this(0f, 0f, 0f, new Direction(), 0f, 0f, true, weaponName, updateFile);
	}

	public WeaponComponent(WeaponComponent weaponComponent, boolean updateFile) {
		this(weaponComponent.pos.x, weaponComponent.pos.y,
				weaponComponent.pos.z, weaponComponent.direction.copy(),
				weaponComponent.pitch, weaponComponent.yaw,
				weaponComponent.copyTarget, weaponComponent.weaponName,
				updateFile);
	}

	public static WeaponComponent read(DataFile node) {
		float x = node.node("pos").getFloat(0);
		float y = node.node("pos").getFloat(1);
		float z = node.node("pos").getFloat(2);
		boolean copyTarget = node.node("copytarget").getBoolean();
		String weaponName = node.node("weaponname").getString();
		Direction direction = Direction.fromString(node.node("direction")
				.getString());
		return new WeaponComponent(x, y, z, direction, 0f, 0f, copyTarget,
				weaponName, false);
	}

	@Override
	public void save(DataFile node) {
		node.node("pos").setFloat(pos.x).setFloat(pos.y).setFloat(pos.z);
		node.node("copytarget").setBoolean(copyTarget);
		node.node("weaponname").setString(weaponName);
		node.node("direction").setString(direction.toString());
	}

	public void calculateTranslated(float holderSize) {
		resetDirection();
		translated.set(0f, 0f, 0f);
		Vectors.addScaled(translated, pos, holderSize, translated);
		Vectors.addScaled(translated, weaponFile.getOriginPoint(),
				weaponFile.getSize(), translated);
	}

	private static final Matrix4f tempDirectionMatrix = new Matrix4f();
	private static final Vector3f tempWeaponPos = new Vector3f();
	private static final Vector3f tempWeaponSize = new Vector3f();
	private static final Vector3f tempOriginPoint = new Vector3f();
	private static final Direction tempFireDirection = new Direction();
	private static final Vector3f tempCameraPos = new Vector3f();
	private static final Vector3f tempTargetPos = new Vector3f();
	private static final Vector3f tempTargetCoords = new Vector3f();

	public void update(Machine holder, Matrix4f shipMatrix, float dt) {
		for (int i = 0; i < reload.length; i++)
			reload[i] += dt;
		cooldown += dt;

		if (!weaponFile.isAutomatic())
			return;

		if (holder.getController().getTargetEnemy() != null) {
			if (copyTarget)
				targetShip = holder.getController().getTargetEnemy();
			else if (RNG.chance(0.02f) || !GameObject.isAlive(targetShip)
					|| targetShip.isDestroyed())
				targetShip = searchTarget(holder);
		} else
			targetShip = null;

		if (!enabled || !GameObject.isAlive(targetShip)
				|| targetShip.isDestroyed()) {
			rotateYaw(-0.5f * dt * Maths.signumZero(yaw));
			rotatePitch(-0.5f * dt * Maths.signumZero(pitch));
			return;
		}

		boolean canReach = canReach(holder, targetShip);
		if (isAutoAim() && canReach)
			shoot(holder, targetShip, shipMatrix);

		if (!canRotateYaw() && !canRotatePitch())
			return;

		Matrices.set(tempDirectionMatrix, direction.getMatrix(null, false));
		tempWeaponPos.set(pos).scale(holder.getSize());
		Vectors.set(tempWeaponSize, weaponFile.getSize() / holder.getSize());
		tempOriginPoint.set(weaponFile.getOriginPoint());
		Matrices.transform(tempDirectionMatrix, tempOriginPoint,
				tempOriginPoint);

		tempFireDirection.set(fireDirection);
		tempFireDirection.rotate(tempFireDirection.getUp(), yaw);
		tempFireDirection.rotate(tempFireDirection.getRight(), pitch);

		Vector3f.add(holder.getPos(), translated, tempCameraPos);
		Vector3f.add(tempCameraPos, tempOriginPoint, tempCameraPos);
		Renderer renderer = GameUtils.getAiPov();
		Camera camera = renderer.getCamera();
		camera.orbit(tempCameraPos, 0f, 0f, 0f, tempFireDirection);
		renderer.updateViewMatrix();

		if (weaponFile.getProjectileFile().getProjectileType() == ProjectileType.RAY)
			tempTargetPos.set(targetShip.getPos());
		else
			tempTargetPos.set(Projectile.getNextTargetPos(holder, targetShip));
		WindowUtils.calculateScreenCoords(tempTargetPos, renderer,
				tempTargetCoords);
		if (tempTargetCoords.z > 0f)
			rotateTo(tempTargetCoords.x, tempTargetCoords.y, dt);
		else {
			rotateYaw(dt);
			if (RNG.chance(0.1f))
				targetShip = null;
		}
		if (RNG.chance(0.75f) && Maths.abs(tempTargetCoords.x) < 0.1f
				&& Maths.abs(tempTargetCoords.y) < 0.1f
				&& tempTargetCoords.z > 0f && canReach)
			shoot(holder, targetShip, shipMatrix);
		if (targetShip != holder.getController().getTargetEnemy())
			if (Maths.abs(tempTargetCoords.x) > 0.75f
					|| Maths.abs(tempTargetCoords.y) > 0.75f
					|| tempTargetCoords.z < 0f || !canReach)
				targetShip = null;
	}

	private Machine searchTarget(Machine holder) {
		Machine targetMachine = holder.getController().getTargetEnemy();
		Machine target = targetMachine;
		if (targetMachine.getFlock() == null || RNG.chance(0.25f))
			return target;
		List<GameObject> list = holder
				.getWorld()
				.getOctTree()
				.query(holder,
						MachineController.MAX_TARGET_DIST * holder.getSize());
		for (int i = 0; i < list.size(); i++)
			if (RNG.chance(0.75f) && list.get(i) instanceof Machine) {
				Machine current = (Machine) list.get(i);
				if (!canReach(holder, current) || !current.isAlive()
						|| current.isDestroyed()
						|| current.getFlock() != targetMachine.getFlock())
					continue;
				target = current;
			}
		return target;
	}

	public boolean isAutoAim() {
		return weaponFile.getProjectileFile().isAutoaim();
	}

	private void rotateTo(float x, float y, float dt) {
		if (canRotateYaw()) {
			float dYaw = Maths.signum(x) * Maths.limit(25f * x * x + 0.1f);
			rotateYaw(dYaw * dt);
		}
		if (canRotatePitch()) {
			float dPitch = Maths.signum(y) * Maths.limit(25f * y * y + 0.1f);
			rotatePitch(dPitch * dt);
		}
	}

	public boolean canReach(Machine holder, Machine targetShip) {
		return Projectile.canReach(holder.getPos(), targetShip.getPos(),
				weaponFile.getProjectileFile(), weaponFile.getSize()
						* weaponFile.getScale());
	}

	public void shipShoot(Machine holder, Machine targetShip,
			Matrix4f holderMatrix) {
		if (!enabled || weaponFile.isAutomatic())
			return;
		shoot(holder, targetShip, holderMatrix);
	}

	private void shoot(Machine holder, Machine targetShip, Matrix4f holderMatrix) {
		if (!enabled)
			return;
		if (weaponFile.isSwitchFirePoints()) {
			ProjectileInfo projInfo = ProjectileInfo.calculate(this,
					currentFirePoint, holder, targetShip, holderMatrix);
			shoot(holder, targetShip, projInfo, currentFirePoint);
			currentFirePoint++;
			if (currentFirePoint >= reload.length)
				currentFirePoint = 0;
		} else {
			List<ProjectileInfo> list = ProjectileInfo.calculateList(this,
					holder, targetShip, holderMatrix);
			for (currentFirePoint = 0; currentFirePoint < list.size(); currentFirePoint++)
				shoot(holder, targetShip, list.get(currentFirePoint),
						currentFirePoint);
		}
	}

	private void shoot(Machine holder, Machine targetShip,
			ProjectileInfo projInfo, int firePoint) {
		if (weaponFile.isSwitchFirePoints()
				&& cooldown < weaponFile.getCooldown()
				|| reload[firePoint] < weaponFile.getReload())
			return;
		reload[firePoint] = 0f;
		cooldown = 0f;
		if (weaponFile.isParticle())
			emitParticles(holder, projInfo);
		playSound(holder, projInfo);
		Projectile.create(holder, targetShip, projInfo);
	}

	private void playSound(Machine holder, ProjectileInfo projInfo) {
		String soundFile = weaponFile.getProjectileFile().getSound();
		holder.playSound(soundFile);
	}

	private void emitParticles(Machine holder, ProjectileInfo projInfo) {
		ParticleSystem system = ParticleSystemFile.get("EXPLOSION").get();
		Vector3f pos = projInfo.pos.negate(null);
		Vector3f forward = Vectors.scale(projInfo.direction.getForward(),
				3f * projInfo.scale, null);
		float scale = 0.4f * projInfo.scale;
		for (int i = 0; i < 4; i++)
			new Particle(system, pos, Vectors.addScaled(holder.getVel(),
					forward, RNG.randomFloat(1f), null).negate(null), 1f,
					RNG.randomFloat(Maths.TWO_PI), scale);
	}

	public void enable() {
		enabled = true;
	}

	public void disable() {
		enabled = false;
	}

	public void switchEnable() {
		enabled = !enabled;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public boolean isReloaded(int firePoint) {
		if (firePoint < 0 || firePoint >= reload.length)
			return false;
		return reload[firePoint] >= weaponFile.getReload();
	}

	public void rotateHolder(Vector3f axis, float angle) {
		fireDirection.rotate(axis, angle);
		Vectors.rotate(angle, axis, translated, translated);
	}

	public void rotatePitch(float angle) {
		if (angle == 0f || !canRotatePitch())
			return;
		angle = weaponFile.getRotationSpeed() * Maths.PI
				* Maths.limit(angle, -1f, 1f);
		pitch += angle;
		if (weaponFile.isLimitPitch())
			pitch = Maths.limit(pitch, weaponFile.getMinPitch(),
					weaponFile.getMaxPitch());
		else
			pitch %= Maths.TWO_PI;
		if (Maths.abs(pitch) < 0.05f)
			pitch = 0f;
	}

	public void rotateYaw(float angle) {
		if (angle == 0f || !canRotateYaw())
			return;
		angle = weaponFile.getRotationSpeed() * Maths.PI
				* Maths.limit(angle, -1f, 1f);
		yaw += angle;
		if (weaponFile.isLimitYaw())
			yaw = Maths.limit(yaw, weaponFile.getMinYaw(),
					weaponFile.getMaxYaw());
		else
			yaw %= Maths.TWO_PI;
		if (Maths.abs(yaw) < 0.05f)
			yaw = 0f;
	}

	public boolean canRotatePitch() {
		return weaponFile.getRotationSpeed() > 0f
				&& (!weaponFile.isLimitPitch() || weaponFile.getMinPitch() != weaponFile
						.getMaxPitch());
	}

	public boolean canRotateYaw() {
		return weaponFile.getRotationSpeed() > 0f
				&& (!weaponFile.isLimitYaw() || weaponFile.getMinYaw() != weaponFile
						.getMaxYaw());
	}

	@Override
	public void resetRotations() {
		super.resetRotations();
		fireDirection.reset();
	}

	@Override
	public WeaponComponent mirrorX() {
		return new WeaponComponent(-pos.x, pos.y, pos.z, direction.copy(),
				pitch, yaw, copyTarget, weaponName, false);
	}

	@Override
	public WeaponComponent mirrorY() {
		return new WeaponComponent(pos.x, -pos.y, pos.z, direction.copy(),
				pitch, yaw, copyTarget, weaponName, false);
	}

	@Override
	public WeaponComponent mirrorZ() {
		return new WeaponComponent(pos.x, pos.y, -pos.z, direction.copy(),
				pitch, yaw, copyTarget, weaponName, false);
	}

}
