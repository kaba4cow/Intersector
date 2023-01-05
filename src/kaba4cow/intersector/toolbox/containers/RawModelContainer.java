package kaba4cow.intersector.toolbox.containers;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import kaba4cow.engine.assets.Loaders;
import kaba4cow.engine.renderEngine.models.RawModel;

public final class RawModelContainer {

	private static final Map<String, RawModel> map = new HashMap<String, RawModel>();

	private RawModelContainer() {

	}

	public static void loadAll() {
		File root = new File("resources/models/");
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
			fileName = fileName.split("resources/models/")[1].split(".obj")[0];
			get(fileName);
		}
	}

	public static RawModel get(String file) {
		if (file.equalsIgnoreCase("null"))
			return null;
		if (!map.containsKey(file)) {
			RawModel model = Loaders.loadToVAO("models/" + file);
			map.put(file, model);
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
