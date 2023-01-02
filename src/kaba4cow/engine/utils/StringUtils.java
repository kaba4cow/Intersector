package kaba4cow.engine.utils;

public final class StringUtils {

	private StringUtils() {

	}

	public static String repeat(String string, int times) {
		String result = "";
		for (int i = 0; i < times; i++)
			result += string;
		return result;
	}

	public static String capitalize(String string) {
		if (string.length() <= 1)
			return string.toUpperCase();
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	public static String format1(float value) {
		return String.format("%.1f", value);
	}

	public static String format2(float value) {
		return String.format("%.2f", value);
	}

	public static String format(float value, int digits) {
		return String.format("%." + digits + "f", value);
	}

}
