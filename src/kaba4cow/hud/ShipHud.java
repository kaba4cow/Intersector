package kaba4cow.hud;

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
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.toolbox.rng.RandomLehmer;
import kaba4cow.engine.utils.GLUtils;
import kaba4cow.files.InfosFile;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.gameobjects.GameObject;
import kaba4cow.gameobjects.cargo.Container;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.gameobjects.machines.Ship;
import kaba4cow.gameobjects.machines.Station;
import kaba4cow.gameobjects.targets.Target;
import kaba4cow.gameobjects.targets.TargetMode;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.renderEngine.fborendering.TextRendering;
import kaba4cow.toolbox.FontContainer;
import kaba4cow.toolbox.RawModelContainer;
import kaba4cow.utils.GameUtils;
import kaba4cow.utils.InfoUtils;
import kaba4cow.utils.RenderUtils;

public final class ShipHud {

	private static final float TEXT_SCALE1 = 1.7f;
	private static final float TEXT_SCALE2 = 1.2f;

	private static final TexturedModel HUD_MODEL1;
	private static final TexturedModel HUDTEXT_MODEL1;
	private static final TexturedModel HUD_MODEL2;
	private static final TexturedModel HUDTEXT_MODEL2;
	private static final Vector3f POSITION;
	private static final Vector2f TEX_OFFSET;
	private static final RendererContainer renderers;

	private static final GUIText[] TEXTS1 = new GUIText[3];
	private static final GUIText[] TEXTS2 = new GUIText[36];

	private static float elapsedTime;
	private static final RNG rng;

	private ShipHud() {

	}

	static {
		renderers = new RendererContainer(GameUtils.getHudPov());
		HUD_MODEL1 = new TexturedModel(RawModelContainer.get("MISC/hud1"), null);
		HUDTEXT_MODEL1 = new TexturedModel(RawModelContainer.get("MISC/hudtext1"),
				ModelTextureFile.get("HUDTEXT1").get());
		HUD_MODEL2 = new TexturedModel(RawModelContainer.get("MISC/hud2"), null);
		HUDTEXT_MODEL2 = new TexturedModel(RawModelContainer.get("MISC/hudtext2"),
				ModelTextureFile.get("HUDTEXT2").get());
		POSITION = new Vector3f();
		TEX_OFFSET = new Vector2f();
		elapsedTime = 0f;
		rng = new RandomLehmer(0l);

		String font = FontContainer.get("hud");

		int i = 0;
		for (int y = 0; y < 3; y++)
			TEXTS1[i++] = new GUIText("", font, new Vector2f(0f, -0.55f - 0.15f * y), TEXT_SCALE1, 1f, true);

		i = 0;
		for (int y = 0; y < 3; y++)
			TEXTS2[i++] = new GUIText("", font, new Vector2f(0f, -0.55f - 0.15f * y), TEXT_SCALE1, 1f, true);

		TEXTS2[i++] = new GUIText("", font, new Vector2f(0f, -0.15f), Maths.SQRT2 * TEXT_SCALE1, 1f, true);

		for (int x = 0; x < 4; x++)
			for (int y = 0; y < 4; y++)
				TEXTS2[i++] = new GUIText("", font, new Vector2f(-0.75f + 0.5f * x, 0.95f - 0.1f * y), TEXT_SCALE2, 1f,
						true);

		for (int x = 0; x < 4; x++)
			for (int y = 0; y < 4; y++)
				TEXTS2[i++] = new GUIText("", font, new Vector2f(-0.75f + 0.5f * x, 0.45f - 0.1f * y), TEXT_SCALE2, 1f,
						true);
	}

	public static void update(float dt) {
		elapsedTime += dt;
	}

