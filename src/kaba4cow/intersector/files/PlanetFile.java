package kaba4cow.intersector.files;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.engine.toolbox.ColorRamp;
import kaba4cow.engine.toolbox.Pair;
import kaba4cow.engine.toolbox.ColorRamp.Element;
import kaba4cow.engine.toolbox.files.DataFile;
import kaba4cow.engine.toolbox.files.TableFile;
import kaba4cow.engine.toolbox.maths.Maths;

public class PlanetFile extends GameFile {

	private static final String LOCATION = "planets/";

	private static final Map<String, PlanetFile> map = new HashMap<String, PlanetFile>();
	private static final List<PlanetFile> list = new ArrayList<PlanetFile>();

	private String name;
	private boolean object;
	private float light;
	private float image;
	private int minHabitability;
	private float ring;
	private float station;
	private int minFlares;
	private int maxFlares;
	private int minChildren;
	private int maxChildren;
	private float childBias;
	private float minSize;
	private float maxSize;
	private float sizeBias;
	private String scale;
	private float minDistance;
	private float maxDistance;
	private float axisOffset;
	private Vector3f[] colors;
	private int cubemap;
	private float minEmission;
	private float maxEmission;
	private int minBands;
	private int maxBands;
	private float minAvgColorBlend;
	private float maxAvgColorBlend;
	private float minBlendPower;
	private float maxBlendPower;
	private float minPositionFactor;
	private float maxPositionFactor;
	private boolean invertPosition;
	private Vector3f minScale;
	private Vector3f maxScale;
	private int[] infoSigns;
	private Vector3f minInfo;
	private Vector3f maxInfo;

	private ColorRamp colorRamp = null;

	private static List<Pair<PlanetFile, List<Pair<PlanetFile, Float>>>> childrenMap;

	public PlanetFile(String fileName) {
		super(fileName);
	}

	@Override
	public void prepareInit() {
		DataFile node = data;

		name = node.node("name").getString();
		object = node.node("object").getBoolean();
		light = node.node("light").getFloat();
		image = node.node("image").getFloat();
		minHabitability = node.node("minhabitability").getInt();
		ring = node.node("ring").getFloat();
		station = node.node("station").getFloat();
		minFlares = node.node("flares").getInt(0);
		maxFlares = node.node("flares").getInt(1);
		minChildren = node.node("children").getInt(0);
		maxChildren = node.node("children").getInt(1);
		childBias = node.node("childbias").getFloat();
		minSize = node.node("size").getFloat(0);
		maxSize = node.node("size").getFloat(1);
		sizeBias = node.node("sizebias").getFloat();
		scale = node.node("scale").getString();
		minDistance = node.node("distance").getFloat(0);
		maxDistance = node.node("distance").getFloat(1);
		axisOffset = node.node("axisoffset").getFloat();

		node = data.node("terrain");
		colors = node.node("colors").toVector3Array();
		cubemap = node.node("cubemap").getInt();
		minEmission = node.node("emission").getFloat(0);
		maxEmission = node.node("emission").getFloat(1);
		minBands = node.node("bands").getInt(0);
		maxBands = node.node("bands").getInt(1);
		minAvgColorBlend = node.node("avgcolorblend").getFloat(0);
		maxAvgColorBlend = node.node("avgcolorblend").getFloat(1);
		minBlendPower = node.node("blendpower").getFloat(0);
		maxBlendPower = node.node("blendpower").getFloat(1);
		minPositionFactor = node.node("positionfactor").getFloat(0);
		maxPositionFactor = node.node("positionfactor").getFloat(1);
		invertPosition = node.node("invertposition").getBoolean();

		node = data.node("terrain").node("scale");
		minScale = node.node("min").getVector3();
		maxScale = node.node("max").getVector3();

		node = data.node("terrain").node("info");
		infoSigns = node.node("sign").toIntArray();
		minInfo = node.node("min").getVector3();
		maxInfo = node.node("max").getVector3();
	}

	@Override
	public void preparePostInit() {

	}

	@Override
	public void save() {
		DataFile node = data.clear();

		node.node("name").setString(name);
		node.node("object").setBoolean(object);
		node.node("light").setFloat(light);
		node.node("image").setFloat(image);
		node.node("minhabitability").setInt(minHabitability);
		node.node("ring").setFloat(ring);
		node.node("station").setFloat(station);
		node.node("flares").setInt(minFlares).setInt(maxFlares);
		node.node("children").setInt(minChildren).setInt(maxChildren);
		node.node("childbias").setFloat(childBias);
		node.node("size").setFloat(minSize).setFloat(maxSize);
		node.node("sizebias").setFloat(sizeBias);
		node.node("scale").setString(scale);
		node.node("distance").setFloat(minDistance).setFloat(maxDistance);
		node.node("axisoffset").setFloat(axisOffset);

		node = data.node("terrain");
		for (int i = 0; i < colors.length; i++)
			node.node("colors").node(i + "").setFloat(colors[i].x)
					.setFloat(colors[i].y).setFloat(colors[i].z);
		node.node("cubemap").setInt(cubemap);
		node.node("emission").setFloat(minEmission).setFloat(maxEmission);
		node.node("bands").setInt(minBands).setInt(maxBands);
		node.node("avgcolorblend").setFloat(minAvgColorBlend)
				.setFloat(maxAvgColorBlend);
		node.node("blendpower").setFloat(minBlendPower).setFloat(maxBlendPower);
		node.node("positionfactor").setFloat(minPositionFactor)
				.setFloat(maxPositionFactor);
		node.node("invertposition").setBoolean(invertPosition);

		node = data.node("terrain").node("scale");
		node.node("min").setVector3(minScale);
		node.node("max").setVector3(maxScale);

		node = data.node("terrain").node("info");
		node.node("sign").setInt(infoSigns[0]).setInt(infoSigns[1])
				.setInt(infoSigns[2]);
		node.node("min").setVector3(minInfo);
		node.node("max").setVector3(maxInfo);

		super.save();
	}

