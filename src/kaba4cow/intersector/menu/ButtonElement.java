package kaba4cow.intersector.menu;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.Input;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class ButtonElement extends MenuElement {

	public static final float FRAMEWIDTH = 0.3f;
	public static final float SCALE = 1.5f;

	protected boolean active;

	public ButtonElement() {
		super();
		this.setScale(SCALE);
		this.active = false;
	}

	@Override
	public void render(RendererContainer renderers) {
		Vector3f color = processMouse();
		text.setColor(2f * color.x, 2f * color.y, 2f * color.z);
		renderers.getTextRenderer().render(text);
		renderers.getDynamicFrameRenderer().render(position,
				FRAMEWIDTH * SCALE, GUIText.getTextHeight(SCALE), true, true,
				color);
	}

	protected Vector3f processMouse() {
		Vector3f color = ACTIVE_COLOR;
		if (isHovered()) {
			if (Input.isButtonDown(0)) {
				active = true;
				playSound();
			}
			if (active) {
				onHold();
				if (Input.isButtonUp(0)) {
					active = false;
					onSelect();
				}
			}
			color = active ? SELECTED_COLOR : HOVERED_COLOR;
		} else
			active = false;
		return color;
	}

	public boolean isHovered() {
		float mouseX = WindowUtils.toNormalizedX(Mouse.getX());
		float mouseY = WindowUtils.toNormalizedY(Mouse.getY());

		float w = FRAMEWIDTH * SCALE;
		float h = GUIText.getTextHeight(SCALE);
		float x = position.x;
		float y = position.y;

		return mouseX >= x - w && mouseX < x + w && mouseY >= y - h
				&& mouseY < y + h;
	}

	public void onSelect() {

	}

	protected void onHold() {

	}

}
