package kaba4cow.states;

import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.galaxyengine.objects.SystemObject;
import kaba4cow.gameobjects.Fraction;
import kaba4cow.gameobjects.Planet;
import kaba4cow.gameobjects.World;
import kaba4cow.gameobjects.machinecontrollers.shipcontrollers.ShipAIController;
import kaba4cow.gameobjects.machines.Machine;
import kaba4cow.gameobjects.machines.Ship;
import kaba4cow.gameobjects.machines.classes.ShipClass;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.renderEngine.renderers.HologramRenderer;
import kaba4cow.renderEngine.renderers.ThrustRenderer;
import kaba4cow.toolbox.flocking.Flock;
import kaba4cow.utils.GalaxyUtils;
import kaba4cow.utils.GameUtils;

public class SceneState extends State {

	private Camera camera;

	private World world;

	private Flock flock;
	private Machine ship;

	private float elapsedTime;
	private float maxTime;

	private float pitch;
	private float yaw;
	private float currentDist;
	private float nextDist;

	public SceneState() {
		super("SCENE");
	}

	@Override
	public boolean isInitializable() {
		return world == null;
	}

	@Override
	public void create() {
		camera = new Camera();
	}

	@Override
	public void init() {
		game.getRenderer().clearLights();
		game.clearParticles(this);

		world = new World();

		Fraction fraction = Fraction.getRandom();

		SystemObject system = GalaxyUtils.generateSystem(fraction.getFractionFile().getCapital());
		List<Planet> planets = world.create(system);
		Planet planet = planets.get(RNG.randomInt(planets.size()));

		flock = new Flock(true);
		float rand = RNG.randomFloat(1f);
		if (rand < 0.2f) {
			addShips(planet, fraction, ShipClass.HEAVYFREIGHTER, 1);
			addShips(planet, fraction, ShipClass.FREIGHTER, 2);
			addShips(planet, fraction, ShipClass.SHUTTLE, 2);
		} else if (rand < 0.4f) {
			addShips(planet, fraction, ShipClass.FRIGATE, 1);
			addShips(planet, fraction, ShipClass.HEAVYFREIGHTER, 2);
			addShips(planet, fraction, ShipClass.CORVETTE, 2);
		} else if (rand < 0.6f) {
			addShips(planet, fraction, ShipClass.FRIGATE, 1);
			addShips(planet, fraction, ShipClass.CORVETTE, 2);
			addShips(planet, fraction, ShipClass.ATTACKSHUTTLE, 2);
		} else if (rand < 0.8f) {
			addShips(planet, fraction, ShipClass.FRIGATE, 1);
			addShips(planet, fraction, ShipClass.ATTACKSHUTTLE, 2);
			addShips(planet, fraction, ShipClass.FIGHTER, 3);
		} else {
			addShips(planet, fraction, ShipClass.CRUISER, 1);
			addShips(planet, fraction, ShipClass.FRIGATE, 2);
			addShips(planet, fraction, ShipClass.CORVETTE, 3);
		}
		ship = flock.randomMachine();

		elapsedTime = 1f;
		maxTime = 0f;
		nextDist = RNG.randomFloat(1f, 3f);
		currentDist = nextDist;

		world.update(0f, null);
	}

	private void addShips(Planet planet, Fraction fraction, ShipClass shipClass, int amount) {
		for (int i = 0; i < amount; i++)
			new Ship(world, fraction, fraction.getRandomShip(shipClass), randVec(planet), new ShipAIController())
					.setFlock(flock);
	}

	@Override
	public void onStateSwitch() {

	}

	@Override
	public void update(float dt) {
		elapsedTime += dt;
		if (elapsedTime >= maxTime) {
			ship = flock.randomMachine();
			elapsedTime = 0f;
			maxTime = RNG.randomFloat(8f, 24f);
			pitch = RNG.randomFloat(-0.5f, 0.5f);
			yaw = RNG.randomFloat(Maths.TWO_PI);
			currentDist = nextDist;
			nextDist = RNG.randomFloat(1f, 3f);
		}
		yaw += 0.05f * dt;

		flock.update(dt);
		ThrustRenderer.update(dt);
		HologramRenderer.update(dt);
		game.updateParticles(this, dt);
		world.update(dt, ship.getPos());
	}

	@Override
	public void render(RendererContainer renderers) {
		game.switchPostProcessing(false);
		game.setRenderer(GameUtils.getPlayerPov());
		game.getRenderer().setCamera(camera);
		float dist = Maths.blend(nextDist, currentDist, 0.8f * elapsedTime / maxTime);
		camera.orbit(ship.getPos(), dist * ship.getSize(), pitch, yaw, flock.getLeader().getDirection());
		game.getRenderer().prepare();

		// AudioManager.setListenerData(camera.getPos(), ship.getVel());

		world.render(renderers);
		game.renderParticles(this);

		game.doPostProcessing();
	}

	private Vector3f randVec(Planet planet) {
		Vector3f pos = planet == null ? new Vector3f() : planet.getPos();
		float off = planet == null ? 0f : 3f * planet.getSize();
		float x = off + pos.x + 1000f * RNG.randomFloat(-1f, 1f);
		float y = pos.y + 100f * RNG.randomFloat(-1f, 1f);
		float z = off + pos.z + 1000f * RNG.randomFloat(-1f, 1f);
		return new Vector3f(x, y, z);
	}

}