	// private static void createChildrenMap() {
	// childrenMap = new ArrayList<Pair<PlanetFile, List<Pair<PlanetFile,
	// Float>>>>();
	//
	// ExcelTable excelFile = ExcelTable.get("PLANETS.xlsx");
	// for (int col = 1; col < excelFile.getColumns(); col++) {
	// PlanetFile parent = get(excelFile.getCell(col, 0).getStringValue());
	// List<Pair<PlanetFile, Float>> children = new ArrayList<Pair<PlanetFile,
	// Float>>();
	// for (int row = 1; row < excelFile.getRows(); row++) {
	// float value = excelFile.getCell(col, row).getNumericValue();
	// if (value > 0f) {
	// PlanetFile childFile = get(excelFile.getCell(0, row)
	// .getStringValue());
	// float childChance = excelFile.getCell(col, row)
	// .getNumericValue();
	// children.add(new Pair<PlanetFile, Float>(childFile,
	// childChance));
	// }
	// }
	// if (children.isEmpty())
	// continue;
	//
	// float sum = 0f;
	// for (Pair<PlanetFile, Float> child : children)
	// sum += child.getB();
	// sum = 1f / sum;
	// float pos = 0f;
	// for (Pair<PlanetFile, Float> child : children) {
	// pos += sum * child.getB();
	// child.setB(100f * pos);
	// }
	//
	// childrenMap
	// .add(new Pair<PlanetFile, List<Pair<PlanetFile, Float>>>(
	// parent, children));
	// }
	// }

	private static void createChildrenMap() {
		childrenMap = new ArrayList<Pair<PlanetFile, List<Pair<PlanetFile, Float>>>>();

		TableFile table = TableFile.read(new File("resources/tables/PLANETS"));

		for (int column = 1; column < table.columns(); column++) {
			PlanetFile parent = get(table.cell(0, column).getString());
			List<Pair<PlanetFile, Float>> children = new ArrayList<Pair<PlanetFile, Float>>();
			for (int row = 1; row < table.rows(); row++) {
				float value = table.cell(row, column).getFloat();
				if (value > 0f) {
					PlanetFile child = get(table.cell(row, 0).getString());
					children.add(new Pair<PlanetFile, Float>(child, value));
				}
			}
			if (children.isEmpty())
				continue;

			float sum = 0f;
			for (Pair<PlanetFile, Float> child : children)
				sum += child.getB();
			sum = 1f / sum;
			float pos = 0f;
			for (Pair<PlanetFile, Float> child : children) {
				pos += sum * child.getB();
				child.setB(100f * pos);
			}

			childrenMap
					.add(new Pair<PlanetFile, List<Pair<PlanetFile, Float>>>(
							parent, children));
		}
	}

	public List<Pair<PlanetFile, Float>> getChildrenMap() {
		if (childrenMap == null)
			createChildrenMap();
		for (int i = 0; i < childrenMap.size(); i++)
			if (childrenMap.get(i).getA() == this)
				return childrenMap.get(i).getB();
		return null;
	}

	public ColorRamp getColorRamp() {
		if (colorRamp == null) {
			colorRamp = new ColorRamp();
			for (int i = 0; i < colors.length; i++) {
				float position = Maths.map(i, 0f, colors.length, 0f, 1f);
				Element element = new Element(colors[i], position, 2f, 0f,
						false);
				colorRamp.addElement(element);
			}
		}
		return colorRamp;
	}

