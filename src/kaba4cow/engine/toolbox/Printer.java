package kaba4cow.engine.toolbox;

public abstract class Printer {

	private static final String COMMA = ", ";
	private static final String NULL = "null";
	private static final String NEWLINE = "\n";

	private static StringBuilder history = new StringBuilder();

	public static void print(Object... args) {
		int length = args.length;
		for (int i = 0; i < length - 1; i++) {
			print(args[i]);
			print(COMMA);
		}
		print(args[length - 1]);
	}

	public static void println(Object... args) {
		int length = args.length;
		for (int i = 0; i < length - 1; i++) {
			print(args[i]);
			print(COMMA);
		}
		print(args[length - 1]);
		println();
	}

	public static void printArray(Object[] array) {
		int length = array.length;
		for (int i = 0; i < length - 1; i++) {
			print(array[i]);
			print(COMMA);
		}
		print(array[length - 1]);
		println();
	}

	public static void printArray(char[] array) {
		int length = array.length;
		for (int i = 0; i < length - 1; i++) {
			print(array[i]);
			print(COMMA);
		}
		print(array[length - 1]);
		println();
	}

	public static void print(Object o) {
		String s = toString(o);
		System.out.print(s);
		history.append(s);
	}

	public static void println(Object o) {
		String s = toString(o);
		System.out.println(s);
		history.append(s).append(NEWLINE);
	}

	public static void println() {
		System.out.println();
		history.append(NEWLINE);
	}

	public static String[] getHistory() {
		return history.toString().split(NEWLINE);
	}

	private static String toString(Object o) {
		return o == null ? NULL : o.toString();
	}

}
