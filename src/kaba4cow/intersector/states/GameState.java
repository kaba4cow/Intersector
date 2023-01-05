package kaba4cow.intersector.states;

import java.util.List;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.Input;
import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.toolbox.CameraManager;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.Intersector;
import kaba4cow.intersector.galaxyengine.objects.SystemObject;
import kaba4cow.intersector.gameobjects.Fraction;
import kaba4cow.intersector.gameobjects.Planet;
import kaba4cow.intersector.gameobjects.World;
import kaba4cow.intersector.gameobjects.cargo.Cargo;
import kaba4cow.intersector.gameobjects.cargo.CargoType;
import kaba4cow.intersector.gameobjects.machines.Ship;
import kaba4cow.intersector.gameobjects.machines.classes.ShipClass;
import kaba4cow.intersector.gameobjects.machines.controllers.shipcontrollers.ShipAIController;
import kaba4cow.intersector.gameobjects.machines.controllers.shipcontrollers.ShipPlayerController;
import kaba4cow.intersector.gameobjects.machines.controllers.shipcontrollers.ShipStaticController;
import kaba4cow.intersector.hud.ShipHud;
import kaba4cow.intersector.menu.MenuElement;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.renderers.HologramRenderer;
import kaba4cow.intersector.renderEngine.renderers.ThrustRenderer;
import kaba4cow.intersector.toolbox.flocking.Flock;
import kaba4cow.intersector.toolbox.flocking.FlockManager;
import kaba4cow.intersector.utils.GalaxyUtils;
import kaba4cow.intersector.utils.GameUtils;

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
		Intersector.switchScene(false);
		game.setRenderer(GameUtils.getPlayerPov());
		game.getRenderer().setCamera(camera);
		game.switchPostProcessing(true);
	}

	@Override
	public void update(float dt) {
		if (Input.isKeyDown(Keyboard.KEY_M) && !ship.isDestroyed() && ship.isAlive())
			Intersector.switchState(States.map);
		else if (Input.isKeyDown(Keyboard.KEY_ESCAPE)) {
			MenuElement.playSound();
			Intersector.switchState(States.pause);
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

		SystemObject system = GalaxyUtils.generateSystem("BALAFARI");
		List<Planet> planets = world.create(system);
		Planet planet = planets.get(RNG.randomInt(planets.size()));

		// createFlock(planet, Fraction.getRandom().getFractionFile()
		// .getFileName(), false, false);
		// createFlock(planet, Fraction.getRandom().getFractionFile()
		// .getFileName(), false, false);

		Fraction fraction0 = Fraction.getRandom();
		ship = new Ship(world, fraction0, fraction0.getRandomShip(ShipClass.CORVETTE), randVec(planet),
				new ShipPlayerController());
		Fraction fraction1 = Fraction.getRandom();
		for (int i = 0; i < 2; i++)
			new Ship(world, fraction1, fraction1.getRandomShip(ShipClass.SHUTTLE), randVec(planet),
					new ShipStaticController());

		// new Station(world, Fraction.get("FRACTION3"),
		// StationFile.get("STATION3"), randVec(planet),
		// new StationAIController());

		for (int i = 0; i < 16; i++)
			new Cargo(world, CargoType.ALUMINIUM, randVec(planet));

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
	}

	public static Vector3f randVec(Planet planet) {
		Vector3f pos = planet == null ? new Vector3f() : planet.getPos();
		float off = planet == null ? 0f : 2f * planet.getSize();
		float x = off + pos.x + 500f * RNG.randomFloat(-1f, 1f);
		float y = pos.y + 100f * RNG.randomFloat(-1f, 1f);
		float z = off + pos.z + 500f * RNG.randomFloat(-1f, 1f);
		return new Vector3f(x, y, z);
	}

}
