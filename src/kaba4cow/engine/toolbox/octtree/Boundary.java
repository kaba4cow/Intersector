package kaba4cow.engine.toolbox.octtree;

import java.io.Serializable;

public class Boundary implements Serializable {

	private static final long serialVersionUID = -4794066171090111916L;

	public float x;
	public float y;
	public float z;
	public float width;
	public float height;
	public float depth;

	public Boundary(float x, float y, float z, float width, float height, float depth) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}

	public boolean intersects(Boundary r) {
		return r.x < x + width && r.x + r.width > x && r.y < y + height && r.y + r.height > y && r.z < z + depth
				&& r.z + r.depth > z;
	}

	public boolean contains(Point p) {
		return p.x >= x && p.x < x + width && p.y >= y && p.y < y + height && p.z >= z && p.z < z + depth;
	}

}
