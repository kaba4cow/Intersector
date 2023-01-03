package kaba4cow.intersector.menu;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class SubHeaderElement extends MenuElement {

	public static final float SCALE = 1f;

	public SubHeaderElement() {
		super();
		this.setScale(SCALE);
	}

	@Override
	public void render(RendererContainer renderers) {
		setPosition(0f, 0.5f - GUIText.getTextHeight(HeaderElement.SCALE));
		Vector3f color = ACTIVE_COLOR;
		text.setColor(2f * color.x, 2f * color.y, 2f * color.z);
		renderers.getTextRenderer().render(text);
	}

}
