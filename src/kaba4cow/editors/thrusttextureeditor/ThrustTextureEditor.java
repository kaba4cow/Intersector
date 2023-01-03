package kaba4cow.editors.thrusttextureeditor;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import kaba4cow.editors.AbstractEditor;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.audio.AudioManager;
import kaba4cow.engine.audio.Source;
import kaba4cow.files.GameFile;
import kaba4cow.files.ThrustTextureFile;
import kaba4cow.intersector.toolbox.Constants;
import kaba4cow.intersector.toolbox.RawModelContainer;
import kaba4cow.intersector.toolbox.SoundContainer;

public class ThrustTextureEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Thrust Texture";

	private ThrustTextureEditorSettings settings;
	private ThrustTextureEditorViewport viewport;

	private JSpinner speedSpinner;
	private JButton soundButton;

	private JButton modelButton;
	private JSlider thrustBrightnessSlider;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	private Source source;

	public ThrustTextureEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "thrusttextures";
	}

	@Override
	public void onNewFileLoaded() {
		ThrustTextureFile file = getViewport().getTextureFile();
		thrustBrightnessSlider.setValue(10);
		if (file == null) {
			speedSpinner.setValue(0f);
			soundButton.setText("null");
		} else {
			speedSpinner.setValue(file.getSpeed());
			soundButton.setText(file.getSound());
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
		settings = new ThrustTextureEditorSettings(this);
		viewport = new ThrustTextureEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {

	}

	@Override
	protected void initProperties() {
		propertiesPanel.setLayout(new GridLayout(16, 1));

		JPanel speedPanel = new JPanel();
		speedPanel.setLayout(new GridLayout(1, 2));
		JLabel speedLabel = new JLabel("speed:");
		speedSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 1f, 0.001f));
		speedSpinner.setToolTipText("speed");
		speedPanel.add(speedLabel);
		speedPanel.add(speedSpinner);

		JPanel soundPanel = new JPanel();
		soundPanel.setLayout(new GridLayout(1, 3));
		JLabel soundLabel = new JLabel("sound:");
		soundButton = new JButton();
		soundButton.setToolTipText("sound");
		soundButton.setVisible(true);
		soundButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(SoundContainer.getArray(), soundButton,
						"Select sound");
			}
		});
		JButton soundPlayButton = new JButton("PLAY");
		soundPlayButton.setToolTipText("play");
		soundPlayButton.setVisible(true);
		soundPlayButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (source == null)
					source = new Source(Constants.GAMEPLAY).setGain(0.25f);
				if (!GameFile.isNull(soundButton.getText()))
					source.play(AudioManager.get(soundButton.getText()));
			}
		});
		soundPanel.add(soundLabel);
		soundPanel.add(soundButton);
		soundPanel.add(soundPlayButton);
		soundPanel.add(createResetButton(soundButton));

		propertiesPanel.add(speedPanel);
		propertiesPanel.add(soundPanel);
	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {
		modelButton = new JButton();
		modelButton.setToolTipText("model");
		modelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), modelButton,
						"Select thrust model file");
			}
		});
		modelButton.setText("THRUST/0");

		thrustBrightnessSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 10);
		thrustBrightnessSlider.setToolTipText("thrust brightness");
		thrustBrightnessSlider.setPaintTicks(true);
		thrustBrightnessSlider.setSnapToTicks(true);
		thrustBrightnessSlider.setMinorTickSpacing(1);
		thrustBrightnessSlider.setMajorTickSpacing(5);

		settingsPanel.add(modelButton);
		settingsPanel.add(thrustBrightnessSlider);
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
	public ThrustTextureEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public ThrustTextureEditorSettings getSettings() {
		return settings;
	}

	public JSlider getThrustBrightnessSlider() {
		return thrustBrightnessSlider;
	}

	public JSpinner getSpeedSpinner() {
		return speedSpinner;
	}

	public JButton getSoundButton() {
		return soundButton;
	}

	public JButton getModelButton() {
		return modelButton;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ThrustTextureEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
