package kaba4cow.intersector.menu;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class HeaderElement extends MenuElement {

	public static final float FRAMEWIDTH = 0.3f;
	public static final float SCALE = 3.5f;

	private SubHeaderElement subHeader;

	public HeaderElement() {
		super();
		this.setScale(SCALE);
	}

	@Override
	public void render(RendererContainer renderers) {
		setPosition(0f, 0.5f);
		Vector3f color = ACTIVE_COLOR;
		text.setColor(2f * color.x, 2f * color.y, 2f * color.z);
		renderers.getTextRenderer().render(text);
		renderers.getStaticFrameRenderer().render(position, FRAMEWIDTH * SCALE,
				GUIText.getTextHeight(SCALE + Maths.SQRT2), true, true, color);
		if (subHeader != null)
			subHeader.render(renderers);
	}

	@Override
	public MenuElement setTextString(String textString) {
		return super.setTextString(textString.toUpperCase());
	}

	public HeaderElement setSubHeader(String text) {
		this.subHeader = new SubHeaderElement();
		this.subHeader.setTextString(text);
		return this;
	}

}
