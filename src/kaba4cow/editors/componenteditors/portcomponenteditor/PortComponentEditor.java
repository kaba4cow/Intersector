package kaba4cow.editors.componenteditors.portcomponenteditor;

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
import kaba4cow.files.MachineFile;
import kaba4cow.files.TextureSetFile;
import kaba4cow.intersector.gameobjects.objectcomponents.ObjectComponent;
import kaba4cow.intersector.gameobjects.objectcomponents.PortComponent;

public class PortComponentEditor extends AbstractEditor implements
		ComponentEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Port Component";

	private PortComponentEditorViewport viewport;
	private PortComponentEditorSettings settings;

	private JLabel indexLabel;
	private JSpinner portXSpinner;
	private JSpinner portYSpinner;
	private JSpinner portZSpinner;
	private JSpinner portMinSpinner;
	private JSpinner portRangeSpinner;
	private JCheckBox portVisibleCheckbox;

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

	private JCheckBoxMenuItem followPortMenuItem;
	private JCheckBoxMenuItem rotateAroundCenterMenuItem;

	public static final float MUL = 10f;
	public static final float STEP = 0.01f;

	public PortComponentEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "machines";
	}

	@Override
	public void onNewFileLoaded() {
		MachineFile file = getViewport().getMachineFile();
		indexLabel.setText("index: 1 / " + viewport.getMaxIndex());
		resetPortInfo(file);
	}

	private void resetPortInfo(MachineFile file) {
		if (file == null || viewport.getMaxIndex() == 0) {
			portXSpinner.setValue(0f);
			portYSpinner.setValue(0f);
			portZSpinner.setValue(0f);
			portMinSpinner.setValue(0f);
			portRangeSpinner.setValue(100f);
			portVisibleCheckbox.setSelected(true);
			return;
		}
		PortComponent portInfo = file.getPort(viewport.getIndex());
		portXSpinner.setValue(portInfo.pos.x * MUL);
		portYSpinner.setValue(portInfo.pos.y * MUL);
		portZSpinner.setValue(portInfo.pos.z * MUL);
		portMinSpinner.setValue(portInfo.min);
		portRangeSpinner.setValue(portInfo.max - portInfo.min);
		portVisibleCheckbox.setSelected(portInfo.visible);
	}

	public void onIndexChanged() {
		MachineFile file = getViewport().getMachineFile();
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / "
				+ viewport.getMaxIndex());
		resetPortInfo(file);
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

		MachineFile file = viewport.getMachineFile();
		if (file != null) {
			if (source == addMenuItem) {
				file.addPort();
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (source == removeMenuItem) {
				file.removePort(viewport.getIndex());
				viewport.changeIndex(-1);
				onIndexChanged();
			}

			if (file.getPorts() > 0) {
				if (source == duplicateMenuItem) {
					PortComponent portInfo = file.getPort(viewport.getIndex());
					file.addPort(new PortComponent(portInfo));
					onIndexChanged();
				}

				if (source == mirrorXMenuItem) {
					PortComponent portInfo = file.getPort(viewport.getIndex());
					file.addPort(portInfo.mirrorX());
					onIndexChanged();
				}

				if (source == mirrorYMenuItem) {
					PortComponent portInfo = file.getPort(viewport.getIndex());
					file.addPort(portInfo.mirrorY());
					onIndexChanged();
				}

				if (source == mirrorZMenuItem) {
					PortComponent portInfo = file.getPort(viewport.getIndex());
					file.addPort(portInfo.mirrorZ());
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
		settings = new PortComponentEditorSettings(this);
		viewport = new PortComponentEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		followPortMenuItem = new JCheckBoxMenuItem("Follow port");
		followPortMenuItem.addActionListener(this);
		settingsMenu.add(followPortMenuItem);

		rotateAroundCenterMenuItem = new JCheckBoxMenuItem(
				"Rotate around center");
		rotateAroundCenterMenuItem.addActionListener(this);
		settingsMenu.add(rotateAroundCenterMenuItem);
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

		JPanel portXPanel = new JPanel();
		portXPanel.setLayout(new GridLayout(1, 2));
		JLabel portXLabel = new JLabel("X:");
		portXSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL, STEP));
		portXSpinner.setToolTipText("port X");
		portXPanel.add(portXLabel);
		portXPanel.add(portXSpinner);

		JPanel portYPanel = new JPanel();
		portYPanel.setLayout(new GridLayout(1, 2));
		JLabel portYLabel = new JLabel("Y:");
		portYSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL, STEP));
		portYSpinner.setToolTipText("port Y");
		portYPanel.add(portYLabel);
		portYPanel.add(portYSpinner);

		JPanel portZPanel = new JPanel();
		portZPanel.setLayout(new GridLayout(1, 2));
		JLabel portZLabel = new JLabel("Z:");
		portZSpinner = new JSpinner(new SpinnerNumberModel(0f, -MUL, MUL, STEP));
		portZSpinner.setToolTipText("port Z");
		portZPanel.add(portZLabel);
		portZPanel.add(portZSpinner);

		JPanel portMinPanel = new JPanel();
		portMinPanel.setLayout(new GridLayout(1, 2));
		JLabel portMinLabel = new JLabel("min size:");
		portMinSpinner = new JSpinner(
				new SpinnerNumberModel(0f, 0f, 10000f, 1f));
		portMinSpinner.setToolTipText("port min size");
		portMinPanel.add(portMinLabel);
		portMinPanel.add(portMinSpinner);

		JPanel portRangePanel = new JPanel();
		portRangePanel.setLayout(new GridLayout(1, 2));
		JLabel portRangeLabel = new JLabel("size range:");
		portRangeSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 10000f,
				1f));
		portRangeSpinner.setToolTipText("port size range");
		portRangePanel.add(portRangeLabel);
		portRangePanel.add(portRangeSpinner);

		JPanel portVisiblePanel = new JPanel();
		portVisiblePanel.setLayout(new GridLayout(1, 3));
		portVisibleCheckbox = new JCheckBox("visible");
		portVisibleCheckbox.setToolTipText("visible");
		portVisiblePanel.add(portVisibleCheckbox);

		JPanel portRotPanel = new JPanel();
		portRotPanel.setLayout(new GridLayout(1, 3));
		JLabel portRotLabel = new JLabel("Rotate:");
		JSpinner portRotSpinner = new JSpinner(new SpinnerNumberModel(0f, -90f,
				90f, 0.01f));
		JButton portApplyRot = createApplyRotationButton(portRotSpinner,
				Direction.Coordinate.Y, settings);
		portRotPanel.add(portRotLabel);
		portRotPanel.add(portRotSpinner);
		portRotPanel.add(portApplyRot);

		propertiesPanel.add(indexPanel);
		propertiesPanel.add(portXPanel);
		propertiesPanel.add(portYPanel);
		propertiesPanel.add(portZPanel);
		propertiesPanel.add(portMinPanel);
		propertiesPanel.add(portRangePanel);
		propertiesPanel.add(portVisiblePanel);
		propertiesPanel.add(portRotPanel);
		propertiesPanel.add(createResetRotationsButton());
	}

	@Override
	public ObjectComponent getRotatableComponent() {
		if (viewport.getMachineFile() != null && viewport.getIndex() >= 0
				&& viewport.getIndex() < viewport.getMaxIndex())
			return viewport.getMachineFile().getPort(viewport.getIndex());
		return null;
	}

	@Override
	public PortComponentEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public PortComponentEditorSettings getSettings() {
		return settings;
	}

	@Override
	public JSpinner getComponentXSpinner() {
		return portXSpinner;
	}

	@Override
	public JSpinner getComponentYSpinner() {
		return portYSpinner;
	}

	@Override
	public JSpinner getComponentZSpinner() {
		return portZSpinner;
	}

	public JSpinner getPortMinSpinner() {
		return portMinSpinner;
	}

	public JSpinner getPortRangeSpinner() {
		return portRangeSpinner;
	}

	public JCheckBox getPortVisibleCheckbox() {
		return portVisibleCheckbox;
	}

	public JCheckBoxMenuItem getFollowPortMenuItem() {
		return followPortMenuItem;
	}

	public JCheckBoxMenuItem getRotateAroundCenterMenuItem() {
		return rotateAroundCenterMenuItem;
	}

	public JButton getTextureSetButton() {
		return textureSetButton;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new PortComponentEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public float getMul() {
		return MUL;
	}
}
