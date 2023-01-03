package kaba4cow.intersector.gui;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.Input;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class ButtonElement extends GUIElement {

	private static final Vector3f UNAVAILABLE = new Vector3f(0.5f, 0.5f, 0.5f);

	private boolean active;
	private boolean available;

	public ButtonElement() {
		super();
		this.active = false;
		this.available = true;
	}

	@Override
	public void render(Vector3f color, RendererContainer renderers) {
		text.setColor(2f * color.x, 2f * color.y, 2f * color.z);
		if (available)
			color = processMouse(color);
		else
			color = Maths.blend(UNAVAILABLE, color, 0.75f, null);
		renderers.getGuiTextRenderer().render(text);
		renderers.getFrameRenderer().render(position, scale.x, scale.y, true,
				true, color);
	}

	protected Vector3f processMouse(Vector3f color) {
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
			color = active ? Vectors.scale(color, 0.75f, null) : Vectors.scale(
					color, 1.15f, null);
		} else
			active = false;
		return color;
	}

	public boolean isHovered() {
		float mouseX = WindowUtils.toNormalizedX(Mouse.getX());
		float mouseY = WindowUtils.toNormalizedY(Mouse.getY());

		float w = scale.x;
		float h = scale.y;
		float x = position.x;
		float y = position.y;

		return mouseX >= x - w && mouseX < x + w && mouseY >= y - h
				&& mouseY < y + h;
	}

	public void onSelect() {

	}

	protected void onHold() {

	}

	public boolean isAvailable() {
		return available;
	}

	public ButtonElement setAvailable(boolean available) {
		this.available = available;
		return this;
	}
}
