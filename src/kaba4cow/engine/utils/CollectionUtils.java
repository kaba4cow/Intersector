package kaba4cow.engine.utils;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.toolbox.rng.RNG;

public final class CollectionUtils {

	private CollectionUtils() {

	}

	public static <T> T[] shuffle(T[] array, RNG rng) {
		for (int i = array.length - 1; i > 0; i--) {
			int j = rng.nextInt(0, i);
			T temp = array[i];
			array[i] = array[j];
			array[j] = temp;
		}
		return array;
	}

	public static <T> List<T> shuffle(List<T> list, RNG rng) {
		for (int i = list.size() - 1; i > 0; i--) {
			int j = rng.nextInt(0, i);
			T temp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, temp);
		}
		return list;
	}

	public static <T> List<T> removeDuplicates(List<T> list) {
		List<T> newList = new ArrayList<T>();
		if (list == null || list.isEmpty())
			return newList;
		int size = list.size();
		for (int i = 0; i < size; i++)
			if (!newList.contains(list.get(i)))
				newList.add(list.get(i));
		return newList;
	}

}
