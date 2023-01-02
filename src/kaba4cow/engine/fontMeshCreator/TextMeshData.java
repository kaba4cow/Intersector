package kaba4cow.engine.fontMeshCreator;

import org.lwjgl.util.vector.Vector2f;

public class TextMeshData {

	private float[] vertexPositions;
	private float[] textureCoords;

	private Vector2f size;

	protected TextMeshData(float[] vertexPositions, float[] textureCoords) {
		this.vertexPositions = vertexPositions;
		this.textureCoords = textureCoords;
		this.size = new Vector2f();
	}

	public float[] getVertexPositions() {
		return vertexPositions;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}

	public Vector2f getSize() {
		return size;
	}

	public int getVertexCount() {
		return vertexPositions.length / 2;
	}

}
