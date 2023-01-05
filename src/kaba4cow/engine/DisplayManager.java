package kaba4cow.engine;

import java.awt.Canvas;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;
import org.newdawn.slick.opengl.ImageIOImageData;

import kaba4cow.engine.toolbox.Printer;

public class DisplayManager {

	public static void create(boolean fullscreen, boolean resizable, Canvas canvas) {
		Printer.println("CREATING DISPLAY...");
		try {
			Display.setTitle(MainProgram.getTitle());
			DisplayMode mode;
			if (fullscreen && canvas == null)
				Display.setDisplayModeAndFullscreen(mode = Display.getDesktopDisplayMode());
			else
				Display.setDisplayMode(mode = new DisplayMode(MainProgram.getWidth(), MainProgram.getHeight()));
			Display.setParent(canvas);
			Display.create(new PixelFormat());
			Display.setResizable(resizable && canvas == null);
			Display.setVSyncEnabled(true);
			Printer.println("DISPLAY CREATED: " + mode.getWidth() + "x" + mode.getHeight());
			Printer.println("SYSTEM: vendor " + GL11.glGetString(GL11.GL_VENDOR) + ", renderer "
					+ GL11.glGetString(GL11.GL_RENDERER) + ", GL " + GL11.glGetString(GL11.GL_VERSION) + ", GLSL "
					+ GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION));
			GL11.glViewport(0, 0, MainProgram.getWidth(), MainProgram.getHeight());
		} catch (LWJGLException e) {
			Printer.println("UNABLE TO CREATE DISPLAY");
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public static void resize() {
		MainProgram.getMainProgram().WIDTH = Display.getWidth();
		MainProgram.getMainProgram().HEIGHT = Display.getHeight();
		MainProgram.getMainProgram().updateAspectRatio();
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public static void setIcons(String path) {
		try {
			ByteBuffer[] icons = new ByteBuffer[2];
			icons[0] = new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File(path + "16.png")), false, false,
					null);
			icons[1] = new ImageIOImageData().imageToByteBuffer(ImageIO.read(new File(path + "32.png")), false, false,
					null);
			Display.setIcon(icons);
		} catch (Exception e) {
			Printer.println("UNABLE TO SET ICONS");
			e.printStackTrace();
		}
	}

	public static void update() {
		if (Display.isResizable() && Display.wasResized())
			resize();
		Display.sync(MainProgram.getFps());
		Display.update();
	}

	public static void close() {
		Printer.println("CLOSING DISPLAY...");
		Display.destroy();
		Printer.println("DISPLAY CLOSED");
	}

}
