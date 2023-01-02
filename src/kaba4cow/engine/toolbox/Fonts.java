package kaba4cow.engine.toolbox;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import kaba4cow.engine.fontMeshCreator.FontType;
import kaba4cow.engine.utils.ProgramUtils;

public class Fonts {

	private static final Map<String, FontType> map = new HashMap<String, FontType>();

	public static void loadAll() {
		File directory = new File("resources/" + ProgramUtils.getFontLocation());
		if (!directory.exists() || !directory.isDirectory())
			return;
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			String name = files[i].getName();
			if (name.endsWith(".fnt"))
				load(name.substring(0, name.length() - 4));
		}
	}

	public static void load(String name) {
		if (map.containsKey(name))
			return;
		FontType font = new FontType(ProgramUtils.getFontLocation() + name);
		map.put(name, font);
	}

	public static void load(String... names) {
		if (names != null)
			for (int i = 0; i < names.length; i++)
				load(names[i]);
	}

	public static FontType get(String name) {
		return map.get(name);
	}

}
