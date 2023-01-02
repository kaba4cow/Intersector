package kaba4cow.galaxyengine.objects;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.Pair;
import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.toolbox.rng.RandomLehmer;
import kaba4cow.files.PlanetFile;
import kaba4cow.galaxyengine.NameGenerator;
import kaba4cow.galaxyengine.NonExistingObjectException;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.gameobjects.Planet;
import kaba4cow.gameobjects.World;
import kaba4cow.gameobjects.projectiles.Projectile;
import kaba4cow.gameobjects.targets.TargetType;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.toolbox.Physics;
import kaba4cow.utils.GalaxyUtils;
import kaba4cow.utils.InfoUtils;

public class PlanetObject extends GameObject {

	public static final float PADDING_X = 2f;
	public static final float PADDING_Y = 1.75f;

	public static final PlanetFile SYSTEM = PlanetFile.get("SYSTEM");
	public static final PlanetFile RING = PlanetFile.get("RING");
	public static final PlanetFile NEBULA = PlanetFile.get("NEBULA");

	public final long seed;
	private final RNG rng;

	public final PlanetFile file;

	public final SystemObject system;
	public final PlanetObject parent;
	public List<PlanetObject> children;

	public String name;
	public Vector3f color;
	public int flares;
	public int position;
	public int parentSkipped;
	public int level;
	public float radius;
	public float distance;
	public float totalDistance;
	public float ringRadius;
	public float rotationSpeed;
	public float rotationInclination;
	public float orbitalPosition;
	public float orbitalSpeed;
	public float orbitalInclination;
	public long terrainSeed;
	public Vector3f worldPosition;

	public int mapX;
	public int mapY;

	public boolean station;

	private Planet planet;

	public PlanetObject(SystemObject system, PlanetObject parent, float distance, int position, int parentSkipped,
			int level, long seedSystem) throws NonExistingObjectException {
		super(null);
		this.system = system;
		this.seed = GalaxyUtils.seedPlanet(seedSystem, position, parentSkipped, level, parent);
		this.rng = new RandomLehmer(seed);

		rng.iterate(Maths.abs(position + parentSkipped + level + (int) seed) % 10);
		if (rng.nextFloat(0f, 1f) >= GalaxyUtils.SYSTEM_DENSITY && system.systemFile == null && parent != null)
			throw new NonExistingObjectException();

		if (system.systemFile == null || parent != null) {
			PlanetFile[] fileArray = new PlanetFile[rng.nextInt(1, 9)];
			for (int i = 0; i < fileArray.length; i++)
				fileArray[i] = createPlanet(rng, parent, position, level, distance);
			this.file = fileArray[rng.nextInt(0, fileArray.length)];
			if (file == null)
				throw new NonExistingObjectException();
		} else
			this.file = PlanetFile.get(system.systemFile.getStar());

		this.station = system.fraction != null && rng.nextFloat(0f, 100f) < file.getStation();
		this.planet = null;

		this.parent = parent;
		this.color = file.getColor(rng.nextFloat(0f, 1f), null);
		this.flares = Maths.getBiasedInt(rng.nextFloat(0f, 1f), file.getMinFlares(), file.getMaxFlares(), 0f);

		this.children = new ArrayList<PlanetObject>();
		this.terrainSeed = rng.getNext();
		this.distance = distance;
		this.position = position;
		this.parentSkipped = parentSkipped;
		this.level = level;

		this.rotationInclination = file.getAxisOffset() * rng.nextFloat(-Maths.PI, Maths.PI);
		if (rng.nextFloat(0f, 100f) < 1f)
			this.rotationInclination *= rng.nextFloat(2f, 8f);
		direction.rotate(Vectors.RIGHT, rotationInclination);
		direction.rotate(Vectors.UP, rng.nextFloat(0f, Maths.TWO_PI));

		this.radius = GalaxyUtils.getScale(file.getScale())
				* Maths.getBiasedFloat(rng.nextFloat(0f, 1f / (level < 2 ? 1f : 2f)), file.getMinSize(),
						file.getMaxSize(), file.getSizeBias());
		if (parent != null)
			this.distance += radius;

		if (rng.nextFloat(0f, 100f) < file.getRing() && level < 2)
			this.ringRadius = rng.nextFloat(RING.getMinSize(), RING.getMaxSize());
		else
			this.ringRadius = 0f;

		this.orbitalPosition = rng.nextFloat(0f, Maths.TWO_PI);
		this.orbitalInclination = (level + 1f) * rng.nextFloat(-0.05f, 0.05f) * Maths.QUARTER_PI;
		if (rng.nextFloat(0f, 100f) < 5f)
			this.orbitalInclination *= rng.nextFloat(4f, 8f);
		this.worldPosition = new Vector3f();

		this.size = file.getImage();

		int numChildren = Maths.getBiasedInt(rng.nextFloat(0f, 1f), file.getMinChildren(), file.getMaxChildren(),
				file.getChildBias());
		if (parent == null)
			numChildren -= system.starsSize() - 1;
		if (level == 2)
			numChildren = 0;
		int skipped = 0;
		totalDistance = radius;
		float distanceFactor = rng.nextFloat(0f, 1f);
		for (int i = 0; i < numChildren; i++) {
			float value = Maths.limit(distanceFactor + rng.nextFloat(-0.05f, 0.15f) / numChildren);
			value = Maths.blend(1f, rng.nextFloat(i / (float) numChildren, 1f), value);
			totalDistance += radius * Maths.map(value, 0f, 1f, file.getMinDistance(), file.getMaxDistance());
			if (i == 0 && ringRadius > 0f) {
				rng.getNext();
				skipped++;
				continue;
			}
			try {
				PlanetObject child = new PlanetObject(system, this, totalDistance, i, skipped, level + 1, seed + i);
				totalDistance += 2f * (child.totalDistance + child.radius);
				children.add(child);
			} catch (NonExistingObjectException e) {
				rng.getNext();
				skipped++;
				continue;
			}
		}
	}

