package kaba4cow.intersector.toolbox.spawners;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.files.ModelTextureFile;
import kaba4cow.intersector.gameobjects.Debris;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.toolbox.containers.RawModelContainer;

public class DebrisSpawner {

	private static List<RawModel> models = null;
	private static final Map<String, TexturedModel[]> map = new HashMap<String, TexturedModel[]>();

	public static void spawn(World world, Vector3f position, Vector3f velocity,
			float size, String metalTexture, Vector3f color) {
		int num = RNG.randomInt(50, 100);

		TexturedModel[] models = getModels(metalTexture);

		float lifeLength = RNG.randomFloat(10f, 14f);
		float scale = 0.09f * size;
		float speed = RNG.randomFloat(0.3f, 0.6f) * size;

		Vector3f pos = position;
		Vector3f vel = new Vector3f();

		for (int i = 0; i < num; i++) {
			if (skip())
				continue;
			Vectors.randomize(-1f, 1f, vel).normalise();
			vel.scale(speed * RNG.randomFloat(RNG.randomFloat(1f), 1f));
			if (RNG.randomFloat(1f) < 0.1f)
				vel.scale(RNG.randomFloat(0.5f, 1.5f));

			addVelocity(velocity, vel);
			addDebris(world, pos, vel, lifeLength, scale, models[i
					% models.length], color);
		}
	}

	private static void addDebris(World world, Vector3f position,
			Vector3f velocity, float lifeLength, float scale,
			TexturedModel model, Vector3f color) {
		new Debris(world, position, velocity, scale
				* RNG.randomFloat(0.75f, 1f), lifeLength
				* RNG.randomFloat(0.75f, 1f), model, color);
	}

	private static boolean skip() {
		return RNG.randomBoolean()
				&& RNG.randomFloat(1f) < MainProgram.getCurrentFPS()
						/ (float) MainProgram.getFps();
	}

	private static void addVelocity(Vector3f parentVel, Vector3f vel) {
		Vectors.addScaled(vel, parentVel, RNG.randomFloat(0.95f, 1.05f), vel);
	}

	private static TexturedModel[] getModels(String metalTexture) {
		if (models == null)
			createRawModels();
		if (!map.containsKey(metalTexture)) {
			TexturedModel[] array = new TexturedModel[models.size()];
			for (int i = 0; i < array.length; i++) {
				RawModel rawModel = models.get(i);
				ModelTextureFile textureFile = ModelTextureFile
						.get(metalTexture);
				array[i] = new TexturedModel(rawModel, textureFile.get());
			}
			map.put(metalTexture, array);
			return array;
		}
		return map.get(metalTexture);
	}

	private static void createRawModels() {
		models = new ArrayList<RawModel>();
		File directory = new File("resources/models/DEBRIS/");
		File[] files = directory.listFiles();
		for (int i = 0; i < files.length; i++) {
			String fileName = files[i].getName();
			if (fileName.endsWith(".obj")) {
				String name = fileName.substring(0, fileName.length() - 4);
				models.add(RawModelContainer.get("DEBRIS/" + name));
			}
		}
	}

}
