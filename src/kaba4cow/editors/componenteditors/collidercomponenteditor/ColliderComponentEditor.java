package kaba4cow.editors.componenteditors.collidercomponenteditor;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import kaba4cow.editors.AbstractEditor;
import kaba4cow.editors.componenteditors.ComponentEditor;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.files.MachineFile;
import kaba4cow.files.TextureSetFile;
import kaba4cow.intersector.gameobjects.objectcomponents.ColliderComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.ObjectComponent;

public class ColliderComponentEditor extends AbstractEditor implements
		ComponentEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Collider Component";

	private ColliderComponentEditorViewport viewport;
	private ColliderComponentEditorSettings settings;

	private JLabel indexLabel;
	private JSpinner colliderXSpinner;
	private JSpinner colliderYSpinner;
	private JSpinner colliderZSpinner;
	private JSpinner colliderSizeSpinner;
	private JSpinner colliderStrenghSpinner;

	private JButton textureSetButton;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	private JMenu editMenu;
	private JMenuItem addMenuItem;
	private JMenuItem removeMenuItem;
	private JMenuItem duplicateMenuItem;
	private JMenuItem mirrorXMenuItem;
	private JMenuItem mirrorYMenuItem;
	private JMenuItem mirrorZMenuItem;

	private JCheckBoxMenuItem followColliderMenuItem;

	public static final float MUL = 10f;
	public static final float STEP = 0.01f;

	public ColliderComponentEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "machines";
	}

	@Override
	public void onNewFileLoaded() {
		MachineFile file = getViewport().getMachineFile();
		indexLabel.setText("index: 1 / " + viewport.getMaxIndex());
		resetColliderInfo(file);
	}

	private void resetColliderInfo(MachineFile file) {
		if (file == null || viewport.getMaxIndex() == 0) {
			colliderXSpinner.setValue(0f);
			colliderYSpinner.setValue(0f);
			colliderZSpinner.setValue(0f);
			colliderSizeSpinner.setValue(1f);
			colliderStrenghSpinner.setValue(0);
			return;
		}
		ColliderComponent colliderInfo = file.getCollider(viewport.getIndex());
		colliderXSpinner.setValue(colliderInfo.pos.x * MUL);
		colliderYSpinner.setValue(colliderInfo.pos.y * MUL);
		colliderZSpinner.setValue(colliderInfo.pos.z * MUL);
		colliderSizeSpinner.setValue(colliderInfo.size * MUL);
		colliderStrenghSpinner.setValue(colliderInfo.strength);
	}

	public void onIndexChanged() {
		MachineFile file = getViewport().getMachineFile();
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / "
				+ viewport.getMaxIndex());
		resetColliderInfo(file);
	}

	@Override
	protected void onActionPerformed(Object source) {
		if (source == openMenuItem) {
			File directory = new File("resources/files/" + getRootDirectory());
			JFileChooser fileChooser = new JFileChooser(directory);
			int i = fileChooser.showOpenDialog(this);
			if (i == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				viewport.loadNewFile(file);
			}
		}

		if (source == saveMenuItem) {
			viewport.save();
		}

		if (source == exitMenuItem) {
			MainProgram.requestClosing();
			dispose();
		}

		MachineFile file = viewport.getMachineFile();
		if (file != null) {
			if (source == addMenuItem) {
				file.addCollider();
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (source == removeMenuItem) {
				file.removeCollider(viewport.getIndex());
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (file.getColliders() > 0) {
				if (source == duplicateMenuItem) {
					ColliderComponent colliderInfo = file.getCollider(viewport
							.getIndex());
					file.addCollider(new ColliderComponent(colliderInfo));
					onIndexChanged();
				}

				if (source == mirrorXMenuItem) {
					ColliderComponent colliderInfo = file.getCollider(viewport
							.getIndex());
					file.addCollider(colliderInfo.mirrorX());
					onIndexChanged();
				}

				if (source == mirrorYMenuItem) {
					ColliderComponent colliderInfo = file.getCollider(viewport
							.getIndex());
					file.addCollider(colliderInfo.mirrorY());
					onIndexChanged();
				}

				if (source == mirrorZMenuItem) {
					ColliderComponent colliderInfo = file.getCollider(viewport
							.getIndex());
					file.addCollider(colliderInfo.mirrorZ());
					onIndexChanged();
				}
			}
		}
	}

	@Override
	protected void initMenuBar() {
		fileMenu = new JMenu("File");
		fileMenu.addActionListener(this);
		menuBar.add(fileMenu);

		openMenuItem = new JMenuItem("Open");
		openMenuItem.addActionListener(this);
		fileMenu.add(openMenuItem);

		fileMenu.add(new JSeparator());

		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(this);
		fileMenu.add(saveMenuItem);

		fileMenu.add(new JSeparator());

		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(this);
		fileMenu.add(exitMenuItem);

		editMenu = new JMenu("Edit");
		editMenu.addActionListener(this);
		menuBar.add(editMenu);

		addMenuItem = new JMenuItem("Add");
		addMenuItem.addActionListener(this);
		editMenu.add(addMenuItem);

		editMenu.add(new JSeparator());

		removeMenuItem = new JMenuItem("Remove");
		removeMenuItem.addActionListener(this);
		editMenu.add(removeMenuItem);

		duplicateMenuItem = new JMenuItem("Duplicate");
		duplicateMenuItem.addActionListener(this);
		editMenu.add(duplicateMenuItem);

		editMenu.add(new JSeparator());

		JMenu mirrorThrustMenu = new JMenu("Mirror");
		mirrorThrustMenu.addActionListener(this);
		editMenu.add(mirrorThrustMenu);

		mirrorXMenuItem = new JMenuItem("X");
		mirrorXMenuItem.addActionListener(this);
		mirrorThrustMenu.add(mirrorXMenuItem);

		mirrorYMenuItem = new JMenuItem("Y");
		mirrorYMenuItem.addActionListener(this);
		mirrorThrustMenu.add(mirrorYMenuItem);

		mirrorZMenuItem = new JMenuItem("Z");
		mirrorZMenuItem.addActionListener(this);
		mirrorThrustMenu.add(mirrorZMenuItem);
	}

	@Override
	protected void initSettingsAndViewport() {
		settings = new ColliderComponentEditorSettings(this);
		viewport = new ColliderComponentEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		followColliderMenuItem = new JCheckBoxMenuItem("Follow collider");
		followColliderMenuItem.addActionListener(this);
		settingsMenu.add(followColliderMenuItem);
	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {
		textureSetButton = new JButton();
		textureSetButton.setToolTipText("texture set");
		textureSetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(TextureSetFile.getList(), textureSetButton,
						"Select texture set file");
			}
		});
		textureSetButton.setText("YELLOW0");

		settingsPanel.add(textureSetButton);
	}

	@Override
	protected void initSettings2(JPanel settingsPanel) {

	}

	@Override
	protected void initSettings3(JPanel settingsPanel) {

	}

	@Override
	protected void initSettings4(JPanel settingsPanel) {

	}

	@Override
	protected void initProperties() {
		propertiesPanel.setLayout(new GridLayout(16, 1));

		JPanel indexPanel = new JPanel();
		indexPanel.setLayout(new GridLayout(1, 2));
		indexLabel = new JLabel("index: 0 / 0");
		indexPanel.add(indexLabel);

		JPanel indexButtonPanel = new JPanel();
		indexButtonPanel.setLayout(new GridLayout(1, 2));
		JButton prevButton = new JButton("PREV");
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewport.changeIndex(-1);
			}
		});
		JButton nextButton = new JButton("NEXT");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewport.changeIndex(+1);
			}
		});
		indexButtonPanel.add(prevButton);
		indexButtonPanel.add(nextButton);
		indexPanel.add(indexButtonPanel);

		JPanel colliderXPanel = new JPanel();
		colliderXPanel.setLayout(new GridLayout(1, 2));
		JLabel colliderXLabel = new JLabel("X:");
		colliderXSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		colliderXSpinner.setToolTipText("collider X");
		colliderXPanel.add(colliderXLabel);
		colliderXPanel.add(colliderXSpinner);

		JPanel colliderYPanel = new JPanel();
		colliderYPanel.setLayout(new GridLayout(1, 2));
		JLabel colliderYLabel = new JLabel("Y:");
		colliderYSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		colliderYSpinner.setToolTipText("collider Y");
		colliderYPanel.add(colliderYLabel);
		colliderYPanel.add(colliderYSpinner);

		JPanel colliderZPanel = new JPanel();
		colliderZPanel.setLayout(new GridLayout(1, 2));
		JLabel colliderZLabel = new JLabel("Z:");
		colliderZSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		colliderZSpinner.setToolTipText("collider Z");
		colliderZPanel.add(colliderZLabel);
		colliderZPanel.add(colliderZSpinner);

		JPanel colliderSizePanel = new JPanel();
		colliderSizePanel.setLayout(new GridLayout(1, 2));
		JLabel sizeLabel = new JLabel("size:");
		colliderSizeSpinner = new JSpinner(new SpinnerNumberModel(MUL, 0f, MUL,
				STEP));
		colliderSizeSpinner.setToolTipText("collider size");
		colliderSizePanel.add(sizeLabel);
		colliderSizePanel.add(colliderSizeSpinner);

		JPanel colliderStrenghPanel = new JPanel();
		colliderStrenghPanel.setLayout(new GridLayout(1, 2));
		JLabel strenghLabel = new JLabel("strengh:");
		colliderStrenghSpinner = new JSpinner(new SpinnerNumberModel(0,
				ColliderComponent.MIN_STRENGTH, ColliderComponent.MAX_STRENGTH,
				1));
		colliderStrenghSpinner.setToolTipText("collider strength");
		colliderStrenghPanel.add(strenghLabel);
		colliderStrenghPanel.add(colliderStrenghSpinner);

		JPanel colliderRotXPanel = new JPanel();
		colliderRotXPanel.setLayout(new GridLayout(1, 3));
		JLabel colliderRotXLabel = new JLabel("Rotate X:");
		JSpinner colliderRotXSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-180f, 180f, 0.01f));
		JButton colliderApplyRotX = this.createApplyRotationButton(
				colliderRotXSpinner, Direction.Coordinate.X, settings);
		colliderRotXPanel.add(colliderRotXLabel);
		colliderRotXPanel.add(colliderRotXSpinner);
		colliderRotXPanel.add(colliderApplyRotX);

		JPanel colliderRotYPanel = new JPanel();
		colliderRotYPanel.setLayout(new GridLayout(1, 3));
		JLabel colliderRotYLabel = new JLabel("Rotate Y:");
		JSpinner colliderRotYSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-180f, 180f, 0.01f));
		JButton colliderApplyRotY = this.createApplyRotationButton(
				colliderRotYSpinner, Direction.Coordinate.Y, settings);
		colliderRotYPanel.add(colliderRotYLabel);
		colliderRotYPanel.add(colliderRotYSpinner);
		colliderRotYPanel.add(colliderApplyRotY);

		JPanel colliderRotZPanel = new JPanel();
		colliderRotZPanel.setLayout(new GridLayout(1, 3));
		JLabel colliderRotZLabel = new JLabel("Rotate Z:");
		JSpinner colliderRotZSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-180f, 180f, 0.01f));
		JButton colliderApplyRotZ = this.createApplyRotationButton(
				colliderRotZSpinner, Direction.Coordinate.Z, settings);
		colliderRotZPanel.add(colliderRotZLabel);
		colliderRotZPanel.add(colliderRotZSpinner);
		colliderRotZPanel.add(colliderApplyRotZ);

		propertiesPanel.add(indexPanel);
		propertiesPanel.add(colliderXPanel);
		propertiesPanel.add(colliderYPanel);
		propertiesPanel.add(colliderZPanel);
		propertiesPanel.add(colliderSizePanel);
		propertiesPanel.add(colliderStrenghPanel);
		propertiesPanel.add(colliderRotXPanel);
		propertiesPanel.add(colliderRotYPanel);
		propertiesPanel.add(colliderRotZPanel);
	}

	@Override
	public ObjectComponent getRotatableComponent() {
		if (viewport.getMachineFile() != null && viewport.getIndex() >= 0
				&& viewport.getIndex() < viewport.getMaxIndex())
			return viewport.getMachineFile().getCollider(viewport.getIndex());
		return null;
	}

	@Override
	public ColliderComponentEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public ColliderComponentEditorSettings getSettings() {
		return settings;
	}

	@Override
	public JSpinner getComponentXSpinner() {
		return colliderXSpinner;
	}

	@Override
	public JSpinner getComponentYSpinner() {
		return colliderYSpinner;
	}

	@Override
	public JSpinner getComponentZSpinner() {
		return colliderZSpinner;
	}

	public JSpinner getColliderSizeSpinner() {
		return colliderSizeSpinner;
	}

	public JSpinner getColliderStrenghSpinner() {
		return colliderStrenghSpinner;
	}

	public JCheckBoxMenuItem getFollowColliderMenuItem() {
		return followColliderMenuItem;
	}

	public JButton getTextureSetButton() {
		return textureSetButton;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ColliderComponentEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public float getMul() {
		return MUL;
	}
}
