package kaba4cow.intersector.states;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.Input;
import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.toolbox.CameraManager;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.engine.utils.WindowUtils;
import kaba4cow.files.SystemFile;
import kaba4cow.intersector.Intersector;
import kaba4cow.intersector.galaxyengine.objects.NebulaObject;
import kaba4cow.intersector.galaxyengine.objects.PlanetObject;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.gameobjects.machines.Ship;
import kaba4cow.intersector.gameobjects.targets.Target;
import kaba4cow.intersector.hud.MapHud;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.renderers.HologramRenderer;
import kaba4cow.intersector.utils.GalaxyUtils;
import kaba4cow.intersector.utils.GameUtils;

public class MapState extends State {

	private Camera galCamera;
	private CameraManager galCameraManager;

	private Vector3f galCursor;
	private CameraManager galCursorManager;

	private SystemObject home;
	private SystemObject target;

	private LinkedList<Vector3f> queue;
	private List<SystemObject> systems;
	private List<Particle> freeSystemParticles;
	private List<NebulaObject> nebulas;
	private List<Particle> freeNebulaParticles;

	private Camera sysCamera;
	private CameraManager sysCameraManager;

	private int sysCursorX;
	private int sysCursorY;

	private List<Planet> planets;
	private PlanetObject[][] systemMap;

	private boolean showSystemMap;

	private Ship ship;

	public MapState() {
		super("MAP");
	}

	@Override
	public boolean isInitializable() {
		return true;
	}

	@Override
	public void create() {
		galCamera = new Camera();
		galCameraManager = new CameraManager().setDistParameters(0.5f, 10f, 0.002f, 4f).setPointParameters(2f)
				.setPitchParameters(-Maths.QUARTER_PI, Maths.QUARTER_PI, 0.1f, 8f).setYawParameters(0.1f, 8f);
		galCameraManager.setInitParameters(1f, 0f, 0f).reset().resetParameters();
		galCameraManager.setInitKey(Keyboard.KEY_X);

		galCursorManager = new CameraManager().setPointParameters(8f);
		galCursorManager.reset().resetParameters();
		galCursorManager.setInitKey(-1);

		sysCamera = new Camera();
		sysCameraManager = new CameraManager().setDistParameters(2f, 16f, 0.002f, 4f).setPointParameters(8f)
				.setPitchParameters(0f, 0f, 0f, 0f).setYawParameters(0f, 0f);
		sysCameraManager.setInitParameters(16f, 0f, 0f).reset().resetParameters();
		sysCameraManager.setInitKey(Keyboard.KEY_X);

		galCursor = new Vector3f();
		home = null;
		target = null;

		queue = new LinkedList<Vector3f>();
		systems = new ArrayList<SystemObject>();
		freeSystemParticles = new ArrayList<Particle>();
		nebulas = new ArrayList<NebulaObject>();
		freeNebulaParticles = new ArrayList<Particle>();
	}

	@Override
	public void init() {
		ship = States.game.getPlayer();

		galCursor.set(0f, 0f, 0f);
		home = ship.getWorld().getSystem();
		target = ship.getController().getTargetSystem();

		galCursor.set(home.offX, home.offY, home.offZ);
		galCursorManager.moveTo(galCursor);
		galCursorManager.reset().resetParameters();

		game.clearParticles(this);
		queue.clear();
		systems.clear();
		freeSystemParticles.clear();
		nebulas.clear();
		freeNebulaParticles.clear();
		updateGalacticView();
	}

	@Override
	public void onStateSwitch() {
		game.setRenderer(GameUtils.getMapPov());
	}

	@Override
	public void update(float dt) {
		while (!queue.isEmpty())
			processPosition(queue.removeFirst());

		if (Input.isKeyDown(Keyboard.KEY_M))
			switchMaps();
		if (Input.isKeyDown(Keyboard.KEY_ESCAPE))
			closeMap();

		if (Input.isKeyDown(Keyboard.KEY_U)) {
			game.clearParticles(this);
			systems.clear();
			freeSystemParticles.clear();
			nebulas.clear();
			freeNebulaParticles.clear();
			updateGalacticView();
		}

		if (showSystemMap) {
			galCameraManager.resetParameters();
			galCursorManager.resetParameters();

			for (int i = 0; i < planets.size(); i++)
				planets.get(i).update(dt);

			if (Input.isKey(Keyboard.KEY_C))
				sysCameraManager.setDist(sysCameraManager.getMinDist());

			moveSystemCursor();
			sysCameraManager.moveTo(systemMap[sysCursorX][sysCursorY].getPos());
			sysCameraManager.update(true, dt);
		} else {
			sysCursorX = 0;
			sysCursorY = 0;
			sysCameraManager.moveTo(Vectors.INIT3);
			sysCameraManager.reset().resetParameters();

			galCursorManager.moveTo(galCursor);
			galCursorManager.update(false, dt);
			galCameraManager.update(true, dt);

			if (moveGalacticCursor(dt))
				updateGalacticView();

			if (Input.isKey(Keyboard.KEY_T)) {
				target = selectGalacticTarget();
				ship.getController().setTargetSystem(target);
			}
			if (Input.isKeyDown(Keyboard.KEY_O))
				home = GalaxyUtils.generateSystem(
						SystemFile.getList().get(RNG.randomInt(SystemFile.getList().size())).getFileName());
		}

		game.updateParticles(this, dt);
		HologramRenderer.update(dt);
	}

