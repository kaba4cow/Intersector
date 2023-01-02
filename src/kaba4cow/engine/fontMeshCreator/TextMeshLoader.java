package kaba4cow.engine.fontMeshCreator;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.renderEngine.models.GUIText;

public class TextMeshLoader {

	public static final float LINE_HEIGHT = 0.03f;
	protected static final int SPACE_ASCII = 32;

	private MetaFile metaFile;

	protected TextMeshLoader(String metaFile) {
		this.metaFile = new MetaFile(metaFile);
	}

	protected TextMeshData createTextMesh(GUIText text) {
		List<Line> lines = createStructure(text);
		TextMeshData data = createQuadVertices(text, lines);
		return data;
	}

	private List<Line> createStructure(GUIText text) {
		char[] chars = text.getTextString().toCharArray();
		List<Line> lines = new ArrayList<Line>();
		Line currentLine = new Line(metaFile.getSpaceWidth(),
				text.getFontSize(), text.getMaxLineLength());
		Word currentWord = new Word(text.getFontSize());
		for (char c : chars) {
			int ascii = (int) c;
			if (ascii == SPACE_ASCII) {
				boolean added = currentLine.attemptToAddWord(currentWord);
				if (!added) {
					lines.add(currentLine);
					currentLine = new Line(metaFile.getSpaceWidth(),
							text.getFontSize(), text.getMaxLineLength());
					currentLine.attemptToAddWord(currentWord);
				}
				currentWord = new Word(text.getFontSize());
				continue;
			}
			Character character = metaFile.getCharacter(ascii);
			currentWord.addCharacter(character);
		}
		completeStructure(lines, currentLine, currentWord, text);
		return lines;
	}

	private void completeStructure(List<Line> lines, Line currentLine,
			Word currentWord, GUIText text) {
		boolean added = currentLine.attemptToAddWord(currentWord);
		if (!added) {
			lines.add(currentLine);
			currentLine = new Line(metaFile.getSpaceWidth(),
					text.getFontSize(), text.getMaxLineLength());
			currentLine.attemptToAddWord(currentWord);
		}
		lines.add(currentLine);
	}

	private TextMeshData createQuadVertices(GUIText text, List<Line> lines) {
		text.setNumberOfLines(lines.size());
		float maxWidth = 0f;
		float curserX = 0f;
		float curserY = 0f;
		if (text.isCenteredY())
			curserY -= 0.25f * (lines.size() - 1) * LINE_HEIGHT
					* text.getFontSize();
		List<Float> vertices = new ArrayList<Float>();
		List<Float> textureCoords = new ArrayList<Float>();
		for (Line line : lines) {
			if (text.isCenteredX())
				curserX = 0.5f * (line.getMaxLength() - line.getLineLength())
						- 0.5f * text.getMaxLineLength();
			if (text.isCenteredY())
				curserY -= 0.5f * LINE_HEIGHT * text.getFontSize();
			for (Word word : line.getWords()) {
				for (Character letter : word.getCharacters()) {
					addVerticesForCharacter(curserX, curserY, letter,
							text.getFontSize(), vertices);
					addTexCoords(textureCoords, letter.getxTextureCoord(),
							letter.getyTextureCoord(),
							letter.getXMaxTextureCoord(),
							letter.getYMaxTextureCoord());
					curserX += letter.getxAdvance() * text.getFontSize();
					if (curserX > maxWidth)
						maxWidth = curserX;
				}
				curserX += metaFile.getSpaceWidth() * text.getFontSize();
			}
			curserX = 0;
			curserY += LINE_HEIGHT * text.getFontSize();
		}
		TextMeshData data = new TextMeshData(listToArray(vertices),
				listToArray(textureCoords));
		data.getSize().set(maxWidth, curserY);
		return data;
	}

	private void addVerticesForCharacter(float curserX, float curserY,
			Character character, float fontSize, List<Float> vertices) {
		float x = curserX + (character.getxOffset() * fontSize);
		float y = curserY + (character.getyOffset() * fontSize);
		float maxX = x + (character.getSizeX() * fontSize);
		float maxY = y + (character.getSizeY() * fontSize);
		float properX = (2 * x) - 1;
		float properY = (-2 * y) + 1;
		float properMaxX = (2 * maxX) - 1;
		float properMaxY = (-2 * maxY) + 1;
		addVertices(vertices, properX, properY, properMaxX, properMaxY);
	}

	private static void addVertices(List<Float> vertices, float x, float y,
			float maxX, float maxY) {
		vertices.add(x);
		vertices.add(y);
		vertices.add(x);
		vertices.add(maxY);
		vertices.add(maxX);
		vertices.add(maxY);
		vertices.add(maxX);
		vertices.add(maxY);
		vertices.add(maxX);
		vertices.add(y);
		vertices.add(x);
		vertices.add(y);
	}

	private static void addTexCoords(List<Float> texCoords, float x, float y,
			float maxX, float maxY) {
		texCoords.add(x);
		texCoords.add(y);
		texCoords.add(x);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(maxY);
		texCoords.add(maxX);
		texCoords.add(y);
		texCoords.add(x);
		texCoords.add(y);
	}

	private static float[] listToArray(List<Float> listOfFloats) {
		float[] array = new float[listOfFloats.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = listOfFloats.get(i);
		return array;
	}

}
