package kaba4cow.intersector.renderEngine.renderers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.assets.Shaders;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.renderEngine.shaders.MachineShader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MachineRenderer extends AbstractRenderer {

	private Map<TexturedModel, LinkedList<Renderable>> map = new HashMap<TexturedModel, LinkedList<Renderable>>();
	private Cubemap cubemap;

	public MachineRenderer(Renderer renderer) {
		super(renderer, MachineShader.get());
	}

	public void render(TexturedModel model, Vector3f color, Matrix4f matrix) {
		if (model == null || model.getRawModel() == null
				|| model.getTexture() == null || matrix == null)
			return;
		if (color == null)
			color = Vectors.UNIT3;
		Renderable renderable = new Renderable(model, color, matrix);
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

	public void setCubemap(Cubemap cubemap) {
		this.cubemap = cubemap;
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		MachineShader shader = getShader();
		for (TexturedModel model : map.keySet()) {
			prepareTexturedModel(model, shader, cubemap);
			LinkedList<Renderable> list = map.get(model);
			int vertexCount = model.getRawModel().getVertexCount();
			while (!list.isEmpty()) {
				Renderable current = list.removeFirst();
				shader.transformationMatrix.loadValue(current.matrix);
				shader.color.loadValue(current.color);
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
		MachineShader shader = getShader();
		shader.start();
		if (Renderer.USE_PROJ_VIEW_MATRICES) {
			shader.projectionMatrix.loadValue(renderer.getProjectionMatrix());
			shader.viewMatrix.loadValue(renderer.getViewMatrix());
			shader.invViewMatrix.loadValue(renderer.getInvViewMatrix());
		} else {
			shader.projectionMatrix.loadValue(new Matrix4f());
			shader.viewMatrix.loadValue(new Matrix4f());
			shader.invViewMatrix.loadValue(new Matrix4f());
		}
		shader.cameraPosition.loadValue(getCamera().getPos());
		shader.ambientLighting.loadValue(renderer.getAmbientLighting());
		List<Light> lights = renderer.getLights();
		Light light;
		for (int i = 0; i < Shaders.getLights(); i++) {
			if (i < lights.size())
				light = lights.get(i);
			else
				light = Light.INVALID;
			shader.lightPosition[i].loadValue(light.getPos());
			shader.lightColor[i].loadValue(light.getColor());
			shader.lightAttenuation[i].loadValue(light.getAttenuation());
		}
		shader.connectTextureUnits();
		bindCubemapTexture(cubemap);
	}

	@Override
	protected void finishRendering() {
		cubemap = null;
		shader.stop();
	}

	@Override
	public MachineShader getShader() {
		return (MachineShader) shader;
	}

	private void prepareTexturedModel(TexturedModel model,
			MachineShader shader, Cubemap skybox) {
		RawModel rawModel = model.getRawModel();
		ModelTexture modelTexture = model.getTexture();
		GLUtils.disableBlending();
		GLUtils.enableCulling(true);
		GLUtils.enableDepthMask();
		GLUtils.bindVertexArray(rawModel.getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableVertexAttribArray(1);
		GLUtils.enableVertexAttribArray(2);
		float reflectivity = skybox == null ? 0f : modelTexture
				.getReflectivity();
		shader.texInfo.loadValue(modelTexture.getShininess(),
				modelTexture.getShineDamper(), reflectivity,
				modelTexture.getEmission());
		GLUtils.activeTexture(0);
		GLUtils.bindTexture2D(modelTexture.getTexture());
	}

	private void unbindTexturedModel() {
		GLUtils.disableVertexAttribArray(0);
		GLUtils.disableVertexAttribArray(1);
		GLUtils.disableVertexAttribArray(2);
		GLUtils.unbindVertexArray();
	}

	private void bindCubemapTexture(Cubemap cubemap) {
		if (cubemap == null)
			return;
		GLUtils.activeTexture(1);
		GLUtils.bindTextureCubemap(cubemap.getTexture());
	}

	private static class Renderable {

		public final TexturedModel model;
		public final Vector3f color;
		public final Matrix4f matrix;

		public Renderable(TexturedModel model, Vector3f color, Matrix4f matrix) {
			this.model = model;
			this.color = color;
			this.matrix = matrix;
		}

	}

}
