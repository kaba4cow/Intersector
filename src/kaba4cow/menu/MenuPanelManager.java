package kaba4cow.menu;

import java.util.HashMap;
import java.util.Map;

import kaba4cow.renderEngine.RendererContainer;

public final class MenuPanelManager {

	private static final Map<String, MenuPanel> map = new HashMap<String, MenuPanel>();

	private MenuPanelManager() {

	}

	public static void render(String tag, RendererContainer renderers) {
		if (!map.containsKey(tag))
			return;
		MenuPanel panel = map.get(tag);
		panel.render(renderers);
	}

	public static void add(MenuPanel panel) {
		if (panel != null)
			map.put(panel.getTag(), panel);
	}

}
