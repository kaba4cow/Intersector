package kaba4cow.intersector.hud;

import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.MainProgram;
import kaba4cow.engine.renderEngine.Renderer;
import kaba4cow.engine.renderEngine.models.GUIText;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.intersector.files.ModelTextureFile;
import kaba4cow.intersector.galaxyengine.objects.PlanetObject;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.gameobjects.machines.Ship;
import kaba4cow.intersector.gameobjects.targets.Target;
import kaba4cow.intersector.gameobjects.targets.TargetMode;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.fborendering.TextRendering;
import kaba4cow.intersector.toolbox.containers.FontContainer;
import kaba4cow.intersector.toolbox.containers.RawModelContainer;
import kaba4cow.intersector.utils.GalaxyUtils;
import kaba4cow.intersector.utils.GameUtils;
import kaba4cow.intersector.utils.InfoUtils;
import kaba4cow.intersector.utils.RenderUtils;

public final class MapHud {

	private static final float TEXT_SCALE = 2f;

	private static final TexturedModel HUD_MODEL;
	private static final TexturedModel SCREEN_MODEL;
	private static final TexturedModel HUDTEXT_MODEL;
	private static final RendererContainer renderers;

	private static final GUIText[] TEXTS = new GUIText[9];

	private MapHud() {

	}

	static {
		renderers = new RendererContainer(GameUtils.getHudPov());
		HUD_MODEL = new TexturedModel(RawModelContainer.get("MISC/maphud"), null);
		SCREEN_MODEL = new TexturedModel(RawModelContainer.get("MISC/maphudscreen"),
				ModelTextureFile.get("SCREEN").get());
		HUDTEXT_MODEL = new TexturedModel(RawModelContainer.get("MISC/maphudtext"),
				ModelTextureFile.get("HUDTEXT1").get());

		String font = FontContainer.get("hud");

		int i = 0;
		for (int y = 0; y < 3; y++)
			TEXTS[i++] = new GUIText("", font, new Vector2f(0f, -0.55f - 0.15f * y), TEXT_SCALE, 1f, true);

		for (int y = 0; y < 3; y++)
			TEXTS[i++] = new GUIText("", font, new Vector2f(0f, -0.05f - 0.15f * y), TEXT_SCALE, 1f, true);

		for (int y = 0; y < 3; y++)
			TEXTS[i++] = new GUIText("", font, new Vector2f(0f, 0.45f - 0.15f * y), TEXT_SCALE, 1f, true);
	}

	public static void renderGalHud(Ship ship, List<SystemObject> systems, SystemObject home, SystemObject target,
			SystemObject cursor) {
		startRendering();
		renderGalHudText(home, target);
		renderGalTargets(systems, home, target, cursor);
		finishRendering(ship);
	}

	public static void renderSysHud(Ship ship, List<Planet> planets, SystemObject system, PlanetObject target) {
		startRendering();
		renderSysHudText(system, target);
		renderSysTargets(planets, target);
		finishRendering(ship);
	}

	private static void startRendering() {
		GLUtils.clearDepthBuffer();
		Matrix4f mat = Direction.INIT.getMatrix(Vectors.INIT3, true);
		Matrices.scale(mat, MainProgram.getAspectRatio(), 1f, 1f);
		renderers.getRenderer().getCamera().orbit(Vectors.INIT3, 1.75f, 0f, 0f);
		renderers.getRenderer().updateViewMatrix();
	}

	private static void finishRendering(Ship ship) {
		Matrix4f mat = Direction.INIT.getMatrix(Vectors.INIT3, true);
		Matrices.scale(mat, MainProgram.getAspectRatio(), 1f, 1f);
		HUD_MODEL.setTexture(ship.getMetalModel().getTexture());
		renderers.getModelRenderer().render(SCREEN_MODEL, null, mat);
		renderers.getModelRenderer().render(HUD_MODEL, null, mat);
		renderers.getModelRenderer().render(HUDTEXT_MODEL, null, mat);
		renderers.processModelRenderers(null);
	}

	private static void renderSysHudText(SystemObject system, PlanetObject target) {
		for (int i = 0; i < TEXTS.length; i++)
			TEXTS[i].setTextString("").setColor(TargetMode.GALACTIC.getColor());

		TEXTS[0].setTextString("Name: " + target.name);
		TEXTS[1].setTextString("Type: " + target.file.getName());
		TEXTS[2].setTextString("Radius: " + InfoUtils.distance(target.radius));

		TEXTS[3].setTextString("Name: " + system.getTargetDescription());
		TEXTS[4].setTextString("Sector: [" + system.posX / GalaxyUtils.SECTOR_SIZE + ", "
				+ -system.posY / GalaxyUtils.SECTOR_SIZE + ", " + system.posZ / GalaxyUtils.SECTOR_SIZE + "]");
		TEXTS[5].setTextString("Position: [" + system.posX + ", " + -system.posY + ", " + system.posZ + "]");

		TEXTS[6].setTextString("Star: " + system.mainObjects[0].file.getName());
		TEXTS[7].setTextString("System size: " + system.systemSize());
		TEXTS[8].setTextString("Allegiance: " + system.allegiance());

		TextRendering.process(HUDTEXT_MODEL.getTexture().getTexture(), TEXTS);
	}

