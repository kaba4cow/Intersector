package kaba4cow.engine.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kaba4cow.engine.toolbox.Printer;

public final class ProgramUtils {

	private static String SCREENSHOT_LOCATION = "";
	private static String SHADER_LOCATION = "";
	private static String FONT_LOCATION = "";
	private static String CUBEMAP_LOCATION = "";
	private static String AUDIO_LOCATION = "";

	private ProgramUtils() {

	}

	public static String getDate() {
		return getDate("MM-dd-YY_HH-mm-ss");
	}

	public static String getDate(String format) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	public static String getTime() {
		DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss");
		return dateFormat.format(Calendar.getInstance().getTime());
	}

	public static String getScreenshotLocation() {
		return SCREENSHOT_LOCATION;
	}

	public static void setScreenshotLocation(String location) {
		SCREENSHOT_LOCATION = location;
		Printer.println("SET SCREENSHOT LOCATION TO: " + location);
	}

	public static String getShaderLocation() {
		return SHADER_LOCATION;
	}

	public static void setShaderLocation(String location) {
		SHADER_LOCATION = location;
		Printer.println("SET SHADER LOCATION TO: " + location);
	}

	public static String getFontLocation() {
		return FONT_LOCATION;
	}

	public static void setFontLocation(String location) {
		FONT_LOCATION = location;
		Printer.println("SET FONT LOCATION TO: " + location);
	}

	public static String getCubemapLocation() {
		return CUBEMAP_LOCATION;
	}

	public static void setCubemapLocation(String location) {
		CUBEMAP_LOCATION = location;
		Printer.println("SET CUBEMAP LOCATION TO: " + location);
	}

	public static String getAudioLocation() {
		return AUDIO_LOCATION;
	}

	public static void setAudioLocation(String location) {
		AUDIO_LOCATION = location;
		Printer.println("SET AUDIO LOCATION TO: " + location);
	}

}
