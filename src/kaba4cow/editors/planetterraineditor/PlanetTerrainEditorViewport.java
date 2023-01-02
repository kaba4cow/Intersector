package kaba4cow.editors.planetterraineditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Vectors;
import kaba4cow.files.PlanetFile;
import kaba4cow.galaxyengine.TerrainGenerator;
import kaba4cow.gameobjects.Planet;
import kaba4cow.renderEngine.fborendering.TerrainRendering;

public class PlanetTerrainEditorViewport extends AbstractEditorViewport {

	private PlanetFile file;

	private Cubemap terrain;
	private TerrainGenerator terrainGenerator;
	private long seed;
	private int colorIndex;

	private Direction direction;

	public PlanetTerrainEditorViewport(PlanetTerrainEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();
		terrain = TerrainRendering.getCubemap(2);
		direction = new Direction();
		loadRequestedFile();
	}

	@Override
	protected void loadRequestedFile() {
		renderer.clearLights();
		renderer.addLight(light);
		if (getNewFile() != null)
			file = PlanetFile.load(getNewFile());
		else
			file = null;
		changeColorIndex(0);
		renderTerrain();
		finishLoadingFile(file);
	}

	@Override
	public void update(float dt) {
		super.update(dt);

		if (isLoadingRequested())
			loadRequestedFile();
	}

	@Override
	public void render() {
		TerrainRendering.process();
		camera.orbit(Vectors.INIT3, 0f, 0f, 1f, cameraManager);
		if (settings.isStaticLight())
			light.setPos(new Vector3f(-50000f, 25000f, 50000f));
		else
			light.setPos(camera.getPos());
		renderer.setWireframe(settings.isRenderWireframe());

		renderer.prepare();

		Cubemap cubemap = settings.isRenderSkybox() ? skybox : cells;
		renderers.getCubemapRenderer().render(cubemap);
		renderers.getCubemapRenderer().process();

		PlanetTerrainEditor editor = getEditor();
		seed = (long) EditorUtils.getIntValue(editor.getSeedSpinner());

		if (file != null) {
			file.setMinEmission(EditorUtils.getFloatValue(editor
					.getEmissionMinSpinner()));
			file.setMaxEmission(EditorUtils.getFloatValue(editor
					.getEmissionMaxSpinner()));

			file.setMinBands(EditorUtils.getIntValue(editor
					.getBandsMinSpinner()));
			file.setMaxBands(EditorUtils.getIntValue(editor
					.getBandsMaxSpinner()));

			file.setMinAvgColorBlend(EditorUtils.getFloatValue(editor
					.getAvgColorBlendMinSpinner()));
			file.setMaxAvgColorBlend(EditorUtils.getFloatValue(editor
					.getAvgColorBlendMaxSpinner()));

			file.setMinBlendPower(EditorUtils.getFloatValue(editor
					.getBlendPowerMinSpinner()));
			file.setMaxBlendPower(EditorUtils.getFloatValue(editor
					.getBlendPowerMaxSpinner()));

			file.setMinPositionFactor(EditorUtils.getFloatValue(editor
					.getPositionFactorMinSpinner()));
			file.setMaxPositionFactor(EditorUtils.getFloatValue(editor
					.getPositionFactorMaxSpinner()));

			file.setInvertPosition(editor.getInvertPositionCheckbox()
					.isSelected());

			file.getMinScale().x = EditorUtils.getFloatValue(editor
					.getMinScaleXSpinner());
			file.getMinScale().y = EditorUtils.getFloatValue(editor
					.getMinScaleYSpinner());
			file.getMinScale().z = EditorUtils.getFloatValue(editor
					.getMinScaleZSpinner());
			file.getMaxScale().x = EditorUtils.getFloatValue(editor
					.getMaxScaleXSpinner());
			file.getMaxScale().y = EditorUtils.getFloatValue(editor
					.getMaxScaleYSpinner());
			file.getMaxScale().z = EditorUtils.getFloatValue(editor
					.getMaxScaleZSpinner());

			file.getInfoSigns()[0] = EditorUtils.getIntValue(editor
					.getInfoSignXSpinner());
			file.getInfoSigns()[1] = EditorUtils.getIntValue(editor
					.getInfoSignYSpinner());
			file.getInfoSigns()[2] = EditorUtils.getIntValue(editor
					.getInfoSignZSpinner());

			file.getMinInfo().x = EditorUtils.getFloatValue(editor
					.getMinInfoXSpinner());
			file.getMinInfo().y = EditorUtils.getFloatValue(editor
					.getMinInfoYSpinner());
			file.getMinInfo().z = EditorUtils.getFloatValue(editor
					.getMinInfoZSpinner());
			file.getMaxInfo().x = EditorUtils.getFloatValue(editor
					.getMaxInfoXSpinner());
			file.getMaxInfo().y = EditorUtils.getFloatValue(editor
					.getMaxInfoYSpinner());
			file.getMaxInfo().z = EditorUtils.getFloatValue(editor
					.getMaxInfoZSpinner());

			Matrix4f mat = direction.getMatrix(Vectors.INIT3, true, 1f);
			renderers.getTerrainRenderer().render(Planet.getModel(1f), terrain,
					terrainGenerator.emission, mat);
		}

		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	public void renderTerrain() {
		if (file == null)
			return;
		terrainGenerator = new TerrainGenerator(file, getColor(), seed);
		TerrainRendering.render(terrain, terrainGenerator);
	}

	public long getSeed() {
		return seed;
	}

	public int getColorIndex() {
		return colorIndex;
	}

	public void changeColorIndex(int dir) {
		if (file == null) {
			colorIndex = 0;
			return;
		}
		colorIndex += dir;
		if (colorIndex < 0)
			colorIndex = getMaxColorIndex() - 1;
		else if (colorIndex >= getMaxColorIndex())
			colorIndex = 0;
		getEditor().getColorIndexLabel().setText(
				"Color: " + getColorIndex() + " / " + (getMaxColorIndex() - 1));
		if (dir != 0)
			renderTerrain();
	}

	public int getMaxColorIndex() {
		return file == null ? 1 : file.getColors().length;
	}

	public Vector3f getColor() {
		if (file == null)
			return Vectors.INIT3;
		return file.getColors()[colorIndex];
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public PlanetTerrainEditor getEditor() {
		return (PlanetTerrainEditor) editor;
	}

	@Override
	public PlanetTerrainEditorSettings getSettings() {
		return (PlanetTerrainEditorSettings) settings;
	}

	public PlanetFile getPlanetFile() {
		return file;
	}

}
