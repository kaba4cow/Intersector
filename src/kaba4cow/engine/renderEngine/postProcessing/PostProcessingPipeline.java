package kaba4cow.engine.renderEngine.postProcessing;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.engine.renderEngine.postProcessing.effects.Bypass;
import kaba4cow.engine.renderEngine.postProcessing.effects.PostProcessingEffect;
import kaba4cow.engine.toolbox.Models;
import kaba4cow.engine.utils.GLUtils;

public class PostProcessingPipeline {

	private List<PostProcessingEffect> list;
	private Bypass bypass;

	public PostProcessingPipeline() {
		this.list = new ArrayList<PostProcessingEffect>();
		this.bypass = new Bypass();
	}

	public final void render(int texture) {
		start();
		for (int i = 0; i < list.size(); i++) {
			PostProcessingEffect current = list.get(i);
			if (!current.isEnabled())
				continue;
			current.render(texture);
			texture = current.getOutputTexture();
		}
		bypass.render(texture);
		end();
	}

	public PostProcessingPipeline add(PostProcessingEffect newEffect) {
		if (newEffect != null)
			list.add(newEffect);
		return this;
	}

	public PostProcessingPipeline addAll(List<PostProcessingEffect> newEffects) {
		if (newEffects != null && !newEffects.isEmpty())
			list.addAll(newEffects);
		return this;
	}

	public List<PostProcessingEffect> getList() {
		return list;
	}

	public PostProcessingEffect get(int index) {
		if (index < 0 || index >= list.size())
			return null;
		return list.get(index);
	}

	public PostProcessingPipeline switchAllEffects(boolean enable) {
		for (int i = 0; i < list.size(); i++)
			if (enable)
				list.get(i).enable();
			else
				list.get(i).disable();
		return this;
	}

	public PostProcessingPipeline switchLastEffects(int index, boolean enable) {
		index = list.size() - index;
		if (index < 0 || index >= list.size())
			return this;
		for (int i = index; i < list.size(); i++)
			if (enable)
				list.get(i).enable();
			else
				list.get(i).disable();
		return this;
	}

	private void start() {
		GLUtils.bindVertexArray(Models.getGuiQuad().getVao());
		GLUtils.enableVertexAttribArray(0);
		GLUtils.disableDepthTest();
	}

	private void end() {
		GLUtils.enableDepthTest();
		GLUtils.disableVertexAttribArray(0);
		GLUtils.unbindVertexArray();
	}

}
