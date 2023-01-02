package kaba4cow.editors.shipeditor;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
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
import kaba4cow.files.ShipFile;
import kaba4cow.files.TextureSetFile;
import kaba4cow.files.ThrustTextureFile;
import kaba4cow.gameobjects.machines.classes.ShipClass;
import kaba4cow.toolbox.RawModelContainer;

public class ShipEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Ship";

	private ShipEditorViewport viewport;
	private ShipEditorSettings settings;

	private JTextField nameTextField;
	private JButton classRankButton;
	private JButton classNameButton;
	private JSpinner sizeSpinner;
	private JSpinner healthSpinner;
	private JSpinner shieldSpinner;
	private JSpinner horSpeedSpinner;
	private JSpinner verSpeedSpinner;
	private JSpinner hyperSpeedSpinner;
	private JSpinner horThrustSpinner;
	private JSpinner horBrakeSpinner;
	private JSpinner verThrustSpinner;
	private JSpinner verBrakeSpinner;
	private JSpinner hyperThrustSpinner;
	private JSpinner aftPowerSpinner;
	private JSpinner aftTimeSpinner;
	private JSpinner aftCooldownSpinner;
	private JSpinner aftSmoothnessSpinner;
	private JSpinner pitchSensSpinner;
	private JSpinner yawSensSpinner;
	private JSpinner rollSensSpinner;
	private JButton modelButton;
	private JButton glassButton;
	private JCheckBox useLightCheckbox;
	private JButton thrustButton;
	private JButton thrustTextureButton;

	private JSlider thrustBrightnessSlider;
	private JButton textureSetButton;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	public ShipEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "machines";
	}

	@Override
	public void onNewFileLoaded() {
		ShipFile file = getViewport().getShipFile();
		thrustBrightnessSlider.setValue(10);
		if (file == null) {
			nameTextField.setText("Unknown");
			classRankButton.setText("null");
			onItemSelected(classRankButton);
			sizeSpinner.setValue(1f);
			healthSpinner.setValue(100f);
			shieldSpinner.setValue(0f);
			horSpeedSpinner.setValue(1f);
			verSpeedSpinner.setValue(1f);
			hyperSpeedSpinner.setValue(1f);
			horThrustSpinner.setValue(1f);
			horBrakeSpinner.setValue(1f);
			verThrustSpinner.setValue(1f);
			verBrakeSpinner.setValue(1f);
			hyperThrustSpinner.setValue(1f);
			aftPowerSpinner.setValue(1f);
			aftTimeSpinner.setValue(1f);
			aftCooldownSpinner.setValue(1f);
			aftSmoothnessSpinner.setValue(1f);
			pitchSensSpinner.setValue(1f);
			yawSensSpinner.setValue(1f);
			rollSensSpinner.setValue(1f);
			modelButton.setText("null");
			glassButton.setText("null");
			useLightCheckbox.setSelected(false);
			thrustButton.setText("null");
			thrustTextureButton.setText("null");
		} else {
			nameTextField.setText(file.getMachineName());
			classRankButton.setText(file.getMachineClass().toString());
			classNameButton.setText(file.getMachineClassName());
			sizeSpinner.setValue(file.getSize());
			healthSpinner.setValue(file.getHealth());
			shieldSpinner.setValue(file.getShield());
			horSpeedSpinner.setValue(file.getHorSpeed());
			verSpeedSpinner.setValue(file.getVerSpeed());
			hyperSpeedSpinner.setValue(file.getHyperSpeed());
			horThrustSpinner.setValue(file.getHorThrust());
			horBrakeSpinner.setValue(file.getHorBrake());
			verThrustSpinner.setValue(file.getVerThrust());
			verBrakeSpinner.setValue(file.getVerBrake());
			hyperThrustSpinner.setValue(file.getHyperThrust());
			aftPowerSpinner.setValue(file.getAftPower());
			aftTimeSpinner.setValue(file.getAftTime());
			aftCooldownSpinner.setValue(file.getAftCooldown());
			aftSmoothnessSpinner.setValue(file.getAftSmoothness());
			pitchSensSpinner.setValue(file.getPitchSens());
			yawSensSpinner.setValue(file.getYawSens());
			rollSensSpinner.setValue(file.getRollSens());
			modelButton.setText(file.getMetalModel());
			glassButton.setText(file.getGlassModel());
			useLightCheckbox.setSelected(file.isUseLight());
			thrustButton.setText(file.getThrust());
			thrustTextureButton.setText(file.getThrustTexture());
		}
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
	}

	@Override
	protected void initSettingsAndViewport() {
		settings = new ShipEditorSettings(this);
		viewport = new ShipEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {

	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {
		thrustBrightnessSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 0);
		thrustBrightnessSlider.setToolTipText("thrust brightness");
		thrustBrightnessSlider.setPaintTicks(true);
		thrustBrightnessSlider.setSnapToTicks(true);
		thrustBrightnessSlider.setMinorTickSpacing(1);
		thrustBrightnessSlider.setMajorTickSpacing(5);

		settingsPanel.add(thrustBrightnessSlider);
	}

	@Override
	protected void initSettings2(JPanel settingsPanel) {
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
	protected void initSettings3(JPanel settingsPanel) {

	}

	@Override
	protected void initSettings4(JPanel settingsPanel) {

	}

	@Override
	protected void initProperties() {
		propertiesPanel.setLayout(new GridLayout(16, 1));

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
				selectEnum(ShipClass.getStringList(), classRankButton,
						"Select ship class");
			}
		});
		classRankPanel.add(classRankLabel);
		classRankPanel.add(classRankButton);
		classRankPanel.add(createResetButton(classRankButton));

		JPanel classNamePanel = new JPanel();
		classNamePanel.setLayout(new GridLayout(1, 2));
		JLabel classNameLabel = new JLabel("class name:");
		classNameButton = new JButton();
		classNameButton.setToolTipText("ship class name");
		classNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] items = ShipClass.valueOf(classRankButton.getText())
						.getNames();
				selectItem(items, classNameButton, "Select ship class name");
			}
		});
		classNamePanel.add(classNameLabel);
		classNamePanel.add(classNameButton);
		classNamePanel.add(createResetButton(classNameButton));

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(1, 4));
		JLabel sizeLabel = new JLabel("Ship info:");
		infoPanel.add(sizeLabel);
		sizeSpinner = new JSpinner(
				new SpinnerNumberModel(1f, 0f, 100000f, 0.1f));
		sizeSpinner.setToolTipText("size");
		infoPanel.add(sizeSpinner);
		healthSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100000f,
				0.5f));
		healthSpinner.setToolTipText("health");
		infoPanel.add(healthSpinner);
		shieldSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100000f,
				0.5f));
		shieldSpinner.setToolTipText("shield");
		infoPanel.add(shieldSpinner);

		JPanel speedsPanel = new JPanel();
		speedsPanel.setLayout(new GridLayout(1, 4));
		JLabel speedsLabel = new JLabel("Speeds:");
		speedsPanel.add(speedsLabel);
		horSpeedSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f,
				0.1f));
		horSpeedSpinner.setToolTipText("horizontal speed");
		speedsPanel.add(horSpeedSpinner);
		verSpeedSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f,
				0.1f));
		verSpeedSpinner.setToolTipText("vertical speed");
		speedsPanel.add(verSpeedSpinner);
		hyperSpeedSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 2f,
				0.01f));
		hyperSpeedSpinner.setToolTipText("hyper speed");
		speedsPanel.add(hyperSpeedSpinner);

		JPanel thrustsPanel = new JPanel();
		thrustsPanel.setLayout(new GridLayout(1, 4));
		JLabel horThrustLabel = new JLabel("Thrusts:");
		thrustsPanel.add(horThrustLabel);
		horThrustSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 16f,
				0.01f));
		horThrustSpinner.setToolTipText("horizontal thrust");
		thrustsPanel.add(horThrustSpinner);
		verThrustSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 16f,
				0.01f));
		verThrustSpinner.setToolTipText("vertical thrust");
		thrustsPanel.add(verThrustSpinner);
		hyperThrustSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 16f,
				0.01f));
		hyperThrustSpinner.setToolTipText("hyper thrust");
		thrustsPanel.add(hyperThrustSpinner);

		JPanel brakesPanel = new JPanel();
		brakesPanel.setLayout(new GridLayout(1, 3));
		JLabel horBrakeLabel = new JLabel("Brakes:");
		brakesPanel.add(horBrakeLabel);
		horBrakeSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 16f,
				0.01f));
		horBrakeSpinner.setToolTipText("horizontal brake");
		brakesPanel.add(horBrakeSpinner);
		verBrakeSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 16f,
				0.01f));
		verBrakeSpinner.setToolTipText("vertical brake");
		brakesPanel.add(verBrakeSpinner);

		JPanel aftPanel = new JPanel();
		aftPanel.setLayout(new GridLayout(1, 5));
		JLabel aftLabel = new JLabel("Afterburner:");
		aftPanel.add(aftLabel);
		aftPowerSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.1f));
		aftPowerSpinner.setToolTipText("power");
		aftPanel.add(aftPowerSpinner);
		aftTimeSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10f, 0.1f));
		aftTimeSpinner.setToolTipText("time");
		aftPanel.add(aftTimeSpinner);
		aftCooldownSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.1f));
		aftCooldownSpinner.setToolTipText("cooldown");
		aftPanel.add(aftCooldownSpinner);
		aftSmoothnessSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, 16f,
				0.1f));
		aftSmoothnessSpinner.setToolTipText("smoothness");
		aftPanel.add(aftSmoothnessSpinner);

		JPanel rotSensPanel = new JPanel();
		rotSensPanel.setLayout(new GridLayout(1, 4));
		JLabel rotSensLabel = new JLabel("Rot sensitivity:");
		rotSensPanel.add(rotSensLabel);
		pitchSensSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 8f,
				0.001f));
		pitchSensSpinner.setToolTipText("pitch sensitivity");
		rotSensPanel.add(pitchSensSpinner);
		yawSensSpinner = new JSpinner(
				new SpinnerNumberModel(1f, 0f, 8f, 0.001f));
		yawSensSpinner.setToolTipText("yaw sensitivity");
		rotSensPanel.add(yawSensSpinner);
		rollSensSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 8f,
				0.001f));
		rollSensSpinner.setToolTipText("roll sensitivity");
		rotSensPanel.add(rollSensSpinner);

		JPanel modelPanel = new JPanel();
		modelPanel.setLayout(new GridLayout(1, 2));
		JLabel modelLabel = new JLabel("metal:");
		modelButton = new JButton();
		modelButton.setToolTipText("metal model");
		modelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), modelButton,
						"Select metal model file");
			}
		});
		modelPanel.add(modelLabel);
		modelPanel.add(modelButton);
		modelPanel.add(createResetButton(modelButton));

		JPanel glassPanel = new JPanel();
		glassPanel.setLayout(new GridLayout(1, 2));
		JLabel glassLabel = new JLabel("glass:");
		glassButton = new JButton();
		glassButton.setToolTipText("glass model");
		glassButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), glassButton,
						"Select glass model file");
			}
		});
		glassPanel.add(glassLabel);
		glassPanel.add(glassButton);
		glassPanel.add(createResetButton(glassButton));

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
				selectItem(RawModelContainer.getArray(), thrustButton,
						"Select thrust model file");
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
				selectAssetFileName(ThrustTextureFile.getList(),
						thrustTextureButton, "Select thrust texture file");
			}
		});
		thrustTexturePanel.add(thrustTextureLabel);
		thrustTexturePanel.add(thrustTextureButton);
		thrustTexturePanel.add(createResetButton(thrustTextureButton));

		propertiesPanel.add(namePanel);
		propertiesPanel.add(classRankPanel);
		propertiesPanel.add(classNamePanel);
		propertiesPanel.add(infoPanel);
		propertiesPanel.add(speedsPanel);
		propertiesPanel.add(thrustsPanel);
		propertiesPanel.add(brakesPanel);
		propertiesPanel.add(aftPanel);
		propertiesPanel.add(rotSensPanel);
		propertiesPanel.add(modelPanel);
		propertiesPanel.add(glassPanel);
		propertiesPanel.add(textureSetPanel);
		propertiesPanel.add(thrustPanel);
		propertiesPanel.add(thrustTexturePanel);
	}

	@Override
	protected void onItemSelected(JButton source) {
		if (source == classRankButton) {
			try {
				ShipClass shipClass = ShipClass.valueOf(classRankButton
						.getText());
				classNameButton.setText(shipClass.getName(0));
			} catch (Exception e) {
				classNameButton.setText("null");
			}
		}
	}

	@Override
	public ShipEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public ShipEditorSettings getSettings() {
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

	public JSpinner getHorThrustSpinner() {
		return horThrustSpinner;
	}

	public JSpinner getHorBrakeSpinner() {
		return horBrakeSpinner;
	}

	public JSpinner getVerThrustSpinner() {
		return verThrustSpinner;
	}

	public JSpinner getVerBrakeSpinner() {
		return verBrakeSpinner;
	}

	public JSpinner getHyperThrustSpinner() {
		return hyperThrustSpinner;
	}

	public JSpinner getAftPowerSpinner() {
		return aftPowerSpinner;
	}

	public JSpinner getAftTimeSpinner() {
		return aftTimeSpinner;
	}

	public JSpinner getAftCooldownSpinner() {
		return aftCooldownSpinner;
	}

	public JSpinner getAftSmoothnessSpinner() {
		return aftSmoothnessSpinner;
	}

	public JSpinner getPitchSensSpinner() {
		return pitchSensSpinner;
	}

	public JSpinner getYawSensSpinner() {
		return yawSensSpinner;
	}

	public JSpinner getRollSensSpinner() {
		return rollSensSpinner;
	}

	public JButton getModelButton() {
		return modelButton;
	}

	public JButton getGlassButton() {
		return glassButton;
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

	public JSlider getThrustBrightnessSlider() {
		return thrustBrightnessSlider;
	}

	public JButton getTextureSetButton() {
		return textureSetButton;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ShipEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
