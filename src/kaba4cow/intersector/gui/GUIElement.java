package kaba4cow.intersector.gui;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.audio.Source;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.toolbox.Constants;
import kaba4cow.intersector.toolbox.containers.FontContainer;

public abstract class GUIElement {

	private static Source source;

	protected Vector2f position;
	protected Vector2f scale;

	protected String textString;
	protected GUIText text;

	protected float frameWidth;

	public GUIElement() {
		this.position = new Vector2f();
		this.scale = new Vector2f(1f, 1f);
		this.text = new GUIText("", FontContainer.get("gui"), new Vector2f(
				position), 1f, 1f, true);
	}

	protected static void playSound() {
		if (source == null)
			source = new Source(Constants.MENU);
		// source.setPitch(RNG.randomFloat(0.98f, 1.02f)).play(
		// AudioManager.get("guigame"));
	}

	public abstract void render(Vector3f color, RendererContainer renderers);

	protected Vector2f getPosition() {
		return position;
	}

	protected GUIElement setPosition(float x, float y) {
		this.position.set(x, y);
		if (text.isCenteredX())
			text.getPosition().x = x;
		else
			text.getPosition().x = x - 0.95f * scale.x;
		if (text.isCenteredY())
			text.getPosition().y = y;
		else
			text.getPosition().y = y + scale.y - GUIText.getTextHeight(1f);
		return this;
	}

	protected GUIElement setPosition(Vector2f position) {
		return setPosition(position.x, position.y);
	}

	protected Vector2f getScale() {
		return scale;
	}

	protected GUIElement setScale(float x, float y) {
		this.scale.set(x, y);
		this.text.setMaxLineLength(0.95f * x);
		return this;
	}

	protected GUIElement setScale(Vector2f scale) {
		return setScale(scale.x, scale.y);
	}

	protected String getText() {
		return text.getTextString();
	}

	public String getTextString() {
		return textString;
	}

	public GUIElement setTextString(String textString) {
		this.textString = textString;
		this.text.setTextString(textString);
		return this;
	}

	protected GUIElement setMaxLineLength(float length) {
		this.text.setMaxLineLength(length);
		return this;
	}

}
