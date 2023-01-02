package kaba4cow.editors.weaponscaleeditor;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
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
import kaba4cow.files.ModelTextureFile;
import kaba4cow.files.WeaponFile;

public class WeaponScaleEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Weapon Scale";

	private WeaponScaleEditorViewport viewport;
	private WeaponScaleEditorSettings settings;

	private JLabel indexLabel;
	private JSpinner sizeSpinner;
	private JSpinner damageSpinner;
	private JSpinner damageDeviationSpinner;
	private JSpinner repetitionsSpinner;
	private JSpinner reloadTimeSpinner;
	private JSpinner cooldownTimeSpinner;

	private JSlider distanceSlider;

	private JButton textureButton;

	private JMenu fileMenu;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	private JCheckBoxMenuItem sortByDamageMenuItem;

	public WeaponScaleEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "weapons";
	}

	@Override
	public void onNewFileLoaded() {
		WeaponFile file = getViewport().getWeaponFile();
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / "
				+ viewport.getMaxIndex());
		sizeSpinner.setValue(file.getSize());
		damageSpinner.setValue(file.getDamage());
		damageDeviationSpinner.setValue(file.getDamageDeviation());
		repetitionsSpinner.setValue(file.getRepeat());
		reloadTimeSpinner.setValue(file.getReload());
		cooldownTimeSpinner.setValue(file.getCooldown());
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
		settings = new WeaponScaleEditorSettings(this);
		viewport = new WeaponScaleEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		sortByDamageMenuItem = new JCheckBoxMenuItem("Sort by damage");
		sortByDamageMenuItem.addActionListener(this);
		settingsMenu.add(sortByDamageMenuItem);
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
		distanceSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 0);
		distanceSlider.setToolTipText("distance");
		distanceSlider.setPaintTicks(true);
		distanceSlider.setSnapToTicks(true);
		distanceSlider.setMinorTickSpacing(1);
		distanceSlider.setMajorTickSpacing(5);

		settingsPanel.add(distanceSlider);
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

		JPanel sizePanel = new JPanel();
		sizePanel.setLayout(new GridLayout(1, 2));
		JLabel sizeLabel = new JLabel("size:");
		sizeSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100000f,
				0.01f));
		sizeSpinner.setToolTipText("size");
		sizePanel.add(sizeLabel);
		sizePanel.add(sizeSpinner);

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

		JPanel reloadInfoPanel = new JPanel();
		reloadInfoPanel.setLayout(new GridLayout(1, 3));
		JLabel reloadTimeInfoLabel = new JLabel("Reload info:");
		reloadInfoPanel.add(reloadTimeInfoLabel);
		reloadTimeSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 100f,
				0.01f));
		reloadTimeSpinner.setToolTipText("reload time");
		reloadInfoPanel.add(reloadTimeSpinner);
		cooldownTimeSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 100f,
				0.01f));
		cooldownTimeSpinner.setToolTipText("cooldown time");
		reloadInfoPanel.add(cooldownTimeSpinner);

		propertiesPanel.add(indexPanel);
		propertiesPanel.add(sizePanel);
		propertiesPanel.add(damageInfoPanel);
		propertiesPanel.add(reloadInfoPanel);
	}

	@Override
	public WeaponScaleEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public WeaponScaleEditorSettings getSettings() {
		return settings;
	}

	public JSpinner getSizeSpinner() {
		return sizeSpinner;
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

	public JSpinner getReloadTimeSpinner() {
		return reloadTimeSpinner;
	}

	public JSpinner getCooldownTimeSpinner() {
		return cooldownTimeSpinner;
	}

	public JSlider getDistanceSlider() {
		return distanceSlider;
	}

	public JButton getTextureButton() {
		return textureButton;
	}

	public JCheckBoxMenuItem getSortByDamageMenuItem() {
		return sortByDamageMenuItem;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new WeaponScaleEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
