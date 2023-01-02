package kaba4cow.engine.renderEngine.models;

import kaba4cow.engine.fontMeshCreator.FontType;
import kaba4cow.engine.fontMeshCreator.TextMeshData;
import kaba4cow.engine.fontMeshCreator.TextMeshLoader;
import kaba4cow.engine.toolbox.Fonts;
import kaba4cow.engine.toolbox.Loaders;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class GUIText {

	private String textString;
	private float fontSize;

	private int textMeshVao;
	private int vertexCount;
	private Vector3f color;

	private Vector2f position;
	private float maxLineLength;
	private int numberOfLines;

	private FontType font;
	private TextMeshData data;

	private boolean centerX;
	private boolean centerY;

	public GUIText(String text, String fontName, Vector2f position,
			float fontSize, float maxLineLength, boolean centeredX,
			boolean centeredY) {
		this.font = Fonts.get(fontName);
		this.position = position;
		this.maxLineLength = maxLineLength;
		this.centerX = centeredX;
		this.centerY = centeredY;
		this.color = new Vector3f(1f, 1f, 1f);
		this.textString = text;
		this.fontSize = fontSize;
		this.updateMesh();
	}

	public GUIText(String text, String fontName, Vector2f position,
			float fontSize, float maxLineLength, boolean centered) {
		this(text, fontName, position, fontSize, maxLineLength, centered,
				centered);
	}

	public static float getTextHeight(float scale) {
		return scale * TextMeshLoader.LINE_HEIGHT;
	}

	public FontType getFont() {
		return font;
	}

	public void setColor(float r, float g, float b) {
		this.color.set(r, g, b);
	}

	public void setColor(Vector3f color) {
		this.color.set(color);
	}

	public Vector3f getColor() {
		return color;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public Vector2f getPosition() {
		return position;
	}

	public void setPosition(float x, float y) {
		this.position.set(x, y);
	}

	public void setPosition(Vector2f position) {
		this.position = position;
	}

	public int getMesh() {
		return textMeshVao;
	}

	public void setMeshInfo(int vao, int verticesCount) {
		this.textMeshVao = vao;
		this.vertexCount = verticesCount;
	}

	public int getVertexCount() {
		return vertexCount;
	}

	public float getFontSize() {
		return fontSize;
	}

	public GUIText setFontSize(float fontSize) {
		if (fontSize != this.fontSize) {
			this.fontSize = fontSize;
			updateMesh();
		}
		return this;
	}

	public void setNumberOfLines(int number) {
		this.numberOfLines = number;
	}

	public boolean isCenteredX() {
		return centerX;
	}

	public GUIText setCenteredX(boolean centerX) {
		if (this.centerX != centerX) {
			this.centerX = centerX;
			updateMesh();
		}
		return this;
	}

	public boolean isCenteredY() {
		return centerY;
	}

	public GUIText setCenteredY(boolean centerY) {
		if (this.centerY != centerY) {
			this.centerY = centerY;
			updateMesh();
		}
		return this;
	}

	public float getMaxLineLength() {
		return maxLineLength;
	}

	public GUIText setMaxLineLength(float maxLineLength) {
		if (maxLineLength != this.maxLineLength) {
			this.maxLineLength = maxLineLength;
			updateMesh();
		}
		return this;
	}

	public String getTextString() {
		return textString;
	}

	public GUIText setTextString(String textString) {
		if (!textString.equals(this.textString)) {
			this.textString = textString;
			updateMesh();
		}
		return this;
	}

	public Vector2f getSize() {
		return data.getSize();
	}

	private GUIText updateMesh() {
		if (textMeshVao != 0)
			Loaders.deleteVAO(textMeshVao);
		data = font.loadText(this);
		int vao = Loaders.loadToVAO(data.getVertexPositions(),
				data.getTextureCoords());
		setMeshInfo(vao, data.getVertexCount());
		return this;
	}

}
