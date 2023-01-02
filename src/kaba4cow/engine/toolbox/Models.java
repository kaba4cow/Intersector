package kaba4cow.engine.toolbox;

import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.models.RawModel;

public class Models {

	private static final RawModel particleQuad;
	private static final RawModel guiQuad;

	private static final RawModel cubemap;

	static {
		float[] particleVertices = { -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f };
		particleQuad = Loaders.loadToVAO(particleVertices, 2);

		float[] guiVertices = { -1f, 1f, -1f, -1f, 1f, 1f, 1f, -1f };
		guiQuad = Loaders.loadToVAO(guiVertices, 2);

		float[] skyboxVertices = { //
				-1f, 1f, -1f, -1f, -1f, -1f, //
				1f, -1f, -1f, 1f, -1f, -1f, //
				1f, 1f, -1f, -1f, 1f, -1f, //
				-1f, -1f, 1f, -1f, -1f, -1f, //
				-1f, 1f, -1f, -1f, 1f, -1f, //
				-1f, 1f, 1f, -1f, -1f, 1f, //
				1f, -1f, -1f, 1f, -1f, 1f, //
				1f, 1f, 1f, 1f, 1f, 1f, //
				1f, 1f, -1f, 1f, -1f, -1f, //
				-1f, -1f, 1f, -1f, 1f, 1f, //
				1f, 1f, 1f, 1f, 1f, 1f, //
				1f, -1f, 1f, -1f, -1f, 1f, //
				-1f, 1f, -1f, 1f, 1f, -1f, //
				1f, 1f, 1f, 1f, 1f, 1f, //
				-1f, 1f, 1f, -1f, 1f, -1f, //
				-1f, -1f, -1f, -1f, -1f, 1f, //
				1f, -1f, -1f, 1f, -1f, -1f, //
				-1f, -1f, 1f, 1f, -1f, 1f };
		for (int i = 0; i < skyboxVertices.length; i++)
			skyboxVertices[i] *= Cubemap.SCALE;
		cubemap = Loaders.loadToVAO(skyboxVertices, 3);
	}

	public static RawModel getParticleQuad() {
		return particleQuad;
	}

	public static RawModel getGuiQuad() {
		return guiQuad;
	}

	public static RawModel getCubemap() {
		return cubemap;
	}

}
