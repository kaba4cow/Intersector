package kaba4cow.gameobjects.projectiles;

import java.util.ArrayList;
import java.util.List;

public enum ProjectileType {

	PROJECTILE(false), CLUSTER(false), CLUSTERROCKET(false), ROCKET(false), LASER(
			true), RAY(true);

	private final boolean usesLaserTexture;

	private ProjectileType(boolean usesLaserTexture) {
		this.usesLaserTexture = usesLaserTexture;
	}

	public static List<String> getStringList() {
		ProjectileType[] values = values();
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < values.length; i++)
			list.add(values[i].toString());
		return list;
	}

	public boolean usesLaserTexture() {
		return usesLaserTexture;
	}

}