	public static void renderHud(Ship ship) {
		if (ship == null || !ship.isAlive())
			return;
		GLUtils.clearDepthBuffer();
		boolean renderHolograms = ship.isHudEnabled();
		float hudChance = ship.getHudElapsedTime();
		rng.setSeed((long) (1000f * elapsedTime));
		if (ship.isDestroyed())
			hudChance = rng.nextFloat(0f, 0.5f);

		TargetMode mode = ship.getController().getMode();

		Matrix4f mat = Direction.INIT.getMatrix(Vectors.INIT3, true);
		Matrices.scale(mat, MainProgram.getAspectRatio(), 1f, 1f);
		renderers.getRenderer().getCamera().orbit(Vectors.INIT3, 1.75f, 0f, 0f);
		renderers.getRenderer().updateViewMatrix();

		GameObject target = null;
		if (renderHolograms) {
			if (target == null)
				target = ship.getController().getTargetEnemy();
			if (target == null)
				target = ship.getController().getTargetFriend();
			if (target == null)
				target = ship.getController().getTargetCargo();
			if (target == null)
				target = ship.getController().getTargetPlanet();
			renderTargets(ship, target, mode, hudChance);
		}

		HUD_MODEL1.setTexture(ship.getMetalModel().getTexture());
		HUD_MODEL2.setTexture(ship.getMetalModel().getTexture());
		renderHudText1(ship, target, mode, hudChance);
		renderers.getModelRenderer().render(HUD_MODEL1, null, mat);
		renderers.getModelRenderer().render(HUDTEXT_MODEL1, null, mat);
		if (ship.isHudInfoEnabled()) {
			renderHudText2(ship, target, mode, hudChance);
			renderers.getModelRenderer().render(HUD_MODEL2, null, mat);
			renderers.getModelRenderer().render(HUDTEXT_MODEL2, null, mat);
		}

		if (renderHolograms) {
			renderShipTarget(ship, target, mode, hudChance);
			renderParametersInfo(ship, mode, hudChance);
			renderShipInfo(ship, mode, hudChance);
			renderTargetInfo(ship, target, mode, hudChance);
		}

		renderers.processModelRenderers(null);
	}

	private static void renderHudText1(Ship ship, GameObject target, TargetMode mode, float hudChance) {
		if (!ship.isHudEnabled()) {
			TextRendering.process(HUDTEXT_MODEL1.getTexture().getTexture());
			return;
		}
		for (int i = 0; i < TEXTS1.length; i++)
			TEXTS1[i].setColor(mode.getColor());
		if (rng.nextFloat(0f, 1f) < hudChance) {
			if (target == null || !target.isTargetable()) {
				TEXTS1[0].setTextString("");
				TEXTS1[1].setTextString(ship.getController().hasLostTarget() ? "Target lost" : "No target selected");
				TEXTS1[2].setTextString("");
			} else {
				float distance = Maths.dist(ship.getPos(), target.getPos())
						- (ship.getCollisionSize() + target.getCollisionSize());
				float time = distance / ship.getVel().length();
				TEXTS1[0].setTextString(target.getTargetDescription());
				TEXTS1[1].setTextString("Type: " + target.getTargetType().getName());
				TEXTS1[2]
						.setTextString("Distance: " + InfoUtils.distance(distance) + " (" + InfoUtils.time(time) + ")");
			}
		} else
			for (int i = 0; i < 3; i++)
				TEXTS1[i].setTextString("");
		TextRendering.process(HUDTEXT_MODEL1.getTexture().getTexture(), TEXTS1);
	}

	private static void renderHudText2(Ship ship, GameObject target, TargetMode mode, float hudChance) {
		if (!ship.isHudEnabled()) {
			TextRendering.process(HUDTEXT_MODEL2.getTexture().getTexture());
			return;
		}
		for (int i = 0; i < TEXTS2.length; i++)
			TEXTS2[i].setColor(mode.getColor());

		if (rng.nextFloat(0f, 1f) < hudChance) {
			TEXTS2[0].setTextString(ship.getTargetDescription());
			TEXTS2[1].setTextString("Fraction: " + ship.getFraction().getFractionName());
			TEXTS2[2]
					.setTextString("Wing: " + (ship.getFlock() == null ? "none" : (ship.getFlock().size() + " units")));
			TEXTS2[3].setTextString("Ship information");
		} else
			for (int i = 0; i < 4; i++)
				TEXTS2[i].setTextString("");

		renderHudText2Info(ship, target, hudChance);

		TextRendering.process(HUDTEXT_MODEL2.getTexture().getTexture(), TEXTS2);
	}

