package kaba4cow.engine.toolbox;

public final class MemoryAnalyzer {

	private static long maxUsage = Long.MIN_VALUE;
	private static long currentUsage = 0l;
	private static long deltaUsage = 0l;

	private static long totalUsage = 0l;
	private static int updates = 0;

	private MemoryAnalyzer() {

	}

	public static void update() {
		deltaUsage = currentUsage;
		currentUsage = Runtime.getRuntime().totalMemory()
				- Runtime.getRuntime().freeMemory();
		deltaUsage = currentUsage - deltaUsage;

		if (currentUsage > maxUsage)
			maxUsage = currentUsage;

		totalUsage += currentUsage;
		updates++;
	}

	public static void printCurrentInfo() {
		Printer.println("MEMORY ANALYZER: current " + getCurrentUsage() / 1024l
				+ " KB, delta " + getDeltaUsage() / 1024l + " KB, total "
				+ Runtime.getRuntime().maxMemory() / 1024l + " KB");
	}

	public static void printFinalInfo() {
		long avgUsage = totalUsage / (long) updates;
		Printer.println("MEMORY ANALYZER: max " + getMaxUsage() / 1024l
				+ " KB, avg " + avgUsage / 1024l + " KB");
	}

	public static long getMaxUsage() {
		return maxUsage;
	}

	public static long getCurrentUsage() {
		return currentUsage;
	}

	public static long getDeltaUsage() {
		return deltaUsage;
	}

}
