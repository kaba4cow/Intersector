package kaba4cow.intersector.utils;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.utils.StringUtils;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.toolbox.Measures;

public abstract class InfoUtils {

	public static String slash(float value1, float value2) {
		return (int) value1 + " / " + (int) value2;
	}

	public static String mass(float value) {
		return StringUtils.format2(value) + " t";
	}

	public static String cash(float value) {
		return (int) value + " ";
	}

	public static String jump(float value) {
		return StringUtils.format2(value) + " ly";
	}

	public static String dist(SystemObject system1, SystemObject system2) {
		float dist = Maths.dist(system1.getPos(), system2.getPos());
		return StringUtils.format2(GalaxyUtils.lightYears(dist)) + " ly";
	}

	public static String distanceAU(float value) {
		return StringUtils.format(value / GalaxyUtils.getScale("AU"), 4)
				+ " AU";
	}

	public static String distance(float value) {
		if (value <= Measures.KILOMETER)
			return StringUtils.format2(value) + " m";
		if (value <= Measures.MEGAMETER)
			return StringUtils.format2(value / Measures.KILOMETER) + " km";
		if (value <= Measures.LIGHT_SECOND)
			return StringUtils.format2(value / Measures.MEGAMETER) + " Mm";
		if (value <= Measures.LIGHT_MINUTE)
			return StringUtils.format2(value / Measures.LIGHT_SECOND) + " Ls";
		if (value <= Measures.LIGHT_HOUR)
			return StringUtils.format2(value / Measures.LIGHT_MINUTE) + " Lm";
		return StringUtils.format2(value / Measures.LIGHT_HOUR) + " Lh";
	}

	public static String time(float value) {
		if (Float.isInfinite(value) || Float.isNaN(value)
				|| value >= 10000f * Measures.YEAR)
			return "N/A";
		value = Maths.abs(value);
		if (value <= Measures.MINUTE)
			return StringUtils.format2(value / Measures.SECOND) + " sec";
		if (value <= Measures.HOUR)
			return StringUtils.format2(value / Measures.MINUTE) + " min";
		if (value <= Measures.DAY)
			return StringUtils.format2(value / Measures.HOUR) + " hrs";
		if (value <= Measures.WEEK)
			return StringUtils.format2(value / Measures.DAY) + " D";
		if (value <= Measures.MONTH)
			return StringUtils.format2(value / Measures.WEEK) + " W";
		if (value <= Measures.YEAR)
			return StringUtils.format2(value / Measures.MONTH) + " M";
		return StringUtils.format2(value / Measures.YEAR) + " Y";
	}

	public static String speed(float value) {
		return distance(value) + "/s";
	}

	public static String percent(float value) {
		return (int) (100f * value) + " %";
	}

	public static String onOff(boolean value) {
		return value ? "ON" : "OFF";
	}

}
