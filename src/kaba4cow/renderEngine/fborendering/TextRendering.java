package kaba4cow.renderEngine.fborendering;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.renderEngine.renderers.TextRenderer;
import kaba4cow.engine.utils.GLUtils;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

public class TextRendering {

	public static int SIZE = 512;

	private static TextRenderer textRenderer;

	private static FrameBufferObject fbo;

	private TextRendering() {

	}

	static {
		Renderer renderer = new Renderer(Projection.SQUARE, 90f, 0.01f, 1000f,
				0f);
		textRenderer = new TextRenderer(renderer);
		fbo = new FrameBufferObject(SIZE, SIZE,
				FrameBufferObject.DEPTH_RENDER_BUFFER,
				FrameBufferObject.LINEAR_SAMPLING);
	}

	public static int process(int texture, GUIText... texts) {
		fbo.bindFrameBuffer();

		GLUtils.bindTexture2D(texture);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
				GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
		textRenderer.getRenderer().prepare();
		for (int i = 0; i < texts.length; i++) {
			texts[i].getPosition().y -= 0.5f * GUIText.getTextHeight(texts[i]
					.getFontSize());
			textRenderer.render(texts[i]);
		}
		textRenderer.process();
		for (int i = 0; i < texts.length; i++)
			texts[i].getPosition().y += 0.5f * GUIText.getTextHeight(texts[i]
					.getFontSize());

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());

		return texture;
	}

}
