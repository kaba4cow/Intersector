package kaba4cow.renderEngine.renderers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.renderEngine.textures.TextureAtlas;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.renderEngine.models.LaserModel;
import kaba4cow.renderEngine.shaders.LaserShader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class LaserRenderer extends AbstractRenderer {

	private Map<LaserModel, LinkedList<Renderable>> map = new HashMap<LaserModel, LinkedList<Renderable>>();

	public LaserRenderer(Renderer renderer) {
		super(renderer, LaserShader.get());
	}

	public void render(LaserModel model, Matrix4f matrix, Vector2f texOffset,
			float brightness) {
		if (model == null || model.getRawModel() == null
				|| model.getTexture() == null || matrix == null)
			return;
		if (texOffset == null)
			texOffset = Vectors.INIT2;
		Renderable renderable = new Renderable(model, matrix, texOffset,
				brightness);
		add(renderable);
	}

	protected void add(Renderable renderable) {
		LinkedList<Renderable> list = map.get(renderable.model);
		if (list == null) {
			list = new LinkedList<Renderable>();
			map.put(renderable.model, list);
		}
		list.add(renderable);
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		LaserShader shader = getShader();
		for (LaserModel model : map.keySet()) {
			prepareTexturedModel(model, shader);
			LinkedList<Renderable> list = map.get(model);
			int vertexCount = model.getRawModel().getVertexCount();
			while (!list.isEmpty()) {
				Renderable current = list.removeFirst();
				shader.transformationMatrix.loadValue(current.matrix);
				shader.brightness.loadValue(current.brightness);
				shader.texOffset.loadValue(current.texOffset);
				if (renderer.isWireframe())
					GLUtils.drawLines(vertexCount);
				else
					GLUtils.drawTriangles(vertexCount);
			}
			unbindTexturedModel();
		}
		map.clear();
		finishRendering();
	}

	@Override
	protected void startRendering() {
		LaserShader shader = getShader();
		shader.start();
		shader.projectionMatrix.loadValue(renderer.getProjectionMatrix());
		shader.viewMatrix.loadValue(renderer.getViewMatrix());
		shader.connectTextureUnits();
		GLUtils.disableCulling();
		GLUtils.enableBlending();
		GLUtils.additiveBlending();
		GLUtils.disableDepthMask();
	}

	@Override
	protected void finishRendering() {
		GLUtils.enableDepthMask();
		GLUtils.disableBlending();
		GLUtils.enableCulling(true);
		shader.stop();
	}

	@Override
	public LaserShader getShader() {
		return (LaserShader) shader;
	}

	private void prepareTexturedModel(LaserModel model, LaserShader shader) {
		RawModel rawModel = model.getRawModel();
		TextureAtlas modelTexture = model.getTexture();
		GLUtils.bindVertexArray(rawModel.getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableVertexAttribArray(1);
		GLUtils.activeTexture(0);
		GLUtils.bindTexture2D(modelTexture.getTexture());
	}

	private void unbindTexturedModel() {
		GLUtils.disableVertexAttribArray(0);
		GLUtils.disableVertexAttribArray(1);
		GLUtils.unbindVertexArray();
	}

	private static class Renderable {

		public final LaserModel model;
		public final Matrix4f matrix;
		public final Vector2f texOffset;
		public final float brightness;

		public Renderable(LaserModel model, Matrix4f matrix,
				Vector2f texOffset, float brightness) {
			this.model = model;
			this.matrix = matrix;
			this.texOffset = texOffset;
			this.brightness = brightness;
		}

	}

}
