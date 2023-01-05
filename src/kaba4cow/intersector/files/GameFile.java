package kaba4cow.intersector.files;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.toolbox.files.DataFile;

public abstract class GameFile {

	private static final List<GameFile> list = new ArrayList<GameFile>();

	protected final String fileName;
	protected final DataFile data;

	protected GameFile(String fileName) {
		Printer.println("CREATING " + getClass().getName() + ": " + fileName);
		this.fileName = fileName;
		this.data = DataFile.read("resources/files/" + getLocation() + fileName);
		list.add(this);
	}

	public static void prepareAllInit() {
		Printer.println("PREPARING GAME FILES: INIT");
		for (int i = 0; i < list.size(); i++)
			list.get(i).prepareInit();
	}

	public static void prepareAllPostInit() {
		Printer.println("PREPARING GAME FILES: POST INIT");
		for (int i = 0; i < list.size(); i++)
			list.get(i).preparePostInit();
	}

	public void save() {
		DataFile.write(data, "resources/files/" + getLocation() + fileName);
	}

	public abstract void prepareInit();

	public abstract void preparePostInit();

	public abstract String getLocation();

	public DataFile data() {
		return data;
	}

	public String getFileName() {
		return fileName;
	}

	public static final boolean isNull(String string) {
		return string == null || string.isEmpty() || string.trim().toLowerCase().equals("null");
	}

	public static void sort(List<? extends GameFile> fileList) {
		Collections.sort(fileList, FileComparator.getInstance());
	}

	private static class FileComparator implements Comparator<GameFile> {

		private static final FileComparator instance = new FileComparator();

		@Override
		public int compare(GameFile arg0, GameFile arg1) {
			return arg0.getFileName().compareToIgnoreCase(arg1.getFileName());
		}

		public static FileComparator getInstance() {
			return instance;
		}

	}

}
