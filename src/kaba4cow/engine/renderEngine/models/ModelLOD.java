package kaba4cow.engine.renderEngine.models;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.textures.TextureAtlas;
import kaba4cow.engine.toolbox.maths.Maths;

import org.lwjgl.util.vector.Vector3f;

public class ModelLOD<T extends TextureAtlas> {

	private final RawModel[] lod;
	private T texture;

	private final int levelCount;

	public ModelLOD(T texture, int levelCount) {
		this.lod = new RawModel[levelCount];
		this.texture = texture;
		this.levelCount = levelCount;
	}

	public int getLevel(Camera camera, Vector3f pos, float distance, float bias) {
		float distToCamera = Maths.dist(camera.getPos(), pos);
		float normIndex = Maths.norm(distToCamera, 0f, levelCount * distance);
		normIndex = Maths.bias(normIndex, bias);
		int index = (int) (normIndex * levelCount);
		if (index < 0)
			return 0;
		if (index >= levelCount)
			return levelCount - 1;
		return index;
	}

	public RawModel get(Camera camera, Vector3f pos, float distance, float bias) {
		return lod[getLevel(camera, pos, distance, bias)];
	}

	public RawModel get(int level) {
		if (level < 0)
			level = 0;
		if (level >= lod.length)
			level = lod.length - 1;
		return lod[level];
	}

	public void setLOD(int index, RawModel model) {
		if (index < 0 || index >= levelCount)
			return;
		lod[index] = model;
	}

	public T getTexture() {
		return texture;
	}

	public void setTexture(T texture) {
		this.texture = texture;
	}

	public int getLevelCount() {
		return levelCount;
	}

}
