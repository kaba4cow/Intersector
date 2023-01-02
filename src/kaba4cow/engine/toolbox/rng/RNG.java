package kaba4cow.engine.toolbox.rng;

import kaba4cow.engine.toolbox.maths.Maths;

public abstract class RNG {

	protected long seed;
	protected long current;

	public RNG(long seed) {
		this.seed = seed;
		this.current = seed;
	}

	public RNG() {
		this(randomLong());
	}

	public long getSeed() {
		return seed;
	}

	public void setSeed(long seed) {
		this.seed = seed;
		this.current = seed;
	}

	public abstract long getNext();

	public void iterate(int iterations) {
		for (int i = 0; i < iterations; i++)
			getNext();
	}

	public boolean nextBoolean() {
		return getNext() % 2 == 0;
	}

	public int nextSign() {
		return nextBoolean() ? 1 : -1;
	}

	public float nextNormal() {
		return ((float) getNext() / (float) (0x7FFFFFFFFFFFFFFFl));
	}

	public int nextInt(int min, int max) {
		if (max <= min)
			return min;
		return (int) (getNext() % (max - min)) + min;
	}

	public long nextLong(long min, long max) {
		if (max <= min)
			return min;
		return (getNext() % (max - min)) + min;
	}

	public float nextFloat(float min, float max) {
		if (max <= min)
			return min;
		return ((float) getNext() / (float) (0x7FFFFFFFFFFFFFFFl) * (max - min))
				+ min;
	}

	public double nextDouble(double min, double max) {
		if (max <= min)
			return min;
		return ((double) getNext() / (double) (0x7FFFFFFFFFFFFFFFl) * (max - min))
				+ min;
	}

	public static boolean chance(float chance) {
		return randomFloat(1f) < chance;
	}

	public static long randomLong(long min, long max) {
		return (long) Maths.map(Math.random(), 0d, 1d, min, max);
	}

	public static long randomLong(long max) {
		return randomLong(0l, max);
	}

	public static long randomLong() {
		return randomLong(0l, Long.MAX_VALUE);
	}

	public static int randomInt(int min, int max) {
		return (int) Maths.map(Math.random(), 0d, 1d, min, max);
	}

	public static int randomInt(int max) {
		return randomInt(0, max);
	}

	public static int randomInt() {
		return randomInt(0, Integer.MAX_VALUE);
	}

	public static float randomFloat(float min, float max) {
		return (float) Maths.map(Math.random(), 0d, 1d, min, max);
	}

	public static float randomFloat(float max) {
		return randomFloat(0f, max);
	}

	public static float randomFloat() {
		return randomFloat(0f, Float.MAX_VALUE);
	}

	public static double randomDouble(double min, double max) {
		return Maths.map(Math.random(), 0d, 1d, min, max);
	}

	public static double randomDouble(double max) {
		return randomDouble(0d, max);
	}

	public static double randomDouble() {
		return randomDouble(0d, Double.MAX_VALUE);
	}

	public static boolean randomBoolean() {
		return Math.random() > 0.5d;
	}

	public static boolean randomBooleanAnd(int iterations) {
		boolean value = true;
		for (int i = 0; i < iterations; i++) {
			value &= RNG.randomBoolean();
			if (!value)
				return value;
		}
		return value;
	}

	public static boolean randomBooleanOr(int iterations) {
		boolean value = false;
		for (int i = 0; i < iterations; i++) {
			value |= RNG.randomBoolean();
			if (value)
				return value;
		}
		return value;
	}

	public static boolean randomBooleanXor(int iterations) {
		boolean value = RNG.randomBoolean();
		for (int i = 1; i < iterations; i++)
			value ^= RNG.randomBoolean();
		return value;
	}

}
