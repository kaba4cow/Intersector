package kaba4cow.intersector.renderEngine.renderers;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.renderEngine.textures.TextureAtlas;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.files.ModelTextureFile;
import kaba4cow.intersector.renderEngine.shaders.HologramShader;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class HologramRenderer extends AbstractRenderer {

	private static final float SPEED = 0.21f;

	private Map<RawModel, LinkedList<Renderable>> map = new HashMap<RawModel, LinkedList<Renderable>>();

	private static float elapsedTime = 0f;

	private static TextureAtlas TEXTURE;

	public HologramRenderer(Renderer renderer) {
		super(renderer, HologramShader.get());
	}

	public static void update(float dt) {
		elapsedTime += dt;
	}

	public void render(RawModel rawModel, Vector3f color,
			TextureAtlas colorTexture, Matrix4f matrix, float brightness,
			float scale, Vector2f texOffset) {
		if (rawModel == null || matrix == null)
			return;
		Renderable renderable = new Renderable(rawModel, color, colorTexture,
				null, matrix, brightness, scale, texOffset);
		add(renderable);
	}

	public void render(RawModel rawModel, Vector3f color, Cubemap colorCube,
			Matrix4f matrix, float brightness, float scale, Vector2f texOffset) {
		if (rawModel == null || matrix == null)
			return;
		Renderable renderable = new Renderable(rawModel, color, null,
				colorCube, matrix, brightness, scale, texOffset);
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
		HologramShader shader = getShader();
		for (RawModel model : map.keySet()) {
			prepareRawModel(model, shader);
			LinkedList<Renderable> list = map.get(model);
			int vertexCount = model.getVertexCount();
			while (!list.isEmpty()) {
				Renderable renderable = list.removeFirst();
				prepareTextures(renderable, shader);
				shader.transformationMatrix.loadValue(renderable.matrix);
				shader.brightness.loadValue(renderable.brightness);
				shader.scale.loadValue(renderable.scale);
				shader.texOffset.loadValue(renderable.texOffset);
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
		HologramShader shader = getShader();
		shader.start();
		shader.time.loadValue(SPEED * elapsedTime);
		if (Renderer.USE_PROJ_VIEW_MATRICES) {
			shader.projectionMatrix.loadValue(renderer.getProjectionMatrix());
			shader.viewMatrix.loadValue(renderer.getViewMatrix());
		} else {
			shader.projectionMatrix.loadValue(new Matrix4f());
			shader.viewMatrix.loadValue(new Matrix4f());
		}
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
	public HologramShader getShader() {
		return (HologramShader) shader;
	}

	private void prepareRawModel(RawModel model, HologramShader shader) {
		if (TEXTURE == null)
			TEXTURE = ModelTextureFile.get("HOLOGRAM").get();
		GLUtils.bindVertexArray(model.getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableVertexAttribArray(1);
		GLUtils.activeTexture(2);
		GLUtils.bindTexture2D(TEXTURE.getTexture());
	}

	private void prepareTextures(Renderable renderable, HologramShader shader) {
		TextureAtlas colorTexture = renderable.colorTexture;
		Cubemap colorCube = renderable.colorCube;
		shader.color.loadValue(renderable.color);
		float usesTexture = 1f;
		if (colorTexture == null)
			usesTexture = -1f;
		if (colorTexture == null && colorCube == null)
			usesTexture = 0f;
		shader.usesTexture.loadValue(usesTexture);
		GLUtils.activeTexture(0);
		GLUtils.bindTexture2D(colorTexture == null ? 0 : colorTexture
				.getTexture());
		GLUtils.activeTexture(1);
		GLUtils.bindTextureCubemap(colorCube == null ? 0 : colorCube
				.getTexture());
		shader.connectTextureUnits();
	}

	private void unbindTexturedModel() {
		GLUtils.disableVertexAttribArray(0);
		GLUtils.disableVertexAttribArray(1);
		GLUtils.unbindVertexArray();
	}

	private static class Renderable {

		public final RawModel model;
		public final Vector3f color;
		public final TextureAtlas colorTexture;
		public final Cubemap colorCube;
		public final Matrix4f matrix;
		public final float brightness;
		public final float scale;
		public final Vector2f texOffset;

		public Renderable(RawModel model, Vector3f color,
				TextureAtlas colorTexture, Cubemap colorCube, Matrix4f matrix,
				float brightness, float scale, Vector2f texOffset) {
			this.model = model;
			this.color = color;
			this.colorTexture = colorTexture;
			this.colorCube = colorCube;
			this.matrix = matrix;
			this.scale = scale;
			this.brightness = brightness;
			this.texOffset = texOffset == null ? Vectors.INIT2 : new Vector2f(
					texOffset);
		}

	}

}