	private static void renderHudText2Info(Ship ship, GameObject target, float hudChance) {
		if (rng.nextFloat(0f, 1f) < hudChance) {
			int i = 4;
			TEXTS2[i++].setTextString("Radius:");
			TEXTS2[i++].setTextString(InfoUtils.distance(ship.getCollisionSize()));
			TEXTS2[i++].setTextString("Speed: ");
			TEXTS2[i++].setTextString(InfoUtils.speed(ship.getVel().length()));

			TEXTS2[i++].setTextString("Hull:");
			TEXTS2[i++].setTextString(InfoUtils.slash(ship.getHealth(), ship.getMaxHealth()));
			TEXTS2[i++].setTextString("Shield: ");
			TEXTS2[i++].setTextString(InfoUtils.slash(ship.getShield(), ship.getMaxShield()));

			if (!ship.isHyperEngaged()) {
				TEXTS2[i++].setTextString("Hor. thrust:");
				TEXTS2[i++].setTextString(InfoUtils.percent(ship.getHorizontalControl().getThrust()));
				TEXTS2[i++].setTextString("Ver. thrust: ");
				TEXTS2[i++].setTextString(InfoUtils.percent(ship.getVerticalControl().getThrust()));
			} else {
				TEXTS2[i++].setTextString("Input thrust:");
				TEXTS2[i++].setTextString(InfoUtils.percent(ship.getHyperControl().getShift()));
				TEXTS2[i++].setTextString("Output thrust: ");
				TEXTS2[i++].setTextString(InfoUtils.percent(ship.getHyperControl().getThrust()));
			}

			TEXTS2[i++].setTextString("Cargo points:");
			TEXTS2[i++].setTextString(InfoUtils.slash(ship.getOccupiedCargos(), ship.getCargos().length));
			TEXTS2[i++].setTextString("Docking points: ");
			TEXTS2[i++].setTextString(InfoUtils.slash(ship.getOccupiedPorts(), ship.getPorts().length));

			for (int j = i; j < TEXTS2.length; j++)
				TEXTS2[j].setTextString("");

			if (target == null)
				return;

			TEXTS2[i++].setTextString("Radius:");
			TEXTS2[i++].setTextString(InfoUtils.distance(target.getCollisionSize()));
			TEXTS2[i++].setTextString("Speed: ");
			TEXTS2[i++].setTextString(InfoUtils.speed(target.getVel().length()));

			if (target instanceof Machine) {
				Machine machine = (Machine) target;

				TEXTS2[i++].setTextString("Hull:");
				TEXTS2[i++].setTextString(InfoUtils.slash(machine.getHealth(), machine.getMaxHealth()));
				TEXTS2[i++].setTextString("Shield: ");
				TEXTS2[i++].setTextString(InfoUtils.slash(machine.getShield(), machine.getMaxShield()));

				if (machine instanceof Ship) {
					Ship machine1 = (Ship) machine;
					if (!machine1.isHyperEngaged()) {
						TEXTS2[i++].setTextString("Hor. thrust:");
						TEXTS2[i++].setTextString(InfoUtils.percent(machine1.getHorizontalControl().getThrust()));
						TEXTS2[i++].setTextString("Ver. thrust: ");
						TEXTS2[i++].setTextString(InfoUtils.percent(machine1.getVerticalControl().getThrust()));
					} else {
						TEXTS2[i++].setTextString("Input thrust:");
						TEXTS2[i++].setTextString(InfoUtils.percent(machine1.getHyperControl().getShift()));
						TEXTS2[i++].setTextString("Output thrust: ");
						TEXTS2[i++].setTextString(InfoUtils.percent(machine1.getHyperControl().getThrust()));
					}
				} else if (machine instanceof Station) {
					TEXTS2[i++].setTextString("Hor. thrust:");
					TEXTS2[i++].setTextString(InfoUtils.percent(0f));
					TEXTS2[i++].setTextString("Ver. thrust: ");
					TEXTS2[i++].setTextString(InfoUtils.percent(0f));
				}

				TEXTS2[i++].setTextString("Cargo points:");
				TEXTS2[i++].setTextString(InfoUtils.slash(machine.getOccupiedCargos(), machine.getCargos().length));
				TEXTS2[i++].setTextString("Docking points: ");
				TEXTS2[i++].setTextString(InfoUtils.slash(machine.getOccupiedPorts(), machine.getPorts().length));
			} else if (target instanceof Container) {
				Container cargo = (Container) target;

				TEXTS2[i++].setTextString("Hull:");
				TEXTS2[i++].setTextString(InfoUtils.slash(cargo.getHealth(), cargo.getMaxHealth()));
				TEXTS2[i++].setTextString("Shield: ");
				TEXTS2[i++].setTextString(InfoUtils.slash(0f, 0f));
			}
		} else
			for (int i = 4; i < 36; i++)
				TEXTS2[i].setTextString("");
	}

