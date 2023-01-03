package kaba4cow.editors.containergroupeditor;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditorViewport;
import kaba4cow.editors.EditorUtils;
import kaba4cow.engine.renderEngine.Cubemap;
import kaba4cow.engine.renderEngine.models.TexturedModel;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.files.ContainerFile;
import kaba4cow.files.ContainerGroupFile;
import kaba4cow.intersector.utils.GameUtils;

public class ContainerGroupEditorViewport extends AbstractEditorViewport {

	private ContainerGroupFile file;

	private int index;
	private int dindex;

	private Direction direction;

	public ContainerGroupEditorViewport(ContainerGroupEditor editor) {
		super(editor);
	}

	@Override
	public void init() {
		super.init();
		direction = new Direction();
		loadRequestedFile();
	}

	@Override
	protected void loadRequestedFile() {
		renderer.clearLights();
		renderer.addLight(light);
		if (getNewFile() != null) {
			file = ContainerGroupFile.load(getNewFile());
		} else {
			file = null;
		}
		index = 0;
		dindex = 0;
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
		changeIndex();
		camera.orbit(cameraManager.getPoint(), 0f, 0f, 1f, cameraManager);
		if (settings.isStaticLight())
			light.setPos(new Vector3f(-50000f, 25000f, 50000f));
		else
			light.setPos(camera.getPos());
		renderer.setWireframe(settings.isRenderWireframe());

		renderer.prepare();

		Cubemap cubemap = settings.isRenderSkybox() ? skybox : cells;
		renderers.getCubemapRenderer().render(cubemap);
		renderers.getCubemapRenderer().process();

		if (file != null) {
			file.setSize(EditorUtils
					.getFloatValue(getEditor().getSizeSpinner()));
			file.setHealth(EditorUtils.getFloatValue(getEditor()
					.getHealthSpinner()));
			file.getContainers().set(index,
					getEditor().getCargoButton().getText());

			if (index < 0)
				index = getMaxIndex() - 1;
			if (index >= getMaxIndex())
				index = 0;

			Vector3f position = new Vector3f();
			float distance = getSettings().getDistance();

			int texture = getSettings().isScrollTextures() ? (int) (2f * GameUtils
					.getTime()) : 0;
			for (int i = 0; i < file.getContainers().size(); i++) {
				ContainerFile currentFile = ContainerFile.get(file
						.getContainer(i));
				position.x -= distance;

				Vector3f currentPosition = new Vector3f(position);
				if (i == index)
					cameraManager.moveTo(currentPosition);

				if (currentFile != null) {
					Matrix4f mat = direction.getMatrix(currentPosition, true,
							1f);
					TexturedModel model = currentFile.getTexturedModel(texture);
					if (model != null)
						renderers.getModelRenderer().render(model, null, mat);
				}

				position.x -= distance;
			}
			renderers.processModelRenderers(cubemap);
		}

		stopPostProcessing(null);
	}

	public void changeIndex() {
		if (file == null || dindex == 0)
			return;
		index += dindex;
		dindex = 0;
		if (index < 0)
			index = getMaxIndex() - 1;
		if (index >= getMaxIndex())
			index = 0;
		getEditor().onIndexChanged();
	}

	public void changeIndex(int dir) {
		dindex = dir;
	}

	public void setIndex(int index) {
		if (index < 0)
			index = getMaxIndex() - 1;
		if (index >= getMaxIndex())
			index = 0;
		this.index = index;
		getEditor().onIndexChanged();
	}

	public int getIndex() {
		return index;
	}

	public int getMaxIndex() {
		if (file == null)
			return 0;
		return file.getContainers().size();
	}

	@Override
	public void save() {
		if (file != null)
			file.save();
	}

	@Override
	public ContainerGroupEditor getEditor() {
		return (ContainerGroupEditor) editor;
	}

	@Override
	public ContainerGroupEditorSettings getSettings() {
		return (ContainerGroupEditorSettings) settings;
	}

	public ContainerFile getCargoFile() {
		if (file == null)
			return null;
		return ContainerFile.get(file.getContainer(index));
	}

	public ContainerGroupFile getCargoGroupFile() {
		return file;
	}

}
