package kaba4cow.intersector.renderEngine.renderers.planets;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.toolbox.Shaders;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.renderEngine.shaders.planets.TerrainShader;

import org.lwjgl.util.vector.Matrix4f;

public class TerrainRenderer extends AbstractRenderer {

	private List<Renderable> map = new ArrayList<Renderable>();

	public TerrainRenderer(Renderer renderer) {
		super(renderer, TerrainShader.get());
	}

	public void render(RawModel model, Cubemap texture, float emission,
			Matrix4f matrix) {
		if (model == null || texture == null || matrix == null)
			return;
		Renderable renderable = new Renderable(model, texture, emission, matrix);
		add(renderable);
	}

	protected void add(Renderable renderable) {
		if (!map.contains(renderable))
			map.add(renderable);
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		TerrainShader shader = getShader();
		for (int i = 0; i < map.size(); i++) {
			prepareRawModel(map.get(i).model, map.get(i).texture, shader);
			shader.transformationMatrix.loadValue(map.get(i).matrix);
			shader.emission.loadValue(map.get(i).emission);
			GLUtils.drawTriangles(map.get(i).model.getVertexCount());
			unbindRawModel();
		}
		map.clear();
		finishRendering();
	}

	private void prepareRawModel(RawModel model, Cubemap texture,
			TerrainShader shader) {
		GLUtils.bindVertexArray(model.getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableVertexAttribArray(1);
		GLUtils.enableVertexAttribArray(2);
		GLUtils.activeTexture(0);
		GLUtils.bindTextureCubemap(texture.getTexture());
		shader.connectTextureUnits();
	}

	@Override
	protected void startRendering() {
		TerrainShader shader = getShader();
		shader.start();
		shader.projectionMatrix.loadValue(renderer.getProjectionMatrix());
		shader.viewMatrix.loadValue(renderer.getViewMatrix());
		shader.ambientLighting.loadValue(renderer.getAmbientLighting());
		List<Light> lights = renderer.getLights();
		for (int i = 0; i < Shaders.getLights(); i++) {
			Light light = null;
			if (i < lights.size())
				light = lights.get(i);
			else
				light = Light.INVALID;
			shader.lightPosition[i].loadValue(light.getPos());
			shader.lightColor[i].loadValue(light.getColor());
			shader.lightAttenuation[i].loadValue(light.getAttenuation());
		}
		GLUtils.disableBlending();
		GLUtils.enableCulling(true);
		GLUtils.enableDepthMask();
	}

	@Override
	protected void finishRendering() {
		shader.stop();
	}

	@Override
	public TerrainShader getShader() {
		return (TerrainShader) shader;
	}

	private void unbindRawModel() {
		GLUtils.disableVertexAttribArray(0);
		GLUtils.disableVertexAttribArray(1);
		GLUtils.disableVertexAttribArray(2);
		GLUtils.unbindVertexArray();
	}

	private static class Renderable {

		public final RawModel model;
		public final Cubemap texture;
		public final float emission;
		public final Matrix4f matrix;

		public Renderable(RawModel model, Cubemap texture, float emission,
				Matrix4f matrix) {
			this.model = model;
			this.texture = texture;
			this.emission = emission;
			this.matrix = matrix;
		}

	}

}
