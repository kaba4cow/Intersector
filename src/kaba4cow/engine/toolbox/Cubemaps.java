package kaba4cow.engine.toolbox;

import java.util.HashMap;
import java.util.Map;

import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.utils.ProgramUtils;

public class Cubemaps {

	private static Map<String, Cubemap> map = new HashMap<String, Cubemap>();

	public static void load(String name) {
		if (map.containsKey(name))
			return;
		Cubemap cubemap = new Cubemap(ProgramUtils.getCubemapLocation() + name);
		map.put(name, cubemap);
	}

	public static void load(String name, int faces) {
		if (map.containsKey(name))
			return;
		Cubemap cubemap = new Cubemap(ProgramUtils.getCubemapLocation() + name,
				faces);
		map.put(name, cubemap);
	}

	public static void load(String... names) {
		if (names != null)
			for (int i = 0; i < names.length; i++)
				load(names[i]);
	}

	public static Cubemap get(String name) {
		return map.get(name);
	}

}
