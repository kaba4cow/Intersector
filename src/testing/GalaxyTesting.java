package testing;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import kaba4cow.engine.Input;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.files.PlanetFile;
import kaba4cow.intersector.galaxyengine.objects.PlanetObject;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.utils.FileUtils;
import kaba4cow.intersector.utils.GalaxyUtils;

public class GalaxyTesting extends MainProgram {

	public GalaxyTesting() {
		super("Galaxy Testing", 30, 400, 400, 0);
	}

	@Override
	public void init() {
		PlanetFile.load(FileUtils.loadFiles(
				new File("resources/files/planets/"), null,
				new ArrayList<String>()));
	}

	@Override
	public void update(float dt) {
		if (Input.isKeyDown(Keyboard.KEY_RETURN)) {
			int x = (int) Maths.map(Mouse.getX(), 0, WIDTH, -1000f, 1000f);
			int y = (int) Maths.map(Mouse.getY(), 0, HEIGHT, -1000f, 1000f);
			GalaxyUtils.generateSystem(x, y, 0);
		}

		if (Input.isKeyDown(Keyboard.KEY_G)) {
			Map<String, List<String>> map = new HashMap<String, List<String>>();
			int range = 200;
			for (int y = -range / 2; y < range / 2; y++)
				for (int x = -range / 2; x < range / 2; x++) {
					SystemObject system = GalaxyUtils.generateSystem(x, y, 0);
					if (system != null)
						process(system.mainObjects[0], map);
				}
			Object[] array = map.keySet().toArray();
			Arrays.sort(array);
			Printer.println();
			for (int i = 0; i < array.length; i++) {
				String string = (String) array[i];
				if (map.get(string).size() > 0)
					Printer.println(string + " : " + map.get(string).size());
			}
		}
	}

	protected void process(PlanetObject planet, Map<String, List<String>> map) {
		String string = planet.file.getName();
		if (!map.containsKey(string))
			map.put(string, new ArrayList<String>());
		if (map.containsKey(string))
			map.get(string).add(string);
		for (int i = 0; i < planet.children.size(); i++)
			process(planet.children.get(i), map);
	}

	protected void process(String string, Map<String, List<String>> map) {
		if (!map.containsKey(string))
			map.put(string, new ArrayList<String>());
		if (map.containsKey(string))
			map.get(string).add(string);
	}

	@Override
	public void render() {
		stopPostProcessing(null);
	}

	@Override
	public void onClose() {

	}

	public static void main(String[] args) {
		start(new GalaxyTesting());
	}

}
