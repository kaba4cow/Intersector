package kaba4cow.engine.fontMeshCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import kaba4cow.engine.toolbox.Printer;

import org.lwjgl.opengl.Display;

public class MetaFile {

	private static final int PAD_TOP = 0;
	private static final int PAD_LEFT = 1;
	private static final int PAD_BOTTOM = 2;
	private static final int PAD_RIGHT = 3;

	private static final int DESIRED_PADDING = 3;

	private static final String SPLITTER = " ";
	private static final String NUMBER_SEPARATOR = ",";

	private float aspectRatio;

	private float verticalPerPixelSize;
	private float horizontalPerPixelSize;
	private float spaceWidth;
	private int[] padding;
	private int paddingWidth;
	private int paddingHeight;

	private Map<Integer, Character> metaData = new HashMap<Integer, Character>();

	private BufferedReader reader;
	private Map<String, String> values = new HashMap<String, String>();

	protected MetaFile(String file) {
		this.aspectRatio = (float) Display.getWidth()
				/ (float) Display.getHeight();
		openFile(file);
		loadPaddingData();
		loadLineSizes();
		loadCharacterData(getValueOfVariable("scaleW"));
		close();
	}

	protected float getSpaceWidth() {
		return spaceWidth;
	}

	protected Character getCharacter(int ascii) {
		return metaData.get(ascii);
	}

	private boolean processNextLine() {
		values.clear();
		String line = null;
		try {
			line = reader.readLine();
		} catch (IOException e) {

		}
		if (line == null || line.startsWith("kerning"))
			return false;
		for (String part : line.split(SPLITTER)) {
			String[] valuePairs = part.split("=");
			if (valuePairs.length == 2)
				values.put(valuePairs[0], valuePairs[1]);
		}
		return true;
	}

	private int getValueOfVariable(String variable) {
		return Integer.parseInt(values.get(variable));
	}

	private int[] getValuesOfVariable(String variable) {
		String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
		int[] actualValues = new int[numbers.length];
		for (int i = 0; i < actualValues.length; i++)
			actualValues[i] = Integer.parseInt(numbers[i]);
		return actualValues;
	}

	private void close() {
		try {
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openFile(String file) {
		Printer.println("LOADING FONT: " + file);
		try {
			FileInputStream in = new FileInputStream(new File("resources/"
					+ file + ".fnt"));
			InputStreamReader isr = new InputStreamReader(in);
			reader = new BufferedReader(isr);
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("COULD NOT READ FONT FILE");
		}
	}

	private void loadPaddingData() {
		processNextLine();
		this.padding = getValuesOfVariable("padding");
		this.paddingWidth = padding[PAD_LEFT] + padding[PAD_RIGHT];
		this.paddingHeight = padding[PAD_TOP] + padding[PAD_BOTTOM];
	}

	private void loadLineSizes() {
		processNextLine();
		int lineHeightPixels = getValueOfVariable("lineHeight") - paddingHeight;
		verticalPerPixelSize = TextMeshLoader.LINE_HEIGHT
				/ (float) lineHeightPixels;
		horizontalPerPixelSize = verticalPerPixelSize / aspectRatio;
	}

	private void loadCharacterData(int imageWidth) {
		processNextLine();
		processNextLine();
		while (processNextLine()) {
			Character c = loadCharacter(imageWidth);
			if (c != null)
				metaData.put(c.getId(), c);
		}
	}

	private Character loadCharacter(int imageSize) {
		int id = getValueOfVariable("id");
		if (id == TextMeshLoader.SPACE_ASCII) {
			this.spaceWidth = (getValueOfVariable("xadvance") - paddingWidth)
					* horizontalPerPixelSize;
			return null;
		}
		float xTex = ((float) getValueOfVariable("x") + (padding[PAD_LEFT] - DESIRED_PADDING))
				/ imageSize;
		float yTex = ((float) getValueOfVariable("y") + (padding[PAD_TOP] - DESIRED_PADDING))
				/ imageSize;
		int width = getValueOfVariable("width")
				- (paddingWidth - (2 * DESIRED_PADDING));
		int height = getValueOfVariable("height")
				- ((paddingHeight) - (2 * DESIRED_PADDING));
		float quadWidth = width * horizontalPerPixelSize;
		float quadHeight = height * verticalPerPixelSize;
		float xTexSize = (float) width / imageSize;
		float yTexSize = (float) height / imageSize;
		float xOff = (getValueOfVariable("xoffset") + padding[PAD_LEFT] - DESIRED_PADDING)
				* horizontalPerPixelSize;
		float yOff = (getValueOfVariable("yoffset") + (padding[PAD_TOP] - DESIRED_PADDING))
				* verticalPerPixelSize;
		float xAdvance = (getValueOfVariable("xadvance") - paddingWidth)
				* horizontalPerPixelSize;
		return new Character(id, xTex, yTex, xTexSize, yTexSize, xOff, yOff,
				quadWidth, quadHeight, xAdvance);
	}
}
