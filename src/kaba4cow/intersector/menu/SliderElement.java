package kaba4cow.intersector.menu;

import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.utils.StringUtils;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class SliderElement extends ButtonElement {

	private float value;
	private float min;
	private float max;

	private boolean renderInt;
	private String suffix;

	public SliderElement() {
		super();
		this.value = 0f;
		this.setBounds(0f, 1f);
		this.renderInt = true;
		this.suffix = "";
	}

	@Override
	public void render(RendererContainer renderers) {
		Vector3f color = processMouse();
		float slider = Maths.mapLimit(value, min, max, 0f, 1f);
		text.setColor(2f * color.x, 2f * color.y, 2f * color.z);
		renderers.getTextRenderer().render(text);
		renderers.getSliderFrameRenderer().render(position, FRAMEWIDTH * SCALE,
				GUIText.getTextHeight(SCALE), true, true, slider, color);
	}

	@Override
	public final void onHold() {
		float mouseX = WindowUtils.toNormalizedX(Mouse.getX());
		float mouseY = WindowUtils.toNormalizedY(Mouse.getY());

		float w = FRAMEWIDTH * SCALE;
		float h = GUIText.getTextHeight(SCALE);
		float x = position.x;
		float y = position.y;

		if (mouseY >= y - h && mouseY < y + h) {
			value = Maths.mapLimit(mouseX, x - 0.975f * w, x + 0.975f * w, min,
					max);
			setValue(getValue());
		}
	}

	public float getFloatValue() {
		return value;
	}

	public int getIntValue() {
		return Maths.round(value);
	}

	public float getValue() {
		return value;
	}

	public SliderElement setValue(float value) {
		this.value = Maths.limit(value, min, max);
		setTextString(textString);
		return this;
	}

	public SliderElement setBounds(float min, float max) {
		this.min = min;
		this.max = max;
		return this;
	}

	@Override
	public SliderElement setTextString(String textString) {
		super.setTextString(textString + ": " + getStringValue() + getSuffix());
		this.textString = textString;
		return this;
	}

	private String getStringValue() {
		if (renderInt)
			return Integer.toString(getIntValue());
		else
			return StringUtils.format2(getFloatValue());
	}

	public String getSuffix() {
		return suffix;
	}

	public SliderElement setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}

	public boolean isRenderInt() {
		return renderInt;
	}

	public SliderElement setRenderInt(boolean renderInt) {
		this.renderInt = renderInt;
		return this;
	}

}
