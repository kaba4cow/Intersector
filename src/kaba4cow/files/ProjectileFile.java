package kaba4cow.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.gameobjects.projectiles.ProjectileType;
import kaba4cow.renderEngine.models.LaserModel;
import kaba4cow.renderEngine.models.ThrustModel;
import kaba4cow.toolbox.RawModelContainer;

public class ProjectileFile extends GameFile {

	private static final String LOCATION = "projectiles/";

	private static final Map<String, ProjectileFile> map = new HashMap<String, ProjectileFile>();
	private static final List<ProjectileFile> list = new ArrayList<ProjectileFile>();

	private String name;
	private String model;
	private String texture;
	private String type;
	private String sound;
	private boolean explode;
	private boolean autoaim;
	private float aiming;
	private float delay;
	private float lifeLength;
	private float speedScale;
	private String thrust;
	private String thrustTexture;

	private TexturedModel texturedModel;
	private LaserModel laserModel;
	private ThrustModel thrustModel;
	private ThrustComponent thrustComponent;

	private ProjectileFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		name = node.node("name").getString();
		model = node.node("model").getString();
		texture = node.node("texture").getString();
		type = node.node("type").getString();
		lifeLength = node.node("lifelength").getFloat();
		speedScale = node.node("speedscale").getFloat();

		node = data.node("fireinfo");
		sound = node.node("sound").getString();
		explode = node.node("explode").getBoolean();
		autoaim = node.node("autoaim").getBoolean();
		aiming = node.node("aiming").getFloat();
		delay = node.node("delay").getFloat();

		node = data.node("thrust");
		thrust = node.node("model").getString();
		thrustTexture = node.node("texture").getString();
		if (isNull(thrust) || isNull(thrustTexture))
			thrustComponent = null;
		else
			thrustComponent = ThrustComponent.read(node);
	}

	@Override
	public void preparePostInit() {
		updateModels();
	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("model").setString(model);
		node.node("texture").setString(texture);
		node.node("type").setString(type);
		node.node("lifelength").setFloat(lifeLength);
		node.node("speedscale").setFloat(speedScale);

		node = data.node("fireinfo");
		node.node("sound").setString(sound);
		node.node("explode").setBoolean(explode);
		node.node("autoaim").setBoolean(autoaim);
		node.node("aiming").setFloat(aiming);
		node.node("delay").setFloat(delay);

		if (thrustComponent != null) {
			node = data.node("thrust");
			node.node("model").setString(thrust);
			node.node("texture").setString(thrustTexture);
			thrustComponent.save(node);
		}

		super.save();
	}

	public void updateModels() {
		texturedModel = createTexturedModel();
		laserModel = createLaserModel();
		thrustModel = createThrustModel();
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getTexture() {
		return texture;
	}

	public void setTexture(String texture) {
		this.texture = texture;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSound() {
		return sound;
	}

	public void setSound(String sound) {
		this.sound = sound;
	}

	public boolean isExplode() {
		return explode;
	}

	public void setExplode(boolean explode) {
		this.explode = explode;
	}

	public boolean isAutoaim() {
		return autoaim;
	}

	public void setAutoaim(boolean autoaim) {
		this.autoaim = autoaim;
	}

	public float getAiming() {
		return aiming;
	}

	public void setAiming(float aiming) {
		this.aiming = aiming;
	}

	public float getDelay() {
		return delay;
	}

	public void setDelay(float delay) {
		this.delay = delay;
	}

	public float getLifeLength() {
		return lifeLength;
	}

	public void setLifeLength(float lifeLength) {
		this.lifeLength = lifeLength;
	}

	public float getSpeedScale() {
		return speedScale;
	}

	public void setSpeedScale(float speedScale) {
		this.speedScale = speedScale;
	}

	public String getThrust() {
		return thrust;
	}

	public void setThrust(String thrust) {
		this.thrust = thrust;
	}

	public String getThrustTexture() {
		return thrustTexture;
	}

	public void setThrustTexture(String thrustTexture) {
		this.thrustTexture = thrustTexture;
	}

	public void setLaserModel(LaserModel laserModel) {
		this.laserModel = laserModel;
	}

	public ThrustComponent getThrustComponent() {
		return thrustComponent;
	}

	public void setThrustComponent(ThrustComponent thrustComponent) {
		this.thrustComponent = thrustComponent;
	}

	public ProjectileType getProjectileType() {
		return ProjectileType.valueOf(type);
	}

	public boolean usesLaserModel() {
		return getProjectileType().usesLaserTexture();
	}

	public TexturedModel createTexturedModel() {
		if (usesLaserModel() || isNull(model) || isNull(texture))
			return texturedModel = null;
		RawModel rawModel = RawModelContainer.get(model);
		ModelTextureFile textureFile = ModelTextureFile.get(texture);
		if (rawModel == null || textureFile == null)
			return null;
		return texturedModel = new TexturedModel(rawModel, textureFile.get());
	}

	public LaserModel createLaserModel() {
		if (!usesLaserModel() || isNull(model) || isNull(texture))
			return laserModel = null;
		RawModel rawModel = RawModelContainer.get(model);
		ModelTextureFile textureFile = ModelTextureFile.get(texture);
		if (rawModel == null || textureFile == null)
			return null;
		return laserModel = new LaserModel(rawModel, textureFile.get());
	}

	public ThrustModel createThrustModel() {
		if (thrustComponent == null || isNull(thrust) || isNull(thrustTexture))
			return thrustModel = null;
		RawModel rawModel = RawModelContainer.get(thrust);
		ThrustTextureFile textureFile = ThrustTextureFile.get(thrustTexture);
		if (rawModel == null || textureFile == null)
			return thrustModel = null;
		return thrustModel = new ThrustModel(rawModel, textureFile.get());
	}

	public TexturedModel getTexturedModel() {
		if (texturedModel == null)
			createTexturedModel();
		return texturedModel;
	}

	public LaserModel getLaserModel() {
		if (laserModel == null)
			createLaserModel();
		return laserModel;
	}

	public ThrustModel getThrustModel() {
		if (thrustModel == null)
			createThrustModel();
		return thrustModel;
	}

	public static ProjectileFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		ProjectileFile file = new ProjectileFile(name);
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

	public static ProjectileFile get(String name) {
		ProjectileFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<ProjectileFile> getList() {
		return list;
	}

}
