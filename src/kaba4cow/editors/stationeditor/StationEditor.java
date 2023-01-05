package kaba4cow.editors.stationeditor;

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
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import kaba4cow.editors.AbstractEditor;
import kaba4cow.engine.MainProgram;
import kaba4cow.intersector.files.StationFile;
import kaba4cow.intersector.files.TextureSetFile;
import kaba4cow.intersector.gameobjects.machines.classes.StationClass;
import kaba4cow.intersector.toolbox.containers.RawModelContainer;

public class StationEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Station";

	private StationEditorViewport viewport;
	private StationEditorSettings settings;

	private JTextField nameTextField;
	private JButton classRankButton;
	private JButton classNameButton;
	private JSpinner sizeSpinner;
	private JSpinner healthSpinner;
	private JButton modelButton;
	private JButton glassButton;
	private JCheckBox useLightCheckbox;

	private JButton textureSetButton;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	public StationEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "machines";
	}

	@Override
	public void onNewFileLoaded() {
		StationFile file = getViewport().getStationFile();
		if (file == null) {
			nameTextField.setText("empty");
			classRankButton.setText("null");
			onItemSelected(classRankButton);
			sizeSpinner.setValue(1f);
			healthSpinner.setValue(100f);
			modelButton.setText("null");
			glassButton.setText("null");
			useLightCheckbox.setSelected(false);
		} else {
			nameTextField.setText(file.getMachineName());
			classRankButton.setText(file.getMachineClass().toString());
			classNameButton.setText(file.getMachineClassName());
			sizeSpinner.setValue(file.getSize());
			healthSpinner.setValue(file.getHealth());
			modelButton.setText(file.getMetalModel());
			glassButton.setText(file.getGlassModel());
			useLightCheckbox.setSelected(file.isUseLight());
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
		settings = new StationEditorSettings(this);
		viewport = new StationEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {

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
		propertiesPanel.setLayout(new GridLayout(17, 1));

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
		classRankButton.setToolTipText("station class");
		classRankButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectEnum(StationClass.getStringList(), classRankButton,
						"Select station class");
			}
		});
		classRankPanel.add(classRankLabel);
		classRankPanel.add(classRankButton);
		classRankPanel.add(createResetButton(classRankButton));

		JPanel classNamePanel = new JPanel();
		classNamePanel.setLayout(new GridLayout(1, 2));
		JLabel classNameLabel = new JLabel("class name:");
		classNameButton = new JButton();
		classNameButton.setToolTipText("station class name");
		classNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String[] items = StationClass
						.valueOf(classRankButton.getText()).getNames();
				selectItem(items, classNameButton, "Select station class name");
			}
		});
		classNamePanel.add(classNameLabel);
		classNamePanel.add(classNameButton);
		classNamePanel.add(createResetButton(classNameButton));

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridLayout(1, 2));
		JLabel sizeLabel = new JLabel("Station info:");
		infoPanel.add(sizeLabel);
		sizeSpinner = new JSpinner(
				new SpinnerNumberModel(1f, 0f, 100000f, 0.1f));
		sizeSpinner.setToolTipText("size");
		infoPanel.add(sizeSpinner);
		healthSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100000f,
				0.5f));
		healthSpinner.setToolTipText("health");
		infoPanel.add(healthSpinner);

		JPanel modelPanel = new JPanel();
		modelPanel.setLayout(new GridLayout(1, 2));
		JLabel modelLabel = new JLabel("model:");
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

		propertiesPanel.add(namePanel);
		propertiesPanel.add(classRankPanel);
		propertiesPanel.add(classNamePanel);
		propertiesPanel.add(infoPanel);
		propertiesPanel.add(modelPanel);
		propertiesPanel.add(glassPanel);
		propertiesPanel.add(textureSetPanel);
	}

	@Override
	protected void onItemSelected(JButton source) {
		if (source == classRankButton) {
			try {
				StationClass stationClass = StationClass
						.valueOf(classRankButton.getText());
				classNameButton.setText(stationClass.getName(0));
			} catch (Exception e) {
				classNameButton.setText("null");
			}
		}
	}

	@Override
	public StationEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public StationEditorSettings getSettings() {
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

	public JButton getModelButton() {
		return modelButton;
	}

	public JButton getGlassButton() {
		return glassButton;
	}

	public JCheckBox getUseLightCheckbox() {
		return useLightCheckbox;
	}

	public JButton getTextureSetButton() {
		return textureSetButton;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new StationEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
