package kaba4cow.editors;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import kaba4cow.engine.MainProgram;

import org.lwjgl.opengl.Display;

public class EditorWindowListener implements WindowListener {

	public EditorWindowListener(AbstractEditor editor) {
		editor.addWindowListener(this);
	}

	@Override
	public void windowOpened(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		MainProgram.requestClosing();
		while (Display.isCreated()) {

		}
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowIconified(WindowEvent e) {

	}

	@Override
	public void windowDeiconified(WindowEvent e) {

	}

	@Override
	public void windowActivated(WindowEvent e) {

	}

	@Override
	public void windowDeactivated(WindowEvent e) {

	}

}
