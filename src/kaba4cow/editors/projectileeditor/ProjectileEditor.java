package kaba4cow.editors.projectileeditor;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
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
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.audio.AudioManager;
import kaba4cow.engine.audio.Source;
import kaba4cow.files.GameFile;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.files.ProjectileFile;
import kaba4cow.files.ThrustTextureFile;
import kaba4cow.intersector.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.intersector.gameobjects.projectiles.ProjectileType;
import kaba4cow.intersector.toolbox.Constants;
import kaba4cow.intersector.toolbox.RawModelContainer;
import kaba4cow.intersector.toolbox.SoundContainer;

public class ProjectileEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Projectile";

	private ProjectileEditorViewport viewport;
	private ProjectileEditorSettings settings;

	private JButton projectileButton;
	private JButton projectileTextureButton;
	private JButton projectileTypeButton;
	private JButton soundButton;
	private JCheckBox explodeCheckbox;
	private JCheckBox autoAimCheckbox;
	private JSpinner aimingSpinner;
	private JSpinner delaySpinner;
	private JSpinner lifeLengthSpinner;
	private JSpinner speedScaleSpinner;
	private JButton thrustButton;
	private JButton thrustTextureButton;
	private JSpinner thrustXSpinner;
	private JSpinner thrustYSpinner;
	private JSpinner thrustZSpinner;
	private JSpinner thrustSizeSpinner;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	private JCheckBoxMenuItem followThrustMenuItem;

	private Source source;

	public ProjectileEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "projectiles";
	}

	@Override
	public void onNewFileLoaded() {
		ProjectileFile file = getViewport().getProjectileFile();
		if (file == null) {
			projectileButton.setText("null");
			projectileTextureButton.setText("null");
			projectileTypeButton.setText(ProjectileType.PROJECTILE.toString());
			soundButton.setText("null");
			explodeCheckbox.setSelected(false);
			autoAimCheckbox.setSelected(false);
			aimingSpinner.setValue(0f);
			delaySpinner.setValue(0f);
			lifeLengthSpinner.setValue(10f);
			speedScaleSpinner.setValue(1f);
			thrustButton.setText("null");
			thrustTextureButton.setText("null");
			thrustXSpinner.setValue(0f);
			thrustYSpinner.setValue(0f);
			thrustZSpinner.setValue(0f);
			thrustSizeSpinner.setValue(1f);
		} else {
			projectileButton.setText(file.getModel());
			projectileTextureButton.setText(file.getTexture());
			projectileTypeButton.setText(file.getType());
			soundButton.setText(file.getSound());
			explodeCheckbox.setSelected(file.isExplode());
			autoAimCheckbox.setSelected(file.isAutoaim());
			aimingSpinner.setValue(file.getAiming());
			delaySpinner.setValue(file.getDelay());
			lifeLengthSpinner.setValue(file.getLifeLength());
			speedScaleSpinner.setValue(file.getSpeedScale());
			thrustButton.setText(file.getThrust());
			thrustTextureButton.setText(file.getThrustTexture());
			ThrustComponent thrustInfo = file.getThrustComponent();
			if (thrustInfo != null) {
				thrustXSpinner.setValue(thrustInfo.pos.x);
				thrustYSpinner.setValue(thrustInfo.pos.y);
				thrustZSpinner.setValue(thrustInfo.pos.z);
				thrustSizeSpinner.setValue(thrustInfo.size);
			} else {
				thrustXSpinner.setValue(0f);
				thrustYSpinner.setValue(0f);
				thrustZSpinner.setValue(0f);
				thrustSizeSpinner.setValue(1f);
			}
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
		settings = new ProjectileEditorSettings(this);
		viewport = new ProjectileEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		followThrustMenuItem = new JCheckBoxMenuItem("Follow thrust");
		followThrustMenuItem.addActionListener(this);
		settingsMenu.add(followThrustMenuItem);
	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {

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

		JPanel projectilePanel = new JPanel();
		projectilePanel.setLayout(new GridLayout(1, 2));
		JLabel projectileLabel = new JLabel("model:");
		projectileButton = new JButton();
		projectileButton.setToolTipText("projectile model");
		projectileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), projectileButton,
						"Select projectile model file");
			}
		});
		projectilePanel.add(projectileLabel);
		projectilePanel.add(projectileButton);
		projectilePanel.add(createResetButton(projectileButton));

		JPanel projectileTexturePanel = new JPanel();
		projectileTexturePanel.setLayout(new GridLayout(1, 2));
		JLabel projectileTextureLabel = new JLabel("texture:");
		projectileTextureButton = new JButton();
		projectileTextureButton.setToolTipText("projectile texture");
		projectileTextureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(ModelTextureFile.getList(),
						projectileTextureButton,
						"Select projectile texture file");
			}
		});
		projectileTexturePanel.add(projectileTextureLabel);
		projectileTexturePanel.add(projectileTextureButton);
		projectileTexturePanel.add(createResetButton(projectileTextureButton));

		JPanel projectileTypePanel = new JPanel();
		projectileTypePanel.setLayout(new GridLayout(1, 2));
		JLabel projectileTypeLabel = new JLabel("type:");
		projectileTypeButton = new JButton();
		projectileTypeButton.setToolTipText("projectile type");
		projectileTypeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectEnum(ProjectileType.getStringList(),
						projectileTypeButton, "Select projectile type");
			}
		});
		projectileTypePanel.add(projectileTypeLabel);
		projectileTypePanel.add(projectileTypeButton);
		projectileTypePanel.add(createResetButton(projectileTypeButton));

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

		JPanel fireinfoPanel = new JPanel();
		fireinfoPanel.setLayout(new GridLayout(1, 5));
		explodeCheckbox = new JCheckBox("Explode", false);
		explodeCheckbox.setToolTipText("explode");
		explodeCheckbox.setVisible(true);
		fireinfoPanel.add(explodeCheckbox);
		autoAimCheckbox = new JCheckBox("Auto aim", false);
		autoAimCheckbox.setToolTipText("auto aim");
		autoAimCheckbox.setVisible(true);
		fireinfoPanel.add(autoAimCheckbox);
		aimingSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 16f, 0.01f));
		aimingSpinner.setToolTipText("aiming");
		fireinfoPanel.add(aimingSpinner);
		delaySpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 10f, 0.01f));
		delaySpinner.setToolTipText("delay");
		fireinfoPanel.add(delaySpinner);

		JPanel lifeLengthPanel = new JPanel();
		lifeLengthPanel.setLayout(new GridLayout(1, 2));
		JLabel lifeLengthLabel = new JLabel("Life length:");
		lifeLengthSpinner = new JSpinner(new SpinnerNumberModel(10f, 0f, 30f,
				0.01f));
		lifeLengthSpinner.setToolTipText("life length");
		lifeLengthPanel.add(lifeLengthLabel);
		lifeLengthPanel.add(lifeLengthSpinner);

		JPanel speedScalePanel = new JPanel();
		speedScalePanel.setLayout(new GridLayout(1, 2));
		JLabel speedScaleLabel = new JLabel("Speed scale:");
		speedScaleSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10f,
				0.01f));
		speedScaleSpinner.setToolTipText("speed scale");
		speedScalePanel.add(speedScaleLabel);
		speedScalePanel.add(speedScaleSpinner);

		JPanel thrustPanel = new JPanel();
		thrustPanel.setLayout(new GridLayout(1, 2));
		JLabel thrustLabel = new JLabel("thrust model:");
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
		JLabel thrustTextureLabel = new JLabel("thrust texture:");
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

		JPanel thrustXPanel = new JPanel();
		thrustXPanel.setLayout(new GridLayout(1, 2));
		JLabel thrustXLabel = new JLabel("X:");
		thrustXSpinner = new JSpinner(
				new SpinnerNumberModel(0f, -1f, 1f, 0.01f));
		thrustXSpinner.setToolTipText("thrust X");
		thrustXPanel.add(thrustXLabel);
		thrustXPanel.add(thrustXSpinner);

		JPanel thrustYPanel = new JPanel();
		thrustYPanel.setLayout(new GridLayout(1, 2));
		JLabel thrustYLabel = new JLabel("Y:");
		thrustYSpinner = new JSpinner(
				new SpinnerNumberModel(0f, -1f, 1f, 0.01f));
		thrustYSpinner.setToolTipText("thrust Y");
		thrustYPanel.add(thrustYLabel);
		thrustYPanel.add(thrustYSpinner);

		JPanel thrustZPanel = new JPanel();
		thrustZPanel.setLayout(new GridLayout(1, 2));
		JLabel thrustZLabel = new JLabel("Z:");
		thrustZSpinner = new JSpinner(
				new SpinnerNumberModel(0f, -1f, 1f, 0.01f));
		thrustZSpinner.setToolTipText("thrust Z");
		thrustZPanel.add(thrustZLabel);
		thrustZPanel.add(thrustZSpinner);

		JPanel thrustSizePanel = new JPanel();
		thrustSizePanel.setLayout(new GridLayout(1, 2));
		JLabel sizeLabel = new JLabel("size:");
		thrustSizeSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 1f,
				0.01f));
		thrustSizeSpinner.setToolTipText("thrust size");
		thrustSizePanel.add(sizeLabel);
		thrustSizePanel.add(thrustSizeSpinner);

		propertiesPanel.add(projectilePanel);
		propertiesPanel.add(projectileTexturePanel);
		propertiesPanel.add(projectileTypePanel);
		propertiesPanel.add(soundPanel);
		propertiesPanel.add(fireinfoPanel);
		propertiesPanel.add(lifeLengthPanel);
		propertiesPanel.add(speedScalePanel);
		propertiesPanel.add(thrustPanel);
		propertiesPanel.add(thrustTexturePanel);
		propertiesPanel.add(thrustXPanel);
		propertiesPanel.add(thrustYPanel);
		propertiesPanel.add(thrustZPanel);
		propertiesPanel.add(thrustSizePanel);
	}

	@Override
	public ProjectileEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public ProjectileEditorSettings getSettings() {
		return settings;
	}

	public JButton getProjectileButton() {
		return projectileButton;
	}

	public JButton getProjectileTextureButton() {
		return projectileTextureButton;
	}

	public JButton getSoundButton() {
		return soundButton;
	}

	public JCheckBox getExplodeCheckbox() {
		return explodeCheckbox;
	}

	public JCheckBox getAutoAimCheckbox() {
		return autoAimCheckbox;
	}

	public JSpinner getAimingSpinner() {
		return aimingSpinner;
	}

	public JSpinner getDelaySpinner() {
		return delaySpinner;
	}

	public JSpinner getLifeLengthSpinner() {
		return lifeLengthSpinner;
	}

	public JSpinner getSpeedScaleSpinner() {
		return speedScaleSpinner;
	}

	public JButton getThrustButton() {
		return thrustButton;
	}

	public JButton getThrustTextureButton() {
		return thrustTextureButton;
	}

	public JButton getProjectileTypeButton() {
		return projectileTypeButton;
	}

	public JSpinner getThrustXSpinner() {
		return thrustXSpinner;
	}

	public JSpinner getThrustYSpinner() {
		return thrustYSpinner;
	}

	public JSpinner getThrustZSpinner() {
		return thrustZSpinner;
	}

	public JSpinner getThrustSizeSpinner() {
		return thrustSizeSpinner;
	}

	public JCheckBoxMenuItem getFollowThrustMenuItem() {
		return followThrustMenuItem;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ProjectileEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