	@Override
	public void render(RendererContainer renderers) {
		if (showSystemMap) {
			game.getRenderer().setCamera(sysCamera);
			sysCamera.orbit(sysCameraManager.getPoint(), 0f, 0f, 1f, sysCameraManager);
			game.getRenderer().prepare();

			for (int i = 0; i < planets.size(); i++)
				planets.get(i).renderMap(renderers);

			renderers.processModelRenderers(null);

			MapHud.renderSysHud(ship, planets, target, systemMap[sysCursorX][sysCursorY]);
		} else {
			game.getRenderer().setCamera(galCamera);
			galCamera.orbit(galCursorManager.getPoint(), 0f, 0f, 1f, galCameraManager);
			game.getRenderer().prepare();

			game.renderParticles(this);

			MapHud.renderGalHud(ship, systems, home, target, selectGalacticTarget());
		}

		game.doPostProcessing();
	}

	public void moveSystemCursor() {
		int dirX = 0;
		int dirY = 0;
		if (Input.isKeyDown(Keyboard.KEY_A))
			dirX--;
		if (Input.isKeyDown(Keyboard.KEY_D))
			dirX++;
		if (Input.isKeyDown(Keyboard.KEY_W))
			dirY--;
		if (Input.isKeyDown(Keyboard.KEY_S))
			dirY++;

		PlanetObject parent = systemMap[sysCursorX][sysCursorY].parent;

		int nextX = sysCursorX + dirX;
		int nextY = sysCursorY + dirY;
		if (nextX < 0)
			nextX = 0;
		if (nextX >= systemMap.length)
			nextX = systemMap.length - 1;
		if (nextY < 0)
			nextY = 0;
		if (nextY >= systemMap[0].length)
			nextY = systemMap[0].length - 1;

		if (sysCursorX != nextX) {
			if (systemMap[nextX][sysCursorY] == null || nextX > 1 && systemMap[nextX][sysCursorY].parent != parent)
				nextX = sysCursorX;
		}
		sysCursorX = nextX;

		if (sysCursorY != nextY) {
			if (sysCursorX == 0) {
				nextY -= dirY;
				if (dirY < 0) {
					for (int p = nextY + dirY; p >= 0; p--)
						if (systemMap[0][p] != null) {
							nextY = p;
							break;
						}
				} else {
					for (int p = nextY + dirY; p < systemMap[0].length; p++)
						if (systemMap[0][p] != null) {
							nextY = p;
							break;
						}
				}
			} else if (systemMap[sysCursorX][nextY] == null || systemMap[sysCursorX][nextY].parent != parent
					&& systemMap[sysCursorX][nextY].parent.parent != parent
					&& systemMap[sysCursorX][nextY].parent != parent.parent)
				nextY = sysCursorY;
		}
		sysCursorY = nextY;
	}

	public void switchMaps() {
		showSystemMap = !showSystemMap;

		if (target == null) {
			showSystemMap = false;
			return;
		}

		if (showSystemMap) {
			game.clearParticles(this);
			systems.clear();
			freeSystemParticles.clear();
			nebulas.clear();
			freeNebulaParticles.clear();

			sysCursorX = 0;
			sysCursorY = 0;
			planets = GalaxyUtils.createPlanets(null, target);

			Planet planet;
			for (int i = 0; i < planets.size(); i++) {
				planet = planets.get(i);
				planet.getPos().set(planet.getPlanetObject().getPos());
				if (planet.getLight() != null)
					game.getRenderer().getLights().get(0).getColor().set(planet.getLight().getColor());
			}

			int width = 0;
			int height = 0;

			for (int i = 0; i < planets.size(); i++) {
				width = Maths.max(width, 1 + planets.get(i).getPlanetObject().mapX);
				height = Maths.max(height, 1 + planets.get(i).getPlanetObject().mapY);
			}
			systemMap = new PlanetObject[width][height];
			for (int i = 0; i < planets.size(); i++) {
				PlanetObject current = planets.get(i).getPlanetObject();
				systemMap[current.mapX][current.mapY] = current;
			}

			target.print();
		} else
			updateGalacticView();
	}

