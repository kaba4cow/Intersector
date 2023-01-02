package kaba4cow.editors;

import java.awt.Component;
import java.awt.Container;
import java.awt.Font;

import javax.swing.JMenu;
import javax.swing.JSpinner;
import javax.swing.border.BevelBorder;

import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public final class EditorUtils {

	public static final Font FONT = new Font("BankGothic Md BT", Font.PLAIN, 15);
	public static final int BEVEL_BORDER = BevelBorder.RAISED;

	private EditorUtils() {

	}

	public static final float getFloatValue(JSpinner spinner) {
		if (spinner == null)
			return 0f;
		Number number = (Number) spinner.getValue();
		if (number == null)
			return 0f;
		return number.floatValue();
	}

	public static final int getIntValue(JSpinner spinner) {
		if (spinner == null)
			return 0;
		Number number = (Number) spinner.getValue();
		if (number == null)
			return 0;
		return number.intValue();
	}

	public static void setFont(Component[] components) {
		for (int i = 0; i < components.length; i++) {
			Component component = components[i];
			component.setFont(FONT);
			if (component instanceof JMenu) {
				Component[] childComponents = ((JMenu) component)
						.getMenuComponents();
				setFont(childComponents);
			} else if (component instanceof Container) {
				Component[] childComponents = ((Container) component)
						.getComponents();
				setFont(childComponents);
			}
		}
	}

	public static final Vector2f getVector2fValue(JSpinner spinnerX,
			JSpinner spinnerY) {
		float x = getFloatValue(spinnerX);
		float y = getFloatValue(spinnerY);
		return new Vector2f(x, y);
	}

	public static final Vector3f getVector3fValue(JSpinner spinnerX,
			JSpinner spinnerY, JSpinner spinnerZ) {
		float x = getFloatValue(spinnerX);
		float y = getFloatValue(spinnerY);
		float z = getFloatValue(spinnerZ);
		return new Vector3f(x, y, z);
	}

}
