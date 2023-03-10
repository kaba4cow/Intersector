package kaba4cow.intersector.renderEngine.fborendering;

import java.util.LinkedList;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.assets.Cubemaps;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.Renderer.Projection;
import kaba4cow.engine.renderEngine.postProcessing.FrameBufferObject;
import kaba4cow.engine.renderEngine.renderers.CubemapRenderer;
import kaba4cow.engine.renderEngine.renderers.ParticleRenderer;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.particles.ParticleSystemManager;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.Settings;
import kaba4cow.intersector.galaxyengine.objects.GalacticObject;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.utils.GalaxyUtils;

public class SkyRendering {

	public static final int SIZE;

	public static final int RANGE = 15;

	private static LinkedList<Renderable> map;

	private static Renderer renderer;
	private static CubemapRenderer cubemapRenderer;
	private static ParticleRenderer particleRenderer;
	private static Direction direction;

	private static FrameBufferObject fbo;

	private static boolean generating;

	static {
		SIZE = Settings.CUBEMAP_SIZES[Settings.getCubemaps()];
		renderer = new Renderer(Projection.SQUARE, 90f, 1f, 10000f, 0f);
		cubemapRenderer = new CubemapRenderer(renderer);
		particleRenderer = new ParticleRenderer(renderer);
		direction = new Direction();
		fbo = new FrameBufferObject(SIZE, SIZE, FrameBufferObject.DEPTH_RENDER_BUFFER,
				FrameBufferObject.LINEAR_SAMPLING);
		map = new LinkedList<Renderable>();
		generating = false;
	}

	public static void render(SystemObject home, Cubemap cubemap) {
		for (int i = 0; i < 6; i++)
			map.add(new Renderable(i, home, cubemap));

//		new Thread("sky") {
//			@Override
//			public void run() {
		generating = true;
		Vector3f center = home.getPos();
		int offX = (int) center.x;
		int offY = (int) center.y;
		int offZ = (int) center.z;
		int range = RANGE / 2;
		float rangeSq = Maths.sqr(range);
		float distSq;
		float minDistSq = Maths.sqr(renderer.getNear());
		GalacticObject current;
		Particle particle;
		float scale;
		int x, y, z;
		for (y = -range; y < range; y++)
			for (x = -range; x < range; x++)
				for (z = -range; z < range; z++) {
					if (x == 0 && y == 0 && z == 0)
						continue;
					distSq = Maths.distSq(0f, 0f, 0f, x, y, z);
					if (distSq < minDistSq || distSq > rangeSq)
						continue;
					current = GalaxyUtils.generateSystem(offX + x, offY + y, offZ + z);
					if (current != null) {
						particle = current.getParticle(null);
						scale = Maths.map(distSq, minDistSq, rangeSq, 0.25f, 2f);
						particle.setScale(particle.getScale() * scale);
						particle.setTint(particle.getTint(), 30f);
						particle.setBrightness(0.5f);
					}
					current = GalaxyUtils.generateNebula(offX + x, offY + y, offZ + z);
					if (current != null)
						particle = current.getParticle(null);
				}
		generating = false;
//			}
//		}.start();
	}

	public static void process() {
		if (map.isEmpty() || generating)
			return;
		Renderable renderable = map.removeFirst();

		Cubemap skybox = Cubemaps.get("skybox");
		Vector3f center = renderable.system.getPos();
		int faceIndex = renderable.face;
		fbo.bindFrameBuffer();

		GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0,
				GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + faceIndex, renderable.cubemap.getTexture(), 0);

		switchToFace(center, faceIndex);
		renderer.prepare();

		cubemapRenderer.render(skybox);
		cubemapRenderer.process();
		GLUtils.clearDepthBuffer();

		ParticleSystemManager.update("MAP", 0f);
		ParticleSystemManager.render("MAP", particleRenderer);
		particleRenderer.process();

		if (map.isEmpty())
			ParticleSystemManager.clear("MAP");

		GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 1);
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	private static void switchToFace(Vector3f center, int faceIndex) {
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

		renderer.getCamera().orbit(center, 0f, 0f, 0f, 0f, 0f, direction);
	}

	public static int size() {
		return map.size();
	}

	private static class Renderable {

		public final int face;
		public final SystemObject system;
		public final Cubemap cubemap;

		public Renderable(int face, SystemObject system, Cubemap cubemap) {
			this.face = face;
			this.system = system;
			this.cubemap = cubemap;
		}

	}

}
