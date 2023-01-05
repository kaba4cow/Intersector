package kaba4cow.intersector.toolbox.containers;

import kaba4cow.intersector.files.InfosFile;

public final class FontContainer {

	private FontContainer() {

	}

	public static String get(String tag) {
		return InfosFile.fonts.data().node(tag).getString();
	}
}
