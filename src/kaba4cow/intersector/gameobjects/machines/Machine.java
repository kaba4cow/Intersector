package kaba4cow.intersector.gameobjects.machines;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.files.MachineFile;
import kaba4cow.files.ShipFile;
import kaba4cow.files.TextureSetFile;
import kaba4cow.intersector.gameobjects.Fraction;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.cargo.Cargo;
import kaba4cow.intersector.gameobjects.cargo.CargoObject;
import kaba4cow.intersector.gameobjects.cargo.CargoType;
import kaba4cow.intersector.gameobjects.cargo.Container;
import kaba4cow.intersector.gameobjects.machinecontrollers.MachineController;
import kaba4cow.intersector.gameobjects.machinecontrollers.shipcontrollers.ShipAIController;
import kaba4cow.intersector.gameobjects.objectcomponents.ColliderComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.ContainerComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.PortComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.WeaponComponent;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.models.ThrustModel;
import kaba4cow.intersector.toolbox.collision.ColliderHolder;
import kaba4cow.intersector.toolbox.flocking.Flock;
import kaba4cow.intersector.toolbox.spawners.DebrisSpawner;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner.Explosion;
import kaba4cow.intersector.toolbox.spawners.ExplosionSpawner.Scale;
import kaba4cow.intersector.utils.RenderUtils;

public abstract class Machine extends GameObject implements ColliderHolder {

	public static final float MAX_DAMAGE_TIME = 5f;

	public static final float MAX_CARGO_PICK_TIME = 0.3f;
	public static final float MAX_CARGO_EJECT_TIME = 0.2f;
	public static final float MAX_CARGO_DIST = 1.5f;

	public static final float MAX_SHIP_PICK_TIME = 0.25f;
	public static final float MAX_SHIP_EJECT_TIME = 0.1f;
	public static final float MAX_SHIP_DIST = 0.5f;

	protected final MachineFile file;

	protected final String textureSet;
	protected final Vector3f machineColor;
	protected final TexturedModel metalModel;
	protected final TexturedModel glassModel;
	protected final ThrustModel thrustModel;

	protected final ThrustComponent[] thrusts;
	protected final ColliderComponent[] colliders;
	protected final WeaponComponent[] weapons;
	protected final ContainerComponent[] containers;
	protected final PortComponent[] ports;

	protected float health;
	protected float shield;
	protected final Cargo[] cargoArray;
	protected final List<Container> containerList;
	protected final List<Ship> shipList;

	protected Fraction fraction;
	protected Flock flock;

	protected boolean turretsEnabled;
	protected boolean launchersEnabled;

	protected float cargoPickTime;
	protected float cargoEjectTime;
	protected float shipPickTime;
	protected float shipEjectTime;
	private float destroyTime;
	private float damageTime;

	private float destroyTimeCargo;
	private float destroyTimeExplosion;

	public Machine(World world, Fraction fraction, MachineFile file,
			Vector3f pos) {
		super(world);

		this.fraction = fraction;
		this.file = file;

		this.textureSet = Manufacturer.get(file.getManufacturer())
				.getRandomTextureSet();
		this.machineColor = fraction.getRandomColor(null);
		this.metalModel = file.getMetalTexturedModel(textureSet);
		this.glassModel = file.getGlassTexturedModel(textureSet,
				file.isUseLight());
		this.thrustModel = file.getThrustModel();

		this.size = file.getSize();
		this.mass = file.getMass();
		this.health = file.getHealth();
		this.shield = file.getShield();
		this.cargoArray = new Cargo[file.getMaxCargo()];
		this.containerList = new ArrayList<Container>();
		this.shipList = new ArrayList<Ship>();
		this.flock = null;
		this.pos = pos;
		this.turretsEnabled = true;
		this.launchersEnabled = true;
		this.cargoPickTime = 0f;
		this.cargoEjectTime = 0f;
		this.shipPickTime = 0f;
		this.shipEjectTime = 0f;
		this.destroyTime = 0f;
		this.damageTime = 0f;

		this.thrusts = new ThrustComponent[file.getThrusts()];
		for (int i = 0; i < thrusts.length; i++)
			thrusts[i] = new ThrustComponent(file.getThrust(i));
		this.colliders = new ColliderComponent[file.getColliders()];
		for (int i = 0; i < colliders.length; i++)
			colliders[i] = new ColliderComponent(file.getCollider(i));
		this.weapons = new WeaponComponent[file.getWeapons()];
		for (int i = 0; i < weapons.length; i++)
			weapons[i] = new WeaponComponent(file.getWeapon(i), true);
		this.containers = new ContainerComponent[file.getContainers()];
		for (int i = 0; i < containers.length; i++)
			containers[i] = new ContainerComponent(file.getContainer(i));
		this.ports = new PortComponent[file.getPorts()];
		for (int i = 0; i < ports.length; i++)
			ports[i] = new PortComponent(file.getPort(i));

		float containerDensity = RNG.randomFloat(1f);
		for (int i = 0; i < containers.length; i++)
			if (RNG.chance(containerDensity))
				addContainer(new Container(world,
						containers[i].containerGroupName,
						CargoType.getRandom(), new Vector3f(pos)));
		float portDensity = RNG.randomFloat(1f);
		for (int i = 0; i < ports.length; i++)
			if (RNG.chance(portDensity)) {
				ShipFile shipFile = fraction.getRandomShip(ports[i].min,
						ports[i].max);
				if (shipFile != null)
					addShip(new Ship(world, fraction, shipFile, new Vector3f(),
							new ShipAIController()), i);
			}
	}

