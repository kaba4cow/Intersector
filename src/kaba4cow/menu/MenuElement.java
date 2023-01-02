package kaba4cow.menu;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.audio.AudioManager;
import kaba4cow.engine.audio.Source;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.files.InfosFile;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.toolbox.Constants;
import kaba4cow.toolbox.FontContainer;

public abstract class MenuElement {

	public static final Vector3f ACTIVE_COLOR;
	public static final Vector3f UNACTIVE_COLOR;
	public static final Vector3f HOVERED_COLOR;
	public static final Vector3f SELECTED_COLOR;

	private static Source source;

	protected Vector2f position;
	protected float scale;

	protected String textString;
	protected GUIText text;

	protected float frameWidth;

	public MenuElement() {
		this.position = new Vector2f();
		this.scale = 1f;
		this.text = new GUIText("", FontContainer.get("menu"), position, 1f, 1f, true);
	}

	static {
		ACTIVE_COLOR = InfosFile.gui.data().node("active").getVector3();
		UNACTIVE_COLOR = InfosFile.gui.data().node("unactive").getVector3();
		HOVERED_COLOR = InfosFile.gui.data().node("hovered").getVector3();
		SELECTED_COLOR = InfosFile.gui.data().node("selected").getVector3();
	}

	public static void playSound() {
		if (source == null)
			source = new Source(Constants.MENU);
		source.play(AudioManager.get("guimenu"));
	}

	public abstract void render(RendererContainer renderers);

	public Vector2f getPosition() {
		return position;
	}

	public MenuElement setPosition(Vector2f position) {
		this.position = position;
		return this;
	}

	public MenuElement setPosition(float x, float y) {
		this.position.set(x, y);
		return this;
	}

	public float getScale() {
		return scale;
	}

	public MenuElement setScale(float scale) {
		this.scale = scale;
		text.setFontSize(scale);
		return this;
	}

	public String getText() {
		return text.getTextString();
	}

	public String getTextString() {
		return textString;
	}

	public MenuElement setTextString(String textString) {
		this.textString = textString;
		this.text.setTextString(textString);
		return this;
	}

}
