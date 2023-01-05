package kaba4cow.intersector.galaxyengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.files.TableFile;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.toolbox.rng.RandomLehmer;
import kaba4cow.engine.utils.StringUtils;
import kaba4cow.intersector.files.InfosFile;
import kaba4cow.intersector.toolbox.Constants;
import kaba4cow.intersector.utils.GalaxyUtils;

public class NameGenerator {

	private static final Map<String, List<String>> map = new HashMap<String, List<String>>();

	private NameGenerator() {

	}

	static {
		TableFile tableFile = TableFile.read("resources/tables/SYLLABLES");
		for (int column = 1; column < tableFile.columns(); column++) {
			String syllable = tableFile.cell(0, column).getString();
			List<String> nextList = new ArrayList<String>();
			for (int row = 1; row < tableFile.rows(); row++) {
				float value = tableFile.cell(row, column).getFloat();
				if (value > 0f) {
					String nextSyllable = tableFile.cell(row, 0).getString();
					nextList.add(nextSyllable);
				}
			}
			map.put(syllable, nextList);
		}
	}

	public static String createPlanetName(int position, int parentSkipped,
			int level, String parentName) {
		if (level < 0)
			return parentName;
		if (level == 0)
			return parentName + " "
					+ Constants.LETTERSU[position - parentSkipped];
		if (level == 1)
			return parentName + (position + 1 - parentSkipped);
		return parentName + Constants.LETTERSL[position - parentSkipped];
	}

	public static String createSectorName(long seedSector) {
		RNG rngSector = new RandomLehmer(seedSector);
		String name = new String();
		int numLetters = rngSector.nextInt(3, 6);
		if (rngSector.nextBoolean())
			numLetters = 2 + Maths.abs((int) seedSector) % 3;
		for (int i = 0; i < numLetters; i++)
			name += Constants.LETTERSU[rngSector.nextInt(0,
					Constants.LETTERSU.length)];
		return name;
	}

	public static String createSectorSystemName(RNG rng, long seedSector,
			int posX, int posY, int posZ) {
		String name = "";
		int x = Maths.abs(posX) % GalaxyUtils.SECTOR_SIZE;
		int y = Maths.abs(posY) % GalaxyUtils.SECTOR_SIZE;
		int z = Maths.abs(posZ) % GalaxyUtils.SECTOR_SIZE;
		name += Constants.HEXS[x];
		name += Constants.HEXS[z];
		name += Constants.HEXS[y];
		name = name.toLowerCase();
		int numLetters = rng.nextInt(0, 3);
		for (int i = 0; i < numLetters; i++)
			name += Constants.LETTERSU[rng
					.nextInt(0, Constants.LETTERSU.length)];
		return createSectorName(seedSector) + "-" + name;
	}

	public static String createSystemNameNew(RNG rng, int minMinLength,
			int maxMinLength, boolean addWord, boolean isDouble) {
		minMinLength = Maths.max(minMinLength, 4);

		String name = new String();
		String prev = "BEGIN";
		int length = 0;
		int minLength = rng.nextInt(minMinLength, maxMinLength);
		while (length < minLength) {
			prev = getSyllableNew(prev, rng);
			if (prev == null)
				length++;
			else {
				rng.iterate(prev.length());
				name += prev;
				length += prev.length();
			}
		}
		name = StringUtils.capitalize(name);

		if (isDouble) {
			String newName = createSystemNameNew(rng, minMinLength / 2,
					maxMinLength / 2, false, false);
			if (rng.nextBoolean())
				name = name + " " + newName;
			else
				name = newName + " " + name;
		} else if (addWord)
			name = addWord(rng, name);

		return name;
	}

	private static String getSyllableNew(String prev, RNG rng) {
		if (prev == null)
			return null;
		rng.iterate(prev.length());
		List<String> list = map.get(prev);
		if (list.isEmpty())
			return null;
		return list.get(rng.nextInt(0, list.size()));
	}

	public static String createSystemName(RNG rng, boolean addMid,
			boolean addWord, boolean isDouble) {
		boolean beginA = rng.nextBoolean();
		boolean endA = rng.nextBoolean();
		String begin = getSyllable("begin", beginA, endA, rng);
		rng.iterate(begin.length());

		boolean useMid = addMid
				&& (rng.nextBoolean() && rng.nextBoolean()
						|| begin.length() <= 2 || !endA);
		if (useMid) {
			beginA = !endA;
			endA = rng.nextBoolean();
		}
		String mid = getSyllable("mid", beginA, endA, rng);
		rng.iterate(mid.length());

		beginA = !endA;
		if (endA && rng.nextFloat(0f, 1f) < 0.2f)
			beginA = rng.nextBoolean();
		endA = rng.nextBoolean();
		String end = getSyllable("end", beginA, endA, rng);
		rng.iterate(end.length());

		String name = begin;
		if (useMid)
			name += mid;
		name += end;
		name = StringUtils.capitalize(name);

		if (isDouble) {
			if (rng.nextBoolean())
				name = name
						+ " "
						+ createSystemName(rng, rng.nextFloat(0f, 1f) < 0.2f,
								false, false);
			else
				name = createSystemName(rng, rng.nextFloat(0f, 1f) < 0.3f,
						false, false) + " " + name;
		}

		if (addWord && !isDouble)
			name = addWord(rng, name);

		return name;
	}

	private static String addWord(RNG rng, String name) {
		boolean usePrefix = rng.nextFloat(0f, 1f) < 0.6f;
		String word = getSyllable(usePrefix ? "prefix" : "suffix", rng);
		word = StringUtils.capitalize(word);
		if (usePrefix)
			name = word + " " + name;
		else
			name = name + " " + word;
		return name;
	}

	private static String getEdges(boolean beginA, boolean endA) {
		return (beginA ? "a" : "b") + (endA ? "a" : "b");
	}

	private static String getSyllable(String part, boolean beginA,
			boolean endA, RNG rng) {
		rng.iterate(part.length());
		return getSyllable(part + getEdges(beginA, endA), rng);
	}

	private static String getSyllable(String type, RNG rng) {
		rng.iterate(type.length());
		String[] array = InfosFile.syllables.data().node(type).toStringArray();
		if (array == null)
			return null;
		return array[rng.nextInt(0, array.length)];
	}

}
