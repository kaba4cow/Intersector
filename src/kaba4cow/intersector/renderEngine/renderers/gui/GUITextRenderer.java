package kaba4cow.intersector.renderEngine.renderers.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import kaba4cow.engine.fontMeshCreator.FontType;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.renderEngine.renderers.AbstractRenderer;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.renderEngine.shaders.gui.GUITextShader;
import kaba4cow.intersector.utils.GameUtils;

public class GUITextRenderer extends AbstractRenderer {

	private Map<FontType, LinkedList<GUIText>> map = new HashMap<FontType, LinkedList<GUIText>>();

	public GUITextRenderer(Renderer renderer) {
		super(renderer, GUITextShader.get());
	}

	public void render(GUIText text) {
		FontType font = text.getFont();
		LinkedList<GUIText> list = map.get(font);
		if (list == null) {
			list = new LinkedList<GUIText>();
			map.put(font, list);
		}
		list.add(text);
	}

	@Override
	public void process() {
		if (map.isEmpty())
			return;
		startRendering();
		for (FontType font : map.keySet()) {
			GLUtils.activeTexture(0);
			GLUtils.bindTexture2D(font.getTextureAtlas());
			LinkedList<GUIText> list = map.get(font);
			while (!list.isEmpty()) {
				GUIText text = list.removeFirst();
				GLUtils.bindVertexArray(text.getMesh());
				GLUtils.enableVertexAttribArray(0);
				GLUtils.enableVertexAttribArray(1);
				getShader().color.loadValue(text.getColor());
				getShader().translation.loadValue(text.getPosition().x + 1f,
						text.getPosition().y);
				GLUtils.drawArraysTriangles(text.getVertexCount());
				GLUtils.disableVertexAttribArray(0);
				GLUtils.disableVertexAttribArray(1);
				GLUtils.unbindVertexArray();
			}
		}
		finishRendering();
	}

	@Override
	protected void startRendering() {
		shader.start();
		getShader().time.loadValue(GameUtils.getTime());
		GLUtils.enableBlending();
		GLUtils.alphaBlending();
		GLUtils.disableDepthTest();
		shader.start();
	}

	@Override
	protected void finishRendering() {
		shader.stop();
		GLUtils.disableBlending();
		GLUtils.enableDepthTest();
		map.clear();
	}

	@Override
	public GUITextShader getShader() {
		return (GUITextShader) shader;
	}

}
