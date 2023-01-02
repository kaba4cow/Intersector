package kaba4cow.engine.toolbox;

import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.utils.ProgramUtils;

import org.lwjgl.opengl.GL11;

public final class ScreenshotManager {

	private static final int BUFFER_COUNT = 4;

	private static final ByteBuffer[] buffers = new ByteBuffer[BUFFER_COUNT];
	private static final boolean[] freeBuffers = new boolean[BUFFER_COUNT];

	private ScreenshotManager() {

	}

	static {
		int width = MainProgram.getWidth();
		int height = MainProgram.getHeight();
		for (int i = 0; i < BUFFER_COUNT; i++) {
			buffers[i] = ByteBuffer.allocateDirect(width * height * 3);
			freeBuffers[i] = true;
		}

		new Thread("SCREENSHOT MANAGER") {
			@Override
			public void run() {
				while (!MainProgram.isCloseRequested()) {
					for (int i = 0; i < BUFFER_COUNT; i++)
						processScreenshot(i);
				}
			}
		}.start();
	}

	private static void processScreenshot(int bufferIndex) {
		if (freeBuffers[bufferIndex])
			return;

		Printer.println("SAVING SCREENSHOT...");

		int width = MainProgram.getWidth();
		int height = MainProgram.getHeight();

		BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		ByteBuffer buffer = buffers[bufferIndex];

		int[] pixels = new int[width * height];
		int i = 0;
		int index;
		int bIndex;
		for (int y = height - 1; y >= 0; y--)
			for (int x = 0; x < width; x++) {
				index = y * width + x;
				bIndex = 3 * index;
				pixels[i] = ((buffer.get(bIndex) & 0xFF) << 16)
						| ((buffer.get(bIndex + 1) & 0xFF) << 8)
						| ((buffer.get(bIndex + 2) & 0xFF) << 0);
				i++;
			}
		image.setRGB(0, 0, width, height, pixels, 0, width);

		File directory = new File(ProgramUtils.getScreenshotLocation());
		if (!directory.exists())
			directory.mkdir();
		File file = new File(ProgramUtils.getScreenshotLocation()
				+ "SCREENSHOT_" + ProgramUtils.getDate("YYMMddHHmmss") + ".png");
		try {
			ImageIO.write(image, "png", file);
			Printer.println("SAVED SCREENSHOT: " + file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		buffers[bufferIndex].clear();
		freeBuffers[bufferIndex] = true;
	}

	public static void takeScreenshot() {
		Printer.println("TAKING SCREENSHOT...");
		int freeBuffer = searchFreeBuffer();
		if (freeBuffer == -1) {
			Printer.println("NO FREE BUFFERS FOUND");
			return;
		}

		int width = MainProgram.getWidth();
		int height = MainProgram.getHeight();
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGB,
				GL11.GL_UNSIGNED_BYTE, buffers[freeBuffer]);
		freeBuffers[freeBuffer] = false;
	}

	private static int searchFreeBuffer() {
		for (int i = 0; i < BUFFER_COUNT; i++)
			if (freeBuffers[i])
				return i;
		return -1;
	}

}
