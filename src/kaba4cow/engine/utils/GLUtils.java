package kaba4cow.engine.utils;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public final class GLUtils {

	public static final int VERTEX_SHADER = GL20.GL_VERTEX_SHADER;
	public static final int FRAGMENT_SHADER = GL20.GL_FRAGMENT_SHADER;

	private GLUtils() {

	}

	public static void clearColorBuffer() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
	}

	public static void clearDepthBuffer() {
		GL11.glClear(GL11.GL_DEPTH_BUFFER_BIT);
	}

	public static void enableCulling(boolean back) {
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(back ? GL11.GL_BACK : GL11.GL_FRONT);
	}

	public static void disableCulling() {
		GL11.glDisable(GL11.GL_CULL_FACE);
	}

	public static void enableDepthMask() {
		GL11.glDepthMask(true);
	}

	public static void disableDepthMask() {
		GL11.glDepthMask(false);
	}

	public static void enableDepthTest() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}

	public static void disableDepthTest() {
		GL11.glDisable(GL11.GL_DEPTH_TEST);
	}

	public static void enableBlending() {
		GL11.glEnable(GL11.GL_BLEND);
	}

	public static void disableBlending() {
		GL11.glDisable(GL11.GL_BLEND);
	}

	public static void alphaBlending() {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	public static void additiveBlending() {
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
	}

	public static void bindTexture2D(int texture) {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
	}

	public static void bindTextureCubemap(int texture) {
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texture);
	}

	public static void activeTexture(int index) {
		if (index >= 0 && index < 32)
			GL13.glActiveTexture(GL13.GL_TEXTURE0 + index);
	}

	public static void bindVertexArray(int index) {
		GL30.glBindVertexArray(index);
	}

	public static void unbindVertexArray() {
		GL30.glBindVertexArray(0);
	}

	public static void enableVertexAttribArray(int index) {
		GL20.glEnableVertexAttribArray(index);
	}

	public static void disableVertexAttribArray(int index) {
		GL20.glDisableVertexAttribArray(index);
	}

	public static void drawLines(int vertexCount) {
		GL11.glDrawElements(GL11.GL_LINES, vertexCount, GL11.GL_UNSIGNED_INT, 0);
	}

	public static void drawTriangles(int vertexCount) {
		GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount,
				GL11.GL_UNSIGNED_INT, 0);
	}

	public static void drawArraysTriangles(int vertexCount) {
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);
	}

	public static void drawArraysTriangleStrip(int vertexCount) {
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, vertexCount);
	}

}
