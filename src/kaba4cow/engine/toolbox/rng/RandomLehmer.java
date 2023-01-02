package kaba4cow.engine.toolbox.rng;

public class RandomLehmer extends RNG {

	public static final RandomLehmer instance = new RandomLehmer(
			RNG.randomLong());

	public RandomLehmer(long seed) {
		super(seed);
	}

	public RandomLehmer() {
		super();
	}

	@Override
	public long getNext() {
		current += 0xE120FC154FA19BD6l;
		long tmp = current * 0x4A39B70DA719B204l;
		long m1 = (tmp >> 16) ^ tmp;
		tmp = m1 * 0x12FAD5C95AF4CE28l;
		long m2 = (tmp >> 16) ^ tmp;
		return m2;
	}

}