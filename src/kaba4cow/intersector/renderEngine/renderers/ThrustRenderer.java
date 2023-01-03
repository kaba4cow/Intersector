package kaba4cow.intersector.renderEngine.renderers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.renderEngine.models.ThrustModel;
import kaba4cow.intersector.renderEngine.shaders.ThrustShader;
import kaba4cow.intersector.renderEngine.textures.ThrustTexture;

import org.lwjgl.util.vector.Matrix4f;

public class ThrustRenderer extends AbstractRenderer {

	private Map<ThrustModel, LinkedList<Renderable>> map = new HashMap<ThrustModel, LinkedList<Renderable>>();

	private static float elapsedTime = 0f;

	public ThrustRenderer(Renderer renderer) {
		super(renderer, ThrustShader.get());
	}

	public static void update(float dt) {
		elapsedTime += dt;
	}

	public void render(ThrustModel model, Matrix4f matrix, float brightness,
			float speed) {
		if (model == null || model.getRawModel() == null
				|| model.getTexture() == null || matrix == null)
			return;
		Renderable renderable = new Renderable(model, matrix, brightness, speed);
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
		ThrustShader shader = getShader();
		for (ThrustModel model : map.keySet()) {
			prepareTexturedModel(model, shader);
			LinkedList<Renderable> list = map.get(model);
			int vertexCount = model.getRawModel().getVertexCount();
			while (!list.isEmpty()) {
				Renderable current = list.removeFirst();
				shader.transformationMatrix.loadValue(current.matrix);
				shader.brightness.loadValue(current.brightness);
				shader.texOffset.loadValue(current.speed * elapsedTime);
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
		ThrustShader shader = getShader();
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
	public ThrustShader getShader() {
		return (ThrustShader) shader;
	}

	private void prepareTexturedModel(ThrustModel model, ThrustShader shader) {
		RawModel rawModel = model.getRawModel();
		ThrustTexture modelTexture = model.getTexture();
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

		public final ThrustModel model;
		public final Matrix4f matrix;
		public final float brightness;
		public final float speed;

		public Renderable(ThrustModel model, Matrix4f matrix, float brightness,
				float speed) {
			this.model = model;
			this.matrix = matrix;
			this.speed = speed;
			this.brightness = brightness;
		}

	}

}
