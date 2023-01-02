package kaba4cow.engine.renderEngine.postProcessing;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.toolbox.Printer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL30;

public class FrameBufferObject {

	private static final List<FrameBufferObject> fbos = new ArrayList<FrameBufferObject>();

	public static final int NONE = 0;
	public static final int DEPTH_TEXTURE = 1;
	public static final int DEPTH_RENDER_BUFFER = 2;

	public static final int LINEAR_SAMPLING = GL11.GL_LINEAR;
	public static final int NEAREST_SAMPLING = GL11.GL_NEAREST;

	private final int width;
	private final int height;

	private int frameBuffer;

	private int texture;
	private int depthTexture;

	private int depthBuffer;
	private int colorBuffer;

	public FrameBufferObject(int width, int height, int depthBufferType,
			int sampling) {
		this.width = width;
		this.height = height;
		this.initializeFrameBuffer(depthBufferType, sampling);
		fbos.add(this);
	}

	public static void cleanUp() {
		Printer.println("CLEANING UP " + fbos.size() + " FBOS");
		for (int i = 0; i < fbos.size(); i++) {
			FrameBufferObject fbo = fbos.get(i);
			GL30.glDeleteFramebuffers(fbo.frameBuffer);
			GL11.glDeleteTextures(fbo.texture);
			GL11.glDeleteTextures(fbo.depthTexture);
			GL30.glDeleteRenderbuffers(fbo.depthBuffer);
			GL30.glDeleteRenderbuffers(fbo.colorBuffer);
		}
	}

	public static int fbos() {
		return fbos.size();
	}

	public void bindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, frameBuffer);
		GL11.glViewport(0, 0, width, height);
	}

	public void unbindFrameBuffer() {
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public void bindToRead() {
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
		GL30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, frameBuffer);
		GL11.glReadBuffer(GL30.GL_COLOR_ATTACHMENT0);
	}

	public int getTexture() {
		return texture;
	}

	public int getDepthTexture() {
		return depthTexture;
	}

	private void initializeFrameBuffer(int type, int sampling) {
		createFrameBuffer();
		createTextureAttachment(sampling);
		if (type == DEPTH_RENDER_BUFFER)
			createDepthBufferAttachment();
		else if (type == DEPTH_TEXTURE)
			createDepthTextureAttachment(sampling);
		unbindFrameBuffer();
	}

	private void createFrameBuffer() {
		frameBuffer = GL30.glGenFramebuffers();
		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, frameBuffer);
		GL11.glDrawBuffer(GL30.GL_COLOR_ATTACHMENT0);
	}

	private void createTextureAttachment(int sampling) {
		texture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, width, height,
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				sampling);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				sampling);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S,
				GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T,
				GL12.GL_CLAMP_TO_EDGE);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
				GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
	}

	private void createDepthTextureAttachment(int sampling) {
		depthTexture = GL11.glGenTextures();
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTexture);
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL14.GL_DEPTH_COMPONENT24,
				width, height, 0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT,
				(ByteBuffer) null);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER,
				sampling);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER,
				sampling);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
				GL30.GL_DEPTH_ATTACHMENT, GL11.GL_TEXTURE_2D, depthTexture, 0);
	}

	private void createDepthBufferAttachment() {
		depthBuffer = GL30.glGenRenderbuffers();
		GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthBuffer);
		GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER,
				GL14.GL_DEPTH_COMPONENT24, width, height);
		GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER,
				GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthBuffer);
	}

}
