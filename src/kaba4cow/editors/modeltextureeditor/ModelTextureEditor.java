package kaba4cow.editors.modeltextureeditor;

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
import javax.swing.SpinnerNumberModel;

import kaba4cow.editors.AbstractEditor;
import kaba4cow.engine.MainProgram;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.intersector.toolbox.RawModelContainer;

public class ModelTextureEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Model Texture";

	private ModelTextureEditorSettings settings;
	private ModelTextureEditorViewport viewport;

	private JSpinner shininessSpinner;
	private JSpinner shinedamperSpinner;
	private JSpinner reflectivitySpinner;
	private JSpinner emissionSpinner;
	private JCheckBox transparentCheckbox;
	private JCheckBox additiveCheckbox;

	private JButton modelButton;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	public ModelTextureEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "modeltextures";
	}

	@Override
	public void onNewFileLoaded() {
		ModelTextureFile file = getViewport().getTextureFile();
		if (file == null) {
			shininessSpinner.setValue(0f);
			shinedamperSpinner.setValue(1f);
			reflectivitySpinner.setValue(0f);
			emissionSpinner.setValue(-1f);
			transparentCheckbox.setSelected(false);
			additiveCheckbox.setSelected(false);
		} else {
			shininessSpinner.setValue(file.getShininess());
			shinedamperSpinner.setValue(file.getShineDamper());
			reflectivitySpinner.setValue(file.getReflectivity());
			emissionSpinner.setValue(file.getEmission());
			transparentCheckbox.setSelected(file.isTransparent());
			additiveCheckbox.setSelected(file.isAdditive());
		}
	}

	@Override
	protected void onActionPerformed(Object source) {
		if (source == openMenuItem) {
			File directory = new File("resources/files/" + getRootDirectory());
			JFileChooser fileChooser = new JFileChooser(directory);
			int i = fileChooser.showOpenDialog(this);
			if (i == JFileChooser.APPROVE_OPTION)
				viewport.loadNewFile(fileChooser.getSelectedFile());
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
		settings = new ModelTextureEditorSettings(this);
		viewport = new ModelTextureEditorViewport(this);
	}

	@Override
	protected void initProperties() {
		propertiesPanel.setLayout(new GridLayout(16, 1));

		JPanel shininessPanel = new JPanel();
		shininessPanel.setLayout(new GridLayout(1, 2));
		JLabel shininessLabel = new JLabel("shininess:");
		shininessSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 1f,
				0.01f));
		shininessSpinner.setToolTipText("shininess");
		shininessPanel.add(shininessLabel);
		shininessPanel.add(shininessSpinner);

		JPanel shinedamperPanel = new JPanel();
		shinedamperPanel.setLayout(new GridLayout(1, 2));
		JLabel shinedamperLabel = new JLabel("shinedamper:");
		shinedamperSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, 128f,
				1f));
		shinedamperSpinner.setToolTipText("shinedamper");
		shinedamperPanel.add(shinedamperLabel);
		shinedamperPanel.add(shinedamperSpinner);

		JPanel reflectivityPanel = new JPanel();
		reflectivityPanel.setLayout(new GridLayout(1, 2));
		JLabel reflectivityLabel = new JLabel("reflectivity:");
		reflectivitySpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 1f,
				0.01f));
		reflectivitySpinner.setToolTipText("reflectivity");
		reflectivityPanel.add(reflectivityLabel);
		reflectivityPanel.add(reflectivitySpinner);

		JPanel emissionPanel = new JPanel();
		emissionPanel.setLayout(new GridLayout(1, 2));
		JLabel emissionLabel = new JLabel("emission:");
		emissionSpinner = new JSpinner(new SpinnerNumberModel(-1f, -1f, 1f,
				0.01f));
		emissionSpinner.setToolTipText("emission");
		emissionPanel.add(emissionLabel);
		emissionPanel.add(emissionSpinner);

		JPanel transparencyPanel = new JPanel();
		transparencyPanel.setLayout(new GridLayout(1, 3));
		transparentCheckbox = new JCheckBox("transparent");
		transparentCheckbox.setToolTipText("transparent");
		additiveCheckbox = new JCheckBox("additive");
		additiveCheckbox.setToolTipText("additive");
		transparencyPanel.add(transparentCheckbox);
		transparencyPanel.add(additiveCheckbox);

		propertiesPanel.add(shininessPanel);
		propertiesPanel.add(shinedamperPanel);
		propertiesPanel.add(reflectivityPanel);
		propertiesPanel.add(emissionPanel);
		propertiesPanel.add(transparencyPanel);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {

	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {
		modelButton = new JButton();
		modelButton.setToolTipText("model");
		modelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), modelButton,
						"Select model file");
			}
		});
		modelButton.setText("MISC/model");

		settingsPanel.add(modelButton);
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
	public ModelTextureEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public ModelTextureEditorSettings getSettings() {
		return settings;
	}

	public JSpinner getShininessSpinner() {
		return shininessSpinner;
	}

	public JSpinner getShinedamperSpinner() {
		return shinedamperSpinner;
	}

	public JSpinner getReflectivitySpinner() {
		return reflectivitySpinner;
	}

	public JSpinner getEmissionSpinner() {
		return emissionSpinner;
	}

	public JCheckBox getTransparentCheckbox() {
		return transparentCheckbox;
	}

	public JCheckBox getAdditiveCheckbox() {
		return additiveCheckbox;
	}

	public JButton getModelButton() {
		return modelButton;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ModelTextureEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
