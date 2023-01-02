package kaba4cow.gameobjects;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.GameSettings;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.octtree.OctTree;
import kaba4cow.engine.toolbox.particles.ParticleSystemManager;
import kaba4cow.galaxyengine.objects.SystemObject;
import kaba4cow.renderEngine.RendererContainer;
import kaba4cow.renderEngine.fborendering.EnvironmentRendering;
import kaba4cow.toolbox.flocking.FlockManager;

public class World {

	private static final float OCT_TREE_RANGE = 250000f;

	private SystemObject system;

	private Cubemap skybox;

	private List<GameObject> list;
	private List<GameObject> queue;
	private OctTree<GameObject> octTree;

	public World() {
		this.system = null;
		this.skybox = new Cubemap(GameSettings.CUBEMAP_SIZES[GameSettings.getCubemaps()]);
		this.list = new ArrayList<GameObject>();
		this.queue = new ArrayList<GameObject>();
		this.octTree = new OctTree<GameObject>(-OCT_TREE_RANGE, -OCT_TREE_RANGE, -OCT_TREE_RANGE, 2f * OCT_TREE_RANGE,
				2f * OCT_TREE_RANGE, 2f * OCT_TREE_RANGE, 4);
	}

	public void update(float dt, Vector3f offset) {
		updateQueue();

		populateOctTree();

		FlockManager.update(dt);

		GameObject current;
		int listSize = list.size();
		for (int i = 0; i < listSize; i++) {
			current = list.get(i);
			if (!current.isAlive())
				continue;
			current.update(dt);
		}

		int steps = GameSettings.COLLISION_STEPS[GameSettings.getCollisions()];
		float stepSize = 1f / (float) steps;
		float cdt = stepSize * dt;
		for (int step = 0; step < steps; step++)
			for (int i = 0; i < listSize; i++) {
				current = list.get(i);
				if (!current.isAlive())
					continue;
				current.move(cdt);
				current.collide(cdt);
			}
		for (int i = list.size() - 1; i >= 0; i--)
			if (!list.get(i).isAlive())
				list.remove(i);

		if (offset != null)
			move(offset);
	}

	public void render(RendererContainer renderers) {
		int listSize = list.size();
		renderers.getRenderer().clearLights();
		for (int i = 0; i < listSize; i++)
			if (list.get(i) instanceof Planet)
				renderers.getRenderer().addLight(((Planet) list.get(i)).getLight());

		for (int i = 0; i < listSize; i++)
			list.get(i).render(renderers);

		EnvironmentRendering.process(renderers.getRenderer(), skybox);

		renderers.getCubemapRenderer().render(EnvironmentRendering.getCubemap());
		renderers.getCubemapRenderer().process();

		renderers.processModelRenderers(EnvironmentRendering.getCubemap());
	}

	private void move(Vector3f offset) {
		offset = new Vector3f(offset);
		for (int i = 0; i < list.size(); i++)
			list.get(i).offset(offset);
		ParticleSystemManager.move("GAME", offset);
	}

	public Cubemap getSkybox() {
		return skybox;
	}

	private void populateOctTree() {
		octTree.clear();
		octTree.populate(list);
	}

	private void updateQueue() {
		for (int i = 0; i < queue.size(); i++) {
			queue.get(i).onSpawn();
			list.add(queue.get(i));
		}
		queue.clear();
	}

	public World addObject(GameObject object) {
		addToQueue(object);
		return this;
	}

	private boolean addToQueue(GameObject object) {
		if (!GameObject.isAlive(object) || list.contains(object) || queue.contains(object))
			return false;
		queue.add(object);
		return true;
	}

	public void clear() {
		list.clear();
		queue.clear();
		octTree.clear();
	}

	public SystemObject getSystem() {
		return system;
	}

	public void setSystem(SystemObject system) {
		this.system = system;
	}

	public List<GameObject> getList() {
		return list;
	}

	public OctTree<GameObject> getOctTree() {
		return octTree;
	}

}
