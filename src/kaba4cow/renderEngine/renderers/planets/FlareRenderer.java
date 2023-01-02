package kaba4cow.renderEngine.renderers.planets;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.renderEngine.shaders.planets.FlareShader;
import kaba4cow.toolbox.RawModelContainer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class FlareRenderer extends AbstractRenderer {

	private List<Renderable> map = new ArrayList<Renderable>();

	private static TexturedModel MODEL;

	public FlareRenderer(Renderer renderer) {
		super(renderer, FlareShader.get());
	}

	public void render(Matrix4f matrix, Vector3f texOffset, Vector3f color,
			float brightness) {
		if (color == null || matrix == null)
			return;
		Renderable renderable = new Renderable(matrix, texOffset, color,
				brightness);
		map.add(renderable);
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		FlareShader shader = getShader();
		int vertexCount = prepareTexturedModel(shader);
		for (int i = 0; i < map.size(); i++) {
			shader.transformationMatrix.loadValue(map.get(i).matrix);
			shader.texOffset.loadValue(map.get(i).texOffset);
			shader.color.loadValue(map.get(i).color);
			shader.brightness.loadValue(map.get(i).brightness);
			if (renderer.isWireframe())
				GLUtils.drawLines(vertexCount);
			else
				GLUtils.drawTriangles(vertexCount);
		}
		unbindTexturedModel();
		map.clear();
		finishRendering();
	}

	@Override
	protected void startRendering() {
		FlareShader shader = getShader();
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
	public FlareShader getShader() {
		return (FlareShader) shader;
	}

	private int prepareTexturedModel(FlareShader shader) {
		if (MODEL == null)
			MODEL = new TexturedModel(RawModelContainer.get("MISC/flare"),
					ModelTextureFile.get("FLARE").get());
		GLUtils.bindVertexArray(MODEL.getRawModel().getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableVertexAttribArray(1);
		GLUtils.activeTexture(0);
		GLUtils.bindTexture2D(MODEL.getTexture().getTexture());
		return MODEL.getRawModel().getVertexCount();
	}

	private void unbindTexturedModel() {
		GLUtils.disableVertexAttribArray(0);
		GLUtils.disableVertexAttribArray(1);
		GLUtils.unbindVertexArray();
	}

	private static class Renderable {

		public final Matrix4f matrix;
		public final Vector3f texOffset;
		public final Vector3f color;
		public final float brightness;

		public Renderable(Matrix4f matrix, Vector3f texOffset, Vector3f color,
				float brightness) {
			this.matrix = matrix;
			this.texOffset = texOffset;
			this.color = color;
			this.brightness = brightness;
		}

	}

}
