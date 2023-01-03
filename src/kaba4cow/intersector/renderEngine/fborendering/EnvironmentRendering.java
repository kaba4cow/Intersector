package kaba4cow.intersector.renderEngine.fborendering;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.GameSettings;
import kaba4cow.intersector.gameobjects.EnvironmentObject;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.utils.GameUtils;

public final class EnvironmentRendering {

	public static final int SIZE;

	public static final float SCALE = 0.0001f;

	private static List<EnvironmentObject> list;

	private static Renderer renderer;
	private static RendererContainer renderers;
	private static Direction direction;

	private static Cubemap cubemap;

	private static FrameBufferObject fbo;

	private EnvironmentRendering() {

	}

	static {
		SIZE = GameSettings.CUBEMAP_SIZES[GameSettings.getCubemaps()];
		renderer = new Renderer(Projection.SQUARE, 90f, 1f, 1000000000f, GameUtils.getPlayerPov().getAmbientLighting());
		renderers = new RendererContainer(renderer);
		list = new ArrayList<EnvironmentObject>();
		direction = new Direction();
		cubemap = new Cubemap(SIZE);
		fbo = new FrameBufferObject(SIZE, SIZE, FrameBufferObject.DEPTH_RENDER_BUFFER,
				FrameBufferObject.LINEAR_SAMPLING);
	}

	public static void render(EnvironmentObject object) {
		if (object == null || list.contains(object))
			return;
		list.add(object);
	}

	public static void process(Renderer renderer, Cubemap skybox) {
		fbo.bindFrameBuffer();

		Camera camera = renderer.getCamera();
		setLights(renderer.getLights(), camera);
		for (int i = 0; i < 6; i++) {
			GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
					GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, cubemap.getTexture(), 0);
			renderScene(camera, skybox, i);
		}
		list.clear();

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	private static void renderScene(Camera camera, Cubemap skybox, int faceIndex) {
		switchToFace(faceIndex, camera);
		renderer.prepare();

		renderers.getCubemapRenderer().render(skybox);
		renderers.getCubemapRenderer().process();
		GLUtils.clearDepthBuffer();

		for (int i = 0; i < list.size(); i++)
			list.get(i).render(camera);

		renderers.processModelRenderers(null);
	}

	private static void switchToFace(int faceIndex, Camera camera) {
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

		renderer.getCamera().orbit(Vectors.INIT3, 0f, 0f, 0f, 0f, 0f, direction);
	}

	private static void setLights(List<Light> lights, Camera camera) {
		List<Light> newLights = new ArrayList<Light>();
		for (int i = 0; i < lights.size(); i++) {
			Light light = new Light(lights.get(i));
			Vectors.scaleToOrigin(light.getPos(), camera.getPos(), SCALE, light.getPos());
			light.getAttenuation().y /= SCALE;
			newLights.add(light);
		}
		renderer.setLights(newLights);
	}

	public static Cubemap getCubemap() {
		return cubemap;
	}

	public static RendererContainer getRenderers() {
		return renderers;
	}

}
