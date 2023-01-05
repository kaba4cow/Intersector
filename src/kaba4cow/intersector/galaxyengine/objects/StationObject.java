package kaba4cow.intersector.galaxyengine.objects;

import java.util.Objects;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.toolbox.rng.RandomLehmer;
import kaba4cow.intersector.files.PlanetFile;
import kaba4cow.intersector.files.StationFile;
import kaba4cow.intersector.gameobjects.Fraction;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.machines.Station;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.toolbox.Physics;
import kaba4cow.intersector.utils.GalaxyUtils;

public class StationObject extends GameObject {

	public static final PlanetFile STATION = PlanetFile.get("STATION");

	public final long seed;
	private final RNG rng;

	public final PlanetObject parent;

	public final StationFile file;

	public String name;
	public float distance;
	public float rotationSpeed;
	public float rotationInclination;
	public float orbitalPosition;
	public float orbitalSpeed;
	public float orbitalInclination;
	public Vector3f worldPosition;

	public StationObject(long seedSystem, PlanetObject parent) throws IllegalStateException {
		super(null);
		this.parent = parent;
		this.seed = GalaxyUtils.seedStation(seedSystem, parent);
		this.rng = new RandomLehmer(seed);

		rng.iterate(Maths.abs((int) seed) % 10);

		this.file = getFraction().getRandomStation(rng);

		this.distance = rng.nextFloat(STATION.getMinDistance(), STATION.getMaxDistance()) * parent.radius
				+ parent.radius;

		this.rotationInclination = rng.nextFloat(0f, 0.1f) * rng.nextFloat(-Maths.PI, Maths.PI);
		direction.rotate(Vectors.RIGHT, rotationInclination);
		direction.rotate(Vectors.UP, rng.nextFloat(0f, Maths.TWO_PI));

		this.orbitalPosition = rng.nextFloat(0f, Maths.TWO_PI);
		this.orbitalInclination = rng.nextFloat(-0.05f, 0.05f) * Maths.QUARTER_PI;
		this.worldPosition = new Vector3f();
	}

	public Fraction getFraction() {
		return parent.system.fraction;
	}

	public Station getStation(World world) {
		return new Station(world, this);
	}

	public void orbit(float dt) {
		orbitalPosition += dt * orbitalSpeed;
	}

	public void move(float distance) {
		this.distance += distance;
	}

	public void calculateRotationSpeeds() {
		orbitalSpeed = Physics.orbitalSpeed(distance, parent.level + 1);
		rotationSpeed = rng.nextFloat(0.0001f, 0.0002f) / (Maths.sqrt(file.getSize()));
	}

	public void calculateWorldPosition() {
		worldPosition.set(0f, 0f, 0f);
		Direction direction = parent.getDirection();
		Vectors.rotate(orbitalPosition, direction.getUp(), direction.getForward(), worldPosition);
		Vectors.rotate(orbitalInclination, direction.getRight(), worldPosition, worldPosition);
		Vectors.addScaled(parent.worldPosition, worldPosition, distance, worldPosition);
	}

	@Override
	public String getTargetDescription() {
		return name;
	}

	@Override
	protected void collide(GameObject obj, float dt) {
		return;
	}

	@Override
	public void update(float dt) {
		return;
	}

	@Override
	public void render(RendererContainer renderers) {
		return;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.PLANET;
	}

	@Override
	public void damage(int colliderIndex, Projectile proj) {
		return;
	}

	@Override
	public void damage(float damage) {
		return;
	}

	@Override
	protected void onDamage(Projectile proj) {
		return;
	}

	@Override
	protected void onDestroy(GameObject src) {

	}

	@Override
	protected void onDestroy() {

	}

	@Override
	public void onSpawn() {
		return;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distance, orbitalInclination, orbitalSpeed, rotationInclination, rotationSpeed, seed);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StationObject other = (StationObject) obj;
		return Float.floatToIntBits(distance) == Float.floatToIntBits(other.distance)
				&& Float.floatToIntBits(orbitalInclination) == Float.floatToIntBits(other.orbitalInclination)
				&& Float.floatToIntBits(orbitalSpeed) == Float.floatToIntBits(other.orbitalSpeed)
				&& Float.floatToIntBits(rotationInclination) == Float.floatToIntBits(other.rotationInclination)
				&& Float.floatToIntBits(rotationSpeed) == Float.floatToIntBits(other.rotationSpeed)
				&& seed == other.seed;
	}

}
