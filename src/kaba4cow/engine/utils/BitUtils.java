package kaba4cow.engine.utils;

public final class BitUtils {

	private BitUtils() {

	}

	public static boolean isSetBit(int number, int index) {
		return (number & (1 << index)) != 0;
	}

	public static int setBit(int number, int index) {
		return number | (1 << index);
	}

	public static int resetBit(int number, int index) {
		return number & ~(1 << index);
	}

	public static int inverseBit(int number, int index) {
		return number ^ (1 << index);
	}

}
