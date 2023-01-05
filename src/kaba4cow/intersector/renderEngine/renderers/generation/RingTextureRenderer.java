package kaba4cow.intersector.renderEngine.renderers.generation;

import kaba4cow.engine.assets.Models;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.galaxyengine.TerrainGenerator;
import kaba4cow.intersector.renderEngine.shaders.generation.RingTextureShader;

public class RingTextureRenderer extends AbstractRenderer {

	private TerrainGenerator renderable;

	public RingTextureRenderer(Renderer renderer) {
		super(renderer, RingTextureShader.get());
		this.renderable = null;
	}

	public void render(TerrainGenerator terrainGenerator) {
		if (terrainGenerator == null)
			return;
		renderable = terrainGenerator;
	}

	@Override
	public void process() {
		if (renderable == null)
			return;
		startRendering();
		RawModel quad = Models.getGuiQuad();
		RingTextureShader shader = getShader();
		shader.seed.loadValue(renderable.noiseSeed);
		shader.info.loadValue(renderable.ringInfo);
		shader.scale.loadValue(renderable.ringScale);
		shader.emission.loadValue(renderable.ringEmission);
		GLUtils.drawArraysTriangleStrip(quad.getVertexCount());
		renderable = null;
		finishRendering();
	}

	@Override
	protected void startRendering() {
		RingTextureShader shader = getShader();
		shader.start();
		shader.connectTextureUnits();
		RawModel quad = Models.getGuiQuad();
		GLUtils.bindVertexArray(quad.getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableBlending();
		GLUtils.additiveBlending();
		GLUtils.disableCulling();
		GLUtils.disableDepthTest();
	}

	@Override
	protected void finishRendering() {
		shader.stop();
		GLUtils.enableDepthTest();
		GLUtils.disableBlending();
		GLUtils.enableCulling(true);
		GLUtils.disableVertexAttribArray(0);
		GLUtils.unbindVertexArray();
	}

	@Override
	public RingTextureShader getShader() {
		return (RingTextureShader) shader;
	}

}
