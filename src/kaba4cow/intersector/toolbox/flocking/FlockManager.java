package kaba4cow.intersector.toolbox.flocking;

import java.util.ArrayList;
import java.util.List;

public final class FlockManager {

	private static final List<Flock> list = new ArrayList<Flock>();

	private FlockManager() {

	}

	public static void update(float dt) {
		Flock current = null;
		for (int i = list.size() - 1; i >= 0; i--) {
			current = list.get(i);
			current.update(dt);
			if (current.isEmpty())
				list.remove(i);
		}
	}

	public static void add(Flock flock) {
		if (flock != null && !list.contains(flock))
			list.add(flock);
	}

	public static void clear() {
		for (int i = 0; i < list.size(); i++)
			list.get(i).clear();
		list.clear();
	}

	public static boolean isEmpty() {
		return list.isEmpty();
	}

	public static int size() {
		return list.size();
	}

}