	@Override
	public void update(float dt) {
		damageTime -= dt;
		cargoPickTime -= dt;
		cargoEjectTime -= dt;
		shipPickTime -= dt;
		shipEjectTime -= dt;
		if (isDestroyed()) {
			destroy(dt);
			destroyTime -= dt;
			if (!isDestroyed())
				onFinalDestroy();
		}
		if (damageTime <= 0f && shield < file.getShield())
			shield = Maths.min(shield + dt, file.getShield());

		rotate(rotationVel.x * dt, rotationVel.y * dt, rotationVel.z * dt);

		Matrix4f matrix = direction.getMatrix(pos, true, size);

		if (!isDestroyed())
			for (int i = 0; i < weapons.length; i++) {
				if (weapons[i].isAutoAim()) {
					if (launchersEnabled)
						weapons[i].enable();
					else
						weapons[i].disable();
				} else {
					if (turretsEnabled)
						weapons[i].enable();
					else
						weapons[i].disable();
				}
				weapons[i].update(this, matrix, dt);
			}
		for (int i = 0; i < colliders.length; i++)
			colliders[i].update(this, matrix, dt);
		for (int i = containerList.size() - 1; i >= 0; i--)
			if (!GameObject.isAlive(containerList.get(i)))
				containerList.remove(i);
		for (int i = shipList.size() - 1; i >= 0; i--)
			if (!GameObject.isAlive(shipList.get(i)))
				shipList.remove(i);
	}

	@Override
	public void render(RendererContainer renderers) {
		Matrix4f matrix = direction.getMatrix(pos, true, size);
		RenderUtils.renderMachine(machineColor, metalModel, glassModel, matrix,
				renderers);
		RenderUtils.renderWeapons(machineColor, textureSet, size, matrix,
				renderers, weapons);
	}

	@Override
	public TargetType getTargetType() {
		if (isDestroyed())
			return null;
		return TargetType.MACHINE;
	}

	@Override
	public String getTargetDescription() {
		return getFullName();
	}

	@Override
	public float getTargetParameter1() {
		return getShield() / getMaxShield();
	}

	@Override
	public float getTargetParameter2() {
		return getHealth() / getMaxHealth();
	}

	@Override
	protected void onDamage(Projectile proj) {
		getController().onDamage(proj);
	}

	@Override
	public void onSpawn() {

	}

	public void resetDirection() {
		for (int i = 0; i < weapons.length; i++)
			weapons[i].calculateTranslated(size);
		for (int i = 0; i < containers.length; i++)
			containers[i].calculateTranslated(size);
		for (int i = 0; i < ports.length; i++)
			ports[i].calculateTranslated(size);
	}

	public boolean canReachManual() {
		Machine target = getController().getTargetEnemy();
		for (int i = 0; i < weapons.length; i++)
			if (!weapons[i].weaponFile.getProjectileFile().isAutoaim()
					&& weapons[i].canReach(this, target))
				return true;
		return false;
	}