	private static void renderTargets(Ship ship, GameObject target, TargetMode mode, float hudChance) {
		if (rng.nextFloat(0f, 1f) >= hudChance)
			return;
		String hologramColor = mode.getHologramColor();
		RawModel target0 = RawModelContainer.get("MISC/target0");
		RawModel target1 = RawModelContainer.get("MISC/target1");
		RawModel target2 = RawModelContainer.get("MISC/target2");
		RawModel target3 = RawModelContainer.get("MISC/target3");
		RawModel target4 = RawModelContainer.get("MISC/target4");
		RawModel galtarget1 = RawModelContainer.get("MISC/galtarget1");
		RawModel galtarget2 = RawModelContainer.get("MISC/galtarget2");
		ModelTexture hudTexture = ModelTextureFile.get("HUD").get();

		List<Target> targetList = Target.getOnScreenObjects(ship, mode.getTargets());
		Target directionTarget = Target.getDirectionTarget(ship);
		if (directionTarget != null)
			targetList.add(directionTarget);
		Target systemTarget = Target.getSystemTarget(ship);
		if (systemTarget != null)
			targetList.add(systemTarget);
		Matrix4f matRotated = null;
		Matrix4f matStatic = null;
		Matrix4f matTarget = null;
		Target current = null;
		GameObject object = null;
		Vector3f screenCoords = new Vector3f();
		Vector3f screenCoordsTarget = new Vector3f();
		float scale = 0.3f;
		float size = 0f;
		float brightness = getBrightness(target == null ? 1f : 0.5f, ship);
		for (int i = 0; i < targetList.size(); i++) {
			current = targetList.get(i);
			object = current.getObject();
			screenCoords.set(current.getScreenCoords());
			size = object == null ? 0.1f : Maths.limit(object.getCollisionSize() / screenCoords.z, 0.1f, 1f);
			screenCoords.z = 0f;
			matStatic = Direction.INIT.getMatrix(screenCoords, false, size);
			Matrices.scale(matStatic, 1f, MainProgram.getAspectRatio(), 1f);
			matRotated = new Matrix4f(matStatic);
			matRotated.rotate(elapsedTime, Vectors.FORWARD);
			if (object == null) {
				if (current == directionTarget)
					RenderUtils.renderHologram(hologramColor, hudTexture, target4, getBrightness(1f, ship), scale, null,
							matStatic, renderers);
				else if (current == systemTarget) {
					float sin = 0.5f * Maths.sin(Maths.TWO_PI * elapsedTime) + 0.5f;
					RenderUtils.renderHologram(hologramColor, hudTexture, galtarget1, getBrightness(sin, ship), scale,
							null, matRotated, renderers);
					RenderUtils.renderHologram(hologramColor, hudTexture, galtarget2, getBrightness(1f, ship), scale,
							null, matRotated, renderers);
				}
				continue;
			} else
				RenderUtils.renderHologram(hologramColor, hudTexture, target1, brightness, scale, null, matRotated,
						renderers);
			if (object instanceof Machine) {
				String machineHologramColor = "NEUTRAL";
				Machine machine = (Machine) object;
				if (machine.getFlock() != null && machine.getFlock() == ship.getFlock())
					machineHologramColor = "FLOCK";
				else if (machine.getController().getTargetEnemy() == ship
						|| ship.getFraction().isEnemy(machine.getFraction()))
					machineHologramColor = "ENEMY";
				RenderUtils.renderHologram(machineHologramColor, hudTexture, target3, brightness, scale, null,
						matStatic, renderers);
			}
			if (object == target) {
				RenderUtils.renderHologram(hologramColor, hudTexture, target0, getBrightness(2f, ship), scale, null,
						matRotated, renderers);
				screenCoordsTarget.set(current.getNextScreenCoords());
				screenCoordsTarget.z = 0f;
				matTarget = Direction.INIT.getMatrix(screenCoordsTarget, false, size);
				Matrices.scale(matTarget, 1f, MainProgram.getAspectRatio(), 1f);
				matTarget.rotate(elapsedTime, Vectors.FORWARD);
				RenderUtils.renderHologram(hologramColor, hudTexture, target2, getBrightness(2f, ship), scale, null,
						matTarget, renderers);
			}
		}
		Renderer.USE_PROJ_VIEW_MATRICES = false;
		renderers.getHologramRenderer().process();
		Renderer.USE_PROJ_VIEW_MATRICES = true;
	}

