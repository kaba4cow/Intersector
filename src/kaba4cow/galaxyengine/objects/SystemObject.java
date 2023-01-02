package kaba4cow.galaxyengine.objects;

import java.util.Arrays;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.toolbox.rng.RandomLehmer;
import kaba4cow.files.ParticleSystemFile;
import kaba4cow.files.SystemFile;
import kaba4cow.galaxyengine.NameGenerator;
import kaba4cow.galaxyengine.NonExistingObjectException;
import kaba4cow.galaxyengine.objects.PlanetObject.PlanetComparator;
import kaba4cow.gameobjects.Fraction;
import kaba4cow.gameobjects.targets.TargetType;
import kaba4cow.utils.GalaxyUtils;

public class SystemObject extends GalacticObject {

	public static final ParticleSystem particleSystem = ParticleSystemFile.get(
			"STAR").get();

	public final SystemFile systemFile;
	public final long seed;
	private final RNG rng;

	public final PlanetObject[] mainObjects;

	public final float offX;
	public final float offY;
	public final float offZ;

	public String name;

	public Fraction fraction;

	private Particle particle;

	public SystemObject(int posX, int posY, int posZ, SystemFile file,
			RNG baseRng) throws NonExistingObjectException {
		super(posX, posY, posZ);
		long seedSector = GalaxyUtils.seedSector(posX, posY, posZ);
		this.systemFile = file;
		if (systemFile == null)
			this.seed = GalaxyUtils.seedSystem(seedSector, posX, posY, posZ);
		else
			this.seed = systemFile.getSeed() ^ baseRng.getNext();
		this.rng = new RandomLehmer(seed);

		if (rng.nextFloat(0f, 1f) > GalaxyUtils.getSectorDensity(posX, posY,
				posZ)
				&& systemFile == null
				&& !GalaxyUtils.isCenter(posX, posY, posZ))
			throw new NonExistingObjectException();

		if (systemFile != null)
			fraction = Fraction.getFraction(systemFile.getFileName());
		if (fraction == null)
			fraction = GalaxyUtils.getFraction(posX, posY, posZ);

		int numChildren = systemFile == null ? Maths.getBiasedInt(
				rng.nextFloat(0f, 1f), PlanetObject.SYSTEM.getMinChildren(),
				PlanetObject.SYSTEM.getMaxChildren(),
				PlanetObject.SYSTEM.getChildBias()) : 1;
		this.mainObjects = new PlanetObject[numChildren];

		this.offX = posX + GalaxyUtils.MAX_SYSTEM_OFF * rng.nextFloat(-1f, 1f);
		this.offY = posY + GalaxyUtils.MAX_SYSTEM_OFF * rng.nextFloat(-1f, 1f);
		this.offZ = posZ + GalaxyUtils.MAX_SYSTEM_OFF * rng.nextFloat(-1f, 1f);
		this.pos = new Vector3f(offX, offY, offZ);

		if (systemFile == null)
			if (rng.nextFloat(0f, 1f) < 0.03f || fraction != null) {
				if (rng.nextFloat(0f, 1f) < 0.1f)
					this.name = NameGenerator.createSystemName(rng, true,
							rng.nextFloat(0f, 1f) < 0.2f,
							rng.nextFloat(0f, 1f) < 0.1f);
				else
					this.name = NameGenerator.createSystemNameNew(rng, 5, 10,
							rng.nextFloat(0f, 1f) < 0.2f,
							rng.nextFloat(0f, 1f) < 0.1f);
			} else
				this.name = NameGenerator.createSectorSystemName(rng,
						seedSector, posX, posY, posZ);
		else
			this.name = systemFile.getName();

		createMainObjects();

		if (systemFile != null)
			if (mainObjects[0].children.size() != systemFile.getPlanets()
					|| stationsSize() != systemFile.getStations())
				throw new NonExistingObjectException();

		this.size = 0f;
		for (int i = 0; i < mainObjects.length; i++)
			this.size += 0.3f * Maths.sqr(mainObjects[i].file.getImage());
		this.size /= (float) mainObjects.length;
	}

