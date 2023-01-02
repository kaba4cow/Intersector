package kaba4cow.editors.componenteditors.cargocomponenteditor;

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
import kaba4cow.files.ContainerGroupFile;
import kaba4cow.files.MachineFile;
import kaba4cow.files.TextureSetFile;
import kaba4cow.gameobjects.objectcomponents.ContainerComponent;
import kaba4cow.gameobjects.objectcomponents.ObjectComponent;

public class CargoComponentEditor extends AbstractEditor implements
		ComponentEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Cargo Component";

	private CargoComponentEditorViewport viewport;
	private CargoComponentEditorSettings settings;

	private JSpinner sizeSpinner;
	private JLabel indexLabel;
	private JButton cargoGroupNameButton;
	private JSpinner cargoXSpinner;
	private JSpinner cargoYSpinner;
	private JSpinner cargoZSpinner;

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

	private JCheckBoxMenuItem followCargoMenuItem;
	private JCheckBoxMenuItem rotateLocalMenuItem;
	private JCheckBoxMenuItem rotateAroundCenterMenuItem;

	public static final float MUL = 10f;
	public static final float STEP = 0.01f;

	public CargoComponentEditor() {
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
		if (file == null)
			sizeSpinner.setValue(1f);
		else
			sizeSpinner.setValue(file.getSize());
		resetCargoInfo(file);
	}

	private void resetCargoInfo(MachineFile file) {
		if (file == null || viewport.getMaxIndex() == 0) {
			cargoGroupNameButton.setText(ContainerGroupFile.getList().get(0)
					.getFileName());
			cargoXSpinner.setValue(0f);
			cargoYSpinner.setValue(0f);
			cargoZSpinner.setValue(0f);
			return;
		}
		if (viewport.getIndex() < viewport.getMaxIndex()) {
			ContainerComponent cargoInfo = file.getContainer(viewport.getIndex());
			cargoGroupNameButton.setText(cargoInfo.containerGroupName);
			cargoXSpinner.setValue(cargoInfo.pos.x * MUL);
			cargoYSpinner.setValue(cargoInfo.pos.y * MUL);
			cargoZSpinner.setValue(cargoInfo.pos.z * MUL);
		}
	}

	public void onIndexChanged() {
		MachineFile file = getViewport().getMachineFile();
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / "
				+ viewport.getMaxIndex());
		resetCargoInfo(file);
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
				file.addContainer();
				viewport.setIndex(viewport.getMaxIndex() - 1);
				onIndexChanged();
			}

			if (source == removeMenuItem) {
				file.removeContainer(viewport.getIndex());
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (file.getContainers() > 0) {
				if (source == duplicateMenuItem) {
					ContainerComponent cargoInfo = file.getContainer(viewport
							.getIndex());
					file.addContainer(new ContainerComponent(cargoInfo));
					onIndexChanged();
				}

				if (source == mirrorXMenuItem) {
					ContainerComponent cargoInfo = file.getContainer(viewport
							.getIndex());
					file.addContainer(cargoInfo.mirrorX());
					onIndexChanged();
				}

				if (source == mirrorYMenuItem) {
					ContainerComponent cargoInfo = file.getContainer(viewport
							.getIndex());
					file.addContainer(cargoInfo.mirrorY());
					onIndexChanged();
				}

				if (source == mirrorZMenuItem) {
					ContainerComponent cargoInfo = file.getContainer(viewport
							.getIndex());
					file.addContainer(cargoInfo.mirrorZ());
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

		JMenu mirrorCargoMenu = new JMenu("Mirror");
		mirrorCargoMenu.addActionListener(this);
		editMenu.add(mirrorCargoMenu);

		mirrorXMenuItem = new JMenuItem("X");
		mirrorXMenuItem.addActionListener(this);
		mirrorCargoMenu.add(mirrorXMenuItem);

		mirrorYMenuItem = new JMenuItem("Y");
		mirrorYMenuItem.addActionListener(this);
		mirrorCargoMenu.add(mirrorYMenuItem);

		mirrorZMenuItem = new JMenuItem("Z");
		mirrorZMenuItem.addActionListener(this);
		mirrorCargoMenu.add(mirrorZMenuItem);
	}

	@Override
	protected void initSettingsAndViewport() {
		settings = new CargoComponentEditorSettings(this);
		viewport = new CargoComponentEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		followCargoMenuItem = new JCheckBoxMenuItem("Follow cargo");
		followCargoMenuItem.addActionListener(this);
		settingsMenu.add(followCargoMenuItem);

		rotateLocalMenuItem = new JCheckBoxMenuItem("Rotate local");
		rotateLocalMenuItem.addActionListener(this);
		settingsMenu.add(rotateLocalMenuItem);

		rotateAroundCenterMenuItem = new JCheckBoxMenuItem(
				"Rotate around center");
		rotateAroundCenterMenuItem.addActionListener(this);
		settingsMenu.add(rotateAroundCenterMenuItem);
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

		JPanel sizePanel = new JPanel();
		sizePanel.setLayout(new GridLayout(1, 2));
		JLabel sizeLabel = new JLabel("Ship size:");
		sizeSpinner = new JSpinner(
				new SpinnerNumberModel(1f, 0f, 100000f, 0.1f));
		sizeSpinner.setToolTipText("size");
		sizePanel.add(sizeLabel);
		sizePanel.add(sizeSpinner);

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

		JPanel cargoNamePanel = new JPanel();
		cargoNamePanel.setLayout(new GridLayout(1, 2));
		JLabel cargoNameLabel = new JLabel("Cargo group:");
		cargoGroupNameButton = new JButton();
		cargoGroupNameButton.setToolTipText("cargo group file");
		cargoGroupNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(ContainerGroupFile.getList(),
						cargoGroupNameButton, "Select cargo group file");
			}
		});
		cargoNamePanel.add(cargoNameLabel);
		cargoNamePanel.add(cargoGroupNameButton);

		JPanel cargoXPanel = new JPanel();
		cargoXPanel.setLayout(new GridLayout(1, 2));
		JLabel cargoXLabel = new JLabel("X:");
		cargoXSpinner = new JSpinner(
				new SpinnerNumberModel(0f, -MUL, MUL, STEP));
		cargoXSpinner.setToolTipText("cargo X");
		cargoXPanel.add(cargoXLabel);
		cargoXPanel.add(cargoXSpinner);

		JPanel cargoYPanel = new JPanel();
		cargoYPanel.setLayout(new GridLayout(1, 2));
		JLabel cargoYLabel = new JLabel("Y:");
		cargoYSpinner = new JSpinner(
				new SpinnerNumberModel(0f, -MUL, MUL, STEP));
		cargoYSpinner.setToolTipText("cargo Y");
		cargoYPanel.add(cargoYLabel);
		cargoYPanel.add(cargoYSpinner);

		JPanel cargoZPanel = new JPanel();
		cargoZPanel.setLayout(new GridLayout(1, 2));
		JLabel cargoZLabel = new JLabel("Z:");
		cargoZSpinner = new JSpinner(
				new SpinnerNumberModel(0f, -MUL, MUL, STEP));
		cargoZSpinner.setToolTipText("cargo Z");
		cargoZPanel.add(cargoZLabel);
		cargoZPanel.add(cargoZSpinner);

		JPanel cargoRotXPanel = new JPanel();
		cargoRotXPanel.setLayout(new GridLayout(1, 3));
		JLabel cargoRotXLabel = new JLabel("Rotate X:");
		JSpinner cargoRotXSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-90f, 90f, 0.01f));
		JButton cargoApplyRotX = this.createApplyRotationButton(
				cargoRotXSpinner, Direction.Coordinate.X, settings);
		cargoRotXPanel.add(cargoRotXLabel);
		cargoRotXPanel.add(cargoRotXSpinner);
		cargoRotXPanel.add(cargoApplyRotX);

		JPanel cargoRotPosXPanel = new JPanel();
		cargoRotPosXPanel.setLayout(new GridLayout(1, 4));
		JButton cargoRotXPos1Button = createRotateButton(1f,
				Direction.Coordinate.X, getSettings());
		cargoRotPosXPanel.add(cargoRotXPos1Button);
		JButton cargoRotXPos15Button = createRotateButton(15f,
				Direction.Coordinate.X, getSettings());
		cargoRotPosXPanel.add(cargoRotXPos15Button);
		JButton cargoRotXPos30Button = createRotateButton(30f,
				Direction.Coordinate.X, getSettings());
		cargoRotPosXPanel.add(cargoRotXPos30Button);
		JButton cargoRotXPos45Button = createRotateButton(45f,
				Direction.Coordinate.X, getSettings());
		cargoRotPosXPanel.add(cargoRotXPos45Button);
		JButton cargoRotXPos90Button = createRotateButton(90f,
				Direction.Coordinate.X, getSettings());
		cargoRotPosXPanel.add(cargoRotXPos90Button);

		JPanel cargoRotNegXPanel = new JPanel();
		cargoRotNegXPanel.setLayout(new GridLayout(1, 4));
		JButton cargoRotXNeg1Button = createRotateButton(-1f,
				Direction.Coordinate.X, getSettings());
		cargoRotNegXPanel.add(cargoRotXNeg1Button);
		JButton cargoRotXNeg15Button = createRotateButton(-15f,
				Direction.Coordinate.X, getSettings());
		cargoRotNegXPanel.add(cargoRotXNeg15Button);
		JButton cargoRotXNeg30Button = createRotateButton(-30f,
				Direction.Coordinate.X, getSettings());
		cargoRotNegXPanel.add(cargoRotXNeg30Button);
		JButton cargoRotXNeg45Button = createRotateButton(-45f,
				Direction.Coordinate.X, getSettings());
		cargoRotNegXPanel.add(cargoRotXNeg45Button);
		JButton cargoRotXNeg90Button = createRotateButton(-90f,
				Direction.Coordinate.X, getSettings());
		cargoRotNegXPanel.add(cargoRotXNeg90Button);

		JPanel cargoRotYPanel = new JPanel();
		cargoRotYPanel.setLayout(new GridLayout(1, 3));
		JLabel cargoRotYLabel = new JLabel("Rotate Y:");
		JSpinner cargoRotYSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-90f, 90f, 0.01f));
		JButton cargoApplyRotY = this.createApplyRotationButton(
				cargoRotYSpinner, Direction.Coordinate.Y, settings);
		cargoRotYPanel.add(cargoRotYLabel);
		cargoRotYPanel.add(cargoRotYSpinner);
		cargoRotYPanel.add(cargoApplyRotY);

		JPanel cargoRotPosYPanel = new JPanel();
		cargoRotPosYPanel.setLayout(new GridLayout(1, 4));
		JButton cargoRotYPos1Button = createRotateButton(1f,
				Direction.Coordinate.Y, getSettings());
		cargoRotPosYPanel.add(cargoRotYPos1Button);
		JButton cargoRotYPos15Button = createRotateButton(15f,
				Direction.Coordinate.Y, getSettings());
		cargoRotPosYPanel.add(cargoRotYPos15Button);
		JButton cargoRotYPos30Button = createRotateButton(30f,
				Direction.Coordinate.Y, getSettings());
		cargoRotPosYPanel.add(cargoRotYPos30Button);
		JButton cargoRotYPos45Button = createRotateButton(45f,
				Direction.Coordinate.Y, getSettings());
		cargoRotPosYPanel.add(cargoRotYPos45Button);
		JButton cargoRotYPos90Button = createRotateButton(90f,
				Direction.Coordinate.Y, getSettings());
		cargoRotPosYPanel.add(cargoRotYPos90Button);

		JPanel cargoRotNegYPanel = new JPanel();
		cargoRotNegYPanel.setLayout(new GridLayout(1, 4));
		JButton cargoRotYNeg1Button = createRotateButton(-1f,
				Direction.Coordinate.Y, getSettings());
		cargoRotNegYPanel.add(cargoRotYNeg1Button);
		JButton cargoRotYNeg15Button = createRotateButton(-15f,
				Direction.Coordinate.Y, getSettings());
		cargoRotNegYPanel.add(cargoRotYNeg15Button);
		JButton cargoRotYNeg30Button = createRotateButton(-30f,
				Direction.Coordinate.Y, getSettings());
		cargoRotNegYPanel.add(cargoRotYNeg30Button);
		JButton cargoRotYNeg45Button = createRotateButton(-45f,
				Direction.Coordinate.Y, getSettings());
		cargoRotNegYPanel.add(cargoRotYNeg45Button);
		JButton cargoRotYNeg90Button = createRotateButton(-90f,
				Direction.Coordinate.Y, getSettings());
		cargoRotNegYPanel.add(cargoRotYNeg90Button);

		JPanel cargoRotZPanel = new JPanel();
		cargoRotZPanel.setLayout(new GridLayout(1, 3));
		JLabel cargoRotZLabel = new JLabel("Rotate Z:");
		JSpinner cargoRotZSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-90f, 90f, 0.01f));
		JButton cargoApplyRotZ = this.createApplyRotationButton(
				cargoRotZSpinner, Direction.Coordinate.Z, settings);
		cargoRotZPanel.add(cargoRotZLabel);
		cargoRotZPanel.add(cargoRotZSpinner);
		cargoRotZPanel.add(cargoApplyRotZ);

		JPanel cargoRotPosZPanel = new JPanel();
		cargoRotPosZPanel.setLayout(new GridLayout(1, 4));
		JButton cargoRotZPos1Button = createRotateButton(1f,
				Direction.Coordinate.Z, getSettings());
		cargoRotPosZPanel.add(cargoRotZPos1Button);
		JButton cargoRotZPos15Button = createRotateButton(15f,
				Direction.Coordinate.Z, getSettings());
		cargoRotPosZPanel.add(cargoRotZPos15Button);
		JButton cargoRotZPos30Button = createRotateButton(30f,
				Direction.Coordinate.Z, getSettings());
		cargoRotPosZPanel.add(cargoRotZPos30Button);
		JButton cargoRotZPos45Button = createRotateButton(45f,
				Direction.Coordinate.Z, getSettings());
		cargoRotPosZPanel.add(cargoRotZPos45Button);
		JButton cargoRotZPos90Button = createRotateButton(90f,
				Direction.Coordinate.Z, getSettings());
		cargoRotPosZPanel.add(cargoRotZPos90Button);

		JPanel cargoRotNegZPanel = new JPanel();
		cargoRotNegZPanel.setLayout(new GridLayout(1, 4));
		JButton cargoRotZNeg1Button = createRotateButton(-1f,
				Direction.Coordinate.Z, getSettings());
		cargoRotNegZPanel.add(cargoRotZNeg1Button);
		JButton cargoRotZNeg15Button = createRotateButton(-15f,
				Direction.Coordinate.Z, getSettings());
		cargoRotNegZPanel.add(cargoRotZNeg15Button);
		JButton cargoRotZNeg30Button = createRotateButton(-30f,
				Direction.Coordinate.Z, getSettings());
		cargoRotNegZPanel.add(cargoRotZNeg30Button);
		JButton cargoRotZNeg45Button = createRotateButton(-45f,
				Direction.Coordinate.Z, getSettings());
		cargoRotNegZPanel.add(cargoRotZNeg45Button);
		JButton cargoRotZNeg90Button = createRotateButton(-90f,
				Direction.Coordinate.Z, getSettings());
		cargoRotNegZPanel.add(cargoRotZNeg90Button);

		JButton cargoRotResetButton = createResetRotationsButton();

		propertiesPanel.add(sizePanel);
		propertiesPanel.add(indexPanel);
		propertiesPanel.add(cargoNamePanel);
		propertiesPanel.add(cargoXPanel);
		propertiesPanel.add(cargoYPanel);
		propertiesPanel.add(cargoZPanel);
		propertiesPanel.add(cargoRotXPanel);
		propertiesPanel.add(cargoRotPosXPanel);
		propertiesPanel.add(cargoRotNegXPanel);
		propertiesPanel.add(cargoRotYPanel);
		propertiesPanel.add(cargoRotPosYPanel);
		propertiesPanel.add(cargoRotNegYPanel);
		propertiesPanel.add(cargoRotZPanel);
		propertiesPanel.add(cargoRotPosZPanel);
		propertiesPanel.add(cargoRotNegZPanel);
		propertiesPanel.add(cargoRotResetButton);
	}

	@Override
	public ObjectComponent getRotatableComponent() {
		if (viewport.getMachineFile() != null && viewport.getIndex() >= 0
				&& viewport.getIndex() < viewport.getMaxIndex())
			return viewport.getMachineFile().getContainer(viewport.getIndex());
		return null;
	}

	@Override
	public CargoComponentEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public CargoComponentEditorSettings getSettings() {
		return settings;
	}

	public JSpinner getSizeSpinner() {
		return sizeSpinner;
	}

	public JButton getCargoGroupNameButton() {
		return cargoGroupNameButton;
	}

	@Override
	public JSpinner getComponentXSpinner() {
		return cargoXSpinner;
	}

	@Override
	public JSpinner getComponentYSpinner() {
		return cargoYSpinner;
	}

	@Override
	public JSpinner getComponentZSpinner() {
		return cargoZSpinner;
	}

	@Override
	public float getMul() {
		return MUL;
	}

	public JCheckBoxMenuItem getFollowCargoMenuItem() {
		return followCargoMenuItem;
	}

	public JCheckBoxMenuItem getRotateLocalMenuItem() {
		return rotateLocalMenuItem;
	}

	public JCheckBoxMenuItem getRotateAroundCenterMenuItem() {
		return rotateAroundCenterMenuItem;
	}

	public JButton getTextureSetButton() {
		return textureSetButton;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new CargoComponentEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
