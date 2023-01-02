package kaba4cow.renderEngine.fborendering;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.galaxyengine.TerrainGenerator;
import kaba4cow.renderEngine.renderers.generation.TerrainTextureRenderer;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

public class TerrainRendering {

	public static final int[] CUBEMAPS = { 64, 128, 256 };

	private static final LinkedList<Renderable> map = new LinkedList<Renderable>();

	private static Direction direction;

	private static final TerrainTextureRenderer terrainTextureRenderer;

	private static Map<Integer, FrameBufferObject> fbos;

	private TerrainRendering() {

	}

	static {
		Renderer renderer = new Renderer(Projection.SQUARE, 90f, 0.01f, 100f,
				0f);
		terrainTextureRenderer = new TerrainTextureRenderer(renderer);
		direction = new Direction();

		fbos = new HashMap<Integer, FrameBufferObject>();
		for (int i = 0; i < CUBEMAPS.length; i++)
			fbos.put(CUBEMAPS[i], new FrameBufferObject(CUBEMAPS[i],
					CUBEMAPS[i], FrameBufferObject.DEPTH_RENDER_BUFFER,
					FrameBufferObject.LINEAR_SAMPLING));
	}

	public static void render(Cubemap terrain, TerrainGenerator terrainGenerator) {
		if (terrain == null || terrainGenerator == null)
			return;
		for (int i = 0; i < 6; i++)
			map.add(new Renderable(i, terrain, terrainGenerator));
	}

	public static void process() {
		if (map.isEmpty())
			return;
		Renderable renderable = map.removeFirst();
		if (renderable == null)
			return;

		fbos.get(renderable.terrain.getSize()).bindFrameBuffer();

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER,
				GL30.GL_COLOR_ATTACHMENT0, GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X
						+ renderable.face, renderable.terrain.getTexture(), 0);
		switchToFace(renderable.face);
		terrainTextureRenderer.getRenderer().prepare();

		terrainTextureRenderer.render(renderable.face,
				renderable.terrainGenerator,
				Direction.INIT.getMatrix(Vectors.INIT3, true));
		terrainTextureRenderer.process();

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public static boolean isEmpty() {
		return map.isEmpty();
	}

	public static int size() {
		return map.size();
	}

	public static Cubemap getCubemap(int size) {
		if (size < 0)
			size = 0;
		if (size >= CUBEMAPS.length)
			size = CUBEMAPS.length - 1;
		return new Cubemap(CUBEMAPS[size]);
	}

	private static void switchToFace(int faceIndex) {
		float pitch = 0f;
		float yaw = 0f;
		float roll = 0f;
		switch (faceIndex) {
		case 0:
			pitch = 0f;
			yaw = 90f;
			roll = 180f;
			break;
		case 1:
			pitch = 0f;
			yaw = -90f;
			roll = 180f;
			break;
		case 2:
			pitch = -90f;
			yaw = 0f;
			roll = -180f;
			break;
		case 3:
			pitch = 90f;
			yaw = 0f;
			roll = -180f;
			break;
		case 4:
			pitch = 0f;
			yaw = 0f;
			roll = 180f;
			break;
		case 5:
			pitch = 0f;
			yaw = 180f;
			roll = 180f;
			break;
		}

		pitch = Maths.toRadians(pitch);
		yaw = Maths.toRadians(yaw);
		roll = Maths.toRadians(roll);

		direction = new Direction();
		direction.rotate(Vectors.RIGHT, pitch);
		direction.rotate(Vectors.UP, yaw);
		direction.rotate(Vectors.FORWARD, roll);

		terrainTextureRenderer.getRenderer().getCamera()
				.orbit(Vectors.INIT3, 0f, 0f, 0f, 0f, 0f, direction);
	}

	private static class Renderable {

		public final int face;
		public final Cubemap terrain;
		public final TerrainGenerator terrainGenerator;

		public Renderable(int face, Cubemap cubemap,
				TerrainGenerator terrainGenerator) {
			this.face = face;
			this.terrain = cubemap;
			this.terrainGenerator = terrainGenerator;
		}

	}

}
