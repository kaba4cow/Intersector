package kaba4cow.engine.renderEngine.renderers;

import java.util.LinkedList;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.shaders.GUIShader;
import kaba4cow.engine.renderEngine.textures.GUITexture;
import kaba4cow.engine.toolbox.Models;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.utils.GLUtils;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

public class GUIRenderer extends AbstractRenderer {

	private LinkedList<GUITexture> map = new LinkedList<GUITexture>();

	public GUIRenderer(Renderer renderer) {
		super(renderer, GUIShader.get());
	}

	public void render(GUITexture texture) {
		if (texture != null)
			map.add(texture);
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		RawModel quad = Models.getGuiQuad();
		Vector2f position = new Vector2f();
		Vector2f scale = new Vector2f();
		while (!map.isEmpty()) {
			GUITexture current = map.removeFirst();
			if (current == null || current.getPosition() == null
					|| current.getScale() == null)
				continue;
			GLUtils.activeTexture(0);
			GLUtils.bindTexture2D(current.getTexture());
			scale.set(current.getScale().x * MainProgram.getInvAspectRatio(),
					current.getScale().y);
			position.set(current.getPosition().x, current.getPosition().y);
			if (!current.isCentered()) {
				position.x += scale.x;
				position.y -= scale.y;
			}
			Matrix4f matrix = Matrices.createTransformationMatrix(position,
					scale);
			getShader().transformationMatrix.loadValue(matrix);
			getShader().progress.loadValue(current.getProgress());
			GLUtils.drawArraysTriangleStrip(quad.getVertexCount());
		}
		finishRendering();
	}

	@Override
	protected void startRendering() {
		shader.start();
		RawModel quad = Models.getGuiQuad();
		GLUtils.bindVertexArray(quad.getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.enableBlending();
		GLUtils.alphaBlending();
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
	public GUIShader getShader() {
		return (GUIShader) shader;
	}

}
