package kaba4cow.utils;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.renderEngine.Image;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.noise.Noise;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.toolbox.rng.RandomLehmer;
import kaba4cow.files.InfosFile;
import kaba4cow.files.SystemFile;
import kaba4cow.galaxyengine.NonExistingObjectException;
import kaba4cow.galaxyengine.objects.NebulaObject;
import kaba4cow.galaxyengine.objects.PlanetObject;
import kaba4cow.galaxyengine.objects.SystemObject;
import kaba4cow.gameobjects.Fraction;
import kaba4cow.gameobjects.Planet;
import kaba4cow.gameobjects.World;

public class GalaxyUtils {

	private static final Image GALAXY;

	public static final int GALAXY_SIZE;
	public static final int GALAXY_HEIGHT;
	public static final int SECTOR_SIZE;

	public static final float SECTOR_DENSITY;
	public static final float SYSTEM_DENSITY;

	public static final float NEBULA_CUTOFF;
	public static final float NEBULA_THRESHOLD_MIN;
	public static final float NEBULA_THRESHOLD_MAX;
	public static final float NEBULA_DENSITY_NOISE;
	public static final float NEBULA_SIZE_NOISE;
	public static final float NEBULA_COLOR_NOISE;

	public static final float MAX_SYSTEM_OFF;

	private static final Noise nebulaNoise;
	private static final RNG systemRng;

	private GalaxyUtils() {

	}

	static {
		GALAXY = new Image("textures/GALAXY");

		GALAXY_SIZE = InfosFile.galaxy.data().node("galaxy").node("size")
				.getInt();
		GALAXY_HEIGHT = InfosFile.galaxy.data().node("galaxy").node("height")
				.getInt();

		SECTOR_SIZE = InfosFile.galaxy.data().node("sector").node("size")
				.getInt();
		SECTOR_DENSITY = InfosFile.galaxy.data().node("sector").node("density")
				.getFloat();

		SYSTEM_DENSITY = InfosFile.galaxy.data().node("system").node("density")
				.getFloat();
		MAX_SYSTEM_OFF = InfosFile.galaxy.data().node("system")
				.node("maxoffset").getFloat();

		NEBULA_CUTOFF = InfosFile.galaxy.data().node("nebula").node("cutoff")
				.getFloat();
		NEBULA_THRESHOLD_MIN = InfosFile.galaxy.data().node("nebula")
				.node("threshold").getFloat(0);
		NEBULA_THRESHOLD_MAX = InfosFile.galaxy.data().node("nebula")
				.node("threshold").getFloat(1);

		NEBULA_DENSITY_NOISE = InfosFile.galaxy.data().node("nebula")
				.node("noise").node("density").getFloat();
		NEBULA_SIZE_NOISE = InfosFile.galaxy.data().node("nebula")
				.node("noise").node("size").getFloat();
		NEBULA_COLOR_NOISE = InfosFile.galaxy.data().node("nebula")
				.node("noise").node("color").getFloat();

		nebulaNoise = new Noise(0x8F3B10A955EC203Dl);
		systemRng = new RandomLehmer(0l);
	}

	public static SystemObject getRandomSystem() {
		SystemObject system = null;
		int rangeXZ = GALAXY_SIZE * SECTOR_SIZE / 2;
		int rangeY = GALAXY_HEIGHT * SECTOR_SIZE / 2;
		while (system == null || !(system.systemSize() < 10)) {
			system = generateSystem(RNG.randomInt(-rangeXZ, rangeXZ),
					RNG.randomInt(-rangeY, rangeY),
					RNG.randomInt(-rangeXZ, rangeXZ));
		}
		return system;
	}

	public static SystemObject getRandomNebula() {
		SystemObject system = null;
		NebulaObject nebula = null;
		int rangeXZ = GALAXY_SIZE * SECTOR_SIZE / 2;
		int rangeY = GALAXY_HEIGHT * SECTOR_SIZE / 2;
		while (system == null
				|| !(system.systemSize() == system.starsSize() && system
						.starsSize() > 0)) {
			nebula = generateNebula(RNG.randomInt(-rangeXZ, rangeXZ),
					RNG.randomInt(-rangeY, rangeY),
					RNG.randomInt(-rangeXZ, rangeXZ));
			if (nebula == null)
				system = null;
			else
				system = generateSystem(nebula.posX, nebula.posY, nebula.posZ);
		}
		return system;
	}

