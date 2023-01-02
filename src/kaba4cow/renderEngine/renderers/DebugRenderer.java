package kaba4cow.renderEngine.renderers;

import java.util.LinkedList;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.renderEngine.shaders.DebugShader;
import kaba4cow.toolbox.RawModelContainer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class DebugRenderer extends AbstractRenderer {

	private static RawModel MODEL;

	private LinkedList<Renderable> map = new LinkedList<Renderable>();

	public DebugRenderer(Renderer renderer) {
		super(renderer, DebugShader.get());
		if (MODEL == null)
			MODEL = RawModelContainer.get("LOD/lod5");
	}

	public void render(Matrix4f matrix, Vector3f color) {
		if (matrix == null)
			return;
		Renderable renderable = new Renderable(matrix, color);
		add(renderable);
	}

	protected void add(Renderable renderable) {
		map.add(renderable);
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		DebugShader shader = getShader();
		prepareModel(MODEL, shader);
		int vertexCount = MODEL.getVertexCount();
		while (!map.isEmpty()) {
			Renderable current = map.removeFirst();
			shader.color.loadValue(current.color);
			shader.transformationMatrix.loadValue(current.matrix);
			GLUtils.drawLines(vertexCount);
		}
		unbindTexturedModel();
		finishRendering();
	}

	@Override
	protected void startRendering() {
		DebugShader shader = getShader();
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
	public DebugShader getShader() {
		return (DebugShader) shader;
	}

	private void prepareModel(RawModel rawModel, DebugShader shader) {
		if (rawModel != null)
			GLUtils.bindVertexArray(rawModel.getVao());
		GLUtils.enableVertexAttribArray(0);
	}

	private void unbindTexturedModel() {
		GLUtils.disableVertexAttribArray(0);
		GLUtils.unbindVertexArray();
	}

	private static class Renderable {

		public final Matrix4f matrix;
		public final Vector3f color;

		public Renderable(Matrix4f matrix, Vector3f color) {
			this.matrix = matrix;
			this.color = color;
		}

	}

}
