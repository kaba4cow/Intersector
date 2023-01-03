package kaba4cow.intersector.menu;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.intersector.renderEngine.RendererContainer;

public class MenuPanel {

	public static final float OFFSET = 0.12f;

	private final String tag;

	private HeaderElement header;
	private List<ButtonElement> elements;

	public MenuPanel(String tag) {
		this.tag = tag;
		this.header = null;
		this.elements = new ArrayList<ButtonElement>();
		MenuPanelManager.add(this);
	}

	public void render(RendererContainer renderers) {
		if (header != null)
			header.render(renderers);
		for (int i = 0; i < elements.size(); i++)
			elements.get(i).render(renderers);
	}

	public HeaderElement setHeader(String title) {
		header = new HeaderElement();
		header.setTextString(title);
		return header;
	}

	public ButtonElement addElement(ButtonElement element, String title) {
		element.setTextString(title);
		element.setPosition(0f, -OFFSET * elements.size());
		elements.add(element);
		return element;
	}

	public String getTag() {
		return tag;
	}

}
