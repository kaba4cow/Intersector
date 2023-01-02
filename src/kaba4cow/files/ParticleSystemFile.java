package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Easing;
import kaba4cow.engine.toolbox.particles.ParticleSystem;
import kaba4cow.engine.toolbox.particles.ParticleSystemManager;

public class ParticleSystemFile extends GameFile {

	private static final String LOCATION = "particlesystems/";

	private static final Map<String, ParticleSystemFile> map = new HashMap<String, ParticleSystemFile>();
	private static final List<ParticleSystemFile> list = new ArrayList<ParticleSystemFile>();

	private String texture;
	private Easing easing;
	private float errorLife;
	private float errorScale;
	private float dScale;
	private float dRotation;
	private String tag;

	private ParticleSystem particleSystem;

	private ParticleSystemFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		texture = node.node("texture").getString();
		tag = node.node("tag").getString();
		errorLife = node.node("errorlife").getFloat();
		errorScale = node.node("errorscale").getFloat();
		dScale = data.node("dscale").getFloat();
		dRotation = data.node("drotation").getFloat();

		try {
			easing = Easing.valueOf(node.node("easing").getString());
		} catch (Exception e) {
			easing = null;
		}
	}

	@Override
	public void preparePostInit() {
		particleSystem = new ParticleSystem(null, null, 0f, 1f, 2f, 1f, 0f, 1f);
		particleSystem.setParticleTexture(ParticleTextureFile.get(texture)
				.get());
		particleSystem.setLifeError(errorLife);
		particleSystem.setScaleError(errorScale);
		particleSystem.setDScale(dScale);
		particleSystem.setDRotation(dRotation);
		particleSystem.setBrightnessEasing(easing);
		ParticleSystemManager.add(tag, particleSystem);
	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("texture").setString(texture);
		node.node("tag").setString(tag);
		node.node("errorlife").setFloat(errorLife);
		node.node("errorscale").setFloat(errorScale);
		node.node("dscale").setFloat(dScale);
		node.node("drotation").setFloat(dRotation);
		node.node("easing").setString(
				easing == null ? "null" : easing.toString());

		super.save();
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public Easing getEasing() {
		return easing;
	}

	public void setEasing(Easing easing) {
		this.easing = easing;
	}

	public float getErrorLife() {
		return errorLife;
	}

	public void setErrorLife(float errorLife) {
		this.errorLife = errorLife;
	}

	public float getErrorScale() {
		return errorScale;
	}

	public void setErrorScale(float errorScale) {
		this.errorScale = errorScale;
	}

	public float getdScale() {
		return dScale;
	}

	public void setdScale(float dScale) {
		this.dScale = dScale;
	}

	public float getdRotation() {
		return dRotation;
	}

	public void setdRotation(float dRotation) {
		this.dRotation = dRotation;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public ParticleSystem getParticleSystem() {
		return particleSystem;
	}

	public void setParticleSystem(ParticleSystem particleSystem) {
		this.particleSystem = particleSystem;
	}

	public ParticleSystem get() {
		return particleSystem;
	}

	public static ParticleSystemFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		ParticleSystemFile file = new ParticleSystemFile(name);
		map.put(name, file);
		list.add(file);
		return file;
	}

	public static void load(List<String> names) {
		for (int i = 0; i < names.size(); i++)
			load(names.get(i));
	}

	public static void load(String... names) {
		if (names != null)
			for (int i = 0; i < names.length; i++)
				load(names[i]);
	}

	public static ParticleSystemFile get(String name) {
		ParticleSystemFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ParticleSystemFile> getList() {
		return list;
	}

}
