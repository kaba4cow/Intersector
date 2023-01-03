package kaba4cow.intersector.gui;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.intersector.renderEngine.RendererContainer;

public class LabelElement extends GUIElement {

	public LabelElement() {
		super();
	}

	@Override
	public void render(Vector3f color, RendererContainer renderers) {
		text.setColor(2f * color.x, 2f * color.y, 2f * color.z);
		renderers.getGuiTextRenderer().render(text);
		renderers.getFrameRenderer().render(position, scale.x, scale.y, true,
				true, color);
	}

}
