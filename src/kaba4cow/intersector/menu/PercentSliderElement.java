package kaba4cow.intersector.menu;

public class PercentSliderElement extends SliderElement {

	public PercentSliderElement() {
		super();
		setBounds(0f, 100f).setSuffix("%").setRenderInt(false);
	}

	@Override
	public float getValue() {
		return 0.01f * super.getValue();
	}

	@Override
	public PercentSliderElement setValue(float value) {
		super.setValue(100f * value);
		return this;
	}

}
