package kaba4cow.menu;

public class CheckboxElement extends ButtonElement {

	private boolean selected;

	public CheckboxElement() {
		super();
		this.selected = false;
	}

	@Override
	public final void onSelect() {
		super.onSelect();
		selected = !selected;
		setTextString(textString);
	}

	public boolean isSelected() {
		return selected;
	}

	public CheckboxElement setSelected(boolean selected) {
		this.selected = selected;
		setTextString(textString);
		return this;
	}

	@Override
	public MenuElement setTextString(String textString) {
		super.setTextString(textString + getString(selected));
		this.textString = textString;
		return this;
	}

	private static String getString(boolean selected) {
		if (selected)
			return ": On";
		else
			return ": Off";
	}

}
