package kaba4cow.renderEngine.renderers;

import java.util.LinkedList;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.renderEngine.shaders.ShieldShader;
import kaba4cow.toolbox.RawModelContainer;

import org.lwjgl.util.vector.Matrix4f;

public class ShieldRenderer extends AbstractRenderer {

	private LinkedList<Renderable> map = new LinkedList<Renderable>();

	private static TexturedModel MODEL;

	public ShieldRenderer(Renderer renderer) {
		super(renderer, ShieldShader.get());
	}

	public void render(Matrix4f matrix, float time, float brightness) {
		if (matrix == null)
			return;
		Renderable renderable = new Renderable(matrix, time, brightness);
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
		ShieldShader shader = getShader();
		prepareTexturedModel(shader);
		int vertexCount = MODEL.getRawModel().getVertexCount();
		while (!map.isEmpty()) {
			Renderable current = map.removeFirst();
			shader.transformationMatrix.loadValue(current.matrix);
			shader.brightness.loadValue(current.brightness);
			shader.texOffset.loadValue(current.time);
			if (renderer.isWireframe())
				GLUtils.drawLines(vertexCount);
			else
				GLUtils.drawTriangles(vertexCount);
		}
		unbindTexturedModel();
		finishRendering();
	}

	@Override
	protected void startRendering() {
		ShieldShader shader = getShader();
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
	public ShieldShader getShader() {
		return (ShieldShader) shader;
	}

	private void prepareTexturedModel(ShieldShader shader) {
		if (MODEL == null)
			MODEL = new TexturedModel(RawModelContainer.get("MISC/SHIELD"),
					ModelTextureFile.get("SHIELD").get());
		RawModel rawModel = MODEL.getRawModel();
		ModelTexture modelTexture = MODEL.getTexture();
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

		public final Matrix4f matrix;
		public final float time;
		public final float brightness;

		public Renderable(Matrix4f matrix, float time, float brightness) {
			this.matrix = matrix;
			this.time = time;
			this.brightness = brightness;
		}

	}

}
