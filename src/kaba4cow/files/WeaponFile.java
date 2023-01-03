package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.intersector.toolbox.RawModelContainer;

public class WeaponFile extends GameFile {

	private static final String LOCATION = "weapons/";

	private static final Map<String, WeaponFile> map = new HashMap<String, WeaponFile>();
	private static final List<WeaponFile> list = new ArrayList<WeaponFile>();

	private String name;
	private float size;
	private String[] models;
	private String projectile;
	private boolean particle;
	private boolean automatic;
	private float reload;
	private float cooldown;
	private float damage;
	private float deviation;
	private List<Vector3f> firePoints;
	private int repeat;
	private float scale;
	private Vector3f originPoint;
	private float rotationSpeed;
	private boolean limitPitch;
	private float minPitch;
	private float maxPitch;
	private boolean limitYaw;
	private float minYaw;
	private float maxYaw;

	private Map<String, TexturedModel[]> texturedModels = new HashMap<String, TexturedModel[]>();

	private boolean componentReady;

	private WeaponFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		name = node.node("name").getString();
		size = node.node("size").getFloat();
		originPoint = node.node("originpoint").getVector3();
		projectile = data.node("projectile").getString();

		node = data.node("models");
		models = new String[3];
		models[0] = node.node("static").getString();
		models[1] = node.node("yaw").getString();
		models[2] = node.node("pitch").getString();

		node = data.node("fireinfo");
		particle = node.node("particle").getBoolean();
		automatic = node.node("automatic").getBoolean();
		reload = node.node("reload").getFloat();
		cooldown = node.node("cooldown").getFloat();
		scale = node.node("scale").getFloat();
		repeat = node.node("repeat").getInt();
		damage = node.node("damage").getFloat();
		deviation = node.node("deviation").getFloat();

		node = data.node("rotation");
		rotationSpeed = node.node("speed").getFloat();
		limitYaw = node.node("yaw").node("limit").getBoolean();
		minYaw = node.node("yaw").node("min").getFloat();
		maxYaw = node.node("yaw").node("max").getFloat();
		limitPitch = node.node("pitch").node("limit").getBoolean();
		minPitch = node.node("pitch").node("min").getFloat();
		maxPitch = node.node("pitch").node("max").getFloat();

		node = data.node("firepoints");
		firePoints = new ArrayList<Vector3f>();
		for (int i = 0; i < node.objectSize(); i++)
			firePoints.add(node.node(i).getVector3());

