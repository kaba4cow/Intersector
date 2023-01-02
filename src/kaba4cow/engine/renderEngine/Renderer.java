package kaba4cow.engine.renderEngine;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.toolbox.maths.Matrices;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class Renderer {

	public static boolean USE_PROJ_VIEW_MATRICES = true;

	private static final List<Renderer> list = new ArrayList<Renderer>();

	private float fov;
	private float near;
	private float far;

	private Vector3f blankColor;

	private Matrix4f projectionMatrix;
	private Matrix4f viewMatrix;

	private Camera camera;

	private List<AbstractRenderer> renderers;

	private List<Light> lights;
	private Vector3f ambientLighting;

	private boolean wireframe;

	private Projection projection;

	public Renderer(Projection projection, float fov, float near, float far,
			Vector3f blankColor) {
		this.projection = projection;
		this.camera = new Camera();
		this.renderers = new ArrayList<AbstractRenderer>();
		this.wireframe = false;
		this.lights = new ArrayList<Light>();
		this.ambientLighting = new Vector3f();
		this.blankColor = blankColor;
		this.createProjectionMatrix(near, far, fov);
		list.add(this);
	}

	public Renderer(Projection projection, float fov, float near, float far,
			float blankColor) {
		this(projection, fov, near, far, new Vector3f(blankColor, blankColor,
				blankColor));
	}

	public void createProjectionMatrix(float near, float far, float fov) {
		this.near = near;
		this.far = far;
		this.fov = fov;
		this.projectionMatrix = projection.createProjectionMatrix(near, far,
				fov);
	}

	public void updateProjectionMatrix() {
		createProjectionMatrix(near, far, fov);
	}

	public static void updateAspectRatio() {
		for (int i = 0; i < list.size(); i++)
			list.get(i).updateProjectionMatrix();
	}

	public Renderer prepare() {
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(blankColor.x, blankColor.y, blankColor.z, 1f);
		updateViewMatrix();
		return this;
	}

	public Renderer updateViewMatrix() {
		viewMatrix = camera.getViewMatrix();
		return this;
	}

	public Renderer addRenderer(AbstractRenderer renderer) {
		if (renderer.getRenderer() == this && !renderers.contains(renderer))
			renderers.add(renderer);
		return this;
	}

	public Renderer removeRenderer(AbstractRenderer renderer) {
		renderers.remove(renderer);
		return this;
	}

	public List<Light> getLights() {
		return lights;
	}

	public Renderer setLights(List<Light> lights) {
		this.lights = lights;
		return this;
	}

	public Renderer clearLights() {
		lights.clear();
		return this;
	}

	public Renderer addLight(Light light) {
		if (light != null)
			lights.add(light);
		return this;
	}

	public Vector3f getAmbientLighting() {
		return ambientLighting;
	}

	public Renderer setAmbientLighting(Vector3f ambientLighting) {
		this.ambientLighting = ambientLighting;
		return this;
	}

	public Renderer setAmbientLighting(float ambientLighting) {
		this.ambientLighting = new Vector3f(ambientLighting, ambientLighting,
				ambientLighting);
		return this;
	}

	public boolean isWireframe() {
		return wireframe;
	}

	public Renderer setWireframe(boolean wireframe) {
		this.wireframe = wireframe;
		return this;
	}

	public Vector3f getBlankColor() {
		return blankColor;
	}

	public Renderer setBlankColor(Vector3f blankColor) {
		this.blankColor = blankColor;
		return this;
	}

	public Matrix4f getProjectionMatrix() {
		return projectionMatrix;
	}

	public Matrix4f getViewMatrix() {
		if (viewMatrix == null)
			viewMatrix = camera.getViewMatrix();
		return viewMatrix;
	}

	public Matrix4f getInvViewMatrix() {
		return Matrices.inverse(getViewMatrix());
	}

	public Camera getCamera() {
		return camera;
	}

	public Renderer setCamera(Camera newCamera) {
		if (newCamera != null)
			this.camera = newCamera;
		return this;
	}

	public float getFov() {
		return fov;
	}

	public Renderer setFov(float fov) {
		if (this.fov != fov) {
			this.fov = fov;
			this.updateProjectionMatrix();
		}
		return this;
	}

	public float getNear() {
		return near;
	}

	public Renderer setNear(float near) {
		if (this.near != near) {
			this.near = near;
			this.updateProjectionMatrix();
		}
		return this;
	}

	public float getFar() {
		return far;
	}

	public Renderer setFar(float far) {
		if (this.far != far) {
			this.far = far;
			this.updateProjectionMatrix();
		}
		return this;
	}

	public static enum Projection {

		DEFAULT {
			@Override
			public Matrix4f createProjectionMatrix(float near, float far,
					float fov) {
				return Matrices.createPerspectiveProjectionMatrix(near, far,
						fov, MainProgram.getAspectRatio());
			}
		}, //
		SQUARE {
			@Override
			public Matrix4f createProjectionMatrix(float near, float far,
					float fov) {
				return Matrices.createPerspectiveProjectionMatrix(near, far,
						fov, 1f);
			}
		}, //
		ORTHO {
			@Override
			public Matrix4f createProjectionMatrix(float near, float far,
					float fov) {
				return Matrices.createOrthographicProjectionMatrix(far, fov,
						MainProgram.getAspectRatio());
			}
		};

		public abstract Matrix4f createProjectionMatrix(float near, float far,
				float fov);

	}

}