	public static SystemObject generateSystem(int posX, int posY, int posZ) {
		systemRng.setSeed(0l);
		SystemFile file = getSystemFile(posX, posY, posZ);
		if (file == null) {
			try {
				return new SystemObject(posX, posY, posZ, null, null);
			} catch (NonExistingObjectException e) {
				return null;
			}
		} else {
			systemRng.setSeed(file.getSeed());
			SystemObject system = null;
			while (system == null) {
				try {
					system = new SystemObject(posX, posY, posZ, file, systemRng);
				} catch (NonExistingObjectException e) {
					system = null;
				}
			}
			return system;
		}
	}

	public static SystemObject generateSystem(String fileName) {
		SystemFile file = SystemFile.get(fileName);
		if (file == null)
			return null;
		SystemObject system = generateSystem(SECTOR_SIZE * file.getPosX()
				+ SECTOR_SIZE / 2, SECTOR_SIZE * file.getPosY() + SECTOR_SIZE
				/ 2, SECTOR_SIZE * file.getPosZ() + SECTOR_SIZE / 2);
		return system;
	}

	public static Fraction getFraction(int posX, int posY, int posZ) {
		List<Fraction> fractions = Fraction.getList();
		float max = 0f;
		Fraction fraction = null;
		for (int i = 0; i < fractions.size(); i++) {
			float density = fractions.get(i).getDensity(posX, posY, posZ);
			if (density > 0f && density > max) {
				max = density;
				fraction = fractions.get(i);
			}
		}
		return fraction;
	}

	public static SystemFile getSystemFile(int posX, int posY, int posZ) {
		if (Maths.abs(posX) % SECTOR_SIZE != SECTOR_SIZE / 2
				|| Maths.abs(posY) % SECTOR_SIZE != SECTOR_SIZE / 2
				|| Maths.abs(posZ) % SECTOR_SIZE != SECTOR_SIZE / 2)
			return null;
		int x = posX;
		int y = posY;
		int z = posZ;
		List<SystemFile> list = SystemFile.getList();
		SystemFile system;
		for (int i = 0; i < list.size(); i++) {
			system = list.get(i);
			if (x == system.getPosX() * SECTOR_SIZE + SECTOR_SIZE / 2
					&& y == system.getPosY() * SECTOR_SIZE + SECTOR_SIZE / 2
					&& z == system.getPosZ() * SECTOR_SIZE + SECTOR_SIZE / 2)
				return system;
		}
		return null;
	}

	public static NebulaObject generateNebula(int posX, int posY, int posZ) {
		float sectorDensity = getSectorDensity(posX, posY, posZ);
		if (sectorDensity <= NEBULA_CUTOFF)
			return null;
		float density = getNebulaDensity(posX, posY, posZ);
		if (density >= NEBULA_THRESHOLD_MIN && density < NEBULA_THRESHOLD_MAX)
			return new NebulaObject(posX, posY, posZ, sectorDensity);
		return null;
	}

	public static List<Planet> createPlanets(World world, SystemObject system) {
		List<Planet> list = new ArrayList<Planet>();
		for (int i = 0; i < system.mainObjects.length; i++)
			list.addAll(createPlanets(world, system.mainObjects[i]));
		return list;
	}

	private static List<Planet> createPlanets(World world, PlanetObject parent) {
		List<Planet> list = new ArrayList<Planet>();
		if (parent.file.isObject()) {
			Planet thisPlanet = parent.getPlanet(world);
			list.add(thisPlanet);
		}
		int systemSize = parent.children.size();
		for (int i = 0; i < systemSize; i++) {
			List<Planet> childList = createPlanets(world,
					parent.children.get(i));
			list.addAll(childList);
		}
		return list;
	}

