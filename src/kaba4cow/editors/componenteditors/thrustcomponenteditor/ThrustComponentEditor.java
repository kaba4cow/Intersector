package kaba4cow.editors.componenteditors.thrustcomponenteditor;

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
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import kaba4cow.editors.AbstractEditor;
import kaba4cow.editors.componenteditors.ComponentEditor;
import kaba4cow.engine.MainProgram;
import kaba4cow.engine.toolbox.maths.Direction;
import kaba4cow.intersector.files.ShipFile;
import kaba4cow.intersector.files.TextureSetFile;
import kaba4cow.intersector.files.ThrustTextureFile;
import kaba4cow.intersector.gameobjects.objectcomponents.ObjectComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.ThrustComponent;
import kaba4cow.intersector.toolbox.containers.RawModelContainer;

public class ThrustComponentEditor extends AbstractEditor implements
		ComponentEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Thrust Component";

	private ThrustComponentEditorViewport viewport;
	private ThrustComponentEditorSettings settings;

	private JLabel indexLabel;
	private JButton thrustButton;
	private JButton thrustTextureButton;
	private JSpinner thrustXSpinner;
	private JSpinner thrustYSpinner;
	private JSpinner thrustZSpinner;
	private JSpinner thrustSizeSpinner;

	private JSlider thrustBrightnessSlider;
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

	private JCheckBoxMenuItem followThrustMenuItem;
	private JCheckBoxMenuItem rotateLocalMenuItem;
	private JCheckBoxMenuItem rotateAroundCenterMenuItem;

	public static final float MUL = 10f;
	public static final float STEP = 0.01f;

	public ThrustComponentEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "machines";
	}

	@Override
	public void onNewFileLoaded() {
		ShipFile file = getViewport().getShipFile();
		indexLabel.setText("index: 1 / " + viewport.getMaxIndex());
		thrustBrightnessSlider.setValue(10);
		resetThrustInfo(file);
		if (file == null) {
			thrustButton.setText("null");
			thrustTextureButton.setText("null");
		} else {
			thrustButton.setText(file.getThrust());
			thrustTextureButton.setText(file.getThrustTexture());
		}
	}

	private void resetThrustInfo(ShipFile file) {
		if (file == null || viewport.getMaxIndex() == 0) {
			thrustXSpinner.setValue(0f);
			thrustYSpinner.setValue(0f);
			thrustZSpinner.setValue(0f);
			thrustSizeSpinner.setValue(1f);
			return;
		}
		ThrustComponent thrustInfo = file.getThrust(viewport.getIndex());
		thrustXSpinner.setValue(thrustInfo.pos.x * MUL);
		thrustYSpinner.setValue(thrustInfo.pos.y * MUL);
		thrustZSpinner.setValue(thrustInfo.pos.z * MUL);
		thrustSizeSpinner.setValue(thrustInfo.size * MUL);
	}

	public void onIndexChanged() {
		ShipFile file = getViewport().getShipFile();
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / "
				+ viewport.getMaxIndex());
		resetThrustInfo(file);
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

		ShipFile file = viewport.getShipFile();
		if (file != null) {
			if (source == addMenuItem) {
				file.addThrust();
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (source == removeMenuItem) {
				file.removeThrust(viewport.getIndex());
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (file.getThrusts() > 0) {
				if (source == duplicateMenuItem) {
					ThrustComponent thrustInfo = file.getThrust(viewport
							.getIndex());
					file.addThrust(new ThrustComponent(thrustInfo));
					onIndexChanged();
				}

				if (source == mirrorXMenuItem) {
					ThrustComponent thrustInfo = file.getThrust(viewport
							.getIndex());
					file.addThrust(thrustInfo.mirrorX());
					onIndexChanged();
				}

				if (source == mirrorYMenuItem) {
					ThrustComponent thrustInfo = file.getThrust(viewport
							.getIndex());
					file.addThrust(thrustInfo.mirrorY());
					onIndexChanged();
				}

				if (source == mirrorZMenuItem) {
					ThrustComponent thrustInfo = file.getThrust(viewport
							.getIndex());
					file.addThrust(thrustInfo.mirrorZ());
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

		editMenu.add(new JSeparator());

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
		settings = new ThrustComponentEditorSettings(this);
		viewport = new ThrustComponentEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		followThrustMenuItem = new JCheckBoxMenuItem("Follow thrust");
		followThrustMenuItem.addActionListener(this);
		settingsMenu.add(followThrustMenuItem);

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

		JPanel thrustXPanel = new JPanel();
		thrustXPanel.setLayout(new GridLayout(1, 2));
		JLabel thrustXLabel = new JLabel("X:");
		thrustXSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		thrustXSpinner.setToolTipText("thrust X");
		thrustXPanel.add(thrustXLabel);
		thrustXPanel.add(thrustXSpinner);

		JPanel thrustYPanel = new JPanel();
		thrustYPanel.setLayout(new GridLayout(1, 2));
		JLabel thrustYLabel = new JLabel("Y:");
		thrustYSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		thrustYSpinner.setToolTipText("thrust Y");
		thrustYPanel.add(thrustYLabel);
		thrustYPanel.add(thrustYSpinner);

		JPanel thrustZPanel = new JPanel();
		thrustZPanel.setLayout(new GridLayout(1, 2));
		JLabel thrustZLabel = new JLabel("Z:");
		thrustZSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL,
				STEP));
		thrustZSpinner.setToolTipText("thrust Z");
		thrustZPanel.add(thrustZLabel);
		thrustZPanel.add(thrustZSpinner);

		JPanel thrustSizePanel = new JPanel();
		thrustSizePanel.setLayout(new GridLayout(1, 2));
		JLabel sizeLabel = new JLabel("size:");
		thrustSizeSpinner = new JSpinner(new SpinnerNumberModel(MUL, 0f, MUL,
				STEP));
		thrustSizeSpinner.setToolTipText("thrust size");
		thrustSizePanel.add(sizeLabel);
		thrustSizePanel.add(thrustSizeSpinner);

		JPanel thrustRotPanel = new JPanel();
		thrustRotPanel.setLayout(new GridLayout(1, 3));
		JLabel thrustRotLabel = new JLabel("Rotate:");
		JSpinner thrustRotSpinner = new JSpinner(new SpinnerNumberModel(0f,
				-90f, 90f, 0.01f));
		JButton thrustApplyRot = this.createApplyRotationButton(
				thrustRotSpinner, Direction.Coordinate.Z, settings);
		thrustRotPanel.add(thrustRotLabel);
		thrustRotPanel.add(thrustRotSpinner);
		thrustRotPanel.add(thrustApplyRot);

		JPanel thrustRotPosPanel = new JPanel();
		thrustRotPosPanel.setLayout(new GridLayout(1, 4));
		JButton thrustRotPos1Button = createRotateButton(1f,
				Direction.Coordinate.Z, getSettings());
		thrustRotPosPanel.add(thrustRotPos1Button);
		JButton thrustRotPos15Button = createRotateButton(15f,
				Direction.Coordinate.Z, getSettings());
		thrustRotPosPanel.add(thrustRotPos15Button);
		JButton thrustRotPos30Button = createRotateButton(30f,
				Direction.Coordinate.Z, getSettings());
		thrustRotPosPanel.add(thrustRotPos30Button);
		JButton thrustRotPos45Button = createRotateButton(45f,
				Direction.Coordinate.Z, getSettings());
		thrustRotPosPanel.add(thrustRotPos45Button);
		JButton thrustRotPos90Button = createRotateButton(90f,
				Direction.Coordinate.Z, getSettings());
		thrustRotPosPanel.add(thrustRotPos90Button);

		JPanel thrustRotNegPanel = new JPanel();
		thrustRotNegPanel.setLayout(new GridLayout(1, 4));
		JButton thrustRotNeg1Button = createRotateButton(-1f,
				Direction.Coordinate.Z, getSettings());
		thrustRotNegPanel.add(thrustRotNeg1Button);
		JButton thrustRotNeg15Button = createRotateButton(-15f,
				Direction.Coordinate.Z, getSettings());
		thrustRotNegPanel.add(thrustRotNeg15Button);
		JButton thrustRotNeg30Button = createRotateButton(-30f,
				Direction.Coordinate.Z, getSettings());
		thrustRotNegPanel.add(thrustRotNeg30Button);
		JButton thrustRotNeg45Button = createRotateButton(-45f,
				Direction.Coordinate.Z, getSettings());
		thrustRotNegPanel.add(thrustRotNeg45Button);
		JButton thrustRotNeg90Button = createRotateButton(-90f,
				Direction.Coordinate.Z, getSettings());
		thrustRotNegPanel.add(thrustRotNeg90Button);

		JButton thrustRotResetButton = createResetRotationsButton();

		propertiesPanel.add(indexPanel);
		propertiesPanel.add(thrustPanel);
		propertiesPanel.add(thrustTexturePanel);
		propertiesPanel.add(thrustXPanel);
		propertiesPanel.add(thrustYPanel);
		propertiesPanel.add(thrustZPanel);
		propertiesPanel.add(thrustSizePanel);
		propertiesPanel.add(thrustRotPosPanel);
		propertiesPanel.add(thrustRotNegPanel);
		propertiesPanel.add(thrustRotResetButton);
	}

	@Override
	public ThrustComponentEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public ThrustComponentEditorSettings getSettings() {
		return settings;
	}

	public JButton getThrustButton() {
		return thrustButton;
	}

	public JButton getThrustTextureButton() {
		return thrustTextureButton;
	}

	@Override
	public JSpinner getComponentXSpinner() {
		return thrustXSpinner;
	}

	@Override
	public JSpinner getComponentYSpinner() {
		return thrustYSpinner;
	}

	@Override
	public JSpinner getComponentZSpinner() {
		return thrustZSpinner;
	}

	public JSpinner getThrustSizeSpinner() {
		return thrustSizeSpinner;
	}

	public JSlider getThrustBrightnessSlider() {
		return thrustBrightnessSlider;
	}

	public JCheckBoxMenuItem getFollowThrustMenuItem() {
		return followThrustMenuItem;
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

	@Override
	public ObjectComponent getRotatableComponent() {
		if (viewport.getShipFile() != null && viewport.getIndex() >= 0
				&& viewport.getIndex() < viewport.getMaxIndex())
			return viewport.getShipFile().getThrust(viewport.getIndex());
		return null;
	}

	@Override
	public float getMul() {
		return MUL;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ThrustComponentEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
