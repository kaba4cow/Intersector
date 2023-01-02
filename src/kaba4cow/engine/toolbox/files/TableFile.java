package kaba4cow.engine.toolbox.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import kaba4cow.engine.toolbox.Printer;

public class TableFile {

	private static final Map<String, TableFile> files = new HashMap<String, TableFile>();

	private static final String SEPARATOR = ",";
	private static final String NEWLINE = "\n";

	private Cell[][] table;

	public TableFile(int columns, int rows) {
		this.table = new Cell[columns][rows];
	}

	public static TableFile get(String fileName) {
		if (!files.containsKey(fileName.toLowerCase())) {
			TableFile file = read(fileName);
			files.put(fileName.toLowerCase(), file);
		}
		return files.get(fileName.toLowerCase());
	}

	public static TableFile read(String fileName) {
		File file = new File(fileName);
		return read(file);
	}

	public static TableFile read(File file) {
		Printer.println("READING FROM FILE \"" + file.getAbsolutePath() + "\"");

		if (!file.exists()) {
			Printer.println("FILE \"" + file.getAbsolutePath() + "\" DOES NOT EXIST");
			return null;
		}

		try {
			FileInputStream in = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();

			LinkedList<LinkedList<Cell>> rowList = new LinkedList<LinkedList<Cell>>();
			int row = 0;
			while (true) {
				line = line.trim();

				if (!line.isEmpty()) {
					LinkedList<Cell> columnList = new LinkedList<Cell>();
					String[] cells = line.split(SEPARATOR);
					for (int column = 0; column < cells.length; column++) {
						Cell cell = new Cell(cells[column]);
						columnList.add(cell);
					}
					rowList.add(columnList);
				}
				row++;

				line = reader.readLine();
				if (line == null)
					break;
			}
			reader.close();

			TableFile tableFile = new TableFile(rowList.size(), rowList.getFirst().size());
			Iterator<LinkedList<Cell>> rowIterator = rowList.iterator();
			row = 0;
			while (rowIterator.hasNext()) {
				LinkedList<Cell> columnList = rowIterator.next();
				Iterator<Cell> columnIterator = columnList.iterator();
				int column = 0;
				while (columnIterator.hasNext()) {
					Cell cell = columnIterator.next();
					tableFile.table[row][column] = cell;
					column++;
				}
				row++;
			}

			files.put(file.getAbsolutePath(), tableFile);
			return tableFile;
		} catch (IOException e) {
			Printer.println("COULD NOT READ FROM FILE \"" + file.getAbsolutePath() + "\"");
			return null;
		}
	}

	public static boolean write(TableFile tableFile, File file) {
		Printer.println("WRITING TO FILE \"" + file.getAbsolutePath() + "\"");

		if (!file.exists())
			try {
				file.createNewFile();
			} catch (IOException e) {
				Printer.println("COULD NOT CREATE FILE \"" + file.getAbsolutePath() + "\"");
				return false;
			}

		try {
			PrintWriter print = new PrintWriter(file);
			String string = tableFile.toString();
			print.append(string);
			print.close();
		} catch (FileNotFoundException e) {
			Printer.println("COULD NOT WRITE TO FILE \"" + file.getAbsolutePath() + "\"");
			return false;
		}
		return true;
	}

	private static void write(TableFile tableFile, StringBuilder writer) {
		for (int i = 0; i < tableFile.table.length; i++) {
			for (int j = 0; j < tableFile.table[i].length; j++) {
				if (j > 0)
					writer.append(SEPARATOR);
				writer.append(tableFile.table[i][j].value);
			}
			writer.append(NEWLINE);
		}
	}

	public int rows() {
		return table.length;
	}

	public int columns() {
		return table[0].length;
	}

	public Cell cell(int row, int column) {
		try {
			return table[row][column];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public void set(int row, int column, Cell cell) {
		try {
			table[row][column] = cell;
		} catch (ArrayIndexOutOfBoundsException e) {

		}
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		write(this, string);
		return string.toString();
	}

	public static class Cell {

		private String value;

		public Cell(String value) {
			this.value = value;
		}

		public String getString() {
			return value;
		}

		public int getInt() {
			try {
				return Integer.parseInt(value);
			} catch (NumberFormatException e) {
				return 0;
			}
		}

		public float getFloat() {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
				return 0f;
			}
		}

		public long getLong() {
			try {
				return Long.parseLong(value);
			} catch (NumberFormatException e) {
				return 0l;
			}
		}

		public double getDouble() {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				return 0d;
			}
		}

	}

}
