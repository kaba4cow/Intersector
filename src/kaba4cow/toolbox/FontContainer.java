package kaba4cow.toolbox;

import kaba4cow.files.InfosFile;

public final class FontContainer {

	private FontContainer() {

	}

	public static String get(String tag) {
		return InfosFile.fonts.data().node(tag).getString();
	}
}
