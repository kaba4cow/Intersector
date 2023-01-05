package kaba4cow.editors.weaponscaleeditor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.intersector.files.WeaponFile;

public class WeaponScaleEditorViewport extends AbstractEditorViewport {

	private static final WeaponSizeComparator sizeComparator = new WeaponSizeComparator();
	private static final WeaponDamageComparator damageComparator = new WeaponDamageComparator();

	private List<WeaponFile> files;
	private int index;
	private int dindex;

	private Direction direction;

	private float dist;
	private float newDist;

	public WeaponScaleEditorViewport(WeaponScaleEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();
		direction = new Direction();

		renderer.clearLights();
		renderer.addLight(light);

		files = WeaponFile.getList();
		Collections.sort(files, sizeComparator);
		index = 0;
		dindex = 0;
		editor.onNewFileLoaded();
	}

	@Override
	protected void loadRequestedFile() {

	}

	@Override
	public void update(float dt) {
		super.update(dt);
		dist = Maths.blend(newDist, dist, 4f * dt);
	}

	@Override
	public void render() {
		changeIndex();
		camera.orbit(cameraManager.getPoint(), 0f, 0f, dist, cameraManager);
		if (settings.isStaticLight())
			light.setPos(new Vector3f(-50000f, 25000f, 50000f));
		else
			light.setPos(camera.getPos());

		renderer.setWireframe(settings.isRenderWireframe());
		renderer.prepare();

		Cubemap cubemap = settings.isRenderSkybox() ? skybox : cells;
		renderers.getCubemapRenderer().render(cubemap);
		renderers.getCubemapRenderer().process();

		WeaponScaleEditor editor = getEditor();
		WeaponFile currentFile = getWeaponFile();

		currentFile.setSize(EditorUtils.getFloatValue(editor.getSizeSpinner()));
		currentFile.setDamage(EditorUtils.getFloatValue(editor
				.getDamageSpinner()));
		currentFile.setDamageDeviation(EditorUtils.getFloatValue(editor
				.getDamageDeviationSpinner()));
		currentFile.setRepeat(EditorUtils.getIntValue(editor
				.getRepetitionsSpinner()));
		currentFile.setReload(EditorUtils.getFloatValue(editor
				.getReloadTimeSpinner()));
		currentFile.setCooldown(EditorUtils.getFloatValue(editor
				.getCooldownTimeSpinner()));

		newDist = currentFile.getSize();
		if (editor.getSettings().isSortDamage())
			Collections.sort(files, damageComparator);
		else
			Collections.sort(files, sizeComparator);

		Vector3f position = new Vector3f();
		float distance = getSettings().getDistance();

		String texture = editor.getTextureButton().getText();
		for (int i = 0; i < files.size(); i++) {
			WeaponFile file = files.get(i);
			position.x -= distance * file.getSize();

			if (file == currentFile)
				index = i;

			Vector3f currentPosition = new Vector3f(position);
			if (i == index)
				cameraManager.moveTo(currentPosition);

			Matrix4f mat = direction.getMatrix(currentPosition, true,
					file.getSize());
			renderers.getModelRenderer().render(
					file.getTexturedStaticModel(texture), null, mat);
			renderers.getModelRenderer().render(
					file.getTexturedYawModel(texture), null, mat);
			renderers.getModelRenderer().render(
					file.getTexturedPitchModel(texture), null, mat);

			position.x -= distance * file.getSize();
		}
		renderers.processModelRenderers(cubemap);

		stopPostProcessing(null);
	}

	public void changeIndex() {
		if (dindex == 0)
			return;
		index += dindex;
		dindex = 0;
		if (index < 0)
			index = files.size() - 1;
		if (index >= files.size())
			index = 0;
		getEditor().onNewFileLoaded();
	}

	public void changeIndex(int dir) {
		dindex = dir;
	}

	public int getIndex() {
		return index;
	}

	public int getMaxIndex() {
		return files.size();
	}

	@Override
	public void save() {
		for (int i = 0; i < files.size(); i++)
			files.get(i).save();
	}

	@Override
	public WeaponScaleEditor getEditor() {
		return (WeaponScaleEditor) editor;
	}

	@Override
	public WeaponScaleEditorSettings getSettings() {
		return (WeaponScaleEditorSettings) settings;
	}

	public WeaponFile getWeaponFile() {
		return files.get(index);
	}

	private static class WeaponSizeComparator implements Comparator<WeaponFile> {

		@Override
		public int compare(WeaponFile o1, WeaponFile o2) {
			return Float.compare(o1.getSize(), o2.getSize());
		}

	}

	private static class WeaponDamageComparator implements
			Comparator<WeaponFile> {

		@Override
		public int compare(WeaponFile o1, WeaponFile o2) {
			return Float.compare(o1.getDamage(), o2.getDamage());
		}

	}

}
