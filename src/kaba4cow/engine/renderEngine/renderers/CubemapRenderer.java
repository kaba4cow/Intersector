package kaba4cow.engine.renderEngine.renderers;

import kaba4cow.engine.assets.Models;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.shaders.CubemapShader;
import kaba4cow.engine.utils.GLUtils;

import org.lwjgl.util.vector.Matrix4f;

public class CubemapRenderer extends AbstractRenderer {

	private Cubemap cubemap;

	public CubemapRenderer(Renderer renderer) {
		super(renderer, CubemapShader.get());
	}

	public void render(Cubemap cubemap) {
		this.cubemap = cubemap;
	}

	@Override
	public void process() {
		if (cubemap == null)
			return;
		startRendering();
		GLUtils.activeTexture(0);
		GLUtils.bindTextureCubemap(cubemap.getTexture());
		GLUtils.drawArraysTriangles(Models.getCubemap().getVertexCount());
		finishRendering();
	}

	@Override
	protected void startRendering() {
		shader.start();
		if (Renderer.USE_PROJ_VIEW_MATRICES) {
			Matrix4f viewMatrix = new Matrix4f(renderer.getViewMatrix());
			viewMatrix.m30 = 0f;
			viewMatrix.m31 = 0f;
			viewMatrix.m32 = 0f;
			getShader().projectionMatrix.loadValue(renderer
					.getProjectionMatrix());
			getShader().viewMatrix.loadValue(viewMatrix);
		} else {
			getShader().projectionMatrix.loadValue(new Matrix4f());
			getShader().viewMatrix.loadValue(new Matrix4f());
		}
		GLUtils.disableDepthMask();
		GLUtils.bindVertexArray(Models.getCubemap().getVao());
		GLUtils.enableVertexAttribArray(0);
	}

	@Override
	protected void finishRendering() {
		cubemap = null;
		GLUtils.disableVertexAttribArray(0);
		GLUtils.unbindVertexArray();
		GLUtils.enableDepthMask();
		shader.stop();
	}

	@Override
	public CubemapShader getShader() {
		return (CubemapShader) shader;
	}

}
