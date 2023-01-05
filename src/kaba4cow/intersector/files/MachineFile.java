package kaba4cow.intersector.files;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaba4cow.engine.renderEngine.models.RawModel;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.renderEngine.textures.ModelTexture;
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.intersector.gameobjects.machines.classes.MachineClass;
import kaba4cow.intersector.gameobjects.objectcomponents.ColliderComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.ContainerComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.PortComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.WeaponComponent;
import kaba4cow.intersector.renderEngine.ThrustModel;
import kaba4cow.intersector.toolbox.containers.RawModelContainer;

public abstract class MachineFile extends GameFile {

	protected static final String LOCATION = "machines/";

	private static final Map<String, MachineFile> map = new HashMap<String, MachineFile>();
	private static final List<MachineFile> list = new ArrayList<MachineFile>();

	protected int classRank;
	protected int className;
	protected String name;
	protected float size;
	protected float collisionSize;
	protected float health;
	protected float shield;
	protected float mass;
	protected int maxCargo;
	protected String manufacturer;
	protected String metalModel;
	protected String glassModel;
	protected boolean useLight;
	protected String thrust;
	protected String thrustTexture;

	protected List<ThrustComponent> thrustComponents;
	protected List<ColliderComponent> colliderComponents;
	protected List<WeaponComponent> weaponComponents;
	protected List<ContainerComponent> containerComponents;
	protected List<PortComponent> portComponents;

	private Map<String, TexturedModel[]> texturedModels = new HashMap<String, TexturedModel[]>();
	private ThrustModel thrustModel;

	protected MachineFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		name = node.node("name").getString();
		manufacturer = node.node("manufacturer").getString();

		node = data.node("class");
		classRank = node.node("rank").getInt();
		className = node.node("name").getInt();

		node = data.node("info");
		size = node.node("size").getFloat();
		health = node.node("health").getFloat();
		shield = node.node("shield").getFloat();
		mass = node.node("mass").getFloat();
		maxCargo = node.node("maxcargo").getInt();

		node = data.node("model");
		useLight = node.node("uselight").getBoolean();
		metalModel = node.node("metal").getString();
		glassModel = node.node("glass").getString();

		node = data.node("thrust");
		thrust = node.node("model").getString();
		thrustTexture = node.node("texture").getString();

		thrustComponents = new ArrayList<ThrustComponent>();
		node = data.node("components").node("thrust");
		for (int i = 0; i < node.objectSize(); i++)
			thrustComponents.add(ThrustComponent.read(node.node(i)));

		colliderComponents = new ArrayList<ColliderComponent>();
		node = data.node("components").node("collider");
		for (int i = 0; i < node.objectSize(); i++)
			colliderComponents.add(ColliderComponent.read(node.node(i)));

		weaponComponents = new ArrayList<WeaponComponent>();
		node = data.node("components").node("weapon");
		for (int i = 0; i < node.objectSize(); i++)
			weaponComponents.add(WeaponComponent.read(node.node(i)));

		containerComponents = new ArrayList<ContainerComponent>();
		node = data.node("components").node("container");
		for (int i = 0; i < node.objectSize(); i++)
			containerComponents.add(ContainerComponent.read(node.node(i)));

		portComponents = new ArrayList<PortComponent>();
		node = data.node("components").node("port");
		for (int i = 0; i < node.objectSize(); i++)
			portComponents.add(PortComponent.read(node.node(i)));

		collisionSize = ColliderComponent
				.calculateCollisionSize(colliderComponents);

