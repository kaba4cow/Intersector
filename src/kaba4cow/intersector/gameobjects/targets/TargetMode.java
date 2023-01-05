package kaba4cow.intersector.gameobjects.targets;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.intersector.files.InfosFile;

public enum TargetMode {

	BATTLE("RED", TargetType.MACHINE), SYSTEM("YELLOW", TargetType.MACHINE,
			TargetType.CARGO), GALACTIC("BLUE", TargetType.PLANET);

	private final String hologramColor;
	private Vector3f color;
	private final TargetType[] targets;

	private TargetMode(String hologramColor, TargetType... targets) {
		this.hologramColor = hologramColor;
		this.color = null;
		this.targets = targets;
	}

	public Vector3f getColor() {
		if (color == null) {
			color = new Vector3f();
			DataFile data = InfosFile.holograms.data().node(hologramColor);
			color.x = data.getFloat(0);
			color.y = data.getFloat(1);
			color.z = data.getFloat(2);
		}
		return color;
	}

	public String getHologramColor() {
		return hologramColor;
	}

	public TargetType[] getTargets() {
		return targets;
	}

}