	private static void renderShipTarget(Ship ship, GameObject target, TargetMode mode, float hudChance) {
		if (rng.nextFloat(0f, 1f) >= hudChance || target == null)
			return;
		setPosition("center");
		Direction direction = target.getDirection().copy();
		direction.rotate(Vectors.UP, elapsedTime);
		Matrix4f mat = direction.getMatrix(POSITION, true);
		float scale = 0.3f * target.getSize() / target.getCollisionSize();
		Matrices.scale(mat, scale);

		String hologramColor = mode.getHologramColor();

		float brightness = getBrightness(1f, ship);

		RenderUtils.renderHologramObject(target, hologramColor, brightness, mat, renderers);
	}

	private static void renderShipInfo(Ship ship, TargetMode mode, float hudChance) {
		String hologramColor = mode.getHologramColor();
		ModelTexture hudTexture = ModelTextureFile.get("HUD").get();

		float scale = 0.3f;
		Matrix4f mat = null;

		RawModel circle0 = RawModelContainer.get("MISC/circle0");
		RawModel circle1 = RawModelContainer.get("MISC/circle1");

		float param1 = ship.getHealth() / ship.getMaxHealth();
		float param2 = ship.getShield() / ship.getMaxShield();
		float param3 = ship.getMaxAutoAimReload();
		float param4 = ship.getMaxManualReload();
		float param5 = ship.isHyperEngaged() ? ship.getHyperControl().getShift()
				: ship.getHorizontalControl().getThrust();
		float param6 = ship.isHyperEngaged() ? ship.getHyperControl().getShiftThrust()
				: ship.getVerticalControl().getThrust();
		float param7 = ship.getHyperControl().getCooldownProgress();
		float param8 = ship.getAfterburnerControl().getCooldownProgress();

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("left", 1);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param1);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("left", 0);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param2);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("left", 3);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param3);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("left", 2);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param4);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("right", 1);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param5);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("right", 0);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param6);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("right", 3);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param7);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("right", 2);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param8);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}
	}

	private static void renderTargetInfo(Ship ship, GameObject target, TargetMode mode, float hudChance) {
		String hologramColor = mode.getHologramColor();
		ModelTexture hudTexture = ModelTextureFile.get("HUD").get();

		float scale = 0.3f;
		Matrix4f mat = null;

		RawModel circle0 = RawModelContainer.get("MISC/circle0");
		RawModel circle1 = RawModelContainer.get("MISC/circle1");

		float param1 = target == null ? 0f : target.getTargetParameter1();
		float param2 = target == null ? 0f : target.getTargetParameter2();

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("top", 0);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param1);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("top", 1);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			setTexOffset(param2);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle0, getBrightness(1f, ship), 1f, null, mat,
					renderers);
			RenderUtils.renderHologram(hologramColor, hudTexture, circle1, getBrightness(1f, ship), 1f, TEX_OFFSET, mat,
					renderers);
		}
	}

	private static void renderParametersInfo(Ship ship, TargetMode mode, float hudChance) {
		String hologramColor = mode.getHologramColor();
		ModelTexture hudTexture = ModelTextureFile.get("HUD").get();

		float scale = 0.15f;
		float brightness = 1f;
		Matrix4f mat = null;

		RawModel turret = RawModelContainer.get("MISC/turret");
		RawModel rocket = RawModelContainer.get("MISC/rocket");
		RawModel modes = null;
		switch (mode) {
		case BATTLE:
			modes = RawModelContainer.get("MISC/mode1");
			break;
		case SYSTEM:
			modes = RawModelContainer.get("MISC/mode2");
			break;
		case GALACTIC:
			modes = RawModelContainer.get("MISC/mode3");
			break;
		}
		RawModel afterburn = RawModelContainer.get("MISC/afterburn");
		RawModel hyper = RawModelContainer.get("MISC/hyper");

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("p", 0);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			brightness = getBrightness(ship.isTurretsEnabled(), ship);
			RenderUtils.renderHologram(hologramColor, hudTexture, turret, brightness, 1f, null, mat, renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("p", 1);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			brightness = getBrightness(ship.isLaunchersEnabled(), ship);
			RenderUtils.renderHologram(hologramColor, hudTexture, rocket, brightness, 1f, null, mat, renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("p", 2);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			RenderUtils.renderHologram(hologramColor, hudTexture, modes, getBrightness(1f, ship), 1f, null, mat,
					renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("p", 3);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			brightness = getBrightness(ship.isHyperEngaged(), ship);
			RenderUtils.renderHologram(hologramColor, hudTexture, hyper, brightness, 1f, null, mat, renderers);
		}

		if (rng.nextFloat(0f, 1f) < hudChance) {
			setPosition("p", 4);
			mat = Direction.INIT.getMatrix(POSITION, true, scale);
			brightness = getBrightness(ship.isAfterburnerEngaged(), ship);
			RenderUtils.renderHologram(hologramColor, hudTexture, afterburn, brightness, 1f, null, mat, renderers);
		}
	}

	private static float getBrightness(boolean value, Ship ship) {
		return getBrightness(value ? 1f : 0.1f, ship);
	}

	private static float getBrightness(float value, Ship ship) {
		return value * Maths.sqr(Maths.min(0.5f * ship.getHudElapsedTime(), 1f));
	}

	private static void setPosition(String tag, int... index) {
		DataFile node = InfosFile.hud.data().node(tag);
		if (index.length == 1)
			node = node.node(index[0]);
		POSITION.x = MainProgram.getAspectRatio() * node.getFloat(0);
		POSITION.y = node.getFloat(1);
		POSITION.z = node.getFloat(2);
	}

	private static void setTexOffset(float value) {
		TEX_OFFSET.y = value >= 0f ? -1f : 1f;
		TEX_OFFSET.x = 0.5f * Maths.limit(1f - Maths.abs(value));
	}

}
