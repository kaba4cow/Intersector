package kaba4cow.intersector.states;

import java.util.ArrayList;
import java.util.List;

import kaba4cow.intersector.Intersector;
import kaba4cow.intersector.renderEngine.RendererContainer;

public abstract class State {

	private static final List<State> list = new ArrayList<State>();

	protected final Intersector game;

	protected final String name;

	protected State prevState;

	public State(String name) {
		this.game = Intersector.getInstance();
		this.name = name;
		this.prevState = null;
		list.add(this);
	}

	public static void createAll() {
		for (int i = 0; i < list.size(); i++)
			list.get(i).create();
	}

	public abstract boolean isInitializable();

	protected boolean isIgnoringPrevState(State prevState) {
		return false;
	}

	public abstract void create();

	public abstract void init();

	public abstract void onStateSwitch();

	public abstract void update(float dt);

	public abstract void render(RendererContainer renderers);

	public String getName() {
		return name;
	}

	public State getPrevState() {
		return prevState;
	}

	public void setPrevState(State prevState) {
		if (!isIgnoringPrevState(prevState))
			this.prevState = prevState;
	}

}
