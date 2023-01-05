package kaba4cow.intersector.gameobjects;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Easing;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.engine.toolbox.particles.Particle;
import kaba4cow.engine.toolbox.rng.RNG;
import kaba4cow.intersector.files.ParticleSystemFile;
import kaba4cow.intersector.gameobjects.projectiles.Projectile;
import kaba4cow.intersector.gameobjects.targets.TargetType;
import kaba4cow.intersector.renderEngine.RendererContainer;

public class Debris extends GameObject {

	private final TexturedModel model;
	private final Vector3f color;

	private final float lifeLength;
	private float elapsedTime;

	public Debris(World world, Vector3f pos, Vector3f vel, float size, float lifeLength, TexturedModel model,
			Vector3f color) {
		super(world);
		this.pos = new Vector3f(pos);
		this.vel = new Vector3f(vel);
		this.size = size;
		this.lifeLength = lifeLength;
		this.elapsedTime = 0f;
		this.model = model;
		this.color = color;
		this.rotate(Vectors.randomize(-1f, 1f, (Vector3f) null).normalise(null), RNG.randomFloat(Maths.TWO_PI));
	}

	@Override
	public boolean isCollidable() {
		return false;
	}

	@Override
	protected void collide(GameObject obj, float dt) {

	}

	@Override
	public void update(float dt) {
		elapsedTime += dt;
		if (elapsedTime >= lifeLength)
			destroy(this);
		else if (RNG.chance(0.1f / elapsedTime))
			new Particle(ParticleSystemFile.get("FIRE").get(), pos.negate(null), vel.negate(null), RNG.randomFloat(4f),
					0f, RNG.randomFloat(2f * size));
	}

	@Override
	public void render(RendererContainer renderers) {
		float scale = 1f - Easing.EASE_IN_EXPO.getValue(elapsedTime / lifeLength);
		Matrix4f mat = direction.getMatrix(pos, true, scale * size);
		renderers.getMachineRenderer().render(model, color, mat);
	}

	@Override
	public TargetType getTargetType() {
		return null;
	}

	@Override
	public void damage(int colliderIndex, Projectile proj) {

	}

	@Override
	public void damage(float damage) {

	}

	@Override
	protected void onDamage(Projectile proj) {

	}

	@Override
	public void onSpawn() {

	}

	@Override
	protected void onDestroy(GameObject src) {

	}

	@Override
	protected void onDestroy() {

	}

}
