package kaba4cow.intersector.renderEngine.renderers.generation;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.toolbox.ColorRamp;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.galaxyengine.TerrainGenerator;
import kaba4cow.intersector.renderEngine.shaders.generation.TerrainTextureShader;
import kaba4cow.intersector.toolbox.containers.RawModelContainer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class TerrainTextureRenderer extends AbstractRenderer {

	private Renderable renderable;

	private static RawModel[] MODEL = new RawModel[6];

	public TerrainTextureRenderer(Renderer renderer) {
		super(renderer, TerrainTextureShader.get());
		this.renderable = null;
	}

	public void render(int face, TerrainGenerator terrainGenerator,
			Matrix4f matrix) {
		if (terrainGenerator == null || matrix == null)
			return;
		renderable = new Renderable(face, terrainGenerator, matrix);
	}

	@Override
	public void process() {
		if (renderable == null)
			return;
		startRendering();
		TerrainTextureShader shader = getShader();
		int vertexCount = prepareRawModel(renderable.face, shader);
		shader.transformationMatrix.loadValue(renderable.matrix);
		shader.scale.loadValue(renderable.scale);
		shader.seed.loadValue(renderable.seed);
		shader.generation.loadValue(renderable.generation);
		shader.info.loadValue(renderable.info);
		ColorRamp colorRamp = renderable.colorRamp;
		for (int j = 0; j < ColorRamp.MAX_NUM; j++) {
			shader.elementColor[j].loadValue(colorRamp.get(j).color);
			shader.elementInfo[j].loadValue(colorRamp.get(j).position,
					colorRamp.get(j).contrast);
		}
		if (renderer.isWireframe())
			GLUtils.drawLines(vertexCount);
		else
			GLUtils.drawTriangles(vertexCount);
		unbindRawModel();
		renderable = null;
		finishRendering();
	}

	@Override
	protected void startRendering() {
		TerrainTextureShader shader = getShader();
		shader.start();
		shader.projectionMatrix.loadValue(renderer.getProjectionMatrix());
		shader.viewMatrix.loadValue(renderer.getViewMatrix());
		GLUtils.disableBlending();
		GLUtils.enableCulling(false);
		GLUtils.enableDepthMask();
	}

	@Override
	protected void finishRendering() {
		shader.stop();
		GLUtils.enableCulling(true);
	}

	@Override
	public TerrainTextureShader getShader() {
		return (TerrainTextureShader) shader;
	}

	private int prepareRawModel(int face, TerrainTextureShader shader) {
		if (MODEL[face] == null)
			MODEL[face] = RawModelContainer.get("CUBEMAPS/" + face);
		GLUtils.bindVertexArray(MODEL[face].getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableVertexAttribArray(1);
		return MODEL[face].getVertexCount();
	}

	private void unbindRawModel() {
		GLUtils.disableVertexAttribArray(0);
		GLUtils.disableVertexAttribArray(1);
		GLUtils.unbindVertexArray();
	}

	private static class Renderable {

		public final int face;
		public final ColorRamp colorRamp;
		public final Vector3f scale;
		public final float seed;
		public final float generation;
		public final Vector3f info;
		public final Matrix4f matrix;

		public Renderable(int face, TerrainGenerator terrainGenerator,
				Matrix4f matrix) {
			this.face = face;
			this.colorRamp = terrainGenerator.colorRamp;
			this.scale = terrainGenerator.scale;
			this.seed = terrainGenerator.noiseSeed;
			this.generation = terrainGenerator.generation;
			this.info = terrainGenerator.info;
			this.matrix = matrix;
		}

	}

}
