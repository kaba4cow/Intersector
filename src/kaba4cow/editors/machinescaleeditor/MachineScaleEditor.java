package kaba4cow.editors.machinescaleeditor;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import kaba4cow.editors.AbstractEditor;
import kaba4cow.engine.MainProgram;
import kaba4cow.intersector.files.MachineFile;
import kaba4cow.intersector.files.ManufacturerFile;
import kaba4cow.intersector.files.ShipFile;
import kaba4cow.intersector.files.StationFile;
import kaba4cow.intersector.files.TextureSetFile;
import kaba4cow.intersector.files.ThrustTextureFile;
import kaba4cow.intersector.gameobjects.machines.classes.ShipClass;
import kaba4cow.intersector.gameobjects.machines.classes.StationClass;
import kaba4cow.intersector.toolbox.containers.RawModelContainer;

public class MachineScaleEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Machine Scale";

	private MachineScaleEditorViewport viewport;
	private MachineScaleEditorSettings settings;

	private JLabel indexLabel;
	private JTextField nameTextField;
	private JButton manufacturerButton;
	private JButton classRankButton;
	private JButton classNameButton;
	private JSpinner sizeSpinner;
	private JSpinner healthSpinner;
	private JSpinner shieldSpinner;
	private JSpinner massSpinner;
	private JSpinner maxCargoSpinner;
	private JButton calculateMassButton;
	private JSpinner horSpeedSpinner;
	private JSpinner verSpeedSpinner;
	private JSpinner hyperSpeedSpinner;
	private JCheckBox useLightCheckbox;
	private JButton thrustButton;
	private JButton thrustTextureButton;

	private JSlider rotationSpeedXSlider;
	private JSlider rotationSpeedYSlider;
	private JSlider rotationSpeedZSlider;

	private JSlider distanceSlider;
	private JSlider thrustBrightnessSlider;
	private JButton textureSetButton;

	private JMenu fileMenu;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	private JCheckBoxMenuItem sortByHealthMenuItem;

	public MachineScaleEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "machines";
	}

	@Override
	public void onNewFileLoaded() {
		MachineFile file = getViewport().getMachineFile();
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / " + viewport.getMaxIndex());
		nameTextField.setText(file.getMachineName());
		manufacturerButton.setText(file.getManufacturer());
		classRankButton.setText(file.getMachineClass().toString());
		classNameButton.setText(file.getMachineClassName());
		sizeSpinner.setValue(file.getSize());
		healthSpinner.setValue(file.getHealth());
		shieldSpinner.setValue(file.getShield());
		massSpinner.setValue(file.getMass());
		maxCargoSpinner.setValue(file.getMaxCargo());
		if (file instanceof ShipFile) {
			ShipFile shipFile = (ShipFile) file;
			horSpeedSpinner.setValue(shipFile.getHorSpeed());
			verSpeedSpinner.setValue(shipFile.getVerSpeed());
			hyperSpeedSpinner.setValue(shipFile.getHyperSpeed());
		} else {
			horSpeedSpinner.setValue(0f);
			verSpeedSpinner.setValue(0f);
			hyperSpeedSpinner.setValue(0f);
		}
		useLightCheckbox.setSelected(file.isUseLight());
		thrustButton.setText(file.getThrust());
		thrustTextureButton.setText(file.getThrustTexture());
		updateTitle(file);
	}

	@Override
	protected void onActionPerformed(Object source) {
		if (source == saveMenuItem) {
			viewport.save();
		}

		if (source == exitMenuItem) {
			MainProgram.requestClosing();
			dispose();
		}
	}

	@Override
	protected void initMenuBar() {
		fileMenu = new JMenu("File");
		fileMenu.addActionListener(this);
		menuBar.add(fileMenu);

		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.addActionListener(this);
		fileMenu.add(saveMenuItem);

		fileMenu.add(new JSeparator());

		exitMenuItem = new JMenuItem("Exit");
		exitMenuItem.addActionListener(this);
		fileMenu.add(exitMenuItem);
	}

	@Override
	protected void initSettingsAndViewport() {
		settings = new MachineScaleEditorSettings(this);
		viewport = new MachineScaleEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		sortByHealthMenuItem = new JCheckBoxMenuItem("Sort by health");
		sortByHealthMenuItem.addActionListener(this);
		settingsMenu.add(sortByHealthMenuItem);
	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {
		distanceSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 0);
		distanceSlider.setToolTipText("distance");
		distanceSlider.setPaintTicks(true);
		distanceSlider.setSnapToTicks(true);
		distanceSlider.setMinorTickSpacing(1);
		distanceSlider.setMajorTickSpacing(5);

		thrustBrightnessSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 0);
		thrustBrightnessSlider.setToolTipText("thrust brightness");
		thrustBrightnessSlider.setPaintTicks(true);
		thrustBrightnessSlider.setSnapToTicks(true);
		thrustBrightnessSlider.setMinorTickSpacing(1);
		thrustBrightnessSlider.setMajorTickSpacing(5);
		thrustBrightnessSlider.setValue(10);

		settingsPanel.add(distanceSlider);
		settingsPanel.add(thrustBrightnessSlider);
	}

	@Override
	protected void initSettings2(JPanel settingsPanel) {
		textureSetButton = new JButton();
		textureSetButton.setToolTipText("texture set");
		textureSetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(TextureSetFile.getList(), textureSetButton, "Select texture set file");
			}
		});
		textureSetButton.setText("YELLOW0");

		settingsPanel.add(textureSetButton);
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

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new GridLayout(1, 2));
		JLabel nameLabel = new JLabel("name:");
		nameTextField = new JTextField();
		nameTextField.setToolTipText("name");
		namePanel.add(nameLabel);
		namePanel.add(nameTextField);

		JPanel classRankPanel = new JPanel();
		classRankPanel.setLayout(new GridLayout(1, 2));
		JLabel classRankLabel = new JLabel("class:");
		classRankButton = new JButton();
		classRankButton.setToolTipText("ship class");
		classRankButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MachineFile file = getViewport().getMachineFile();
				if (file instanceof ShipFile)
					selectEnum(ShipClass.getStringList(), classRankButton, "Select ship class");
				else if (file instanceof StationFile)
					selectEnum(StationClass.getStringList(), classRankButton, "Select station class");
			}
		});
		classRankPanel.add(classRankLabel);
		classRankPanel.add(classRankButton);
		classRankPanel.add(createResetButton(classRankButton));

		JPanel classNamePanel = new JPanel();
		classNamePanel.setLayout(new GridLayout(1, 2));
		JLabel classNameLabel = new JLabel("class name:");
		classNameButton = new JButton();
		classNameButton.setToolTipText("ship class");
		classNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MachineFile file = getViewport().getMachineFile();
				selectItem(file.getMachineClass().getNames(), classNameButton, "Select class name");
			}
		});
		classNamePanel.add(classNameLabel);
		classNamePanel.add(classNameButton);
		classNamePanel.add(createResetButton(classNameButton));

		JPanel manufacturerPanel = new JPanel();
		manufacturerPanel.setLayout(new GridLayout(1, 2));
		JLabel manufacturerLabel = new JLabel("manufacturer:");
		manufacturerButton = new JButton();
		manufacturerButton.setToolTipText("manufacturer");
		manufacturerButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(ManufacturerFile.getList(), manufacturerButton, "Select manufacturer");
			}
		});
		manufacturerPanel.add(manufacturerLabel);
		manufacturerPanel.add(manufacturerButton);

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(1, 4));
		JLabel infoLabel = new JLabel("Ship info:");
		infoPanel.add(infoLabel);
		sizeSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100000f, 0.1f));
		sizeSpinner.setToolTipText("size");
		infoPanel.add(sizeSpinner);
		healthSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 1000000f, 0.5f));
		healthSpinner.setToolTipText("health");
		infoPanel.add(healthSpinner);
		shieldSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 1000000f, 0.5f));
		shieldSpinner.setToolTipText("shield");
		infoPanel.add(shieldSpinner);

		JPanel massPanel = new JPanel();
		massPanel.setLayout(new GridLayout(1, 4));
		JLabel massLabel = new JLabel("Mass:");
		massPanel.add(massLabel);
		massSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 1000000f, 0.1f));
		massSpinner.setToolTipText("mass");
		massPanel.add(massSpinner);
		maxCargoSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f, 1f));
		maxCargoSpinner.setToolTipText("max cargo");
		massPanel.add(maxCargoSpinner);
		calculateMassButton = new JButton("ADJUST");
		calculateMassButton.setToolTipText("Adjust mass");
		calculateMassButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				MachineFile file = viewport.getMachineFile();
				file.calculateMass();
				massSpinner.setValue(file.getMass());
				maxCargoSpinner.setValue(file.getMaxCargo());
			}
		});
		massPanel.add(calculateMassButton);

		JPanel speedsPanel = new JPanel();
		speedsPanel.setLayout(new GridLayout(1, 4));
		JLabel speedsLabel = new JLabel("Speeds:");
		speedsPanel.add(speedsLabel);
		horSpeedSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f, 0.1f));
		horSpeedSpinner.setToolTipText("horizontal speed");
		speedsPanel.add(horSpeedSpinner);
		verSpeedSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f, 0.1f));
		verSpeedSpinner.setToolTipText("vertical speed");
		speedsPanel.add(verSpeedSpinner);
		hyperSpeedSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 2f, 0.01f));
		hyperSpeedSpinner.setToolTipText("hyper speed");
		speedsPanel.add(hyperSpeedSpinner);

		JPanel textureSetPanel = new JPanel();
		textureSetPanel.setLayout(new GridLayout(1, 2));
		JLabel textureSetLabel = new JLabel("textureset:");
		useLightCheckbox = new JCheckBox("Use light:");
		useLightCheckbox.setToolTipText("use light");
		useLightCheckbox.setVisible(true);
		textureSetPanel.add(textureSetLabel);
		textureSetPanel.add(useLightCheckbox);

		JPanel thrustPanel = new JPanel();
		thrustPanel.setLayout(new GridLayout(1, 2));
		JLabel thrustLabel = new JLabel("thrust:");
		thrustButton = new JButton();
		thrustButton.setToolTipText("thrust model");
		thrustButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), thrustButton, "Select thrust model file");
			}
		});
		thrustPanel.add(thrustLabel);
		thrustPanel.add(thrustButton);
		thrustPanel.add(createResetButton(thrustButton));

		JPanel thrustTexturePanel = new JPanel();
		thrustTexturePanel.setLayout(new GridLayout(1, 2));
		JLabel thrustTextureLabel = new JLabel("thrusttexture:");
		thrustTextureButton = new JButton();
		thrustTextureButton.setToolTipText("thrust texture");
		thrustTextureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(ThrustTextureFile.getList(), thrustTextureButton, "Select thrust texture file");
			}
		});
		thrustTexturePanel.add(thrustTextureLabel);
		thrustTexturePanel.add(thrustTextureButton);
		thrustTexturePanel.add(createResetButton(thrustTextureButton));

		propertiesPanel.add(indexPanel);
		propertiesPanel.add(namePanel);
		propertiesPanel.add(manufacturerPanel);
		propertiesPanel.add(classRankPanel);
		propertiesPanel.add(classNamePanel);
		propertiesPanel.add(infoPanel);
		propertiesPanel.add(massPanel);
		propertiesPanel.add(speedsPanel);
		propertiesPanel.add(textureSetPanel);
		propertiesPanel.add(thrustPanel);
		propertiesPanel.add(thrustTexturePanel);
	}

	@Override
	protected void onItemSelected(JButton source) {
		if (source == classRankButton)
			if (viewport.getMachineFile() instanceof ShipFile) {
				ShipClass shipClass = ShipClass.valueOf(classRankButton.getText());
				classNameButton.setText(shipClass.getName(0));
			} else if (viewport.getMachineFile() instanceof StationFile) {
				StationClass stationClass = StationClass.valueOf(classRankButton.getText());
				classNameButton.setText(stationClass.getName(0));
			}
	}

	@Override
	public MachineScaleEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public MachineScaleEditorSettings getSettings() {
		return settings;
	}

	public JTextField getNameTextField() {
		return nameTextField;
	}

	public JButton getClassRankButton() {
		return classRankButton;
	}

	public JButton getClassNameButton() {
		return classNameButton;
	}

	public JButton getManufacturerButton() {
		return manufacturerButton;
	}

	public JSpinner getMassSpinner() {
		return massSpinner;
	}

	public JSpinner getMaxCargoSpinner() {
		return maxCargoSpinner;
	}

	public JSpinner getSizeSpinner() {
		return sizeSpinner;
	}

	public JSpinner getHealthSpinner() {
		return healthSpinner;
	}

	public JSpinner getShieldSpinner() {
		return shieldSpinner;
	}

	public JSpinner getHorSpeedSpinner() {
		return horSpeedSpinner;
	}

	public JSpinner getVerSpeedSpinner() {
		return verSpeedSpinner;
	}

	public JSpinner getHyperSpeedSpinner() {
		return hyperSpeedSpinner;
	}

	public JCheckBox getUseLightCheckbox() {
		return useLightCheckbox;
	}

	public JButton getThrustButton() {
		return thrustButton;
	}

	public JButton getThrustTextureButton() {
		return thrustTextureButton;
	}

	public JSlider getRotationSpeedXSlider() {
		return rotationSpeedXSlider;
	}

	public JSlider getRotationSpeedYSlider() {
		return rotationSpeedYSlider;
	}

	public JSlider getRotationSpeedZSlider() {
		return rotationSpeedZSlider;
	}

	public JSlider getDistanceSlider() {
		return distanceSlider;
	}

	public JSlider getThrustBrightnessSlider() {
		return thrustBrightnessSlider;
	}

	public JCheckBoxMenuItem getSortByHealthMenuItem() {
		return sortByHealthMenuItem;
	}

	public JButton getTextureSetButton() {
		return textureSetButton;
	}

	public static void main(String[] args) {
		new MachineScaleEditor();
	}
}