	public float getMaxManualReload() {
		WeaponComponent best = null;
		float maxReload = Float.NEGATIVE_INFINITY;
		float currentMaxReload;
		for (int i = 0; i < weapons.length; i++) {
			if (weapons[i].isAutoAim() || weapons[i].weaponFile.isAutomatic())
				continue;
			currentMaxReload = Float.NEGATIVE_INFINITY;
			for (int j = 0; j < weapons[i].reload.length; j++)
				currentMaxReload = Maths.max(
						currentMaxReload,
						weapons[i].reload[j]
								/ weapons[i].weaponFile.getReload());
			if (currentMaxReload > maxReload) {
				maxReload = currentMaxReload;
				best = weapons[i];
			}
		}
		if (best == null)
			return 0f;
		return Maths.limit(maxReload);
	}

	public float getMaxAutoAimReload() {
		WeaponComponent best = null;
		float maxReload = Float.NEGATIVE_INFINITY;
		float currentMaxReload;
		for (int i = 0; i < weapons.length; i++) {
			if (!weapons[i].isAutoAim() || weapons[i].weaponFile.isAutomatic())
				continue;
			currentMaxReload = Float.NEGATIVE_INFINITY;
			for (int j = 0; j < weapons[i].reload.length; j++)
				currentMaxReload = Maths.max(
						currentMaxReload,
						weapons[i].reload[j]
								/ weapons[i].weaponFile.getReload());
			if (currentMaxReload > maxReload) {
				maxReload = currentMaxReload;
				best = weapons[i];
			}
		}
		if (best == null)
			return 0f;
		return Maths.limit(maxReload);
	}

	public boolean canReachAutoAim() {
		Machine target = getController().getTargetEnemy();
		for (int i = 0; i < weapons.length; i++)
			if (weapons[i].isAutoAim() && weapons[i].canReach(this, target))
				return true;
		return false;
	}

	public Machine shootManual() {
		Matrix4f matrix = direction.getMatrix(pos, true, size);
		Machine target = getController().getTargetEnemy();
		for (int i = 0; i < weapons.length; i++)
			if (!weapons[i].isAutoAim())
				weapons[i].shipShoot(this, target, matrix);
		return this;
	}

	public Machine shootAutoAim() {
		Matrix4f matrix = direction.getMatrix(pos, true, size);
		Machine target = getController().getTargetEnemy();
		for (int i = 0; i < weapons.length; i++)
			if (weapons[i].isAutoAim())
				weapons[i].shipShoot(this, target, matrix);
		return this;
	}

	@Override
	protected void collide(GameObject obj, float dt) {
		if (obj instanceof Ship) {
			Ship ship = (Ship) obj;
			if (ship.getParent() == this)
				return;
		}
		if (obj instanceof CargoObject) {
			CargoObject cargo = (CargoObject) obj;
			if (cargo.getParent() != null && cargo.getParent() == this)
				return;
			if (cargo.getParent() != null && cargo.getParent() instanceof Ship) {
				Ship ship = (Ship) cargo.getParent();
				if (ship.getParent() == this)
					return;
			}
		}
		if (obj instanceof ColliderHolder)
			ColliderHolder.super.collide((ColliderHolder) obj, dt);
		else if (obj instanceof Projectile) {
			Projectile proj = (Projectile) obj;
			if (proj.getParent() == this || !proj.isActive())
				return;
			Matrix4f shipMatrix = direction.getMatrix(pos, true, size);
			ColliderComponent[] infos = colliders;
			Matrix4f mat1 = null;
			Vector3f colliderPos = new Vector3f();
			Vector3f colliderScale = new Vector3f();
			Vector3f laserPos = proj.getPos().negate(null);
			for (int i = 0; i < infos.length; i++) {
				Vectors.set(colliderScale, infos[i].size * getCollidersScale());
				mat1 = new Matrix4f();
				mat1.translate(infos[i].pos.negate(null));
				mat1.scale(colliderScale);
				Matrix4f.mul(shipMatrix, mat1, mat1);
				Matrices.getTranslation(mat1, colliderPos);

				float radius = (size * infos[i].size + 1f);
				float distSq = Maths.distSq(colliderPos, laserPos) - radius
						* radius;
				if (distSq <= 0f) {
					Vectors.addScaled(colliderPos,
							Maths.direction(colliderPos, laserPos), radius,
							colliderPos);
					hit(world, proj.getDirection(), colliderPos, radius);
					damage(i, proj);
					obj.destroy(this);
					return;
				}
			}
		}
	}

