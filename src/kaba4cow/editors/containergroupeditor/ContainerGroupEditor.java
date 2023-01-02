package kaba4cow.editors.containergroupeditor;

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
import kaba4cow.engine.MainProgram;
import kaba4cow.files.ContainerFile;
import kaba4cow.files.ContainerGroupFile;

public class ContainerGroupEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Container Group";

	private ContainerGroupEditorViewport viewport;
	private ContainerGroupEditorSettings settings;

	private JLabel indexLabel;
	private JSpinner healthSpinner;
	private JSpinner sizeSpinner;
	private JButton cargoButton;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	private JMenu editMenu;
	private JMenuItem addMenuItem;
	private JMenuItem removeMenuItem;

	private JCheckBoxMenuItem scrollTexturesMenuItem;
	private JSlider distanceSlider;

	public ContainerGroupEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "containergroups";
	}

	@Override
	public void onNewFileLoaded() {
		ContainerGroupFile file = getViewport().getCargoGroupFile();
		indexLabel.setText("index: 1 / " + viewport.getMaxIndex());
		if (file == null) {
			cargoButton.setText("null");
			sizeSpinner.setValue(0f);
			healthSpinner.setValue(0f);
		} else {
			cargoButton.setText(file.getContainer(viewport.getIndex()));
			sizeSpinner.setValue(file.getSize());
			healthSpinner.setValue(file.getHealth());
		}
	}

	public void onIndexChanged() {
		indexLabel.setText("index: " + (viewport.getIndex() + 1) + " / "
				+ viewport.getMaxIndex());
		ContainerGroupFile file = getViewport().getCargoGroupFile();
		cargoButton.setText(file.getContainer(viewport.getIndex()));
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

		ContainerGroupFile file = viewport.getCargoGroupFile();

		if (file != null) {
			if (source == addMenuItem) {
				file.addContainer(cargoButton.getText());
				viewport.setIndex(viewport.getMaxIndex() - 1);
				onIndexChanged();
			}

			if (source == removeMenuItem) {
				file.removeContainer(viewport.getIndex());
				viewport.changeIndex(-1);
				onIndexChanged();
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
	}

	@Override
	protected void initSettingsAndViewport() {
		settings = new ContainerGroupEditorSettings(this);
		viewport = new ContainerGroupEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {
		settingsMenu.addSeparator();

		scrollTexturesMenuItem = new JCheckBoxMenuItem("Scroll textures");
		scrollTexturesMenuItem.addActionListener(this);
		settingsMenu.add(scrollTexturesMenuItem);
	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {
		distanceSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 0);
		distanceSlider.setToolTipText("distance");
		distanceSlider.setPaintTicks(true);
		distanceSlider.setSnapToTicks(true);
		distanceSlider.setMinorTickSpacing(1);
		distanceSlider.setMajorTickSpacing(5);

		settingsPanel.add(distanceSlider);
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
		propertiesPanel.setLayout(new GridLayout(18, 1));

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
		sizeSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 1000f, 1f));
		sizeSpinner.setToolTipText("size");
		sizePanel.add(sizeLabel);
		sizePanel.add(sizeSpinner);

		JPanel healthPanel = new JPanel();
		healthPanel.setLayout(new GridLayout(1, 2));
		JLabel healthLabel = new JLabel("health:");
		healthSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, 10000f, 1f));
		healthSpinner.setToolTipText("health");
		healthPanel.add(healthLabel);
		healthPanel.add(healthSpinner);

		JPanel cargoPanel = new JPanel();
		cargoPanel.setLayout(new GridLayout(1, 2));
		JLabel cargoLabel = new JLabel("cargo:");
		cargoButton = new JButton();
		cargoButton.setToolTipText("cargo");
		cargoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(ContainerFile.getList(), cargoButton,
						"Select cargo file");
			}
		});
		cargoPanel.add(cargoLabel);
		cargoPanel.add(cargoButton);

		propertiesPanel.add(indexPanel);
		propertiesPanel.add(sizePanel);
		propertiesPanel.add(healthPanel);
		propertiesPanel.add(cargoPanel);
	}

	@Override
	public ContainerGroupEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public ContainerGroupEditorSettings getSettings() {
		return settings;
	}

	public JSpinner getHealthSpinner() {
		return healthSpinner;
	}

	public JSpinner getSizeSpinner() {
		return sizeSpinner;
	}

	public JButton getCargoButton() {
		return cargoButton;
	}

	public JCheckBoxMenuItem getScrollTexturesMenuItem() {
		return scrollTexturesMenuItem;
	}

	public JSlider getDistanceSlider() {
		return distanceSlider;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ContainerGroupEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