	public void print() {
		String padding = "";
		for (int i = 0; i < level; i++)
			padding += "  ";
		String info = padding + file.getName() + "  [";
		info += InfoUtils.distanceAU(distance) + " / ";
		info += InfoUtils.distance(radius) + "]  [";
		info += InfoUtils.time(Maths.TWO_PI / orbitalSpeed) + " / ";
		info += InfoUtils.time(Maths.TWO_PI / rotationSpeed) + "]  [";
		info += "HZ" + habitability(distance) + "]";
		if (station)
			info += " [station]";
		Printer.println(info);

		if (ringRadius > 0f)
			Printer.println(padding + "  Ring / " + InfoUtils.distance(ringRadius * radius));
		for (int i = 0; i < children.size(); i++)
			children.get(i).print();
	}

	public Planet getPlanet(World world) {
		if (planet == null)
			planet = new Planet(world, this);
		return planet;
	}

	public boolean contains(String planet) {
		if (file.getFileName().equalsIgnoreCase(planet))
			return true;
		for (int i = 0; i < children.size(); i++)
			if (children.get(i).contains(planet))
				return true;
		return false;
	}

	public int systemSize() {
		if (children.isEmpty())
			return 1;
		int sum = 1;
		for (int i = 0; i < children.size(); i++)
			sum += children.get(i).systemSize();
		return sum;
	}

	public int habitability(float distance) {
		if (parent != null)
			return parent.habitability(parent.distance + distance);
		else
			distance -= this.distance;
		return Physics.habitability(distance, radius);
	}

	public int stationsSize() {
		int sum = station ? 1 : 0;
		for (int i = 0; i < children.size(); i++)
			sum += children.get(i).stationsSize();
		return sum;
	}

	public void orbit(float dt) {
		orbitalPosition += dt * orbitalSpeed;
	}

	public void move(float distance) {
		this.distance += distance;
	}

	public void createName() {
		name = NameGenerator.createPlanetName(position, parentSkipped, level,
				parent == null ? system.name : parent.name);
		for (int i = 0; i < children.size(); i++)
			children.get(i).createName();
	}

	public void calculateMapPosition() {
		if (parent == null) {
			if (position > 0) {
				int height = 0;
				PlanetObject prev = system.mainObjects[position - 1];
				for (int i = 0; i < prev.children.size(); i++)
					height = Maths.max(height, prev.children.get(i).children.size());
				mapY = prev.mapY + height + 1;
			}
		} else if (level == 1) {
			mapX = 1 + position - parentSkipped;
			mapY = parent.mapY;
		} else if (level == 2) {
			mapX = parent.mapX;
			mapY = parent.mapY + (1 + position - parentSkipped);
		}
		pos = new Vector3f(PADDING_X * mapX, PADDING_Y * mapY, 0f);
		for (int i = 0; i < children.size(); i++)
			children.get(i).calculateMapPosition();
	}

