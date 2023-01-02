package kaba4cow.engine.renderEngine.postProcessing;

import org.lwjgl.opengl.GL11;

public class ImageRenderer {

	private FrameBufferObject fbo;

	public ImageRenderer(int width, int height, int sampling) {
		this.fbo = new FrameBufferObject(width, height, FrameBufferObject.NONE,
				sampling);
	}

	public ImageRenderer() {
		this.fbo = null;
	}

	public void renderQuad() {
		if (fbo != null)
			fbo.bindFrameBuffer();
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glDrawArrays(GL11.GL_TRIANGLE_STRIP, 0, 4);
		if (fbo != null)
			fbo.unbindFrameBuffer();
	}

	public int getOutputTexture() {
		return fbo.getTexture();
	}

}
