package kaba4cow.engine.toolbox.rng;

public class RandomXOR extends RNG {

	public static final RandomXOR instance = new RandomXOR(RNG.randomLong());

	public RandomXOR(long seed) {
		super(seed);
	}

	public RandomXOR() {
		super();
	}

	@Override
	public long getNext() {
		long t1 = current >> 2;
		long t2 = current << 4;
		long t3 = current >> 8;
		long t4 = current << 16;
		current ^= (t1 * 0xEA3Bl) ^ (t2 * 0xC985l) ^ (t3 * 0x4CC3l)
				^ (t4 * 0x2AF8l);
		return current & 0x7FFFFFFFFFFFFFFFl;
	}

}
