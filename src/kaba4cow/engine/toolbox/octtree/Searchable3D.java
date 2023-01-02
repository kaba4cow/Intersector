package kaba4cow.engine.toolbox.octtree;

public interface Searchable3D {

	float getTreeX();

	float getTreeY();

	float getTreeZ();

	default Point toPoint() {
		return new Point(getTreeX(), getTreeY(), getTreeZ());
	}

}