	public void closeMap() {
		if (showSystemMap)
			switchMaps();
		else
			Intersector.switchState(prevState);
	}

	public SystemObject selectGalacticTarget() {
		List<Target> targetList = Target.getOnGalMapObjects(systems, game.getRenderer());
		float minDist = Float.POSITIVE_INFINITY;
		float maxSize = 0f;
		float mouseX = WindowUtils.toNormalizedX(Mouse.getX());
		float mouseY = WindowUtils.toNormalizedY(Mouse.getY());
		Target closest = null;
		Target current = null;
		for (int i = 0; i < targetList.size(); i++) {
			current = targetList.get(i);
			float size = 0.35f * current.getObject().getSize() / current.getScreenCoords().z;
			if (current.getScreenCoords().z > minDist || current.getObject().getSize() < maxSize
					|| Maths.dist(mouseX, mouseY, current.getScreenCoords().x, current.getScreenCoords().y) > size)
				continue;
			minDist = current.getScreenCoords().z;
			maxSize = size;
			closest = current;
		}
		if (closest == null)
			return null;
		return (SystemObject) closest.getObject();
	}

	private void updateGalacticView() {
		int offX = (int) galCursor.x;
		int offY = (int) galCursor.y;
		int offZ = (int) galCursor.z;
		int range = 9;
		float rangeSq = range * range;
		SystemObject system = null;
		NebulaObject nebula = null;
		for (int i = systems.size() - 1; i >= 0; i--) {
			system = systems.get(i);
			if (Maths.distSq(offX, 2f * offY, offZ, system.posX, 2f * system.posY, system.posZ) >= rangeSq) {
				systems.remove(i);
				freeSystemParticles.add(system.getParticle(null).removeFromSystem());
			}
		}
		for (int i = nebulas.size() - 1; i >= 0; i--) {
			nebula = nebulas.get(i);
			if (Maths.distSq(offX, 2f * offY, offZ, nebula.posX, 2f * nebula.posY, nebula.posZ) >= rangeSq) {
				nebulas.remove(i);
				freeNebulaParticles.add(nebula.getParticle(null).removeFromSystem());
			}
		}
		for (int y = -range / 2; y < range / 2; y++)
			for (int x = -range; x < range; x++)
				for (int z = -range; z < range; z++) {
					if (Maths.distSq(0f, 0f, 0f, x, 2f * y, z) < rangeSq)
						queue.add(new Vector3f(offX + x, offY + y, offZ + z));
				}
	}

	private void processPosition(Vector3f position) {
		int x = (int) position.x;
		int y = (int) position.y;
		int z = (int) position.z;
		SystemObject system = GalaxyUtils.generateSystem(x, y, z);
		if (system != null && !systems.contains(system)) {
			systems.add(system);
			system.getParticle(freeSystemParticles);
		}
		NebulaObject nebula = GalaxyUtils.generateNebula(x, y, z);
		if (nebula != null && !nebulas.contains(nebula)) {
			nebulas.add(nebula);
			nebula.getParticle(freeNebulaParticles);
		}
	}

	private boolean moveGalacticCursor(float dt) {
		int prevX = (int) galCursor.x;
		int prevY = (int) galCursor.y;
		int prevZ = (int) galCursor.z;

		float speed = 4f * dt;
		if (Input.isKey(Keyboard.KEY_LSHIFT))
			speed *= 16f;

		Vector3f forward = galCamera.getDirection().getForward();
		forward.y = 0f;
		forward.normalise();

		Vector3f right = galCamera.getDirection().getRight();
		right.y = 0f;
		right.normalise();

		Vector3f up = Vectors.UP;

		if (Input.isKey(Keyboard.KEY_S))
			Vectors.addScaled(galCursor, forward, speed, galCursor);
		if (Input.isKey(Keyboard.KEY_W))
			Vectors.subScaled(galCursor, forward, speed, galCursor);

		if (Input.isKey(Keyboard.KEY_D))
			Vectors.addScaled(galCursor, right, speed, galCursor);
		if (Input.isKey(Keyboard.KEY_A))
			Vectors.subScaled(galCursor, right, speed, galCursor);

		if (Input.isKey(Keyboard.KEY_R))
			Vectors.addScaled(galCursor, up, speed, galCursor);
		if (Input.isKey(Keyboard.KEY_F))
			Vectors.subScaled(galCursor, up, speed, galCursor);

		if (Input.isKey(Keyboard.KEY_C)) {
			if (Input.isKey(Keyboard.KEY_LSHIFT))
				galCursor.set(home.offX, home.offY, home.offZ);
			else if (target != null)
				galCursor.set(target.offX, target.offY, target.offZ);
		}

		return prevX != (int) galCursor.x || prevY != (int) galCursor.y || prevZ != (int) galCursor.z;
	}

}