	public Vector3f getColor(float position, Vector3f dest) {
		return getColorRamp().getColor(position, dest);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isObject() {
		return object;
	}

	public void setObject(boolean object) {
		this.object = object;
	}

	public float getLight() {
		return light;
	}

	public void setLight(float light) {
		this.light = light;
	}

	public float getImage() {
		return image;
	}

	public void setImage(float image) {
		this.image = image;
	}

	public int getMinHabitability() {
		return minHabitability;
	}

	public void setMinHabitability(int minHabitability) {
		this.minHabitability = minHabitability;
	}

	public float getRing() {
		return ring;
	}

	public void setRing(float ring) {
		this.ring = ring;
	}

	public float getStation() {
		return station;
	}

	public void setStation(float station) {
		this.station = station;
	}

	public int getMinFlares() {
		return minFlares;
	}

	public void setMinFlares(int minFlares) {
		this.minFlares = minFlares;
	}

	public int getMaxFlares() {
		return maxFlares;
	}

	public void setMaxFlares(int maxFlares) {
		this.maxFlares = maxFlares;
	}

	public int getMinChildren() {
		return minChildren;
	}

	public void setMinChildren(int minChildren) {
		this.minChildren = minChildren;
	}

	public int getMaxChildren() {
		return maxChildren;
	}

	public void setMaxChildren(int maxChildren) {
		this.maxChildren = maxChildren;
	}

	public float getChildBias() {
		return childBias;
	}

	public void setChildBias(float childBias) {
		this.childBias = childBias;
	}

	public float getMinSize() {
		return minSize;
	}

	public void setMinSize(float minSize) {
		this.minSize = minSize;
	}

	public float getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(float maxSize) {
		this.maxSize = maxSize;
	}

	public float getSizeBias() {
		return sizeBias;
	}

	public void setSizeBias(float sizeBias) {
		this.sizeBias = sizeBias;
	}

	public String getScale() {
		return scale;
	}

	public void setScale(String scale) {
		this.scale = scale;
	}

	public float getMinDistance() {
		return minDistance;
	}

	public void setMinDistance(float minDistance) {
		this.minDistance = minDistance;
	}

	public float getMaxDistance() {
		return maxDistance;
	}

	public void setMaxDistance(float maxDistance) {
		this.maxDistance = maxDistance;
	}

	public float getAxisOffset() {
		return axisOffset;
	}

	public void setAxisOffset(float axisOffset) {
		this.axisOffset = axisOffset;
	}

	public Vector3f[] getColors() {
		return colors;
	}

	public void setColors(Vector3f[] colors) {
		this.colors = colors;
	}

	public int getCubemap() {
		return cubemap;
	}

	public void setCubemap(int cubemap) {
		this.cubemap = cubemap;
	}

	public float getMinEmission() {
		return minEmission;
	}

	public void setMinEmission(float minEmission) {
		this.minEmission = minEmission;
	}

	public float getMaxEmission() {
		return maxEmission;
	}

	public void setMaxEmission(float maxEmission) {
		this.maxEmission = maxEmission;
	}

	public int getMinBands() {
		return minBands;
	}

	public void setMinBands(int minBands) {
		this.minBands = minBands;
	}

	public int getMaxBands() {
		return maxBands;
	}

	public void setMaxBands(int maxBands) {
		this.maxBands = maxBands;
	}

	public float getMinAvgColorBlend() {
		return minAvgColorBlend;
	}

	public void setMinAvgColorBlend(float minAvgColorBlend) {
		this.minAvgColorBlend = minAvgColorBlend;
	}

	public float getMaxAvgColorBlend() {
		return maxAvgColorBlend;
	}

	public void setMaxAvgColorBlend(float maxAvgColorBlend) {
		this.maxAvgColorBlend = maxAvgColorBlend;
	}

	public float getMinBlendPower() {
		return minBlendPower;
	}

	public void setMinBlendPower(float minBlendPower) {
		this.minBlendPower = minBlendPower;
	}

	public float getMaxBlendPower() {
		return maxBlendPower;
	}

	public void setMaxBlendPower(float maxBlendPower) {
		this.maxBlendPower = maxBlendPower;
	}

	public float getMinPositionFactor() {
		return minPositionFactor;
	}

	public void setMinPositionFactor(float minPositionFactor) {
		this.minPositionFactor = minPositionFactor;
	}

	public float getMaxPositionFactor() {
		return maxPositionFactor;
	}

	public void setMaxPositionFactor(float maxPositionFactor) {
		this.maxPositionFactor = maxPositionFactor;
	}

	public boolean isInvertPosition() {
		return invertPosition;
	}

	public void setInvertPosition(boolean invertPosition) {
		this.invertPosition = invertPosition;
	}

	public Vector3f getMinScale() {
		return minScale;
	}

	public void setMinScale(Vector3f minScale) {
		this.minScale = minScale;
	}

	public Vector3f getMaxScale() {
		return maxScale;
	}

	public void setMaxScale(Vector3f maxScale) {
		this.maxScale = maxScale;
	}

	public int[] getInfoSigns() {
		return infoSigns;
	}

	public void setInfoSigns(int[] infoSigns) {
		this.infoSigns = infoSigns;
	}

	public Vector3f getMinInfo() {
		return minInfo;
	}

	public void setMinInfo(Vector3f minInfo) {
		this.minInfo = minInfo;
	}

	public Vector3f getMaxInfo() {
		return maxInfo;
	}

	public void setMaxInfo(Vector3f maxInfo) {
		this.maxInfo = maxInfo;
	}

	@Override
	public String getLocation() {
		return LOCATION;
	}

	public static PlanetFile load(String name) {
		if (isNull(name))
			return null;
		if (map.containsKey(name))
			return get(name);
		PlanetFile file = new PlanetFile(name);
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

	public static PlanetFile get(String name) {
		PlanetFile file = map.get(name);
		if (file == null)
			return load(name);
		return file;
	}

	public static List<PlanetFile> getList() {
		return list;
	}

}