	private static void renderGalHudText(SystemObject home, SystemObject target) {
		for (int i = 0; i < TEXTS.length; i++)
			TEXTS[i].setTextString("").setColor(TargetMode.GALACTIC.getColor());

		if (home != null) {
			TEXTS[6].setTextString("Name: " + home.getTargetDescription());
			TEXTS[7].setTextString("Sector: [" + home.posX / GalaxyUtils.SECTOR_SIZE + ", "
					+ -home.posY / GalaxyUtils.SECTOR_SIZE + ", " + home.posZ / GalaxyUtils.SECTOR_SIZE + "]");
			TEXTS[8].setTextString("Position: [" + home.posX + ", " + -home.posY + ", " + home.posZ + "]");
		}

		if (target == null) {
			TEXTS[1].setTextString("No target selected");
		} else {
			TEXTS[0].setTextString("Distance: " + InfoUtils.dist(home, target));
			TEXTS[1].setTextString("Star: " + target.mainObjects[0].file.getName());
			TEXTS[2].setTextString("System size: " + target.systemSize());

			TEXTS[3].setTextString("Name: " + target.getTargetDescription());
			TEXTS[4].setTextString("Sector: [" + target.posX / GalaxyUtils.SECTOR_SIZE + ", "
					+ -target.posY / GalaxyUtils.SECTOR_SIZE + ", " + target.posZ / GalaxyUtils.SECTOR_SIZE + "]");
			TEXTS[5].setTextString("Position: [" + target.posX + ", " + -target.posY + ", " + target.posZ + "]");
		}

		TextRendering.process(HUDTEXT_MODEL.getTexture().getTexture(), TEXTS);
	}

	private static void renderGalTargets(List<SystemObject> systems, SystemObject home, SystemObject target,
			SystemObject cursor) {
		String hologramColorInfo = TargetMode.GALACTIC.getHologramColor();
		RawModel target0 = RawModelContainer.get("MISC/galtarget0");
		RawModel target1 = RawModelContainer.get("MISC/galtarget1");
		RawModel target2 = RawModelContainer.get("MISC/galtarget2");
		RawModel target3 = RawModelContainer.get("MISC/systarget2");
		ModelTexture hudTexture = ModelTextureFile.get("HUD").get();

		List<Target> targetList = Target.getOnGalMapObjects(systems, GameUtils.getMapPov());
		Matrix4f matStatic = null;
		Matrix4f matRotated = null;
		Target current = null;
		SystemObject object = null;
		Vector3f screenCoords = new Vector3f();
		Vector3f arScale = new Vector3f(1f, MainProgram.getAspectRatio(), 1f);
		float scale = 0.3f;
		float size = 0f;
		for (int i = 0; i < targetList.size(); i++) {
			current = targetList.get(i);
			object = (SystemObject) current.getObject();
			screenCoords.set(current.getScreenCoords());
			size = 0.5f * Maths.limit(object.getSize() / screenCoords.z, 0.1f, 1f);
			screenCoords.z = 0f;
			matStatic = Direction.INIT.getMatrix(screenCoords, false, size);
			matStatic.scale(arScale);
			matRotated = new Matrix4f(matStatic);
			matRotated.rotate(GameUtils.getTime(), Vectors.FORWARD);
			if (object.equals(home))
				RenderUtils.renderHologram(hologramColorInfo, hudTexture, target0, 2f, scale, null, matRotated,
						renderers);
			if (object.equals(target))
				RenderUtils.renderHologram(hologramColorInfo, hudTexture, target1, 2f, scale, null, matRotated,
						renderers);
			if (object == cursor)
				RenderUtils.renderHologram(hologramColorInfo, hudTexture, target2, 2f, scale, null, matRotated,
						renderers);
			if (object.fraction != null)
				RenderUtils.renderHologram(object.fraction.getFractionFile().getMainColor(), hudTexture, target3, 2f,
						scale, null, matStatic, renderers);
		}
		Renderer.USE_PROJ_VIEW_MATRICES = false;
		renderers.getHologramRenderer().process();
		Renderer.USE_PROJ_VIEW_MATRICES = true;
	}

	private static void renderSysTargets(List<Planet> planets, PlanetObject target) {
		RawModel target2 = RawModelContainer.get("MISC/systarget2");
		ModelTexture hudTexture = ModelTextureFile.get("HUD").get();

		List<Target> targetList = Target.getOnSysMapObjects(planets, GameUtils.getMapPov());
		Matrix4f matStatic = null;
		// Matrix4f matRotated = null;
		Target current = null;
		PlanetObject object = null;
		Vector3f screenCoords = new Vector3f();
		Vector3f arScale = new Vector3f(1f, MainProgram.getAspectRatio(), 1f);
		float scale = 0.3f;
		float size = 0f;
		for (int i = 0; i < targetList.size(); i++) {
			current = targetList.get(i);
			object = ((Planet) current.getObject()).getPlanetObject();
			screenCoords.set(current.getScreenCoords());
			size = Maths.PI * object.getSize() / screenCoords.z;
			screenCoords.z = 0f;
			matStatic = Direction.INIT.getMatrix(screenCoords, false, size);
			matStatic.scale(arScale);
			// matRotated = new Matrix4f(matStatic);
			// matRotated.rotate(elapsedTime, Vectors.FORWARD);
			if (object.hasStation())
				RenderUtils.renderHologram(object.system.fraction.getFractionFile().getMainColor(), hudTexture, target2,
						2f, scale, null, matStatic, renderers);
		}
		Renderer.USE_PROJ_VIEW_MATRICES = false;
		renderers.getHologramRenderer().process();
		Renderer.USE_PROJ_VIEW_MATRICES = true;
	}

}
