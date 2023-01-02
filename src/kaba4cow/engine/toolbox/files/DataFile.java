package kaba4cow.engine.toolbox.files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;

import kaba4cow.engine.toolbox.Pair;
import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.utils.StringUtils;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class DataFile {

	private static final Map<String, DataFile> files = new HashMap<String, DataFile>();

	private static final String INDENTATION = "\t";
	private static final String ASSIGNMENT = " = ";
	private static final char QUOTATION = '\"';
	private static final char SEPARATOR = ',';
	private static final char NEWLINE = '\n';

	private static int indentCount;

	private Vector<String> content;

	private Vector<Pair<String, DataFile>> objects;
	private HashMap<String, Integer> map;

	public DataFile() {
		this.content = new Vector<String>();
		this.objects = new Vector<Pair<String, DataFile>>();
		this.map = new HashMap<String, Integer>();
	}

	public static DataFile get(String fileName) {
		if (!files.containsKey(fileName.toLowerCase())) {
			DataFile file = read(fileName);
			files.put(fileName.toLowerCase(), file);
		}
		return files.get(fileName.toLowerCase());
	}

	public static DataFile read(String fileName) {
		File file = new File(fileName);
		return read(file);
	}

	public static DataFile read(File file) {
		Printer.println("READING FROM FILE \"" + file.getAbsolutePath() + "\"");
		DataFile dataFile = new DataFile();

		if (!file.exists()) {
			Printer.println("FILE \"" + file.getAbsolutePath() + "\" DOES NOT EXIST");
			return null;
		}

		String[] propertyLine;
		String propertyName = "";
		String propertyValue = "";

		Stack<DataFile> stack = new Stack<DataFile>();
		stack.push(dataFile);

		try {
			FileInputStream in = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			String line = reader.readLine();
			while (true) {
				line = line.trim();

				if (!line.isEmpty()) {
					if (line.contains(ASSIGNMENT)) {
						propertyLine = line.split(ASSIGNMENT);
						propertyName = propertyLine[0].trim();
						propertyValue = propertyLine[1].trim();

						boolean inQuotes = false;
						String token = "";
						int tokenCount = 0;
						for (int i = 0; i < propertyValue.length(); i++) {
							char c = propertyValue.charAt(i);
							if (c == QUOTATION)
								inQuotes = !inQuotes;
							else {
								if (inQuotes) {
									token += c;
								} else {
									if (c == SEPARATOR) {
										token = token.trim();
										stack.lastElement().node(propertyName).setString(token, tokenCount);
										token = "";
										tokenCount++;
									} else {
										token += c;
									}
								}
							}
						}
						if (!token.isEmpty()) {
							token = token.trim();
							stack.lastElement().node(propertyName).setString(token, tokenCount);
						}
					} else {
						char begin = line.charAt(0);
						if (begin == '{') {
							stack.push(stack.lastElement().node(propertyName));
						} else if (begin == '}') {
							stack.pop();
						} else {
							propertyName = line;
						}
					}
				}

				line = reader.readLine();
				if (line == null)
					break;
			}
			reader.close();
			files.put(file.getAbsolutePath(), dataFile);
		} catch (IOException e) {
			Printer.println("COULD NOT READ FROM FILE \"" + file.getAbsolutePath() + "\"");
		}

		return dataFile;
	}

	public static boolean write(DataFile dataFile, String fileName) {
		File file = new File(fileName);
		return write(dataFile, file);
	}

	public static boolean write(DataFile dataFile, File file) {
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
			String string = dataFile.toString();
			print.append(string);
			print.close();
		} catch (FileNotFoundException e) {
			Printer.println("COULD NOT WRITE TO FILE \"" + file.getAbsolutePath() + "\"");
			return false;
		}
		return true;
	}

	private static void write(DataFile dataFile, StringBuilder writer) {
		for (int i = 0; i < dataFile.objects.size(); i++) {
			Pair<String, DataFile> property = dataFile.objects.get(i);
			if (property.getB().objects.isEmpty()) {
				writer.append(StringUtils.repeat(INDENTATION, indentCount) + property.getA());
				writer.append(ASSIGNMENT);
				int items = property.getB().contentSize();
				for (int j = 0; j < property.getB().contentSize(); j++) {
					String string = property.getB().getString(j);
					if (string.indexOf(SEPARATOR) != -1)
						writer.append(QUOTATION + string + QUOTATION);
					else
						writer.append(string);
					if (items > 1)
						writer.append(SEPARATOR);
					items--;
				}
				writer.append(NEWLINE);
			} else {
				if (i > 0)
					writer.append(NEWLINE);
				writer.append(StringUtils.repeat(INDENTATION, indentCount) + property.getA() + NEWLINE
						+ StringUtils.repeat(INDENTATION, indentCount) + "{" + NEWLINE);
				indentCount++;
				write(property.getB(), writer);
				writer.append(StringUtils.repeat(INDENTATION, indentCount) + "}" + NEWLINE);
			}
		}
		if (indentCount > 0)
			indentCount--;
	}

	public DataFile node(String name) {
		if (!map.containsKey(name)) {
			map.put(name, objects.size());
			objects.add(new Pair<String, DataFile>(name, new DataFile()));
		}
		return objects.get(map.get(name)).getB();
	}

	public DataFile node(int index) {
		if (index < 0 || index >= objectSize())
			return null;
		return objects.get(index).getB();
	}

	public DataFile remove(String node) {
		if (!map.containsKey(node))
			return this;
		int index = map.remove(node);
		objects.remove(index);
		for (String key : map.keySet())
			if (map.get(key) > index)
				map.replace(key, map.get(key) - 1);
		return this;
	}

	public DataFile clear() {
		content.clear();
		objects.clear();
		map.clear();
		return this;
	}

	public int contentSize() {
		return content.size();
	}

	public int objectSize() {
		return objects.size();
	}

	public String getString(int item) {
		if (item >= content.size())
			return "";
		else
			return content.get(item);
	}

	public String getString() {
		return getString(0);
	}

	public DataFile setString(String string, int item) {
		if (item >= content.size())
			content.add(string);
		else
			content.set(item, string);
		return this;
	}

	public DataFile setString(String string) {
		return setString(string, contentSize());
	}

	public boolean getBoolean(int item) {
		if (item >= content.size())
			return false;
		else
			return Boolean.parseBoolean(content.get(item));
	}

	public boolean getBoolean() {
		return getBoolean(0);
	}

	public DataFile setBoolean(boolean value, int item) {
		return setString(Boolean.toString(value), item);
	}

	public DataFile setBoolean(boolean value) {
		return setBoolean(value, contentSize());
	}

	public double getDouble(int item) {
		try {
			return Double.parseDouble(getString(item));
		} catch (NumberFormatException e) {
			return 0d;
		}
	}

	public double getDouble() {
		return getDouble(0);
	}

	public DataFile setDouble(double value, int item) {
		return setString(Double.toString(value), item);
	}

	public DataFile setDouble(double value) {
		return setDouble(value, contentSize());
	}

	public float getFloat(int item) {
		try {
			return Float.parseFloat(getString(item));
		} catch (NumberFormatException e) {
			return 0f;
		}
	}

	public float getFloat() {
		return getFloat(0);
	}

	public DataFile setFloat(float value, int item) {
		return setString(Float.toString(value), item);
	}

	public DataFile setFloat(float value) {
		return setFloat(value, contentSize());
	}

	public long getLong(int item) {
		try {
			return Long.parseLong(getString(item));
		} catch (NumberFormatException e) {
			return 0l;
		}
	}

	public long getLong() {
		return getLong(0);
	}

	public DataFile setLong(long value, int item) {
		return setString(Long.toString(value), item);
	}

	public DataFile setLong(long value) {
		return setLong(value, contentSize());
	}

	public int getInt(int item) {
		try {
			return Integer.parseInt(getString(item));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public int getInt() {
		return getInt(0);
	}

	public DataFile setInt(int value, int item) {
		return setString(Integer.toString(value), item);
	}

	public DataFile setInt(int value) {
		return setInt(value, contentSize());
	}

	public Vector2f getVector2() {
		Vector2f vector = new Vector2f();
		vector.x = getFloat(0);
		vector.y = getFloat(1);
		return vector;
	}

	public DataFile setVector2(float x, float y) {
		setFloat(x, 0);
		setFloat(y, 1);
		return this;
	}

	public DataFile setVector2(Vector2f vector) {
		setVector2(vector.x, vector.y);
		return this;
	}

	public Vector3f getVector3() {
		Vector3f vector = new Vector3f();
		vector.x = getFloat(0);
		vector.y = getFloat(1);
		vector.z = getFloat(2);
		return vector;
	}

	public DataFile setVector3(float x, float y, float z) {
		setFloat(x, 0);
		setFloat(y, 1);
		setFloat(z, 2);
		return this;
	}

	public DataFile setVector3(Vector3f vector) {
		setVector3(vector.x, vector.y, vector.z);
		return this;
	}

	public Vector4f getVector4() {
		Vector4f vector = new Vector4f();
		vector.x = getFloat(0);
		vector.y = getFloat(1);
		vector.z = getFloat(2);
		vector.w = getFloat(3);
		return vector;
	}

	public DataFile setVector4(float x, float y, float z, float w) {
		setFloat(x, 0);
		setFloat(y, 1);
		setFloat(z, 2);
		setFloat(w, 3);
		return this;
	}

	public DataFile setVector4(Vector4f vector) {
		setVector4(vector.x, vector.y, vector.z, vector.w);
		return this;
	}

	public String[] toStringArray() {
		String[] array = new String[contentSize()];
		for (int i = 0; i < array.length; i++)
			array[i] = getString(i);
		return array;
	}

	public double[] toDoubleArray() {
		double[] array = new double[contentSize()];
		for (int i = 0; i < array.length; i++)
			array[i] = getDouble(i);
		return array;
	}

	public float[] toFloatArray() {
		float[] array = new float[contentSize()];
		for (int i = 0; i < array.length; i++)
			array[i] = getFloat(i);
		return array;
	}

	public long[] toLongArray() {
		long[] array = new long[contentSize()];
		for (int i = 0; i < array.length; i++)
			array[i] = getLong(i);
		return array;
	}

	public int[] toIntArray() {
		int[] array = new int[contentSize()];
		for (int i = 0; i < array.length; i++)
			array[i] = getInt(i);
		return array;
	}

	public Vector2f[] toVector2Array() {
		Vector2f[] array = new Vector2f[objectSize()];
		for (int i = 0; i < array.length; i++)
			array[i] = node(i).getVector2();
		return array;
	}

	public Vector3f[] toVector3Array() {
		Vector3f[] array = new Vector3f[objectSize()];
		for (int i = 0; i < array.length; i++)
			array[i] = node(i).getVector3();
		return array;
	}

	public Vector4f[] toVector4Array() {
		Vector4f[] array = new Vector4f[objectSize()];
		for (int i = 0; i < array.length; i++)
			array[i] = node(i).getVector4();
		return array;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder();
		indentCount = 0;
		write(this, string);
		return string.toString();
	}

}
