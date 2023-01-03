package kaba4cow.intersector.toolbox;

public abstract class Measures {

	public static final float SECOND = 1f;
	public static final float MINUTE = 60f * SECOND;
	public static final float HOUR = 60f * MINUTE;
	public static final float DAY = 24f * HOUR;
	public static final float WEEK = 7f * DAY;
	public static final float MONTH = 30f * DAY;
	public static final float YEAR = 12f * MONTH;

	public static final float METER = 1f;
	public static final float KILOMETER = 1000f * METER;
	public static final float MEGAMETER = 1000f * KILOMETER;
	public static final float LIGHT_SECOND = 299792458f;
	public static final float LIGHT_MINUTE = 60f * LIGHT_SECOND;
	public static final float LIGHT_HOUR = 60f * LIGHT_MINUTE;

	public static final float[] TIME = { SECOND, MINUTE, HOUR, DAY, WEEK,
			MONTH, YEAR };

	public static final float[] DISTANCE = { METER, KILOMETER, MEGAMETER,
			LIGHT_SECOND, LIGHT_MINUTE, LIGHT_HOUR };

	private Measures() {

	}

}
