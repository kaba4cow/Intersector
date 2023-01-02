package kaba4cow.renderEngine.renderers.gui;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.toolbox.Models;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.renderEngine.shaders.gui.FrameShader;
import kaba4cow.utils.GameUtils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FrameRenderer extends AbstractRenderer {

	private List<Renderable> map = new ArrayList<Renderable>();

	public FrameRenderer(Renderer renderer) {
		super(renderer, FrameShader.get());
	}

	public void render(Vector2f pos, float scaleX, float scaleY,
			boolean centerX, boolean centerY, Vector3f color) {
		if (pos == null || scaleX == 0f || scaleY == 0f || color == null)
			return;
		map.add(new Renderable(pos, scaleX, scaleY, centerX, centerY, color));
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		RawModel quad = Models.getGuiQuad();
		Vector2f position = new Vector2f();
		Vector2f scale = new Vector2f();
		for (int i = 0; i < map.size(); i++) {
			Renderable current = map.get(i);
			scale.set(current.scaleX, current.scaleY);
			position.set(current.pos.x, current.pos.y);
			if (!current.centerX)
				position.x -= scale.x;
			if (!current.centerY)
				position.y -= scale.y;
			Matrix4f matrix = Matrices.createTransformationMatrix(position,
					scale);
			getShader().transformationMatrix.loadValue(matrix);
			getShader().color.loadValue(current.color);
			GLUtils.drawArraysTriangleStrip(quad.getVertexCount());
		}
		map.clear();
		finishRendering();
	}

	@Override
	protected void startRendering() {
		shader.start();
		getShader().time.loadValue(GameUtils.getTime());
		RawModel quad = Models.getGuiQuad();
		GLUtils.bindVertexArray(quad.getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableBlending();
		GLUtils.additiveBlending();
		GLUtils.disableDepthTest();
	}

	@Override
	protected void finishRendering() {
		GLUtils.enableDepthTest();
		GLUtils.disableBlending();
		GLUtils.disableVertexAttribArray(0);
		GLUtils.unbindVertexArray();
		shader.stop();
	}

	@Override
	public FrameShader getShader() {
		return (FrameShader) shader;
	}

	private static class Renderable {

		public Vector2f pos;
		public float scaleX;
		public float scaleY;
		public boolean centerX;
		public boolean centerY;

		public Vector3f color;

		public Renderable(Vector2f pos, float scaleX, float scaleY,
				boolean centerX, boolean centerY, Vector3f color) {
			this.pos = pos;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.centerX = centerX;
			this.centerY = centerY;
			this.color = color;
		}

	}

}