	private void createMainObjects() {
		float distance = 0f;
		for (int i = 0; i < mainObjects.length; i++)
			try {
				mainObjects[i] = new PlanetObject(this, null, 0f, i, 0, 0, seed);
				distance = Maths.max(distance, mainObjects[i].totalDistance);
			} catch (NonExistingObjectException e) {

			}

		Arrays.sort(mainObjects, PlanetComparator.instance);
		float radius = 0f;
		boolean hasStations = false;
		for (int i = 0; i < mainObjects.length; i++) {
			mainObjects[i].position = i;
			mainObjects[i].createName();
			mainObjects[i].calculateMapPosition();
			mainObjects[i].move(i
					* distance
					* rng.nextFloat(PlanetObject.SYSTEM.getMinDistance(),
							PlanetObject.SYSTEM.getMaxDistance()));
			mainObjects[i].move(i * radius);
			mainObjects[i].calculateRotationSpeeds();
			mainObjects[i].calculateWorldPosition();
			radius += mainObjects[i].radius
					* rng.nextFloat(mainObjects[i].file.getMinDistance(),
							mainObjects[i].file.getMaxDistance());
			if (!hasStations && mainObjects[i].stationsSize() > 0)
				hasStations = true;
		}
		if (!hasStations)
			fraction = null;
	}

	public void print() {
		if (systemFile != null)
			Printer.println("\n--- SYSTEM FILE ---");
		else
			Printer.println();
		Printer.println(name + " [" + posX + " " + posY + " " + posZ + "]"
				+ " [" + allegiance() + "]");
		for (int i = 0; i < mainObjects.length; i++)
			mainObjects[i].print();
		Printer.println(systemSize(), starsSize(), stationsSize());
		Printer.println(seed);
	}

	public String allegiance() {
		if (fraction == null)
			return "Independent";
		return fraction.getFractionName();
	}

	public int starsSize() {
		return mainObjects.length;
	}

	public int systemSize() {
		int sum = 0;
		for (int i = 0; i < mainObjects.length; i++)
			sum += mainObjects[i].systemSize();
		return sum;
	}

	public int stationsSize() {
		int sum = 0;
		for (int i = 0; i < mainObjects.length; i++)
			sum += mainObjects[i].stationsSize();
		return sum;
	}

	public boolean contains(String planet) {
		for (int i = 0; i < mainObjects.length; i++)
			if (mainObjects[i].contains(planet))
				return true;
		return false;
	}

	public Particle getParticle(List<Particle> freeParticles) {
		if (freeParticles == null || freeParticles.isEmpty()) {
			if (particle == null) {
				particle = new Particle(particleSystem, new Vector3f(-offX,
						-offY, -offZ), Vectors.INIT3, 0f, 0f, size);
				particle.setTint(getParticleTintColor(), getParticleTintPower());
			}
		} else if (particle == null) {
			particle = freeParticles.remove(0);
			particle.getPos().set(-offX, -offY, -offZ);
			particle.setScale(size);
			particle.setTint(getParticleTintColor(), getParticleTintPower());
			particle.returnToSystem();
		}
		return particle;
	}

	@Override
	protected Vector3f getParticleTintColor() {
		Vector3f sum = new Vector3f();
		for (int i = 0; i < mainObjects.length; i++)
			Vector3f.add(sum, Maths.blend(mainObjects[i].color, Vectors.UNIT3,
					0.75f, null), sum);
		sum.scale(1f / (float) mainObjects.length);
		return sum;
	}

	@Override
	protected float getParticleTintPower() {
		float sum = 0f;
		for (int i = 0; i < mainObjects.length; i++)
			sum += mainObjects[i].file.getLight();
		return sum / (float) mainObjects.length;
	}

	@Override
	protected float getParticleLifeFactor() {
		return 0f;
	}

	@Override
	protected float getParticleRotation() {
		return 0f;
	}

	@Override
	protected ParticleSystem getParticleSystem() {
		return particleSystem;
	}

	@Override
	public String getTargetDescription() {
		return name;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.SYSTEM;
	}

}