	@Override
	public boolean hasShield() {
		return shield > 0f;
	}

	@Override
	public void damage(float damage) {
		if (!isAlive())
			return;
		damage = Maths.abs(damage);
		damageTime = MAX_DAMAGE_TIME;
		if (hasShield()) {
			shield -= damage;
			if (shield < 0f)
				damage = -shield;
			else
				damage = 0f;
			shield = Maths.max(shield, 0f);
		}
		health = Maths.max(health - damage, 0f);
		if (health <= 0f)
			destroy(this);
	}

	@Override
	public void damage(int colliderIndex, Projectile proj) {
		if (!isAlive())
			return;
		float damage = colliders[colliderIndex].getDamage(proj);
		if (!hasShield())
			colliders[colliderIndex].damage(damage);
		damage(damage);
		onDamage(proj);
	}

	@Override
	public void burn(ColliderComponent collider, float damage) {
		if (!isAlive())
			return;
		damageTime = 4f;
		health -= damage;
		if (health <= 0f)
			destroy(this);
	}

	@Override
	public void destroy(GameObject src) {
		if (isDestroyed())
			return;
		destroyTime = RNG.randomFloat(file.getMachineClass()
				.getMinDestroyTime(), file.getMachineClass()
				.getMaxDestroyTime());
		destroyTimeCargo = 0f;
		destroyTimeExplosion = 0f;
		for (int i = 0; i < colliders.length; i++)
			if (RNG.randomFloat(1f) < 0.05f)
				colliders[i].setOnFire();
	}

	@Override
	protected void onDestroy(GameObject src) {

	}

	@Override
	protected void onDestroy() {
		for (int i = 0; i < containerList.size(); i++)
			containerList.get(i).destroy();
		for (int i = 0; i < shipList.size(); i++)
			shipList.get(i).destroy();
	}

	private void onFinalDestroy() {
		super.destroy(this);
		DebrisSpawner.spawn(world, pos, vel, size,
				TextureSetFile.get(textureSet).getMetalTexture(), machineColor);
		ExplosionSpawner.spawn(this, pos, vel, size, null, Scale.LARGE);
		for (int i = 0; i < containerList.size(); i++)
			containerList.get(i).onParentDestroy();
		for (int i = 0; i < shipList.size(); i++)
			shipList.get(i).onParentDestroy();
	}

	protected void destroy(float dt) {
		destroyTimeCargo += dt;
		destroyTimeExplosion += dt;
		if (destroyTimeCargo >= RNG.randomFloat(0.2f, 1f)) {
			removeContainers();
			destroyTimeCargo = 0f;
		}
		if (destroyTimeExplosion >= RNG.randomFloat(0.1f, 0.5f)) {
			int index = RNG.randomInt(colliders.length);
			Vector3f colliderPos = new Vector3f();
			getColliderPosition(index, colliderPos);
			ExplosionSpawner.spawn(this, colliderPos.negate(null), vel,
					RNG.randomFloat(1f, 2f) * colliders[index].size * size,
					Explosion.SPHERE, Scale.MEDIUM);
			destroyTimeExplosion = 0f;
		}
	}

	public boolean isDestroyed() {
		return destroyTime > 0f;
	}

	@Override
	public void rotate(Vector3f axis, float angle) {
		super.rotate(axis, angle);
		for (int i = 0; i < weapons.length; i++)
			weapons[i].rotateHolder(axis, angle);
		for (int i = 0; i < containers.length; i++)
			containers[i].rotateShip(axis, angle);
		for (int i = 0; i < ports.length; i++)
			ports[i].rotateShip(axis, angle);

		for (int i = 0; i < shipList.size(); i++)
			shipList.get(i).rotate(axis, angle);
	}

	public String getFullName() {
		return Manufacturer.get(file.getManufacturer()).getShortName() + " "
				+ file.getFullName();
	}

	public abstract MachineFile getFile();

