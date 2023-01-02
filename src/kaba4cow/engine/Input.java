package kaba4cow.engine;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Input {

	private static final int NONE = 0;
	private static final int PRESSED = 1;
	private static final int DOWN = 2;
	private static final int RELEASED = 3;

	private static final int[] keyboardStates = new int[Keyboard.KEYBOARD_SIZE];
	private static final int[] mouseStates = new int[Mouse.getButtonCount()];

	public static void update() {
		reset();

		for (int i = 0; i < keyboardStates.length; i++) {
			if (Keyboard.isKeyDown(i))
				keyboardStates[i] = DOWN;
		}
		while (Keyboard.next()) {
			int key = Keyboard.getEventKey();
			if (key < 0)
				continue;
			if (Keyboard.getEventKeyState()) {
				if (!Keyboard.isRepeatEvent())
					keyboardStates[key] = PRESSED;
			} else
				keyboardStates[key] = RELEASED;
		}

		for (int i = 0; i < mouseStates.length; i++) {
			if (Mouse.isButtonDown(i))
				mouseStates[i] = DOWN;
		}
		while (Mouse.next()) {
			int button = Mouse.getEventButton();
			if (button < 0)
				continue;
			if (Mouse.getEventButtonState())
				mouseStates[button] = PRESSED;
			else
				mouseStates[button] = RELEASED;
		}
	}

	public static void reset() {
		for (int i = 0; i < keyboardStates.length; i++)
			keyboardStates[i] = NONE;
		for (int i = 0; i < mouseStates.length; i++)
			mouseStates[i] = NONE;
	}

	public static boolean isKey(int key) {
		return keyboardStates[key] == DOWN;
	}

	public static boolean isKeyDown(int key) {
		return keyboardStates[key] == PRESSED;
	}

	public static boolean isKeyUp(int key) {
		return keyboardStates[key] == RELEASED;
	}

	public static boolean isButton(int key) {
		return mouseStates[key] == DOWN;
	}

	public static boolean isButtonDown(int key) {
		return mouseStates[key] == PRESSED;
	}

	public static boolean isButtonUp(int key) {
		return mouseStates[key] == RELEASED;
	}

}
