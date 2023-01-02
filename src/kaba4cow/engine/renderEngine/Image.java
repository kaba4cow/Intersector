package kaba4cow.engine.renderEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import kaba4cow.engine.toolbox.Printer;
import kaba4cow.engine.toolbox.maths.Maths;

public class Image {

	protected int width;
	protected int height;
	protected int[] pixels;

	public Image(String file) {
		Printer.println("LOADING IMAGE: " + file);
		try {
			BufferedImage image = ImageIO.read(new File("resources/" + file
					+ ".png"));
			width = image.getWidth();
			height = image.getHeight();
			pixels = image.getRGB(0, 0, width, height, null, 0, width);
			image.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Image(File file) {
		Printer.println("LOADING IMAGE: " + file);
		try {
			BufferedImage image = ImageIO.read(file);
			width = image.getWidth();
			height = image.getHeight();
			pixels = image.getRGB(0, 0, width, height, null, 0, width);
			image.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Image(int[] pixels, int width, int height) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
	}

	public Image(int width, int height) {
		this.pixels = new int[width * height];
		this.width = width;
		this.height = height;
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = 0xFF000000;
	}

	public Image(int size) {
		this.pixels = new int[size * size];
		this.width = size;
		this.height = size;
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = 0xFF000000;
	}

	public Image(int width, int height, int color) {
		this.pixels = new int[width * height];
		this.width = width;
		this.height = height;
		for (int i = 0; i < pixels.length; i++)
			pixels[i] = color;
	}

	public Image get(int x, int y, int targetWidth, int targetHeight) {
		Image res = new Image(targetWidth, targetHeight);
		for (int i = 0; i < targetWidth; i++) {
			for (int j = 0; j < targetHeight; j++) {
				int color = getPixel(x + i, y + j);
				res.setPixel(i, j, color);
			}
		}
		return res;
	}

	public int sample(float sampleX, float sampleY) {
		sampleX = Maths.abs(sampleX);
		sampleX = sampleX - (int) sampleX;
		sampleY = Maths.abs(sampleY);
		sampleY = sampleY - (int) sampleY;
		int x = (int) (width * sampleX);
		int y = (int) (height * sampleY);
		return pixels[y * width + x];
	}

	public int sample(float sampleX, float sampleY, boolean reflectX,
			boolean reflectY) {
		if (reflectX)
			sampleX %= 2f;
		if (reflectY)
			sampleY %= 2f;
		if (reflectX && (sampleX < 0f || sampleX >= 1f))
			sampleX = 1f - sampleX % 1f;
		if (reflectY && (sampleY < 0f || sampleY >= 1f))
			sampleY = 1f - sampleY % 1f;
		sampleX %= 1f;
		sampleY %= 1f;
		int x = (int) Maths.limit(width * sampleX, 0, width - 1);
		int y = (int) Maths.limit(height * sampleY, 0, height - 1);
		return pixels[y * width + x];
	}

	public int getPixel(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		return color;
	}

	public int getPixel(int index) {
		if (index < 0 || index >= pixels.length)
			return 0;
		int color = pixels[index];
		return color;
	}

	public void setPixel(int x, int y, int color) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return;
		int index = y * width + x;
		pixels[index] = color;
	}

	public void setPixel(int index, int color) {
		if (index < 0 || index >= pixels.length)
			return;
		pixels[index] = color;
	}

	public int getA(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		int value = (color >> 24) & 0xFF;
		return value;
	}

	public int getR(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		int value = (color >> 16) & 0xFF;
		return value;
	}

	public int getG(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		int value = (color >> 8) & 0xFF;
		return value;
	}

	public int getB(int x, int y) {
		if (x < 0 || x >= width || y < 0 || y >= height)
			return 0;
		int index = y * width + x;
		int color = pixels[index];
		int value = (color >> 0) & 0xFF;
		return value;
	}

	public int getA(int index) {
		if (index < 0 || index >= pixels.length)
			return 0;
		int color = pixels[index];
		int value = (color >> 24) & 0xFF;
		return value;
	}

	public int getR(int index) {
		if (index < 0 || index >= pixels.length)
			return 0;
		int color = pixels[index];
		int value = (color >> 16) & 0xFF;
		return value;
	}

	public int getG(int index) {
		if (index < 0 || index >= pixels.length)
			return 0;
		int color = pixels[index];
		int value = (color >> 8) & 0xFF;
		return value;
	}

	public int getB(int index) {
		if (index < 0 || index >= pixels.length)
			return 0;
		int color = pixels[index];
		int value = (color >> 0) & 0xFF;
		return value;
	}

	public int getLength() {
		return pixels.length;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public float getAspectRatio() {
		return (float) height / (float) width;
	}

	public Image copy() {
		return new Image(pixels.clone(), width, height);
	}

}