	public Flock getFlock() {
		return flock;
	}

	public Machine setFlock(Flock flock) {
		this.flock = flock;
		if (flock != null)
			flock.add(this);
		for (int i = 0; i < shipList.size(); i++)
			shipList.get(i).setFlock(flock);
		return this;
	}

	public Machine pickCargoObject(CargoObject cargo) {
		if (cargoPickTime > 0f)
			return this;
		if (cargo instanceof Container && addContainer((Container) cargo))
			cargoPickTime = MAX_CARGO_PICK_TIME;
		else if (cargo instanceof Cargo && addCargo((Cargo) cargo))
			cargoPickTime = MAX_CARGO_PICK_TIME;
		return this;
	}

	public Machine ejectContainers() {
		if (cargoEjectTime > 0f || containerList.isEmpty())
			return this;
		removeContainers();
		cargoEjectTime = MAX_CARGO_EJECT_TIME;
		return this;
	}

	public boolean canPickShip(Ship ship) {
		if (shipPickTime > 0f)
			return false;
		float shipSize = ship.getCollisionSize();
		for (int i = 0; i < ports.length; i++)
			if (!ports[i].occupied && shipSize >= ports[i].min
					&& shipSize <= ports[i].max)
				return true;
		return false;
	}

	public Machine ejectShips() {
		if (shipEjectTime > 0f || shipList.isEmpty())
			return this;
		removeShip(shipList.get(0));
		return this;
	}

	public void requestPickShip(Ship ship) {
		if (shipList.contains(ship))
			return;
		addShip(ship);
	}

	public void requestEjectShip(Ship ship) {
		if (shipEjectTime > 0f || !shipList.contains(ship))
			return;
		removeShip(ship);
	}

	public boolean allPortsOccupied() {
		for (int i = 0; i < ports.length; i++)
			if (!ports[i].occupied)
				return false;
		return true;
	}

	public boolean allPortsOccupied(float size) {
		for (int i = 0; i < ports.length; i++)
			if (!ports[i].occupied && size >= ports[i].min
					&& size <= ports[i].max)
				return false;
		return true;
	}

	public boolean addCargo(Cargo cargo) {
		if (cargo.getParent() != null || cargo.getParent() == this)
			return false;
		for (int i = 0; i < cargoArray.length; i++)
			if (setCargo(i, cargo))
				return true;
		return true;
	}

	private boolean setCargo(int index, Cargo cargo) {
		if (cargo == null || cargoArray[index] != null || !isPickable(cargo))
			return false;
		cargo.setParent(this);
		cargoArray[index] = cargo;
		return true;
	}

	public boolean isPickable(Cargo cargo) {
		if (cargo == null || cargo.getParent() != null)
			return false;
		return cargo.getParent() == null
				&& Maths.distSq(cargo.getPos(), pos) < Maths.sqr(MAX_CARGO_DIST
						* size);
	}

	public boolean addContainer(Container container) {
		if (container.getParent() != null || container.getParent() == this)
			return false;
		for (int i = 0; i < containers.length; i++)
			if (setContainer(i, container))
				return true;
		return true;
	}

	public boolean addContainer(Container container, int index) {
		if (container.getParent() != null || container.getParent() == this)
			return false;
		if (containers[index].occupied)
			return addContainer(container);
		else
			return setContainer(index, container);
	}

	private boolean setContainer(int index, Container container) {
		if (container == null || !isPickable(container, index))
			return false;
		container.setParent(this, containers[index]);
		containerList.add(container);
		return true;
	}

	public boolean isPickable(Container container) {
		if (container == null || container.getParent() != null)
			return false;
		for (int i = 0; i < containers.length; i++)
			if (isPickable(container, i))
				return true;
		return false;
	}

	public boolean isPickable(Container container, int index) {
		if (cargoPickTime > 0f || container == null
				|| container.getParent() != null)
			return false;
		String groupName = container.getGroupFile().getFileName();
		if (containers[index].occupied
				|| !containers[index].containerGroupName
						.equalsIgnoreCase(groupName))
			return false;
		return container.getParent() == null
				&& Maths.distSq(container.getPos(), pos) < Maths
						.sqr(MAX_CARGO_DIST * size);
	}

