package kaba4cow.engine.renderEngine.textures;

public class ModelTexture extends TextureAtlas {

	private float shininess = 0f;
	private float shineDamper = 1f;
	private float reflectivity = 0f;
	private float emission = -1f;

	public ModelTexture(int textureID) {
		super(textureID);
	}

	public float getShininess() {
		return shininess;
	}

	public ModelTexture setShininess(float shininess) {
		this.shininess = shininess;
		return this;
	}

	public float getShineDamper() {
		return shineDamper;
	}

	public ModelTexture setShineDamper(float shineDamper) {
		this.shineDamper = shineDamper;
		return this;
	}

	public float getReflectivity() {
		return reflectivity;
	}

	public ModelTexture setReflectivity(float reflectivity) {
		this.reflectivity = reflectivity;
		return this;
	}

	public float getEmission() {
		return emission;
	}

	public ModelTexture setEmission(float emission) {
		this.emission = emission;
		return this;
	}

}