	public void calculateRotationSpeeds() {
		PlanetObject parent = this.parent;
		if (parent == null && position > 0)
			parent = system.mainObjects[0];
		if (parent != null)
			orbitalSpeed = Physics.orbitalSpeed(distance, level);
		else
			orbitalSpeed = 0f;
		rotationSpeed = rng.nextFloat(0.001f, 0.05f + 0.025f * level)
				/ (rng.nextFloat(0.9f, 1.1f) * Maths.sqrt(radius));
		if (rng.nextFloat(0f, 100f) < 1f)
			rotationSpeed *= rng.nextFloat(-0.05f, 0.15f);
		else if (rng.nextFloat(0f, 100f) < 3f * level - 2f * position)
			rotationSpeed = orbitalSpeed;
		for (int i = 0; i < children.size(); i++)
			children.get(i).calculateRotationSpeeds();
	}

	public void calculateWorldPosition() {
		worldPosition.set(0f, 0f, 0f);
		if (distance > 0f) {
			Vector3f parentPosition = parent == null ? Vectors.INIT3 : parent.worldPosition;
			Direction direction = parent == null ? Direction.INIT : parent.direction;
			Vectors.rotate(orbitalPosition, direction.getUp(), direction.getForward(), worldPosition);
			Vectors.rotate(orbitalInclination, direction.getRight(), worldPosition, worldPosition);
			Vectors.addScaled(parentPosition, worldPosition, distance, worldPosition);
		}
		for (int i = 0; i < children.size(); i++)
			children.get(i).calculateWorldPosition();
	}

	public static PlanetFile createPlanet(RNG rng, PlanetObject parent, int position, int level, float distance) {
		int habitability = parent == null ? 1 : parent.habitability(distance);
		rng.iterate(habitability + position + level);
		if (parent != null)
			rng.iterate(parent.position + parent.level);
		List<Pair<PlanetFile, Float>> parentMap = parent == null ? SYSTEM.getChildrenMap()
				: parent.file.getChildrenMap();
		float pos = rng.nextFloat(0f, 100f);
		for (Pair<PlanetFile, Float> child : parentMap) {
			if (pos <= child.getB() && habitability >= child.getA().getMinHabitability())
				return child.getA();
		}
		return null;
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
	public void onSpawn() {
		return;
	}

	@Override
	protected void onDestroy(GameObject src) {
		return;
	}

	@Override
	protected void onDestroy() {
		return;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((color == null) ? 0 : color.hashCode());
		result = prime * result + Float.floatToIntBits(distance);
		result = prime * result + level;
		result = prime * result + parentSkipped;
		result = prime * result + position;
		result = prime * result + Float.floatToIntBits(radius);
		result = prime * result + Float.floatToIntBits(rotationSpeed);
		result = prime * result + (int) (seed ^ (seed >>> 32));
		result = prime * result + (int) (terrainSeed ^ (terrainSeed >>> 32));
		result = prime * result + ((worldPosition == null) ? 0 : worldPosition.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PlanetObject other = (PlanetObject) obj;
		if (color == null) {
			if (other.color != null)
				return false;
		} else if (!color.equals(other.color))
			return false;
		if (Float.floatToIntBits(distance) != Float.floatToIntBits(other.distance))
			return false;
		if (level != other.level)
			return false;
		if (parentSkipped != other.parentSkipped)
			return false;
		if (position != other.position)
			return false;
		if (Float.floatToIntBits(radius) != Float.floatToIntBits(other.radius))
			return false;
		if (Float.floatToIntBits(rotationSpeed) != Float.floatToIntBits(other.rotationSpeed))
			return false;
		if (seed != other.seed)
			return false;
		if (terrainSeed != other.terrainSeed)
			return false;
		if (worldPosition == null) {
			if (other.worldPosition != null)
				return false;
		} else if (!worldPosition.equals(other.worldPosition))
			return false;
		return true;
	}

	public static class PlanetComparator implements Comparator<PlanetObject> {

		public static final PlanetComparator instance = new PlanetComparator();

		@Override
		public int compare(PlanetObject o1, PlanetObject o2) {
			return Float.compare(o2.radius, o1.radius);
		}

	}

}
