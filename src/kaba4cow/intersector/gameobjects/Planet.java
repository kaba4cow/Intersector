package kaba4cow.intersector.gameobjects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.Camera;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.Light;
import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.Loaders;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Matrices;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.noise.Noise;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.galaxyengine.TerrainGenerator;
import kaba4cow.intersector.galaxyengine.objects.PlanetObject;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;
import kaba4cow.intersector.renderEngine.fborendering.EnvironmentRendering;
import kaba4cow.intersector.renderEngine.fborendering.RingRendering;
import kaba4cow.intersector.renderEngine.fborendering.TerrainRendering;
import kaba4cow.intersector.toolbox.RawModelContainer;
import testing.SystemTesting;

public class Planet extends GameObject implements EnvironmentObject {

	private static RawModel[] modelLOD;

	private final PlanetObject planetObject;

	private Cubemap terrain;
	private ModelTexture ring;
	private TerrainGenerator terrainGenerator;
	private Light light;
	private Flare[] flares;

	public Planet(World world, PlanetObject planetObject) {
		super(world);

		this.planetObject = planetObject;

		this.pos = new Vector3f(planetObject.worldPosition);
		this.size = planetObject.radius;
		this.direction = planetObject.direction.copy();

		pos.scale(SystemTesting.SCALE);
		size *= SystemTesting.SCALE;

		this.terrainGenerator = new TerrainGenerator(planetObject.file, planetObject.color, planetObject.terrainSeed);
		this.light = terrainGenerator.createLight(size, planetObject.file.getLight());

		this.terrain = TerrainRendering.getCubemap(planetObject.file.getCubemap());
		TerrainRendering.render(terrain, terrainGenerator);
		this.ring = planetObject.ringRadius > 0f ? RingRendering.setTexture(this) : null;
		if (ring != null)
			RingRendering.render(ring, terrainGenerator);

		if (planetObject.flares > 0) {
			this.flares = new Flare[planetObject.flares];
			for (int i = 0; i < flares.length; i++)
				flares[i] = new Flare(this);
		} else
			this.flares = null;
	}

	@Override
	protected void finalize() throws Throwable {
		Loaders.deleteTexture(terrain.getTexture());
		if (ring != null)
			Loaders.deleteTexture(ring.getTexture());
		super.finalize();
	}

	@Override
	public void update(float dt) {
		rotate(direction.getUp(), planetObject.rotationSpeed * dt);
		// planetObject.orbit(dt);
		// planetObject.calculateWorldPosition();
		// Vector3f.sub(planetObject.worldPosition, off, pos);
		// pos.scale(SystemTesting.SCALE);

		if (flares != null)
			for (int i = 0; i < flares.length; i++)
				flares[i].update(dt);
	}

	@Override
	public void render(RendererContainer renderers) {
		renderers.getRenderer().addLight(getLight());
		EnvironmentRendering.render(this);
		if (flares != null)
			for (int i = 0; i < flares.length; i++)
				EnvironmentRendering.render(flares[i]);
	}

	public void renderMap(RendererContainer renderers) {
		Matrix4f matrix = direction.getMatrix(planetObject.getPos(), true, planetObject.getSize());
		renderers.getTerrainRenderer().render(getModel(1f), terrain, terrainGenerator.emission, matrix);
		matrix = direction.getMatrix(planetObject.getPos(), true, planetObject.getSize() * planetObject.ringRadius);
		renderers.getRingRenderer().render(ring, terrainGenerator.ringColor, matrix);

		if (flares != null)
			for (int i = 0; i < flares.length; i++)
				flares[i].render(planetObject.getPos(), planetObject.getSize(), renderers);
	}

	public void renderModel(RendererContainer renderers) {
		renderers.getRenderer().addLight(getLight());
		RawModel model = getModel(1f);

		Matrix4f matrix = direction.getMatrix(pos, true, size);
		renderers.getTerrainRenderer().render(model, terrain, terrainGenerator.emission, matrix);

		if (ring != null) {
			matrix = direction.getMatrix(pos, true, size * planetObject.ringRadius);
			renderers.getRingRenderer().render(ring, terrainGenerator.ringColor, matrix);
		}

		if (flares != null)
			for (int i = 0; i < flares.length; i++)
				flares[i].render(pos, size, renderers);
	}

	@Override
	public void render(Camera camera) {
		Vector3f scaledPos = Vectors.scaleToOrigin(pos, Vectors.INIT3, EnvironmentRendering.SCALE, null);
		Matrix4f matrix = direction.getMatrix(scaledPos, true, EnvironmentRendering.SCALE * size);

		RawModel model = getModel(pos, size, camera);
		EnvironmentRendering.getRenderers().getTerrainRenderer().render(model, terrain, terrainGenerator.emission,
				matrix);

		if (ring != null) {
			matrix = direction.getMatrix(scaledPos, true, EnvironmentRendering.SCALE * size * planetObject.ringRadius);
			EnvironmentRendering.getRenderers().getRingRenderer().render(ring, terrainGenerator.ringColor, matrix);
		}
	}

	public static RawModel getModel(Vector3f pos, float size, Camera camera) {
		float dist = Maths.dist(pos, camera.getPos()) - size;
		float normIndex = Maths.map(dist / size, 4f, 128f, 1f, 0f);
		return getModel(normIndex);
	}