		componentReady = false;
	}

	@Override
	public void preparePostInit() {

	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("size").setFloat(size);
		node.node("originpoint").setVector3(originPoint);
		node.node("projectile").setString(projectile);

		node = data.node("models");
		node.node("static").setString(models[0]);
		node.node("yaw").setString(models[1]);
		node.node("pitch").setString(models[2]);

		node = data.node("fireinfo");
		node.node("particle").setBoolean(particle);
		node.node("automatic").setBoolean(automatic);
		node.node("reload").setFloat(reload);
		node.node("cooldown").setFloat(cooldown);
		node.node("scale").setFloat(scale);
		node.node("repeat").setInt(repeat);
		node.node("damage").setFloat(damage);
		node.node("deviation").setFloat(deviation);

		node = data.node("rotation");
		node.node("speed").setFloat(rotationSpeed);
		node.node("yaw").node("limit").setBoolean(limitYaw);
		node.node("yaw").node("min").setFloat(minYaw);
		node.node("yaw").node("max").setFloat(maxYaw);
		node.node("pitch").node("limit").setBoolean(limitPitch);
		node.node("pitch").node("min").setFloat(minPitch);
		node.node("pitch").node("max").setFloat(maxPitch);

		node = data.node("firepoints");
		for (int i = 0; i < firePoints.size(); i++)
			node.node("" + i).setVector3(firePoints.get(i));

		super.save();
	}

	public WeaponFile update() {
		if (componentReady || repeat <= 1) {
			componentReady = true;
			return this;
		}
		Vector3f[] array = getFirePointArray();
		for (int i = 1; i < repeat; i++)
			for (int j = 0; j < array.length; j++)
				firePoints.add(new Vector3f(array[j]));
		componentReady = true;
		return this;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Vector3f getOriginPoint() {
		return originPoint;
	}

	public void setOriginPoint(Vector3f originPoint) {
		this.originPoint = originPoint;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public boolean isParticle() {
		return particle;
	}

	public void setParticle(boolean particle) {
		this.particle = particle;
	}

	public boolean isAutomatic() {
		return automatic;
	}

	public void setAutomatic(boolean automatic) {
		this.automatic = automatic;
	}

	public float getDamage() {
		return damage;
	}

	public void setDamage(float damage) {
		this.damage = damage;
	}

	public float getDamageDeviation() {
		return deviation;
	}

	public void setDamageDeviation(float deviation) {
		this.deviation = deviation;
	}

	public float getReload() {
		return reload;
	}

	public void setReload(float reload) {
		this.reload = reload;
	}

	public float getCooldown() {
		return cooldown;
	}

	public void setCooldown(float cooldown) {
		this.cooldown = cooldown;
	}

	public int getRepeat() {
		return repeat;
	}

	public void setRepeat(int repeat) {
		this.repeat = repeat;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getStaticModel() {
		return models[0];
	}

	public void setStaticModel(String staticModel) {
		this.models[0] = staticModel;
	}

	public String getYawModel() {
		return models[1];
	}

	public void setYawModel(String yawModel) {
		this.models[1] = yawModel;
	}

	public String getPitchModel() {
		return models[2];
	}

	public void setPitchModel(String pitchModel) {
		this.models[2] = pitchModel;
	}

	public String getProjectile() {
		return projectile;
	}

	public void setProjectile(String projectile) {
		this.projectile = projectile;
	}

	public float getRotationSpeed() {
		return rotationSpeed;
	}

	public void setRotationSpeed(float rotationSpeed) {
		this.rotationSpeed = rotationSpeed;
	}

	public boolean isLimitYaw() {
		return limitYaw;
	}

	public void setLimitYaw(boolean limitYaw) {
		this.limitYaw = limitYaw;
	}

	public float getMinYaw() {
		return minYaw;
	}

	public void setMinYaw(float minYaw) {
		this.minYaw = minYaw;
	}

	public float getMaxYaw() {
		return maxYaw;
	}

	public void setMaxYaw(float maxYaw) {
		this.maxYaw = maxYaw;
	}

	public boolean isLimitPitch() {
		return limitPitch;
	}

	public void setLimitPitch(boolean limitPitch) {
		this.limitPitch = limitPitch;
	}

	public float getMinPitch() {
		return minPitch;
	}

	public void setMinPitch(float minPitch) {
		this.minPitch = minPitch;
	}

	public float getMaxPitch() {
		return maxPitch;
	}

	public void setMaxPitch(float maxPitch) {
		this.maxPitch = maxPitch;
	}

	public boolean isSwitchFirePoints() {
		return cooldown > 0f;
	}

	public Vector3f[] getFirePointArray() {
		Vector3f[] array = new Vector3f[firePoints.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = firePoints.get(i);
		return array;
	}

	public int getFirePoints() {
		return firePoints.size();
	}

	public Vector3f getFirePoint(int index) {
		return firePoints.get(index);
	}

	public void addFirePoint(Vector3f firePoint) {
		firePoints.add(firePoint);
	}

	public void addFirePoint() {
		addFirePoint(new Vector3f());
	}

	public void removeFirePoint(int index) {
		if (index >= 0 && index < getFirePoints())
			firePoints.remove(index);
	}

	public ProjectileFile getProjectileFile() {
		return ProjectileFile.get(projectile);
	}

	private void createModels(String metalTexture) {
		ModelTexture texture = ModelTextureFile.get(metalTexture).get();
		RawModel staticRawModel = RawModelContainer.get(models[0]);
		RawModel yawRawModel = RawModelContainer.get(models[1]);
		RawModel pitchRawModel = RawModelContainer.get(models[2]);
		TexturedModel[] modelArray = new TexturedModel[3];
		modelArray[0] = staticRawModel == null ? null : new TexturedModel(
				staticRawModel, texture);
		modelArray[1] = yawRawModel == null ? null : new TexturedModel(
				yawRawModel, texture);
		modelArray[2] = pitchRawModel == null ? null : new TexturedModel(
				pitchRawModel, texture);
		texturedModels.put(metalTexture, modelArray);
	}

	public TexturedModel getTexturedStaticModel(String metalTexture) {
		if (isNull(metalTexture))
			return null;
		if (texturedModels.containsKey(metalTexture))
			return texturedModels.get(metalTexture)[0];
		else
			createModels(metalTexture);
		return texturedModels.get(metalTexture)[0];
	}

	public TexturedModel getTexturedYawModel(String metalTexture) {
		if (isNull(metalTexture))
			return null;
		if (texturedModels.containsKey(metalTexture))
			return texturedModels.get(metalTexture)[1];
		else
			createModels(metalTexture);
		return texturedModels.get(metalTexture)[1];
	}

	public TexturedModel getTexturedPitchModel(String metalTexture) {
		if (isNull(metalTexture))
			return null;
		if (texturedModels.containsKey(metalTexture))
			return texturedModels.get(metalTexture)[2];
		else
			createModels(metalTexture);
		return texturedModels.get(metalTexture)[2];
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public static WeaponFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		WeaponFile file = new WeaponFile(name);
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

	public static WeaponFile get(String name) {
		WeaponFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<WeaponFile> getList() {
		return list;
	}

}
