package kaba4cow.renderEngine.renderers.planets;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.Shaders;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.renderEngine.shaders.planets.RingShader;
import kaba4cow.toolbox.RawModelContainer;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class RingRenderer extends AbstractRenderer {

	private static RawModel MODEL;

	private List<Renderable> map = new ArrayList<Renderable>();

	public RingRenderer(Renderer renderer) {
		super(renderer, RingShader.get());
	}

	public void render(ModelTexture texture, Vector3f color, Matrix4f matrix) {
		if (texture == null || color == null || matrix == null)
			return;
		Renderable renderable = new Renderable(texture, color, matrix);
		map.add(renderable);
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		RingShader shader = getShader();
		prepareTexturedModel(shader);
		for (int i = 0; i < map.size(); i++) {
			ModelTexture modelTexture = map.get(i).texture;
			GLUtils.enableBlending();
			if (modelTexture.isAdditive())
				GLUtils.additiveBlending();
			else
				GLUtils.alphaBlending();
			shader.texInfo.loadValue(modelTexture.getShininess(),
					modelTexture.getShineDamper());
			GLUtils.activeTexture(0);
			GLUtils.bindTexture2D(modelTexture.getTexture());
			shader.transformationMatrix.loadValue(map.get(i).matrix);
			shader.color.loadValue(map.get(i).color);
			int vertexCount = MODEL.getVertexCount();
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
		RingShader shader = getShader();
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
		shader.connectTextureUnits();
	}

	@Override
	protected void finishRendering() {
		shader.stop();
	}

	@Override
	public RingShader getShader() {
		return (RingShader) shader;
	}

	private void prepareTexturedModel(RingShader shader) {
		if (MODEL == null)
			MODEL = RawModelContainer.get("MISC/ring");
		GLUtils.disableCulling();
		GLUtils.disableDepthMask();
		GLUtils.bindVertexArray(MODEL.getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableVertexAttribArray(1);
		GLUtils.enableVertexAttribArray(2);
	}

	private void unbindTexturedModel() {
		GLUtils.disableVertexAttribArray(0);
		GLUtils.disableVertexAttribArray(1);
		GLUtils.disableVertexAttribArray(2);
		GLUtils.unbindVertexArray();
	}

	private static class Renderable {

		public final ModelTexture texture;
		public final Vector3f color;
		public final Matrix4f matrix;

		public Renderable(ModelTexture texture, Vector3f color, Matrix4f matrix) {
			this.texture = texture;
			this.color = color;
			this.matrix = matrix;
		}

	}

}
