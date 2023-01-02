package kaba4cow.engine.fontMeshCreator;

public class Character {

	private int id;
	private float xTextureCoord;
	private float yTextureCoord;
	private float xMaxTextureCoord;
	private float yMaxTextureCoord;
	private float xOffset;
	private float yOffset;
	private float sizeX;
	private float sizeY;
	private float xAdvance;

	protected Character(int id, float xTextureCoord, float yTextureCoord, float xTexSize, float yTexSize, float xOffset,
			float yOffset, float sizeX, float sizeY, float xAdvance) {
		this.id = id;
		this.xTextureCoord = xTextureCoord;
		this.yTextureCoord = yTextureCoord;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.xMaxTextureCoord = xTexSize + xTextureCoord;
		this.yMaxTextureCoord = yTexSize + yTextureCoord;
		this.xAdvance = xAdvance;
	}

	protected int getId() {
		return id;
	}

	protected float getxTextureCoord() {
		return xTextureCoord;
	}

	protected float getyTextureCoord() {
		return yTextureCoord;
	}

	protected float getXMaxTextureCoord() {
		return xMaxTextureCoord;
	}

	protected float getYMaxTextureCoord() {
		return yMaxTextureCoord;
	}

	protected float getxOffset() {
		return xOffset;
	}

	protected float getyOffset() {
		return yOffset;
	}

	protected float getSizeX() {
		return sizeX;
	}

	protected float getSizeY() {
		return sizeY;
	}

	protected float getxAdvance() {
		return xAdvance;
	}

}