		for (int i = 0; i < weaponComponents.size(); i++)
			weaponComponents.get(i).calculateTranslated(size);
		for (int i = 0; i < containerComponents.size(); i++)
			containerComponents.get(i).calculateTranslated(size);
		for (int i = 0; i < portComponents.size(); i++)
			portComponents.get(i).calculateTranslated(size);
	}

	@Override
	public void preparePostInit() {
		createThrustModel();
	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("manufacturer").setString(manufacturer);

		node = data.node("class");
		node.node("rank").setInt(classRank);
		node.node("name").setInt(className);

		node = data.node("info");
		node.node("size").setFloat(size);
		node.node("health").setFloat(health);
		node.node("shield").setFloat(shield);
		node.node("mass").setFloat(mass);
		node.node("maxcargo").setInt(maxCargo);

		node = data.node("model");
		node.node("uselight").setBoolean(useLight);
		node.node("metal").setString(metalModel);
		node.node("glass").setString(glassModel);

		node = data.node("thrust");
		node.node("model").setString(thrust);
		node.node("texture").setString(thrustTexture);

		node = data.node("components").node("thrust");
		for (int i = 0; i < thrustComponents.size(); i++)
			thrustComponents.get(i).save(node.node(i + ""));

		node = data.node("components").node("collider");
		for (int i = 0; i < colliderComponents.size(); i++)
			colliderComponents.get(i).save(node.node(i + ""));

		node = data.node("components").node("weapon");
		for (int i = 0; i < weaponComponents.size(); i++)
			weaponComponents.get(i).save(node.node(i + ""));

		node = data.node("components").node("container");
		for (int i = 0; i < containerComponents.size(); i++)
			containerComponents.get(i).save(node.node(i + ""));

		node = data.node("components").node("port");
		for (int i = 0; i < portComponents.size(); i++)
			portComponents.get(i).save(node.node(i + ""));

		super.save();
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public void calculateMass() {
		mass = 0f;
		for (int i = 0; i < colliderComponents.size(); i++)
			mass += 0.75f
					* size
					* colliderComponents.get(i).size
					* Maths.map(colliderComponents.get(i).strength,
							ColliderComponent.MIN_STRENGTH,
							ColliderComponent.MAX_STRENGTH, 0.9f, 1.1f);
		mass = (int) (10f * mass) / 10f;
		maxCargo = (int) (0.3f * mass);
	}

	public float getMinTotalMass() {
		return mass;
	}

	public float getMaxTotalMass() {
		float total = getMinTotalMass() + maxCargo;
		for (int i = 0; i < containerComponents.size(); i++)
			total += containerComponents.get(i).containerGroupFile.getMass();
		return total;
	}

	public abstract MachineClass getMachineClass();

	public abstract void setMachineClassInfo(String machineClass,
			String machineClassName);

	public String getMachineClassName() {
		return getMachineClass().getName(className);
	}

	public String getFullName() {
		return name + " class " + getMachineClassName();
	}

	public int getClassRank() {
		return classRank;
	}

	public void setClassRank(int classRank) {
		this.classRank = classRank;
	}

	public int getClassName() {
		return className;
	}

	public void setClassName(int className) {
		this.className = className;
	}

	public String getMachineName() {
		return name;
	}

	public void setMachineName(String name) {
		this.name = name;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	public int getMaxCargo() {
		return maxCargo;
	}

	public void setMaxCargo(int maxCargo) {
		this.maxCargo = maxCargo;
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public float getCollisionSize() {
		return collisionSize;
	}

	public float getHealth() {
		return health;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public float getShield() {
		return shield;
	}

	public void setShield(float shield) {
		this.shield = shield;
	}

	public String getMetalModel() {
		return metalModel;
	}

	public void setMetalModel(String model) {
		this.metalModel = model;
	}

	public String getGlassModel() {
		return glassModel;
	}

	public void setGlassModel(String glassModel) {
		this.glassModel = glassModel;
	}

	public boolean isUseLight() {
		return useLight;
	}

	public void setUseLight(boolean useLight) {
		this.useLight = useLight;
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

	public ThrustComponent[] getThrustArray() {
		ThrustComponent[] array = new ThrustComponent[thrustComponents.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = thrustComponents.get(i);
		return array;
	}

	public int getThrusts() {
		return thrustComponents.size();
	}

	public ThrustComponent getThrust(int index) {
		return thrustComponents.get(index);
	}

	public void addThrust(ThrustComponent thrustInfo) {
		thrustComponents.add(thrustInfo);
	}

	public void addThrust() {
		addThrust(new ThrustComponent());
	}

	public void removeThrust(int index) {
		if (index >= 0 && index < getThrusts())
			thrustComponents.remove(index);
	}

	public ColliderComponent[] getColliderArray() {
		ColliderComponent[] array = new ColliderComponent[colliderComponents
				.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = colliderComponents.get(i);
		return array;
	}

	public int getColliders() {
		return colliderComponents.size();
	}

	public ColliderComponent getCollider(int index) {
		return colliderComponents.get(index);
	}

	public void addCollider(ColliderComponent colliderInfo) {
		colliderComponents.add(colliderInfo);
	}

	public void addCollider() {
		addCollider(new ColliderComponent());
	}

	public void removeCollider(int index) {
		if (index >= 0 && index < getColliders())
			colliderComponents.remove(index);
	}

	public WeaponComponent[] getWeaponArray() {
		WeaponComponent[] array = new WeaponComponent[weaponComponents.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = weaponComponents.get(i);
		return array;
	}

	public int getWeapons() {
		return weaponComponents.size();
	}

	public WeaponComponent getWeapon(int index) {
		return weaponComponents.get(index);
	}

	public void addWeapon(WeaponComponent colliderInfo) {
		weaponComponents.add(colliderInfo);
	}

	public void addWeapon() {
		addWeapon(new WeaponComponent(
				WeaponFile.getList().get(0).getFileName(), false));
	}

	public void removeWeapon(int index) {
		if (index >= 0 && index < getWeapons())
			weaponComponents.remove(index);
	}

	public ContainerComponent[] getContainerArray() {
		ContainerComponent[] array = new ContainerComponent[containerComponents
				.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = containerComponents.get(i);
		return array;
	}

	public int getContainers() {
		return containerComponents.size();
	}

	public ContainerComponent getContainer(int index) {
		return containerComponents.get(index);
	}

	public void addContainer(ContainerComponent container) {
		containerComponents.add(container);
	}

	public void addContainer() {
		addContainer(new ContainerComponent(ContainerGroupFile.getList().get(0)
				.getFileName()));
	}

	public void removeContainer(int index) {
		if (index >= 0 && index < getContainers())
			containerComponents.remove(index);
	}

	public PortComponent[] getPortArray() {
		PortComponent[] array = new PortComponent[portComponents.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = portComponents.get(i);
		return array;
	}

	public int getPorts() {
		return portComponents.size();
	}

	public PortComponent getPort(int index) {
		return portComponents.get(index);
	}

	public void addPort() {
		portComponents.add(new PortComponent(0f, 0f, 0f, 0f, 10f, true, 0f));
	}

	public void addPort(PortComponent portInfo) {
		portComponents.add(portInfo);
	}

	public void removePort(int index) {
		if (index >= 0 && index < getPorts())
			portComponents.remove(index);
	}

	public String getMetalTexture(String textureSet) {
		TextureSetFile textureSetFile = TextureSetFile.get(textureSet);
		return textureSetFile.getMetalTexture();
	}

	public String getGlassTexture(String textureSet) {
		TextureSetFile textureSetFile = TextureSetFile.get(textureSet);
		return useLight ? textureSetFile.getLightTexture() : textureSetFile
				.getGlassTexture();
	}

	private void createTexturedModels(String textureSet) {
		TextureSetFile textureSetFile = TextureSetFile.get(textureSet);
		if (textureSetFile == null)
			return;
		ModelTexture metalModelTexture = ModelTextureFile.get(
				textureSetFile.getMetalTexture()).get();
		ModelTexture glassModelTexture = ModelTextureFile.get(
				textureSetFile.getGlassTexture()).get();
		ModelTexture lightModelTexture = ModelTextureFile.get(
				textureSetFile.getLightTexture()).get();
		RawModel metalRawModel = RawModelContainer.get(metalModel);
		RawModel glassRawModel = RawModelContainer.get(glassModel);
		TexturedModel[] modelArray = new TexturedModel[4];
		modelArray[0] = metalRawModel == null ? null : new TexturedModel(
				metalRawModel, metalModelTexture);
		modelArray[1] = glassRawModel == null ? null : new TexturedModel(
				glassRawModel, glassModelTexture);
		modelArray[2] = glassRawModel == null ? null : new TexturedModel(
				glassRawModel, lightModelTexture);
		texturedModels.put(textureSet, modelArray);
	}

	public TexturedModel getMetalTexturedModel(String textureSet) {
		if (isNull(metalModel) || isNull(textureSet))
			return null;
		if (!texturedModels.containsKey(textureSet))
			createTexturedModels(textureSet);
		return texturedModels.get(textureSet)[0];
	}

	public TexturedModel getGlassTexturedModel(String textureSet,
			boolean useLight) {
		if (isNull(glassModel) || isNull(textureSet))
			return null;
		if (!texturedModels.containsKey(textureSet))
			createTexturedModels(textureSet);
		return texturedModels.get(textureSet)[useLight ? 2 : 1];
	}

	public ThrustModel createThrustModel() {
		if (isNull(thrust) || isNull(thrustTexture))
			return thrustModel = null;
		RawModel rawModel = RawModelContainer.get(thrust);
		ThrustTextureFile textureFile = ThrustTextureFile.get(thrustTexture);
		if (rawModel == null || textureFile == null)
			return thrustModel = null;
		return thrustModel = new ThrustModel(rawModel, textureFile.get());
	}

	public ThrustModel getThrustModel() {
		if (thrustModel == null)
			createThrustModel();
		return thrustModel;
	}

	public static MachineFile load(String name) {
		if (name == null)
			return null;
		if (map.containsKey(name))
			return get(name);
		MachineFile file = null;
		if (name.startsWith("SHIP"))
			file = ShipFile.load(name);
		else if (name.startsWith("STATION"))
			file = StationFile.load(name);
		return file;
	}

	public static void load(MachineFile file) {
		if (file != null && !map.containsValue(file)) {
			map.put(file.getFileName(), file);
			list.add(file);
		}
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

	public static MachineFile get(String name) {
		MachineFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<MachineFile> getChildrenList() {
		return list;
	}

}
