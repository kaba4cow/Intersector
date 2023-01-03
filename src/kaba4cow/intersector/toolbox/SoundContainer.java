package kaba4cow.intersector.toolbox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import kaba4cow.engine.audio.AudioManager;

public final class SoundContainer {

	private static final Map<String, Integer> map = new HashMap<String, Integer>();

	private SoundContainer() {

	}

	public static void loadAll() {
		File root = new File("resources/sounds/");
		load(root);
	}

	private static void load(File file) {
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++)
				load(files[i]);
		} else {
			String fileName = file.getAbsolutePath();
			fileName = fileName.replace("\\", "/");
			fileName = fileName.split("resources/sounds/")[1].split(".wav")[0];
			get(fileName);
		}
	}

	public static Integer get(String file) {
		if (file.equalsIgnoreCase("null"))
			return null;
		if (!map.containsKey(file)) {
			Integer source = AudioManager.load(file);
			map.put(file, source);
		}
		return map.get(file);
	}

	public static String[] getArray() {
		String[] array = new String[map.keySet().size()];
		int i = 0;
		for (String string : map.keySet())
			array[i++] = string;
		return array;
	}
}
