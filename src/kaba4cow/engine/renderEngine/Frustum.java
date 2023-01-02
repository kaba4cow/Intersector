package kaba4cow.engine.renderEngine;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector4f;

public class Frustum {

	private static final int VERTEX_COUNT = 8;

	private Renderer renderer;
	private Camera camera;

	private Vector4f[] originalVertices;
	private Vector4f[] frustumVertices;

	private float frustumLength;

	private float nearWidth;
	private float nearHeight;
	private float farWidth;
	private float farHeight;

	private Matrix4f transform;

	public Frustum(Renderer renderer) {
		this.renderer = renderer;
		this.camera = renderer.getCamera();
		this.originalVertices = new Vector4f[VERTEX_COUNT];
		this.frustumVertices = new Vector4f[VERTEX_COUNT];
		this.frustumLength = renderer.getFar();
		this.transform = new Matrix4f();
		initFrustumVertices();
		calculateOriginalVertices();
	}

	public void update() {
		updateCameraTransform();
		for (int i = 0; i < VERTEX_COUNT; i++)
			Matrix4f.transform(transform, originalVertices[i],
					frustumVertices[i]);
	}

	public void update(float distance) {
		if (frustumLength != distance) {
			frustumLength = distance;
			calculateOriginalVertices();
		}
		update();
	}

	private void calculateWidthsAndHeights() {
		float tan = Maths.tan(Maths.toRadians(0.5f * renderer.getFov()));
		farWidth = frustumLength * tan;
		nearWidth = renderer.getNear() * tan;
		farHeight = farWidth * MainProgram.getInvAspectRatio();
		nearHeight = nearWidth * MainProgram.getInvAspectRatio();
	}

	private void calculateOriginalVertices() {
		calculateWidthsAndHeights();
		for (int i = 0; i < VERTEX_COUNT; i++)
			originalVertices[i] = getVertex((i / 4) % 2 == 0, i % 2 == 0,
					(i / 2) % 2 == 0);
	}

	private Vector4f getVertex(boolean isNear, boolean positiveX,
			boolean positiveY) {
		Vector4f vertex = new Vector4f();
		Vector2f sizes = isNear ? new Vector2f(nearWidth, nearHeight)
				: new Vector2f(farWidth, farHeight);
		vertex.x = positiveX ? sizes.x : -sizes.x;
		vertex.y = positiveY ? sizes.y : -sizes.y;
		vertex.z = isNear ? -renderer.getNear() : -frustumLength;
		vertex.w = 1f;
		return vertex;
	}

	private void initFrustumVertices() {
		for (int i = 0; i < VERTEX_COUNT; i++)
			frustumVertices[i] = new Vector4f();
	}

	private void updateCameraTransform() {
		transform.setIdentity();
		transform.translate(camera.getPos());
		Matrix4f.mul(transform, camera.getDirection().getMatrix(null, true),
				transform);
	}

	public Vector4f getPoint5() {
		return frustumVertices[RNG.randomInt(4) + 4];
	}

}
