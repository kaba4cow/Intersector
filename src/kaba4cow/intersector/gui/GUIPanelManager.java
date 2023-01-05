package kaba4cow.intersector.gui;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.files.InfosFile;
import kaba4cow.intersector.renderEngine.RendererContainer;

public final class GUIPanelManager {

	private static final Map<String, GUIPanel> map = new HashMap<String, GUIPanel>();

	private GUIPanelManager() {

	}

	public static void render(String tag, String colorInfo,
			RendererContainer renderers) {
		if (!map.containsKey(tag))
			return;
		GLUtils.clearDepthBuffer();
		GUIPanel panel = map.get(tag);
		Vector3f color = InfosFile.holograms.data().node(colorInfo)
				.getVector3();
		panel.render(color, renderers);
	}

	public static void add(GUIPanel panel) {
		if (panel != null)
			map.put(panel.getTag(), panel);
	}

}
