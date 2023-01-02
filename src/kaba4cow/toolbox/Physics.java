package kaba4cow.toolbox;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.utils.GalaxyUtils;

public abstract class Physics {

	private Physics() {

	}

	public static float orbitalSpeed(float distance, int level) {
		distance /= GalaxyUtils.getScale("AU");
		if (level >= 2)
			distance /= 0.002569f;
		float periodSq = Maths.pow(distance, 3f);
		float timing = level >= 2 ? Measures.WEEK : Measures.YEAR;
		return Maths.TWO_PI / (timing * Maths.sqrt(periodSq));
	}

	public static int habitability(float distance, float radius) {
		distance /= GalaxyUtils.getScale("AU");
		radius /= GalaxyUtils.getScale("S");
		float habitability = 2.87f * Maths.pow(distance, 0.72f) - 2.06f
				* Maths.pow(radius, 0.78f);
		float off = (radius + 0.23f) / (habitability - 0.71f);
		if (habitability < 0.95f - off)
			return -1;
		if (habitability > 1.37f + off)
			return 1;
		return 0;
	}

}