	public boolean allContainersOccupied() {
		for (int i = 0; i < containers.length; i++)
			if (!containers[i].occupied)
				return false;
		return true;
	}

	public Machine removeContainer(Container container) {
		if (container.getParent() != this)
			return this;
		container.onEject();
		containerList.remove(container);
		return this;
	}

	public Machine removeContainers() {
		if (!containerList.isEmpty())
			removeContainer(containerList.get(0));
		return this;
	}

	public boolean isPickable(Ship ship) {
		if (shipPickTime > 0f || ship == null || ship.getParent() != null)
			return false;
		for (int i = 0; i < ports.length; i++)
			if (isPickable(ship, i))
				return true;
		return false;
	}

	public boolean isPickable(Ship ship, int index) {
		if (shipPickTime > 0f || ship == null || ship.getParent() != null)
			return false;
		if (ports[index].occupied || ship.getCollisionSize() < ports[index].min
				|| ship.getCollisionSize() > ports[index].max)
			return false;
		return ship.getParent() == null
				&& Maths.distSq(ship.getPos(), pos) < Maths.sqr(MAX_SHIP_DIST
						* size);
	}

	public Machine addShip(Ship ship) {
		if (ship.getParent() != null && ship.getParent() != this)
			return this;
		for (int i = 0; i < ports.length; i++)
			if (isPickable(ship, i)) {
				setShip(i, ship);
				return this;
			}
		return this;
	}

	public Machine addShip(Ship ship, int index) {
		if (ship.getParent() != null && ship.getParent() != this)
			return this;
		if (ports[index].occupied)
			addShip(ship);
		else
			setShip(index, ship);
		return this;
	}

	private Machine setShip(int index, Ship ship) {
		ship.setParent(this, ports[index]);
		shipList.add(ship);
		shipPickTime = MAX_SHIP_PICK_TIME;
		return this;
	}

	public Machine removeShip(Ship ship) {
		if (shipEjectTime > 0f || ship == null || ship.getParent() != null
				&& ship.getParent() != this)
			return this;
		ship.onEject();
		shipList.remove(ship);
		shipEjectTime = MAX_SHIP_EJECT_TIME;
		return this;
	}

	public Machine removeShips() {
		if (!shipList.isEmpty())
			removeShip(shipList.get(0));
		return this;
	}

	@Override
	public float getCollisionSize() {
		return size * file.getCollisionSize();
	}

	public int getOccupiedCargos() {
		int num = 0;
		for (int i = 0; i < containers.length; i++)
			if (containers[i].occupied)
				num++;
		return num;
	}

	public int getOccupiedPorts() {
		int num = 0;
		for (int i = 0; i < ports.length; i++)
			if (ports[i].occupied)
				num++;
		return num;
	}

	public float getHealth() {
		return health;
	}

	public float getMaxHealth() {
		return file.getHealth();
	}

	public float getShield() {
		return shield;
	}

	public float getMaxShield() {
		return file.getShield();
	}

	public boolean isTurretsEnabled() {
		return turretsEnabled;
	}

	public void enableTurrets() {
		turretsEnabled = true;
	}

	public void disableTurrets() {
		turretsEnabled = false;
	}

	public void switchTurretsEnabled() {
		turretsEnabled = !turretsEnabled;
	}

	public boolean isLaunchersEnabled() {
		return launchersEnabled;
	}

	public void enableLaunchers() {
		launchersEnabled = true;
	}

	public void disableLaunchers() {
		launchersEnabled = false;
	}

	public void switchLaunchersEnabled() {
		launchersEnabled = !launchersEnabled;
	}

	public float getDamageTime() {
		return damageTime;
	}

	public Fraction getFraction() {
		return fraction;
	}

	public String getTextureSet() {
		return textureSet;
	}

	public TexturedModel getMetalModel() {
		return metalModel;
	}

	public TexturedModel getGlassModel() {
		return glassModel;
	}

	public abstract MachineController getController();

	public ThrustComponent[] getThrusts() {
		return thrusts;
	}

	@Override
	public ColliderComponent[] getColliders() {
		return colliders;
	}

	public WeaponComponent[] getWeapons() {
		return weapons;
	}

	public ContainerComponent[] getCargos() {
		return containers;
	}

	public PortComponent[] getPorts() {
		return ports;
	}

}
