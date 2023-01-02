package kaba4cow.engine.toolbox.particles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kaba4cow.engine.renderEngine.renderers.ParticleRenderer;

import org.lwjgl.util.vector.Vector3f;

public final class ParticleSystemManager {

	private static final Map<String, List<ParticleSystem>> map = new HashMap<String, List<ParticleSystem>>();

	private ParticleSystemManager() {

	}

	public static void update(String tag, float dt) {
		List<ParticleSystem> list = getList(tag);
		if (list != null)
			for (int i = 0; i < list.size(); i++)
				list.get(i).update(dt);
	}

	public static void update(float dt) {
		for (String tag : map.keySet())
			update(tag, dt);
	}

	public static void move(String tag, Vector3f off) {
		List<ParticleSystem> list = getList(tag);
		if (list != null)
			for (int i = 0; i < list.size(); i++)
				list.get(i).move(off);
	}

	public static void move(Vector3f off) {
		for (String tag : map.keySet())
			move(tag, off);
	}

	public static void render(String tag, ParticleRenderer renderer) {
		List<ParticleSystem> list = getList(tag);
		if (list != null)
			for (int i = 0; i < list.size(); i++)
				list.get(i).render(renderer);
	}

	public static void render(ParticleRenderer renderer) {
		for (String tag : map.keySet())
			render(tag, renderer);
	}

	public static void clear(String tag) {
		List<ParticleSystem> list = getList(tag);
		if (list != null)
			for (int i = 0; i < list.size(); i++)
				list.get(i).clear();
	}

	public static void clear() {
		for (String tag : map.keySet())
			clear(tag);
	}

	public static void add(String tag, ParticleSystem system) {
		List<ParticleSystem> list = getList(tag);
		if (list == null) {
			list = new ArrayList<ParticleSystem>();
			map.put(tag, list);
		}
		if (!list.contains(system))
			list.add(system);
	}

	public static Set<String> getKeySet() {
		return map.keySet();
	}

	public static List<ParticleSystem> getList(String tag) {
		return map.get(tag);
	}

}