	public static float getSectorDensity(int posX, int posY, int posZ) {
		int sectorPosX = posX / SECTOR_SIZE + GALAXY_SIZE / 2;
		int sectorPosZ = posZ / SECTOR_SIZE + GALAXY_SIZE / 2;
		float densityY = Maths.map(Maths.abs(posY), 0f, SECTOR_SIZE
				* GALAXY_HEIGHT / 2, 1f, 0f);
		return SECTOR_DENSITY * densityY * GALAXY.getR(sectorPosX, sectorPosZ)
				* Maths.DIV255;
	}

	public static float getNebulaDensity(int posX, int posY, int posZ) {
		float d = NEBULA_DENSITY_NOISE;
		float value = nebulaNoise.getNoiseValue(d * posX, d * posY, d * posZ);
		return value;
	}

	public static float getNebulaColor(int posX, int posY, int posZ) {
		float d = NEBULA_COLOR_NOISE;
		float value = nebulaNoise.getNoiseValue(d * posX, d * posY, d * posZ);
		return value;
	}

	public static float getNebulaSize(int posX, int posY, int posZ) {
		float d = NEBULA_SIZE_NOISE;
		float value = nebulaNoise.getNoiseValue(d * posX, d * posY, d * posZ);
		return value;
	}

	public static long seedSector(int posX, int posY, int posZ) {
		long x = (long) (posX / SECTOR_SIZE) + 0xE8C5Dl;
		long y = (long) (posY / SECTOR_SIZE) + 0xB3209l;
		long z = (long) (posZ / SECTOR_SIZE) + 0x4FE8Bl;
		long s = (long) (x + y + z + SECTOR_SIZE) + 0x9F371l;
		long seed = (x & 0xFFFF) << 48 | (y & 0xFFFF) << 32
				| (z & 0xFFFF) << 16 | (s & 0xFFFF) << 0;
		seed += (x & 0xFFFFF) << 40 | (y & 0xFFFFF) << 20 | (z & 0xFFFFF) << 0;
		return ~seed;
	}

	public static long seedNebula(int posX, int posY, int posZ) {
		long x = (long) posX + 0xB2905l;
		long y = (long) posY + 0x1F059l;
		long z = (long) posZ + 0x7E29Al;
		long s = (long) (x + y + z + SECTOR_SIZE) + 0xC24FBl;
		long seed = (x & 0xFFFF) << 48 | (y & 0xFFFF) << 32
				| (z & 0xFFFF) << 16 | (s & 0xFFFF) << 0;
		return seed;
	}

	public static long seedSystem(long seedSector, int posX, int posY, int posZ) {
		long x = (long) posX;
		long y = (long) posY;
		long z = (long) posZ;
		long seed = (x & 0xFFFF) << 48 | (y & 0xFFFF) << 32
				| (z & 0xFFFF) << 16 | (seedSector & 0xFFFF) << 0;
		return seed;
	}

	public static long seedPlanet(long seedSystem, int position,
			int parentSkipped, int level, PlanetObject parent) {
		long p = (long) position + 0x313Bl;
		long s = (long) parentSkipped + 0x12D5l;
		long l = (long) level + 0xA307l;
		long r = parent == null ? 0x5555l : Double
				.doubleToLongBits(parent.seed);
		if (parent != null) {
			p ^= (long) parent.position + 0x3F42Bl;
			s ^= (long) parent.parentSkipped + 0x5BD23l;
			l ^= (long) parent.level + 0xE4C6Fl;
		}
		long seed = (p & 0xFFFF) << 48 | (s & 0xFFFF) << 32
				| (l & 0xFFFF) << 16 | (r & 0xFFFF) << 0;
		return seed ^ seedSystem;
	}

	public static boolean isCenter(int posX, int posY, int posZ) {
		return posX == 0 && posZ == 0 && posY == 0;
	}

	public static float getScale(String scale) {
		return InfosFile.scales.data().node(scale).getFloat();
	}

	public static float lightYears(float value) {
		return value * getScale("LY");
	}

}
