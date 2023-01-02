package kaba4cow.engine.toolbox.octtree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OctTree<T extends Searchable3D> implements Serializable {

	private static final long serialVersionUID = 5816870074061849056L;

	public static final int DEFAULT_CAPACITY = 8;

	private final int capacity;

	private Boundary boundary;

	private List<T> points;

	private OctTree<T> nw1;
	private OctTree<T> ne1;
	private OctTree<T> sw1;
	private OctTree<T> se1;
	private OctTree<T> nw2;
	private OctTree<T> ne2;
	private OctTree<T> sw2;
	private OctTree<T> se2;

	private boolean subdivided;

	public OctTree(float x, float y, float z, float rangeX, float rangeY,
			float rangeZ, int capacity) {
		this.points = new ArrayList<T>();
		this.clear();
		this.boundary = new Boundary(x, y, z, rangeX, rangeY, rangeZ);
		this.capacity = capacity;
	}

	public OctTree(float x, float y, float z, float rangeX, float rangeY,
			float rangeZ) {
		this(x, y, z, rangeX, rangeX, rangeZ, DEFAULT_CAPACITY);
	}

	public OctTree(Boundary boundary, int capacity) {
		this.points = new ArrayList<T>();
		this.clear();
		this.boundary = boundary;
		this.capacity = capacity;
	}

	public OctTree(Boundary boundary) {
		this(boundary, DEFAULT_CAPACITY);
	}

	private OctTree(OctTree<T> parent, Boundary boundary) {
		this(boundary, parent.getCapacity());
	}

	public void clear() {
		points.clear();
		subdivided = false;
		nw1 = null;
		ne1 = null;
		sw1 = null;
		se1 = null;
		nw2 = null;
		ne2 = null;
		sw2 = null;
		se2 = null;
	}

	public List<T> query(Boundary search, T ignored) {
		List<T> found = new ArrayList<T>();

		if (!boundary.intersects(search))
			return found;

		T point = null;
		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);
			if (point != ignored && search.contains(point.toPoint()))
				found.add(point);
		}

		if (subdivided) {
			found.addAll(nw1.query(search, ignored));
			found.addAll(ne1.query(search, ignored));
			found.addAll(sw1.query(search, ignored));
			found.addAll(se1.query(search, ignored));
			found.addAll(nw2.query(search, ignored));
			found.addAll(ne2.query(search, ignored));
			found.addAll(sw2.query(search, ignored));
			found.addAll(se2.query(search, ignored));
		}

		return found;
	}

	public List<T> query(Boundary search) {
		List<T> found = new ArrayList<T>();

		if (!boundary.intersects(search))
			return found;

		T point = null;
		for (int i = 0; i < points.size(); i++) {
			point = points.get(i);
			if (search.contains(point.toPoint()))
				found.add(point);
		}

		if (subdivided) {
			found.addAll(nw1.query(search));
			found.addAll(ne1.query(search));
			found.addAll(sw1.query(search));
			found.addAll(se1.query(search));
			found.addAll(nw2.query(search));
			found.addAll(ne2.query(search));
			found.addAll(sw2.query(search));
			found.addAll(se2.query(search));
		}

		return found;
	}

	public List<T> query(float x, float y, float z, float rangeX, float rangeY,
			float rangeZ, T ignored) {
		return query(new Boundary(x - rangeX, y - rangeY, z - rangeZ,
				2f * rangeX, 2f * rangeY, 2f * rangeZ), ignored);
	}

	public List<T> query(float x, float y, float z, float rangeX, float rangeY,
			float rangeZ) {
		return query(x, y, z, rangeX, rangeX, rangeZ, null);
	}

	public List<T> query(float x, float y, float z, float range) {
		return query(x, y, z, range, range, range);
	}

	public List<T> query(T center, float rangeX, float rangeY, float rangeZ) {
		Boundary boundary = new Boundary(center.getTreeX() - rangeX,
				center.getTreeY() - rangeY, center.getTreeZ() - rangeZ,
				2f * rangeX, 2f * rangeY, 2f * rangeZ);
		return query(boundary, center);
	}

	public List<T> query(T center, float range) {
		return query(center, range, range, range);
	}

	public boolean insert(T point) {
		if (!boundary.contains(point.toPoint()))
			return false;

		if (points.size() < capacity) {
			addPoint(point);
			return true;
		} else {
			if (!subdivided)
				subdivide();

			if (nw1.insert(point))
				return true;
			if (ne1.insert(point))
				return true;
			if (sw1.insert(point))
				return true;
			if (se1.insert(point))
				return true;
			if (nw2.insert(point))
				return true;
			if (ne2.insert(point))
				return true;
			if (sw2.insert(point))
				return true;
			if (se2.insert(point))
				return true;
			return false;
		}
	}

	public void populate(List<T> list) {
		if (list == null || list.isEmpty())
			return;
		for (int i = 0; i < list.size(); i++)
			insert(list.get(i));
	}

	private void subdivide() {
		float x = boundary.x;
		float y = boundary.y;
		float z = boundary.z;
		float w = 0.5f * boundary.width;
		float h = 0.5f * boundary.height;
		float d = 0.5f * boundary.depth;

		Boundary nwB1 = new Boundary(x, y, z, w, h, d);
		nw1 = new OctTree<T>(this, nwB1);

		Boundary neB1 = new Boundary(x + w, y, z, w, h, d);
		ne1 = new OctTree<T>(this, neB1);

		Boundary swB1 = new Boundary(x, y + h, z, w, h, d);
		sw1 = new OctTree<T>(this, swB1);

		Boundary seB1 = new Boundary(x + w, y + h, z, w, h, d);
		se1 = new OctTree<T>(this, seB1);

		Boundary nwB2 = new Boundary(x, y, z + d, w, h, d);
		nw2 = new OctTree<T>(this, nwB2);

		Boundary neB2 = new Boundary(x + w, y, z + d, w, h, d);
		ne2 = new OctTree<T>(this, neB2);

		Boundary swB2 = new Boundary(x, y + h, z + d, w, h, d);
		sw2 = new OctTree<T>(this, swB2);

		Boundary seB2 = new Boundary(x + w, y + h, z + d, w, h, d);
		se2 = new OctTree<T>(this, seB2);

		subdivided = true;
	}

	private void addPoint(T point) {
		points.add(point);
	}

	public int getCapacity() {
		return capacity;
	}

	public int getSize() {
		int num = points.size();
		if (subdivided)
			num += nw1.getSize() + ne1.getSize() + sw1.getSize()
					+ se1.getSize() + nw2.getSize() + ne2.getSize()
					+ sw2.getSize() + se2.getSize();
		return num;
	}

}
