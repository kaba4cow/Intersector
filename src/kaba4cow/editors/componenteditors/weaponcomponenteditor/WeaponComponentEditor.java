package kaba4cow.editors.componenteditors.weaponcomponenteditor;

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
import kaba4cow.editors.componenteditors.ComponentEditor;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.intersector.files.MachineFile;
import kaba4cow.intersector.files.TextureSetFile;
import kaba4cow.intersector.files.WeaponFile;
import kaba4cow.intersector.gameobjects.objectcomponents.ObjectComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.WeaponComponent;

public class WeaponComponentEditor extends AbstractEditor implements
		ComponentEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Weapon Component";

	private WeaponComponentEditorViewport viewport;
	private WeaponComponentEditorSettings settings;

	private JLabel indexLabel;
	private JButton weaponNameButton;
	private JCheckBox weaponCopyTargetCheckbox;
	private JSpinner weaponXSpinner;
	private JSpinner weaponYSpinner;
	private JSpinner weaponZSpinner;

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

	private JCheckBoxMenuItem followWeaponMenuItem;
	private JCheckBoxMenuItem rotateLocalMenuItem;
	private JCheckBoxMenuItem rotateAroundCenterMenuItem;
	private JCheckBoxMenuItem renderTargetInfoMenuItem;

	public static final float MUL = 10f;
	public static final float STEP = 0.01f;

	public WeaponComponentEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "machines";
	}

	@Override
	public void onNewFileLoaded() {
		MachineFile file = getViewport().getShipFile();
		indexLabel.setText("index: 1 / " + viewport.getMaxIndex());
		resetWeaponInfo(file);
	}

	private void resetWeaponInfo(MachineFile file) {
		if (file == null || viewport.getMaxIndex() == 0) {
			weaponNameButton.setText("empty");
			weaponCopyTargetCheckbox.setSelected(true);
			weaponXSpinner.setValue(0f);
			weaponYSpinner.setValue(0f);
			weaponZSpinner.setValue(0f);
			return;
		}
		if (viewport.getIndex() < viewport.getMaxIndex()) {
			WeaponComponent weaponInfo = file.getWeapon(viewport.getIndex());
			weaponNameButton.setText(weaponInfo.weaponName);
			weaponCopyTargetCheckbox.setSelected(weaponInfo.copyTarget);
			weaponXSpinner.setValue(weaponInfo.pos.x * MUL);
			weaponYSpinner.setValue(weaponInfo.pos.y * MUL);
			weaponZSpinner.setValue(weaponInfo.pos.z * MUL);
		}
	}

	public void onIndexChanged() {
		MachineFile file = getViewport().getShipFile();
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / "
				+ viewport.getMaxIndex());
		resetWeaponInfo(file);
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

		MachineFile file = viewport.getShipFile();
		if (file != null) {
			if (source == addMenuItem) {
				file.addWeapon();
				viewport.setIndex(viewport.getMaxIndex() - 1);
				onIndexChanged();
			}

			if (source == removeMenuItem) {
				file.removeWeapon(viewport.getIndex());
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (file.getWeapons() > 0) {
				if (source == duplicateMenuItem) {
					WeaponComponent weaponInfo = file.getWeapon(viewport
							.getIndex());
					file.addWeapon(new WeaponComponent(weaponInfo, false));
					onIndexChanged();
				}

				if (source == mirrorXMenuItem) {
					WeaponComponent weaponInfo = file.getWeapon(viewport
							.getIndex());
					file.addWeapon(weaponInfo.mirrorX());
					onIndexChanged();
				}

				if (source == mirrorYMenuItem) {
					WeaponComponent weaponInfo = file.getWeapon(viewport
							.getIndex());
					file.addWeapon(weaponInfo.mirrorY());
					onIndexChanged();
				}

				if (source == mirrorZMenuItem) {
					WeaponComponent weaponInfo = file.getWeapon(viewport
							.getIndex());
					file.addWeapon(weaponInfo.mirrorZ());
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

		JMenu mirrorWeaponMenu = new JMenu("Mirror");
		mirrorWeaponMenu.addActionListener(this);
		editMenu.add(mirrorWeaponMenu);

		mirrorXMenuItem = new JMenuItem("X");
		mirrorXMenuItem.addActionListener(this);
		mirrorWeaponMenu.add(mirrorXMenuItem);

		mirrorYMenuItem = new JMenuItem("Y");
		mirrorYMenuItem.addActionListener(this);
		mirrorWeaponMenu.add(mirrorYMenuItem);

		mirrorZMenuItem = new JMenuItem("Z");
		mirrorZMenuItem.addActionListener(this);
		mirrorWeaponMenu.add(mirrorZMenuItem);
	}

	@Override
	protected void initSettingsAndViewport() {
		settings = new WeaponComponentEditorSettings(this);
		viewport = new WeaponComponentEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		followWeaponMenuItem = new JCheckBoxMenuItem("Follow weapon");
		followWeaponMenuItem.addActionListener(this);
		settingsMenu.add(followWeaponMenuItem);

		rotateLocalMenuItem = new JCheckBoxMenuItem("Rotate local");
		rotateLocalMenuItem.addActionListener(this);
		settingsMenu.add(rotateLocalMenuItem);

		rotateAroundCenterMenuItem = new JCheckBoxMenuItem(
				"Rotate around center");
		rotateAroundCenterMenuItem.addActionListener(this);
		settingsMenu.add(rotateAroundCenterMenuItem);

		renderTargetInfoMenuItem = new JCheckBoxMenuItem("Render target info");
		renderTargetInfoMenuItem.addActionListener(this);
		settingsMenu.add(renderTargetInfoMenuItem);
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

		JPanel weaponNamePanel = new JPanel();
		weaponNamePanel.setLayout(new GridLayout(1, 2));
		JLabel weaponNameLabel = new JLabel("weapon:");
		weaponNameButton = new JButton();
		weaponNameButton.setToolTipText("weapon file");
		weaponNameButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(WeaponFile.getList(), weaponNameButton,
						"Select weapon file");
			}
		});
		weaponNamePanel.add(weaponNameLabel);
		weaponNamePanel.add(weaponNameButton);

		JPanel weaponCopyTargetPanel = new JPanel();
		weaponCopyTargetPanel.setLayout(new GridLayout(1, 3));
		weaponCopyTargetCheckbox = new JCheckBox("copy target");
		weaponCopyTargetCheckbox.setToolTipText("copy target");
		weaponCopyTargetPanel.add(weaponCopyTargetCheckbox);

		JPanel weaponXPanel = new JPanel();
		weaponXPanel.setLayout(new GridLayout(1, 2));
		JLabel weaponXLabel = new JLabel("X:");
		weaponXSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		weaponXSpinner.setToolTipText("weapon X");
		weaponXPanel.add(weaponXLabel);
		weaponXPanel.add(weaponXSpinner);

		JPanel weaponYPanel = new JPanel();
		weaponYPanel.setLayout(new GridLayout(1, 2));
		JLabel weaponYLabel = new JLabel("Y:");
		weaponYSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		weaponYSpinner.setToolTipText("weapon Y");
		weaponYPanel.add(weaponYLabel);
		weaponYPanel.add(weaponYSpinner);

		JPanel weaponZPanel = new JPanel();
		weaponZPanel.setLayout(new GridLayout(1, 2));
		JLabel weaponZLabel = new JLabel("Z:");
		weaponZSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		weaponZSpinner.setToolTipText("weapon Z");
		weaponZPanel.add(weaponZLabel);
		weaponZPanel.add(weaponZSpinner);

		JPanel weaponRotXPanel = new JPanel();
		weaponRotXPanel.setLayout(new GridLayout(1, 3));
		JLabel weaponRotXLabel = new JLabel("Rotate X:");
		JSpinner weaponRotXSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-90f, 90f, 0.01f));
		JButton weaponApplyRotX = this.createApplyRotationButton(
				weaponRotXSpinner, Direction.Coordinate.X, settings);
		weaponRotXPanel.add(weaponRotXLabel);
		weaponRotXPanel.add(weaponRotXSpinner);
		weaponRotXPanel.add(weaponApplyRotX);

		JPanel weaponRotPosXPanel = new JPanel();
		weaponRotPosXPanel.setLayout(new GridLayout(1, 5));
		JButton weaponRotXPos5Button = createRotateButton(5f,
				Direction.Coordinate.X, getSettings());
		weaponRotPosXPanel.add(weaponRotXPos5Button);
		JButton weaponRotXPos15Button = createRotateButton(15f,
				Direction.Coordinate.X, getSettings());
		weaponRotPosXPanel.add(weaponRotXPos15Button);
		JButton weaponRotXPos30Button = createRotateButton(30f,
				Direction.Coordinate.X, getSettings());
		weaponRotPosXPanel.add(weaponRotXPos30Button);
		JButton weaponRotXPos45Button = createRotateButton(45f,
				Direction.Coordinate.X, getSettings());
		weaponRotPosXPanel.add(weaponRotXPos45Button);
		JButton weaponRotXPos90Button = createRotateButton(90f,
				Direction.Coordinate.X, getSettings());
		weaponRotPosXPanel.add(weaponRotXPos90Button);

		JPanel weaponRotNegXPanel = new JPanel();
		weaponRotNegXPanel.setLayout(new GridLayout(1, 5));
		JButton weaponRotXNeg5Button = createRotateButton(-5f,
				Direction.Coordinate.X, getSettings());
		weaponRotNegXPanel.add(weaponRotXNeg5Button);
		JButton weaponRotXNeg15Button = createRotateButton(-15f,
				Direction.Coordinate.X, getSettings());
		weaponRotNegXPanel.add(weaponRotXNeg15Button);
		JButton weaponRotXNeg30Button = createRotateButton(-30f,
				Direction.Coordinate.X, getSettings());
		weaponRotNegXPanel.add(weaponRotXNeg30Button);
		JButton weaponRotXNeg45Button = createRotateButton(-45f,
				Direction.Coordinate.X, getSettings());
		weaponRotNegXPanel.add(weaponRotXNeg45Button);
		JButton weaponRotXNeg90Button = createRotateButton(-90f,
				Direction.Coordinate.X, getSettings());
		weaponRotNegXPanel.add(weaponRotXNeg90Button);

		JPanel weaponRotYPanel = new JPanel();
		weaponRotYPanel.setLayout(new GridLayout(1, 3));
		JLabel weaponRotYLabel = new JLabel("Rotate Y:");
		JSpinner weaponRotYSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-90f, 90f, 0.01f));
		JButton weaponApplyRotY = this.createApplyRotationButton(
				weaponRotYSpinner, Direction.Coordinate.Y, settings);
		weaponRotYPanel.add(weaponRotYLabel);
		weaponRotYPanel.add(weaponRotYSpinner);
		weaponRotYPanel.add(weaponApplyRotY);

		JPanel weaponRotPosYPanel = new JPanel();
		weaponRotPosYPanel.setLayout(new GridLayout(1, 5));
		JButton weaponRotYPos5Button = createRotateButton(5f,
				Direction.Coordinate.Y, getSettings());
		weaponRotPosYPanel.add(weaponRotYPos5Button);
		JButton weaponRotYPos15Button = createRotateButton(15f,
				Direction.Coordinate.Y, getSettings());
		weaponRotPosYPanel.add(weaponRotYPos15Button);
		JButton weaponRotYPos30Button = createRotateButton(30f,
				Direction.Coordinate.Y, getSettings());
		weaponRotPosYPanel.add(weaponRotYPos30Button);
		JButton weaponRotYPos45Button = createRotateButton(45f,
				Direction.Coordinate.Y, getSettings());
		weaponRotPosYPanel.add(weaponRotYPos45Button);
		JButton weaponRotYPos90Button = createRotateButton(90f,
				Direction.Coordinate.Y, getSettings());
		weaponRotPosYPanel.add(weaponRotYPos90Button);

		JPanel weaponRotNegYPanel = new JPanel();
		weaponRotNegYPanel.setLayout(new GridLayout(1, 5));
		JButton weaponRotYNeg5Button = createRotateButton(-5f,
				Direction.Coordinate.Y, getSettings());
		weaponRotNegYPanel.add(weaponRotYNeg5Button);
		JButton weaponRotYNeg15Button = createRotateButton(-15f,
				Direction.Coordinate.Y, getSettings());
		weaponRotNegYPanel.add(weaponRotYNeg15Button);
		JButton weaponRotYNeg30Button = createRotateButton(-30f,
				Direction.Coordinate.Y, getSettings());
		weaponRotNegYPanel.add(weaponRotYNeg30Button);
		JButton weaponRotYNeg45Button = createRotateButton(-45f,
				Direction.Coordinate.Y, getSettings());
		weaponRotNegYPanel.add(weaponRotYNeg45Button);
		JButton weaponRotYNeg90Button = createRotateButton(-90f,
				Direction.Coordinate.Y, getSettings());
		weaponRotNegYPanel.add(weaponRotYNeg90Button);

		JPanel weaponRotZPanel = new JPanel();
		weaponRotZPanel.setLayout(new GridLayout(1, 3));
		JLabel weaponRotZLabel = new JLabel("Rotate Z:");
		JSpinner weaponRotZSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-90f, 90f, 0.01f));
		JButton weaponApplyRotZ = this.createApplyRotationButton(
				weaponRotZSpinner, Direction.Coordinate.Z, settings);
		weaponRotZPanel.add(weaponRotZLabel);
		weaponRotZPanel.add(weaponRotZSpinner);
		weaponRotZPanel.add(weaponApplyRotZ);

		JPanel weaponRotPosZPanel = new JPanel();
		weaponRotPosZPanel.setLayout(new GridLayout(1, 5));
		JButton weaponRotZPos5Button = createRotateButton(5f,
				Direction.Coordinate.Z, getSettings());
		weaponRotPosZPanel.add(weaponRotZPos5Button);
		JButton weaponRotZPos15Button = createRotateButton(15f,
				Direction.Coordinate.Z, getSettings());
		weaponRotPosZPanel.add(weaponRotZPos15Button);
		JButton weaponRotZPos30Button = createRotateButton(30f,
				Direction.Coordinate.Z, getSettings());
		weaponRotPosZPanel.add(weaponRotZPos30Button);
		JButton weaponRotZPos45Button = createRotateButton(45f,
				Direction.Coordinate.Z, getSettings());
		weaponRotPosZPanel.add(weaponRotZPos45Button);
		JButton weaponRotZPos90Button = createRotateButton(90f,
				Direction.Coordinate.Z, getSettings());
		weaponRotPosZPanel.add(weaponRotZPos90Button);

		JPanel weaponRotNegZPanel = new JPanel();
		weaponRotNegZPanel.setLayout(new GridLayout(1, 5));
		JButton weaponRotZNeg5Button = createRotateButton(-5f,
				Direction.Coordinate.Z, getSettings());
		weaponRotNegZPanel.add(weaponRotZNeg5Button);
		JButton weaponRotZNeg15Button = createRotateButton(-15f,
				Direction.Coordinate.Z, getSettings());
		weaponRotNegZPanel.add(weaponRotZNeg15Button);
		JButton weaponRotZNeg30Button = createRotateButton(-30f,
				Direction.Coordinate.Z, getSettings());
		weaponRotNegZPanel.add(weaponRotZNeg30Button);
		JButton weaponRotZNeg45Button = createRotateButton(-45f,
				Direction.Coordinate.Z, getSettings());
		weaponRotNegZPanel.add(weaponRotZNeg45Button);
		JButton weaponRotZNeg90Button = createRotateButton(-90f,
				Direction.Coordinate.Z, getSettings());
		weaponRotNegZPanel.add(weaponRotZNeg90Button);

		JButton weaponRotResetButton = createResetRotationsButton();

		propertiesPanel.add(indexPanel);
		propertiesPanel.add(weaponNamePanel);
		propertiesPanel.add(weaponCopyTargetPanel);
		propertiesPanel.add(weaponXPanel);
		propertiesPanel.add(weaponYPanel);
		propertiesPanel.add(weaponZPanel);
		propertiesPanel.add(weaponRotXPanel);
		propertiesPanel.add(weaponRotPosXPanel);
		propertiesPanel.add(weaponRotNegXPanel);
		propertiesPanel.add(weaponRotYPanel);
		propertiesPanel.add(weaponRotPosYPanel);
		propertiesPanel.add(weaponRotNegYPanel);
		propertiesPanel.add(weaponRotZPanel);
		propertiesPanel.add(weaponRotPosZPanel);
		propertiesPanel.add(weaponRotNegZPanel);
		propertiesPanel.add(weaponRotResetButton);
	}

	@Override
	public ObjectComponent getRotatableComponent() {
		if (viewport.getShipFile() != null && viewport.getIndex() >= 0
				&& viewport.getIndex() < viewport.getMaxIndex())
			return viewport.getShipFile().getWeapon(viewport.getIndex());
		return null;
	}

	@Override
	public WeaponComponentEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public WeaponComponentEditorSettings getSettings() {
		return settings;
	}

	public JButton getWeaponNameButton() {
		return weaponNameButton;
	}

	public JCheckBox getWeaponCopyTargetCheckbox() {
		return weaponCopyTargetCheckbox;
	}

	@Override
	public JSpinner getComponentXSpinner() {
		return weaponXSpinner;
	}

	@Override
	public JSpinner getComponentYSpinner() {
		return weaponYSpinner;
	}

	@Override
	public JSpinner getComponentZSpinner() {
		return weaponZSpinner;
	}

	@Override
	public float getMul() {
		return MUL;
	}

	public JCheckBoxMenuItem getFollowWeaponMenuItem() {
		return followWeaponMenuItem;
	}

	public JCheckBoxMenuItem getRotateLocalMenuItem() {
		return rotateLocalMenuItem;
	}

	public JCheckBoxMenuItem getRotateAroundCenterMenuItem() {
		return rotateAroundCenterMenuItem;
	}

	public JCheckBoxMenuItem getRenderTargetInfoMenuItem() {
		return renderTargetInfoMenuItem;
	}

	public JButton getTextureSetButton() {
		return textureSetButton;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new WeaponComponentEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
