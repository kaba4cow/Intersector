package kaba4cow.gui;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.Game;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.utils.GameUtils;
import kaba4cow.utils.RenderUtils;

public class HologramElement extends GUIElement {

	private GameObject object;

	private final Vector3f worldPosition;
	private final Direction direction;

	public HologramElement() {
		super();
		this.worldPosition = new Vector3f();
		this.direction = new Direction();
		text.setCenteredX(true).setCenteredY(false);
	}

	@Override
	public void render(Vector3f color, RendererContainer renderers) {
		if (object != null) {
			direction.reset();
			direction.rotate(Vectors.UP, 0.5f * GameUtils.getTime());
			worldPosition.set(-position.x, -position.y, 0f);
			float matrixScale = 0.75f * Maths.min(scale.x, scale.y)
					* (object.getSize() / object.getCollisionSize());
			Matrix4f matrix = direction.getMatrix(worldPosition, true,
					matrixScale, Game.getAspectRatio() * matrixScale,
					matrixScale);
			RenderUtils.renderHologramObject(object, color, 1f, matrix,
					renderers);
		}

		Renderer.USE_PROJ_VIEW_MATRICES = false;
		renderers.getHologramRenderer().process();
		Renderer.USE_PROJ_VIEW_MATRICES = true;

		text.setColor(2f * color.x, 2f * color.y, 2f * color.z);
		renderers.getGuiTextRenderer().render(text);
		color = Vectors.scale(color, 0.5f, null);
		renderers.getFrameRenderer().render(position, scale.x, scale.y, true,
				true, color);
	}

	public HologramElement setObject(GameObject object) {
		this.object = object;
		return this;
	}

}
