package kaba4cow.gui;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.renderEngine.RendererContainer;

public class GUIPanel extends GUIElement {

	private final String tag;

	private final int columns;
	private final int rows;
	private GUIElement[][] elements;

	public GUIPanel(String tag, int columns, int rows) {
		this.tag = tag;
		this.columns = columns;
		this.rows = rows;
		this.elements = new GUIElement[columns][rows];
		if (tag != null)
			GUIPanelManager.add(this);
	}

	public GUIPanel(int columns, int rows) {
		this(null, columns, rows);
	}

	@Override
	public void render(Vector3f color, RendererContainer renderers) {
		for (int i = 0; i < columns; i++)
			for (int j = 0; j < rows; j++)
				if (elements[i][j] != null)
					elements[i][j].render(color, renderers);
	}

	public GUIElement addElement(GUIElement element, int column, int row) {
		return addElement(element, column, row, 1, 1);
	}

	public GUIElement addElement(GUIElement element, int column, int row,
			int width, int height) {
		if (element == null || column >= columns || row >= rows || width < 1
				|| height < 1)
			return null;
		row = rows - height - row;
		float scaleX = width * scale.x;
		float scaleY = height * scale.y;
		float w = scaleX / (float) columns;
		float h = scaleY / (float) rows;
		element.setScale(w, h);
		float x = Maths.map(column, 0, columns, position.x - scale.x,
				position.x + scale.x) + w;
		float y = Maths.map(row, 0, rows, position.y - scale.y, position.y
				+ scale.y)
				+ h;
		element.setPosition(x, y);
		elements[column][row] = element;
		return element;
	}

	public String getTag() {
		return tag;
	}

	public int getColumns() {
		return columns;
	}

	public int getRows() {
		return rows;
	}

}
