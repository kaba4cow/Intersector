package kaba4cow.states;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.Game;
import kaba4cow.engine.Input;
import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.toolbox.CameraManager;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.files.StationFile;
import kaba4cow.galaxyengine.objects.SystemObject;
import kaba4cow.gameobjects.Fraction;
import kaba4cow.gameobjects.Planet;
import kaba4cow.gameobjects.World;
import kaba4cow.gameobjects.machinecontrollers.shipcontrollers.ShipAIController;
import kaba4cow.gameobjects.machinecontrollers.shipcontrollers.ShipPlayerController;
import kaba4cow.gameobjects.machinecontrollers.shipcontrollers.ShipStaticController;
import kaba4cow.gameobjects.machinecontrollers.stationcontrollers.StationAIController;
import kaba4cow.gameobjects.machines.Ship;
import kaba4cow.gameobjects.machines.Station;
import kaba4cow.gameobjects.machines.classes.ShipClass;
import kaba4cow.hud.ShipHud;
import kaba4cow.menu.MenuElement;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.renderEngine.renderers.HologramRenderer;
import kaba4cow.renderEngine.renderers.ThrustRenderer;
import kaba4cow.toolbox.flocking.Flock;
import kaba4cow.toolbox.flocking.FlockManager;
import kaba4cow.utils.GalaxyUtils;
import kaba4cow.utils.GameUtils;

public class GameState extends State {

	private Camera camera;
	private CameraManager cameraManager;

	private World world;

	private Ship ship;

	public GameState() {
		super("GAME");
	}

	@Override
	public boolean isInitializable() {
		if (prevState instanceof MapState || prevState instanceof PauseState)
			return false;
		return true;
	}

	@Override
	public void create() {
		camera = new Camera();
		cameraManager = new CameraManager().setDistParameters(1f, 4f, 0.002f, 1f).setPointParameters(16f)
				.setPitchParameters(-Maths.HALF_PI, Maths.HALF_PI, 0.2f, 8f).setYawParameters(0.2f, 8f);
		cameraManager.setInitParameters(2f, 0.1f * Maths.HALF_PI, 0f).reset().resetParameters();
		cameraManager.setInitKey(Keyboard.KEY_X);
	}

	@Override
	public void init() {
		world = new World();
		reset();
	}

	@Override
	public void onStateSwitch() {
		Game.switchScene(false);
		game.setRenderer(GameUtils.getPlayerPov());
		game.getRenderer().setCamera(camera);
		game.switchPostProcessing(true);
	}

	@Override
	public void update(float dt) {
		if (Input.isKeyDown(Keyboard.KEY_M) && !ship.isDestroyed() && ship.isAlive())
			Game.switchState(States.map);
		else if (Input.isKeyDown(Keyboard.KEY_ESCAPE)) {
			MenuElement.playSound();
			Game.switchState(States.pause);
		}

		cameraManager.update(true, dt);

		ShipHud.update(dt);
		ThrustRenderer.update(dt);
		HologramRenderer.update(dt);
		game.updateParticles(this, dt);
		world.update(dt, ship.getPos());

		if (Input.isKeyDown(Keyboard.KEY_RETURN))
			reset();
	}

	@Override
	public void render(RendererContainer renderers) {
		camera.orbit(ship.getPos(), 0f, 0f, Maths.SQRT2 * ship.getSize(), cameraManager, ship.getDirection());
		game.getRenderer().prepare();

		world.render(renderers);
		game.renderParticles(this);
		ShipHud.renderHud((Ship) ship);

		game.doPostProcessing();
	}

	public void reset() {
		FlockManager.clear();
		world.clear();

		SystemObject system = GalaxyUtils.getRandomSystem();
		List<Planet> planets = world.create(system);
		Planet planet = planets.get(RNG.randomInt(planets.size()));

		// createFlock(planet, Fraction.getRandom().getFractionFile()
		// .getFileName(), false, false);
		// createFlock(planet, Fraction.getRandom().getFractionFile()
		// .getFileName(), false, false);

		Fraction fraction0 = Fraction.getRandom();
		Flock flock0 = new Flock(false);
		ship = new Ship(world, fraction0, fraction0.getRandomShip(ShipClass.CORVETTE), randVec(planet),
				new ShipPlayerController());
		ship.setFlock(flock0);
		Fraction fraction1 = Fraction.getRandom();
		Flock flock1 = new Flock(false);
		for (int i = 0; i < 2; i++) {
			Ship ship = new Ship(world, fraction1, fraction1.getRandomShip(ShipClass.SHUTTLE), randVec(planet),
					new ShipStaticController());
			ship.setFlock(flock1);
		}

		// new Station(world, Fraction.get("FRACTION3"),
		// StationFile.get("STATION3"), randVec(planet),
		// new StationAIController());

		// for (int i = 0; i < 16; i++)
		// new Cargo(world, CargoType.ALUMINIUM, randVec(planet));

		world.update(0f, null);
	}

	public Ship getPlayer() {
		return ship;
	}

	public void createFlock(Planet planet, String fractionName, boolean addPlayer, boolean addStation) {
		Fraction fraction = Fraction.get(fractionName);
		Flock flock = new Flock(false);
		for (int i = 0; i < 1; i++)
			new Ship(world, fraction, fraction.getRandomShip(ShipClass.CRUISER), randVec(planet),
					new ShipAIController()).setFlock(flock);
		for (int i = 0; i < 1; i++)
			new Ship(world, fraction, fraction.getRandomShip(ShipClass.FRIGATE), randVec(planet),
					new ShipAIController()).setFlock(flock);
		for (int i = 0; i < 2; i++)
			new Ship(world, fraction, fraction.getRandomShip(ShipClass.CORVETTE), randVec(planet),
					new ShipAIController()).setFlock(flock);
		for (int i = 0; i < 4; i++)
			new Ship(world, fraction, fraction.getRandomShip(ShipClass.FIGHTER), randVec(planet),
					new ShipAIController()).setFlock(flock);

		ship = (Ship) flock.randomMachine();

		if (addPlayer)
			ship = (Ship) new Ship(world, fraction, fraction.getRandomShip(ShipClass.CORVETTE), randVec(planet),
					new ShipPlayerController()).setFlock(flock);

		if (addStation)
			new Station(world, fraction, StationFile.get("STATION2"), randVec(planet), new StationAIController())
					.setFlock(flock);
	}

	public static Vector3f randVec(Planet planet) {
		Vector3f pos = planet == null ? new Vector3f() : planet.getPos();
		float off = planet == null ? 0f : 2f * planet.getSize();
		float x = off + pos.x + 1000f * RNG.randomFloat(-1f, 1f);
		float y = pos.y + 100f * RNG.randomFloat(-1f, 1f);
		float z = off + pos.z + 1000f * RNG.randomFloat(-1f, 1f);
		return new Vector3f(x, y, z);
	}

}
