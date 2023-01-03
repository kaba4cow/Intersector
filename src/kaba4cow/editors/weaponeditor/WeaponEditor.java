package kaba4cow.editors.weaponeditor;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import org.lwjgl.util.vector.Vector3f;

import kaba4cow.editors.AbstractEditor;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.toolbox.maths.Maths;
import kaba4cow.files.ModelTextureFile;
import kaba4cow.files.ProjectileFile;
import kaba4cow.files.WeaponFile;
import kaba4cow.intersector.toolbox.RawModelContainer;

public class WeaponEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Weapon";

	private WeaponEditorViewport viewport;
	private WeaponEditorSettings settings;

	private JTextField nameTextField;
	private JSpinner sizeSpinner;
	private JSpinner fireScaleSpinner;
	private JButton staticModelButton;
	private JButton yawModelButton;
	private JButton pitchModelButton;
	private JButton projectileButton;
	private JSpinner originPointXSpinner;
	private JSpinner originPointYSpinner;
	private JSpinner originPointZSpinner;
	private JLabel indexLabel;
	private JSpinner firePointXSpinner;
	private JSpinner firePointYSpinner;
	private JSpinner firePointZSpinner;
	private JSpinner rotationSpeedSpinner;
	private JCheckBox limitPitchCheckbox;
	private JSpinner minPitchSpinner;
	private JSpinner maxPitchSpinner;
	private JCheckBox limitYawCheckbox;
	private JSpinner minYawSpinner;
	private JSpinner maxYawSpinner;
	private JSpinner damageSpinner;
	private JSpinner damageDeviationSpinner;
	private JSpinner repetitionsSpinner;
	private JSpinner reloadTimeSpinner;
	private JSpinner cooldownTimeSpinner;
	private JCheckBox emitParticleCheckbox;
	private JCheckBox automaticCheckbox;

	private JButton textureButton;

	private JSlider yawSlider;
	private JSlider pitchSlider;

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

	private JCheckBoxMenuItem showRotationMenuItem;
	private JCheckBoxMenuItem followFirePointMenuItem;

	public static final float MUL = 10f;
	public static final float STEP = 0.01f;

	public WeaponEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "weapons";
	}

	@Override
	public void onNewFileLoaded() {
		WeaponFile file = getViewport().getWeaponFile();
		indexLabel.setText("index: 1 / " + viewport.getMaxIndex());
		resetFirePointInfo(file);
		yawSlider.setValue(yawSlider.getMinimum());
		pitchSlider.setValue(pitchSlider.getMinimum());
		if (file == null) {
			nameTextField.setText("Unknown");
			sizeSpinner.setValue(1f);
			fireScaleSpinner.setValue(1f);
			originPointXSpinner.setValue(0f);
			originPointYSpinner.setValue(0f);
			originPointZSpinner.setValue(0f);
			staticModelButton.setText("null");
			yawModelButton.setText("null");
			pitchModelButton.setText("null");
			projectileButton.setText("null");
			rotationSpeedSpinner.setValue(1f);
			limitYawCheckbox.setSelected(false);
			minYawSpinner.setEnabled(true);
			maxYawSpinner.setEnabled(true);
			minYawSpinner.setValue(0f);
			maxYawSpinner.setValue(0f);
			limitPitchCheckbox.setSelected(false);
			minPitchSpinner.setEnabled(true);
			maxPitchSpinner.setEnabled(true);
			minPitchSpinner.setValue(0f);
			maxPitchSpinner.setValue(0f);
			damageSpinner.setValue(1f);
			damageDeviationSpinner.setValue(0f);
			repetitionsSpinner.setValue(1);
			reloadTimeSpinner.setValue(1f);
			cooldownTimeSpinner.setValue(1f);
			emitParticleCheckbox.setSelected(false);
			automaticCheckbox.setSelected(false);
		} else {
			nameTextField.setText(file.getName());
			sizeSpinner.setValue(file.getSize());
			fireScaleSpinner.setValue(file.getScale());
			originPointXSpinner.setValue(file.getOriginPoint().x);
			originPointYSpinner.setValue(file.getOriginPoint().y);
			originPointZSpinner.setValue(file.getOriginPoint().z);
			staticModelButton.setText(file.getStaticModel());
			yawModelButton.setText(file.getYawModel());
			pitchModelButton.setText(file.getPitchModel());
			projectileButton.setText(file.getProjectile());
			rotationSpeedSpinner.setValue(file.getRotationSpeed());
			limitYawCheckbox.setSelected(file.isLimitYaw());
			minYawSpinner.setEnabled(limitYawCheckbox.isSelected());
			maxYawSpinner.setEnabled(limitYawCheckbox.isSelected());
			minYawSpinner.setValue(file.getMinYaw() / Maths.PI);
			maxYawSpinner.setValue(file.getMaxYaw() / Maths.PI);
			limitPitchCheckbox.setSelected(file.isLimitPitch());
			minPitchSpinner.setValue(file.getMinPitch() / Maths.PI);
			maxPitchSpinner.setValue(file.getMaxPitch() / Maths.PI);
			minPitchSpinner.setEnabled(limitPitchCheckbox.isSelected());
			maxPitchSpinner.setEnabled(limitPitchCheckbox.isSelected());
			damageSpinner.setValue(file.getDamage());
			damageDeviationSpinner.setValue(file.getDamageDeviation());
			repetitionsSpinner.setValue(file.getRepeat());
			reloadTimeSpinner.setValue(file.getReload());
			cooldownTimeSpinner.setValue(file.getCooldown());
			emitParticleCheckbox.setSelected(file.isParticle());
			automaticCheckbox.setSelected(file.isAutomatic());
		}
	}

	private void resetFirePointInfo(WeaponFile file) {
		if (file == null || viewport.getMaxIndex() == 0) {
			firePointXSpinner.setValue(0f);
			firePointYSpinner.setValue(0f);
			firePointZSpinner.setValue(0f);
			return;
		}
		Vector3f firePoint = file.getFirePoint(viewport.getIndex());
		firePointXSpinner.setValue(firePoint.x * MUL);
		firePointYSpinner.setValue(firePoint.y * MUL);
		firePointZSpinner.setValue(firePoint.z * MUL);
	}

	public void onIndexChanged() {
		WeaponFile file = getViewport().getWeaponFile();
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / "
				+ viewport.getMaxIndex());
		resetFirePointInfo(file);
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

		WeaponFile file = viewport.getWeaponFile();
		if (file != null) {
			if (source == addMenuItem) {
				file.addFirePoint();
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (source == removeMenuItem) {
				file.removeFirePoint(viewport.getIndex());
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (file.getFirePoints() > 0) {
				if (source == duplicateMenuItem) {
					Vector3f firePoint = new Vector3f(
							file.getFirePoint(viewport.getIndex()));
					file.addFirePoint(firePoint);
					onIndexChanged();
				}

				if (source == mirrorXMenuItem) {
					Vector3f firePoint = new Vector3f(
							file.getFirePoint(viewport.getIndex()));
					firePoint.x *= -1f;
					file.addFirePoint(firePoint);
					onIndexChanged();
				}

				if (source == mirrorYMenuItem) {
					Vector3f firePoint = new Vector3f(
							file.getFirePoint(viewport.getIndex()));
					firePoint.y *= -1f;
					file.addFirePoint(firePoint);
					onIndexChanged();
				}

				if (source == mirrorZMenuItem) {
					Vector3f firePoint = new Vector3f(
							file.getFirePoint(viewport.getIndex()));
					firePoint.z *= -1f;
					file.addFirePoint(firePoint);
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
		settings = new WeaponEditorSettings(this);
		viewport = new WeaponEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		followFirePointMenuItem = new JCheckBoxMenuItem("Follow fire point");
		followFirePointMenuItem.addActionListener(this);
		settingsMenu.add(followFirePointMenuItem);

		showRotationMenuItem = new JCheckBoxMenuItem("Show rotation");
		showRotationMenuItem.addActionListener(this);
		settingsMenu.add(showRotationMenuItem);
	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {
		textureButton = new JButton();
		textureButton.setToolTipText("texture");
		textureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(ModelTextureFile.getList(), textureButton,
						"Select weapon texture file");
			}
		});
		textureButton.setText("METAL/0");

		settingsPanel.add(textureButton);
	}

	@Override
	protected void initSettings2(JPanel settingsPanel) {
		pitchSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, -100);
		pitchSlider.setToolTipText("pitch");

		yawSlider = new JSlider(JSlider.HORIZONTAL, -100, 100, -100);
		yawSlider.setToolTipText("yaw");

		settingsPanel.add(pitchSlider);
		settingsPanel.add(yawSlider);
	}

	@Override
	protected void initSettings3(JPanel settingsPanel) {

	}

	@Override
	protected void initSettings4(JPanel settingsPanel) {

	}

	@Override
	protected void initProperties() {
		propertiesPanel.setLayout(new GridLayout(19, 1));

		JPanel namePanel = new JPanel();
		namePanel.setLayout(new GridLayout(1, 2));
		JLabel nameLabel = new JLabel("name:");
		nameTextField = new JTextField();
		nameTextField.setToolTipText("name");
		namePanel.add(nameLabel);
		namePanel.add(nameTextField);

		JPanel sizePanel = new JPanel();
		sizePanel.setLayout(new GridLayout(1, 2));
		JLabel sizeLabel = new JLabel("size:");
		sizeSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100000f,
				0.01f));
		sizeSpinner.setToolTipText("size");
		sizePanel.add(sizeLabel);
		sizePanel.add(sizeSpinner);

		JPanel fireScalePanel = new JPanel();
		fireScalePanel.setLayout(new GridLayout(1, 2));
		JLabel fireScaleLabel = new JLabel("firescale:");
		fireScaleSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.01f));
		fireScaleSpinner.setToolTipText("fire scale");
		fireScalePanel.add(fireScaleLabel);
		fireScalePanel.add(fireScaleSpinner);

		JPanel staticModelPanel = new JPanel();
		staticModelPanel.setLayout(new GridLayout(1, 2));
		JLabel staticModelLabel = new JLabel("staticmodel:");
		staticModelButton = new JButton();
		staticModelButton.setToolTipText("static model");
		staticModelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), staticModelButton,
						"Select static model file");
			}
		});
		staticModelPanel.add(staticModelLabel);
		staticModelPanel.add(staticModelButton);
		staticModelPanel.add(createResetButton(staticModelButton));

		JPanel yawModelPanel = new JPanel();
		yawModelPanel.setLayout(new GridLayout(1, 2));
		JLabel yawModelLabel = new JLabel("yawmodel:");
		yawModelButton = new JButton();
		yawModelButton.setToolTipText("yaw model");
		yawModelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), yawModelButton,
						"Select yaw model file");
			}
		});
		yawModelPanel.add(yawModelLabel);
		yawModelPanel.add(yawModelButton);
		yawModelPanel.add(createResetButton(yawModelButton));

		JPanel pitchModelPanel = new JPanel();
		pitchModelPanel.setLayout(new GridLayout(1, 2));
		JLabel pitchModelLabel = new JLabel("pitchmodel:");
		pitchModelButton = new JButton();
		pitchModelButton.setToolTipText("pitch model");
		pitchModelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectItem(RawModelContainer.getArray(), pitchModelButton,
						"Select pitch model file");
			}
		});
		pitchModelPanel.add(pitchModelLabel);
		pitchModelPanel.add(pitchModelButton);
		pitchModelPanel.add(createResetButton(pitchModelButton));

		JPanel laserTexturePanel = new JPanel();
		laserTexturePanel.setLayout(new GridLayout(1, 2));
		JLabel laserTextureLabel = new JLabel("projectile:");
		projectileButton = new JButton();
		projectileButton.setToolTipText("projectile file");
		projectileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(ProjectileFile.getList(), projectileButton,
						"Select projectile file");

			}
		});
		laserTexturePanel.add(laserTextureLabel);
		laserTexturePanel.add(projectileButton);
		laserTexturePanel.add(createResetButton(projectileButton));

		JPanel originPointXPanel = new JPanel();
		originPointXPanel.setLayout(new GridLayout(1, 2));
		JLabel originPointXLabel = new JLabel("Origin X:");
		originPointXSpinner = new JSpinner(new SpinnerNumberModel(0f, -100f
				* MUL, 100f * MUL, STEP));
		originPointXSpinner.setToolTipText("origin X");
		originPointXPanel.add(originPointXLabel);
		originPointXPanel.add(originPointXSpinner);

		JPanel originPointYPanel = new JPanel();
		originPointYPanel.setLayout(new GridLayout(1, 2));
		JLabel originPointYLabel = new JLabel("Origin Y:");
		originPointYSpinner = new JSpinner(new SpinnerNumberModel(0f, -100f
				* MUL, 100f * MUL, STEP));
		originPointYSpinner.setToolTipText("origin Y");
		originPointYPanel.add(originPointYLabel);
		originPointYPanel.add(originPointYSpinner);

		JPanel originPointZPanel = new JPanel();
		originPointZPanel.setLayout(new GridLayout(1, 2));
		JLabel originPointZLabel = new JLabel("Origin Z:");
		originPointZSpinner = new JSpinner(new SpinnerNumberModel(0f, -100f
				* MUL, 100f * MUL, STEP));
		originPointZSpinner.setToolTipText("origin Z");
		originPointZPanel.add(originPointZLabel);
		originPointZPanel.add(originPointZSpinner);

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

		JPanel firePointXPanel = new JPanel();
		firePointXPanel.setLayout(new GridLayout(1, 2));
		JLabel firePointXLabel = new JLabel("X:");
		firePointXSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-100f * MUL, 100f * MUL, STEP));
		firePointXSpinner.setToolTipText("fire point X");
		firePointXPanel.add(firePointXLabel);
		firePointXPanel.add(firePointXSpinner);

		JPanel firePointYPanel = new JPanel();
		firePointYPanel.setLayout(new GridLayout(1, 2));
		JLabel firePointYLabel = new JLabel("Y:");
		firePointYSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-100f * MUL, 100f * MUL, STEP));
		firePointYSpinner.setToolTipText("fire point Y");
		firePointYPanel.add(firePointYLabel);
		firePointYPanel.add(firePointYSpinner);

		JPanel firePointZPanel = new JPanel();
		firePointZPanel.setLayout(new GridLayout(1, 2));
		JLabel firePointZLabel = new JLabel("Z:");
		firePointZSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-100f * MUL, 100f * MUL, STEP));
		firePointZSpinner.setToolTipText("fire point Z");
		firePointZPanel.add(firePointZLabel);
		firePointZPanel.add(firePointZSpinner);

		JPanel rotationSpeedPanel = new JPanel();
		rotationSpeedPanel.setLayout(new GridLayout(1, 2));
		JLabel rotationSpeedLabel = new JLabel("rotation speed:");
		rotationSpeedSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 10f,
				0.01f));
		rotationSpeedSpinner.setToolTipText("rotation speed");
		rotationSpeedPanel.add(rotationSpeedLabel);
		rotationSpeedPanel.add(rotationSpeedSpinner);

		JPanel pitchInfoPanel = new JPanel();
		pitchInfoPanel.setLayout(new GridLayout(1, 4));
		JLabel pitchInfoLabel = new JLabel("Pitch:");
		pitchInfoPanel.add(pitchInfoLabel);
		limitPitchCheckbox = new JCheckBox("Limit", getSettings()
				.isFollowFirePoint());
		limitPitchCheckbox.setToolTipText("limit pitch");
		limitPitchCheckbox.setVisible(true);
		limitPitchCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean enable = e.getStateChange() == 1;
				minPitchSpinner.setEnabled(enable);
				maxPitchSpinner.setEnabled(enable);
			}
		});
		pitchInfoPanel.add(limitPitchCheckbox);
		minPitchSpinner = new JSpinner(new SpinnerNumberModel(0f, -2f, 2f,
				0.01f));
		minPitchSpinner.setToolTipText("min pitch");
		pitchInfoPanel.add(minPitchSpinner);
		maxPitchSpinner = new JSpinner(new SpinnerNumberModel(0f, -2f, 2f,
				0.01f));
		maxPitchSpinner.setToolTipText("max pitch");
		pitchInfoPanel.add(maxPitchSpinner);

		JPanel yawInfoPanel = new JPanel();
		yawInfoPanel.setLayout(new GridLayout(1, 4));
		JLabel yawInfoLabel = new JLabel("Yaw:");
		yawInfoPanel.add(yawInfoLabel);
		limitYawCheckbox = new JCheckBox("Limit", getSettings()
				.isFollowFirePoint());
		limitYawCheckbox.setToolTipText("limit yaw");
		limitYawCheckbox.setVisible(true);
		limitYawCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				boolean enable = e.getStateChange() == 1;
				minYawSpinner.setEnabled(enable);
				maxYawSpinner.setEnabled(enable);
			}
		});
		yawInfoPanel.add(limitYawCheckbox);
		minYawSpinner = new JSpinner(new SpinnerNumberModel(0f, -2f, 2f, 0.01f));
		minYawSpinner.setToolTipText("min yaw");
		yawInfoPanel.add(minYawSpinner);
		maxYawSpinner = new JSpinner(new SpinnerNumberModel(0f, -2f, 2f, 0.01f));
		maxYawSpinner.setToolTipText("max yaw");
		yawInfoPanel.add(maxYawSpinner);

		JPanel damageInfoPanel = new JPanel();
		damageInfoPanel.setLayout(new GridLayout(1, 4));
		JLabel damageInfoLabel = new JLabel("Damage info:");
		damageInfoPanel.add(damageInfoLabel);
		damageSpinner = new JSpinner(
				new SpinnerNumberModel(0f, 0f, 1000f, 0.1f));
		damageSpinner.setToolTipText("damage");
		damageInfoPanel.add(damageSpinner);
		damageDeviationSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f,
				1f, 0.01f));
		damageDeviationSpinner.setToolTipText("damage deviation");
		damageInfoPanel.add(damageDeviationSpinner);
		repetitionsSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, 16f,
				1f));
		repetitionsSpinner.setToolTipText("repetitions");
		damageInfoPanel.add(repetitionsSpinner);

		JPanel fireInfoPanel = new JPanel();
		fireInfoPanel.setLayout(new GridLayout(1, 4));
		JLabel fireInfoLabel = new JLabel("Fire info:");
		fireInfoPanel.add(fireInfoLabel);
		emitParticleCheckbox = new JCheckBox("Emit particle", getSettings()
				.isFollowFirePoint());
		emitParticleCheckbox.setVisible(true);
		emitParticleCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				getViewport().getWeaponFile().setParticle(
						e.getStateChange() == 1);
			}
		});
		emitParticleCheckbox.setToolTipText("emit particle");
		fireInfoPanel.add(emitParticleCheckbox);
		automaticCheckbox = new JCheckBox("Auto fire", getSettings()
				.isFollowFirePoint());
		automaticCheckbox.setVisible(true);
		automaticCheckbox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				getViewport().getWeaponFile().setAutomatic(
						e.getStateChange() == 1);
			}
		});
		automaticCheckbox.setToolTipText("auto aim/fire");
		fireInfoPanel.add(automaticCheckbox);
		reloadTimeSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 100f,
				0.01f));
		reloadTimeSpinner.setToolTipText("reload time");
		fireInfoPanel.add(reloadTimeSpinner);
		cooldownTimeSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 100f,
				0.01f));
		cooldownTimeSpinner.setToolTipText("cooldown time");
		fireInfoPanel.add(cooldownTimeSpinner);

		propertiesPanel.add(namePanel);
		propertiesPanel.add(sizePanel);
		propertiesPanel.add(fireScalePanel);
		propertiesPanel.add(staticModelPanel);
		propertiesPanel.add(yawModelPanel);
		propertiesPanel.add(pitchModelPanel);
		propertiesPanel.add(laserTexturePanel);
		propertiesPanel.add(originPointXPanel);
		propertiesPanel.add(originPointYPanel);
		propertiesPanel.add(originPointZPanel);
		propertiesPanel.add(indexPanel);
		propertiesPanel.add(firePointXPanel);
		propertiesPanel.add(firePointYPanel);
		propertiesPanel.add(firePointZPanel);
		propertiesPanel.add(rotationSpeedPanel);
		propertiesPanel.add(pitchInfoPanel);
		propertiesPanel.add(yawInfoPanel);
		propertiesPanel.add(damageInfoPanel);
		propertiesPanel.add(fireInfoPanel);
	}

	@Override
	public WeaponEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public WeaponEditorSettings getSettings() {
		return settings;
	}

	public JTextField getNameTextField() {
		return nameTextField;
	}

	public JSpinner getSizeSpinner() {
		return sizeSpinner;
	}

	public JSpinner getFireScaleSpinner() {
		return fireScaleSpinner;
	}

	public JSpinner getOriginPointXSpinner() {
		return originPointXSpinner;
	}

	public JSpinner getOriginPointYSpinner() {
		return originPointYSpinner;
	}

	public JSpinner getOriginPointZSpinner() {
		return originPointZSpinner;
	}

	public JButton getStaticModelButton() {
		return staticModelButton;
	}

	public JButton getYawModelButton() {
		return yawModelButton;
	}

	public JButton getPitchModelButton() {
		return pitchModelButton;
	}

	public JButton getProjectileButton() {
		return projectileButton;
	}

	public JButton getTextureButton() {
		return textureButton;
	}

	public JSpinner getFirePointXSpinner() {
		return firePointXSpinner;
	}

	public JSpinner getFirePointYSpinner() {
		return firePointYSpinner;
	}

	public JSpinner getFirePointZSpinner() {
		return firePointZSpinner;
	}

	public JSpinner getDamageSpinner() {
		return damageSpinner;
	}

	public JSpinner getDamageDeviationSpinner() {
		return damageDeviationSpinner;
	}

	public JSpinner getRepetitionsSpinner() {
		return repetitionsSpinner;
	}

	public JSpinner getRotationSpeedSpinner() {
		return rotationSpeedSpinner;
	}

	public JCheckBox getLimitPitchCheckbox() {
		return limitPitchCheckbox;
	}

	public JSpinner getMinPitchSpinner() {
		return minPitchSpinner;
	}

	public JSpinner getMaxPitchSpinner() {
		return maxPitchSpinner;
	}

	public JCheckBox getLimitYawCheckbox() {
		return limitYawCheckbox;
	}

	public JSpinner getMinYawSpinner() {
		return minYawSpinner;
	}

	public JSpinner getMaxYawSpinner() {
		return maxYawSpinner;
	}

	public JSpinner getReloadTimeSpinner() {
		return reloadTimeSpinner;
	}

	public JSpinner getCooldownTimeSpinner() {
		return cooldownTimeSpinner;
	}

	public JCheckBox getAutomaticCheckbox() {
		return automaticCheckbox;
	}

	public JCheckBoxMenuItem getShowRotationMenuItem() {
		return showRotationMenuItem;
	}

	public JCheckBoxMenuItem getFollowFirePointMenuItem() {
		return followFirePointMenuItem;
	}

	public JSlider getYawSlider() {
		return yawSlider;
	}

	public JSlider getPitchSlider() {
		return pitchSlider;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new WeaponEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
