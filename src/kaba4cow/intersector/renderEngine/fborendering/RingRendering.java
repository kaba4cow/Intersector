package kaba4cow.intersector.renderEngine.fborendering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.Loaders;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.galaxyengine.TerrainGenerator;
import kaba4cow.intersector.gameobjects.GameObject;
import kaba4cow.intersector.renderEngine.renderers.generation.RingTextureRenderer;

public final class RingRendering {

	public static int SIZE = 256;

	private static final Map<ModelTexture, GameObject> textures = new HashMap<ModelTexture, GameObject>();

	private static final LinkedList<Renderable> map = new LinkedList<Renderable>();

	private static RingTextureRenderer ringRenderer;

	private static FrameBufferObject fbo;

	private RingRendering() {

	}

	static {
		Renderer renderer = new Renderer(Projection.SQUARE, 90f, 0.01f, 1000f,
				0f);
		ringRenderer = new RingTextureRenderer(renderer);
		fbo = new FrameBufferObject(SIZE, SIZE,
				FrameBufferObject.DEPTH_RENDER_BUFFER,
				FrameBufferObject.LINEAR_SAMPLING);
	}

	public static void render(ModelTexture texture,
			TerrainGenerator terrainGenerator) {
		if (texture == null || terrainGenerator == null)
			return;
		map.add(new Renderable(texture, terrainGenerator));
	}

	public static void process() {
		if (map.isEmpty())
			return;
		Renderable renderable = map.removeFirst();
		if (renderable == null)
			return;
		int texture = renderable.texture.getTexture();

		fbo.bindFrameBuffer();

		GLUtils.bindTexture2D(texture);
		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
				GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, texture, 0);
		ringRenderer.getRenderer().prepare();
		ringRenderer.render(renderable.terrainGenerator);
		ringRenderer.process();

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public static boolean isEmpty() {
		return map.isEmpty();
	}

	public static int size() {
		return map.size();
	}

	public static ModelTexture setTexture(GameObject planet) {
		ModelTexture result = null;
		for (ModelTexture texture : textures.keySet())
			if (result == null && textures.get(texture) == null)
				result = texture;
		if (result == null) {
			result = (ModelTexture) new ModelTexture(Loaders.loadTexture(
					"textures/RING", true)).setShininess(1f).setShineDamper(8f)
					.setTransparent(true).setAdditive(false);
		}
		textures.put(result, planet);
		return result;
	}

	private static class Renderable {

		public final ModelTexture texture;
		public final TerrainGenerator terrainGenerator;

		public Renderable(ModelTexture texture,
				TerrainGenerator terrainGenerator) {
			this.texture = texture;
			this.terrainGenerator = terrainGenerator;
		}

	}

}
