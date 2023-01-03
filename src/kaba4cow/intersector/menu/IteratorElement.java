package kaba4cow.intersector.menu;

public class IteratorElement extends ButtonElement {

	public static String[] DEFAULT3 = { "Low", "Medium", "High" };
	public static String[] DEFAULT5 = { "Very Low", "Low", "Medium", "High",
			"Very High" };

	private int index;
	private String[] list;

	public IteratorElement() {
		super();
		this.index = 0;
		this.list = new String[1];
	}

	@Override
	public final void onSelect() {
		super.onSelect();
		index = (index + 1) % list.length;
		setTextString(textString);
	}

	public IteratorElement setList(String... list) {
		this.list = list;
		index = index % list.length;
		setTextString(textString);
		return this;
	}

	public int getIndex() {
		return index;
	}

	public IteratorElement setIndex(int index) {
		this.index = index % list.length;
		setTextString(textString);
		return this;
	}

	@Override
	public MenuElement setTextString(String textString) {
		super.setTextString(textString + ": " + list[index]);
		this.textString = textString;
		return this;
	}

}