	public static RawModel getModel(float normIndex) {
		if (modelLOD == null) {
			modelLOD = new RawModel[10];
			for (int i = 0; i < modelLOD.length; i++)
				modelLOD[i] = RawModelContainer.get("LOD/lod" + i);
		}
		int index = (int) (modelLOD.length * normIndex);
		if (index < 0)
			index = 0;
		if (index >= modelLOD.length)
			index = modelLOD.length - 1;
		return modelLOD[index];
	}

	public Cubemap getTerrain() {
		return terrain;
	}

	public ModelTexture getRing() {
		return ring;
	}

	public TerrainGenerator getTerrainGenerator() {
		return terrainGenerator;
	}

	@Override
	public TargetType getTargetType() {
		return TargetType.PLANET;
	}

	@Override
	public String getTargetDescription() {
		return planetObject.file.getName() + " " + planetObject.name;
	}

	public PlanetObject getPlanetObject() {
		return planetObject;
	}

	@Override
	public void damage(int colliderIndex, Projectile proj) {
		return;
	}

	@Override
	public void damage(float damage) {
		return;
	}

	@Override
	protected void onDamage(Projectile proj) {
		return;
	}

	@Override
	public void onSpawn() {
		return;
	}

	@Override
	protected void onDestroy(GameObject src) {
		return;
	}

	@Override
	protected void onDestroy() {
		return;
	}

	@Override
	protected void collide(GameObject obj, float dt) {
		if (obj instanceof Planet)
			return;
		obj.getVel().set(0f, 0f, 0f);
		obj.destroy(this);
	}

	public Light getLight() {
		if (light != null)
			light.getPos().set(pos.negate(null));
		return light;
	}

	public static class Flare implements EnvironmentObject {

		private final Planet planet;

		private Noise noise;

		private Direction direction;
		private Vector3f position;

		private Vector3f color;

		private float elapsedTime;

		private float rotation;
		private float offsetSpeed;

		private Vector3f offsets;

		public Flare(Planet planet) {
			this.planet = planet;
			this.noise = new Noise(RNG.randomLong());
			this.direction = new Direction();
			this.color = planet.getPlanetObject().color;
			this.offsets = new Vector3f();
			this.reset();
			this.elapsedTime = RNG.randomFloat(60f);
		}

		private void reset() {
			this.elapsedTime = 0f;
			this.position = Vectors.randomize(-Maths.TWO_PI, Maths.TWO_PI, (Vector3f) null);
			this.offsets.x = RNG.randomFloat(100f);
			this.offsets.y = RNG.randomFloat(0.092f, 0.211f);
			this.offsets.z = RNG.randomFloat(0.003f, 0.042f);
			this.rotation = RNG.randomFloat(Maths.TWO_PI);
			this.offsetSpeed = RNG.randomFloat(0.5f, 1f);
		}

		public void update(float dt) {
			elapsedTime += dt;
			offsets.x += offsetSpeed * dt;
		}

		@Override
		public void render(Camera camera) {
			Vector3f scaledPos = Vectors.scaleToOrigin(planet.pos, Vectors.INIT3, EnvironmentRendering.SCALE, null);

			render(scaledPos, EnvironmentRendering.SCALE * planet.size, EnvironmentRendering.getRenderers());
		}

		public void render(Vector3f pos, float size, RendererContainer renderers) {
			direction.set(planet.direction);
			direction.rotate(planet.direction.getRight(), position.x);
			direction.rotate(planet.direction.getForward(), position.y);
			direction.rotate(planet.direction.getUp(), position.z);
			direction.rotate(direction.getForward(), rotation);

			float scaleNoise1 = noise.getCombinedValue(0.093f * offsets.x, 0f, 2);
			float scaleNoise2 = 0.5f * Maths.saw(-0.132f * offsets.x, 7) + 0.5f;
			float scaleNoise = scaleNoise1 * scaleNoise2;
			scaleNoise = Maths.pow(Maths.limit(scaleNoise), 2.86f);
			if (elapsedTime < 10f)
				scaleNoise = Maths.map(elapsedTime, 0f, 10f, 0f, scaleNoise);
			float dBrightness = 1f - 1.3f * scaleNoise;
			if (dBrightness <= 0f) {
				reset();
				return;
			}
			float scaleX = Maths.map(1f - scaleNoise * scaleNoise, 0f, 1f, 0.38f, 1.76f);
			float scaleZ = Maths.map(scaleNoise, 0f, 1f, 1.07f, 1.29f);
			float brightnessNoise = noise.getNoiseValue(0f, 0.923f * offsets.x);
			if (elapsedTime < 10f)
				brightnessNoise = Maths.map(elapsedTime, 0f, 10f, 0f, brightnessNoise);
			float brightness = Maths.map(brightnessNoise, 0f, 1f, 1.53f, 7.68f) * dBrightness;
			if (elapsedTime < 10f)
				brightness = Maths.map(elapsedTime, 0f, 10f, 0f, brightness);

			Matrix4f matrix = direction.getMatrix(pos, true, size);
			Matrices.scale(matrix, scaleX, 1f, scaleZ);
			renderers.getFlareRenderer().render(matrix, offsets, color,
					brightness * planet.getPlanetObject().file.getLight());
		}
	}

}
