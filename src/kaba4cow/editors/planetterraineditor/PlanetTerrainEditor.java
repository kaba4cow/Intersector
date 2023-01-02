package kaba4cow.editors.planetterraineditor;

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
import kaba4cow.files.PlanetFile;

public class PlanetTerrainEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Planet";

	private PlanetTerrainEditorViewport viewport;
	private PlanetTerrainEditorSettings settings;

	private JLabel colorIndexLabel;
	private JSpinner seedSpinner;
	private JSpinner emissionMinSpinner;
	private JSpinner emissionMaxSpinner;
	private JSpinner bandsMinSpinner;
	private JSpinner bandsMaxSpinner;
	private JSpinner avgColorBlendMinSpinner;
	private JSpinner avgColorBlendMaxSpinner;
	private JSpinner blendPowerMinSpinner;
	private JSpinner blendPowerMaxSpinner;
	private JSpinner positionFactorMinSpinner;
	private JSpinner positionFactorMaxSpinner;
	private JCheckBox invertPositionCheckbox;
	private JSpinner minScaleXSpinner;
	private JSpinner minScaleYSpinner;
	private JSpinner minScaleZSpinner;
	private JSpinner maxScaleXSpinner;
	private JSpinner maxScaleYSpinner;
	private JSpinner maxScaleZSpinner;
	private JSpinner infoSignXSpinner;
	private JSpinner infoSignYSpinner;
	private JSpinner infoSignZSpinner;
	private JSpinner minInfoXSpinner;
	private JSpinner minInfoYSpinner;
	private JSpinner minInfoZSpinner;
	private JSpinner maxInfoXSpinner;
	private JSpinner maxInfoYSpinner;
	private JSpinner maxInfoZSpinner;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	public PlanetTerrainEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "planets";
	}

	@Override
	public void onNewFileLoaded() {
		PlanetFile file = getViewport().getPlanetFile();
		if (file == null) {
			emissionMinSpinner.setValue(0f);
			emissionMaxSpinner.setValue(0f);

			bandsMinSpinner.setValue(0f);
			bandsMaxSpinner.setValue(0f);

			avgColorBlendMinSpinner.setValue(0f);
			avgColorBlendMaxSpinner.setValue(0f);

			blendPowerMinSpinner.setValue(0f);
			blendPowerMaxSpinner.setValue(0f);

			positionFactorMinSpinner.setValue(0f);
			positionFactorMaxSpinner.setValue(0f);

			invertPositionCheckbox.setSelected(false);

			minScaleXSpinner.setValue(0f);
			minScaleYSpinner.setValue(0f);
			minScaleZSpinner.setValue(0f);
			maxScaleXSpinner.setValue(0f);
			maxScaleYSpinner.setValue(0f);
			maxScaleZSpinner.setValue(0f);

			infoSignXSpinner.setValue(0f);
			infoSignYSpinner.setValue(0f);
			infoSignZSpinner.setValue(0f);

			minInfoXSpinner.setValue(0f);
			minInfoYSpinner.setValue(0f);
			minInfoZSpinner.setValue(0f);
			maxInfoXSpinner.setValue(0f);
			maxInfoYSpinner.setValue(0f);
			maxInfoZSpinner.setValue(0f);
		} else {
			emissionMinSpinner.setValue(file.getMinEmission());
			emissionMaxSpinner.setValue(file.getMaxEmission());

			bandsMinSpinner.setValue(file.getMinBands());
			bandsMaxSpinner.setValue(file.getMaxBands());

			avgColorBlendMinSpinner.setValue(file.getMinAvgColorBlend());
			avgColorBlendMaxSpinner.setValue(file.getMaxAvgColorBlend());

			blendPowerMinSpinner.setValue(file.getMinBlendPower());
			blendPowerMaxSpinner.setValue(file.getMaxBlendPower());

			positionFactorMinSpinner.setValue(file.getMinPositionFactor());
			positionFactorMaxSpinner.setValue(file.getMaxPositionFactor());

			invertPositionCheckbox.setSelected(file.isInvertPosition());

			minScaleXSpinner.setValue(file.getMinScale().x);
			minScaleYSpinner.setValue(file.getMinScale().y);
			minScaleZSpinner.setValue(file.getMinScale().z);
			maxScaleXSpinner.setValue(file.getMaxScale().x);
			maxScaleYSpinner.setValue(file.getMaxScale().y);
			maxScaleZSpinner.setValue(file.getMaxScale().z);

			infoSignXSpinner.setValue(file.getInfoSigns()[0]);
			infoSignYSpinner.setValue(file.getInfoSigns()[1]);
			infoSignZSpinner.setValue(file.getInfoSigns()[2]);

			minInfoXSpinner.setValue(file.getMinInfo().x);
			minInfoYSpinner.setValue(file.getMinInfo().y);
			minInfoZSpinner.setValue(file.getMinInfo().z);
			maxInfoXSpinner.setValue(file.getMaxInfo().x);
			maxInfoYSpinner.setValue(file.getMaxInfo().y);
			maxInfoZSpinner.setValue(file.getMaxInfo().z);
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
		settings = new PlanetTerrainEditorSettings(this);
		viewport = new PlanetTerrainEditorViewport(this);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {

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

		JPanel seedPanel = new JPanel();
		seedPanel.setLayout(new GridLayout(1, 3));
		JLabel seedLabel = new JLabel("Seed:");
		seedPanel.add(seedLabel);
		seedSpinner = new JSpinner(new SpinnerNumberModel(0f, -1000000f,
				1000000f, 1f));
		seedSpinner.setToolTipText("seed");
		seedPanel.add(seedSpinner);
		JButton renderButton = new JButton("RENDER");
		renderButton.setToolTipText("render terrain");
		renderButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getViewport().renderTerrain();
			}
		});
		seedPanel.add(renderButton);

		JPanel colorIndexPanel = new JPanel();
		colorIndexPanel.setLayout(new GridLayout(1, 3));
		colorIndexLabel = new JLabel("Color: 0 / 0");
		colorIndexPanel.add(colorIndexLabel);
		JButton prevButton = new JButton("PREV");
		prevButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewport.changeColorIndex(-1);
			}
		});
		JButton nextButton = new JButton("NEXT");
		nextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				viewport.changeColorIndex(+1);
			}
		});
		colorIndexPanel.add(prevButton);
		colorIndexPanel.add(nextButton);

		JPanel emissionPanel = new JPanel();
		emissionPanel.setLayout(new GridLayout(1, 3));
		JLabel emissionLabel = new JLabel("Emission:");
		emissionPanel.add(emissionLabel);
		emissionMinSpinner = new JSpinner(new SpinnerNumberModel(-1f, -1f, 16f,
				0.01f));
		emissionMinSpinner.setToolTipText("min emission");
		emissionPanel.add(emissionMinSpinner);
		emissionMaxSpinner = new JSpinner(new SpinnerNumberModel(-1f, -1f, 16f,
				0.01f));
		emissionMaxSpinner.setToolTipText("max emission");
		emissionPanel.add(emissionMaxSpinner);

		JPanel bandsPanel = new JPanel();
		bandsPanel.setLayout(new GridLayout(1, 3));
		JLabel bandsLabel = new JLabel("Bands:");
		bandsPanel.add(bandsLabel);
		bandsMinSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, 32f, 1f));
		bandsMinSpinner.setToolTipText("min bands");
		bandsPanel.add(bandsMinSpinner);
		bandsMaxSpinner = new JSpinner(new SpinnerNumberModel(1f, 1f, 32f, 1f));
		bandsMaxSpinner.setToolTipText("max bands");
		bandsPanel.add(bandsMaxSpinner);

		JPanel avgColorBlendPanel = new JPanel();
		avgColorBlendPanel.setLayout(new GridLayout(1, 3));
		JLabel minAvgColorBlendLabel = new JLabel("Avg Col Blend:");
		avgColorBlendPanel.add(minAvgColorBlendLabel);
		avgColorBlendMinSpinner = new JSpinner(new SpinnerNumberModel(-1f, -1f,
				16f, 0.01f));
		avgColorBlendMinSpinner.setToolTipText("min avg color blend");
		avgColorBlendPanel.add(avgColorBlendMinSpinner);
		avgColorBlendMaxSpinner = new JSpinner(new SpinnerNumberModel(-1f, -1f,
				16f, 0.01f));
		avgColorBlendMaxSpinner.setToolTipText("max avg color blend");
		avgColorBlendPanel.add(avgColorBlendMaxSpinner);

		JPanel blendPowerPanel = new JPanel();
		blendPowerPanel.setLayout(new GridLayout(1, 3));
		JLabel minBlendPowerLabel = new JLabel("Avg Col Blend:");
		blendPowerPanel.add(minBlendPowerLabel);
		blendPowerMinSpinner = new JSpinner(new SpinnerNumberModel(-1f, -1f,
				16f, 0.01f));
		blendPowerMinSpinner.setToolTipText("min blend power");
		blendPowerPanel.add(blendPowerMinSpinner);
		blendPowerMaxSpinner = new JSpinner(new SpinnerNumberModel(-1f, -1f,
				16f, 0.01f));
		blendPowerMaxSpinner.setToolTipText("max blend power");
		blendPowerPanel.add(blendPowerMaxSpinner);

		JPanel positionFactorPanel = new JPanel();
		positionFactorPanel.setLayout(new GridLayout(1, 3));
		JLabel positionFactorLabel = new JLabel("Position Factor:");
		positionFactorPanel.add(positionFactorLabel);
		positionFactorMinSpinner = new JSpinner(new SpinnerNumberModel(0.5f,
				0f, 1f, 0.001f));
		positionFactorMinSpinner.setToolTipText("min position factor");
		positionFactorPanel.add(positionFactorMinSpinner);
		positionFactorMaxSpinner = new JSpinner(new SpinnerNumberModel(0.5f,
				0f, 1f, 0.001f));
		positionFactorMaxSpinner.setToolTipText("max position factor");
		positionFactorPanel.add(positionFactorMaxSpinner);

		JPanel invertPositionPanel = new JPanel();
		invertPositionPanel.setLayout(new GridLayout(1, 4));
		JLabel invertPositionLabel = new JLabel("Invert position:");
		invertPositionPanel.add(invertPositionLabel);
		invertPositionCheckbox = new JCheckBox("Value", false);
		invertPositionCheckbox.setToolTipText("invert position");
		invertPositionCheckbox.setVisible(true);
		invertPositionPanel.add(invertPositionCheckbox);

		JPanel minScalePanel = new JPanel();
		minScalePanel.setLayout(new GridLayout(1, 4));
		JLabel minScaleLabel = new JLabel("Min Scale:");
		minScalePanel.add(minScaleLabel);
		minScaleXSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f,
				0.001f));
		minScaleXSpinner.setToolTipText("min scale x");
		minScalePanel.add(minScaleXSpinner);
		minScaleYSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f,
				0.001f));
		minScaleYSpinner.setToolTipText("min scale y");
		minScalePanel.add(minScaleYSpinner);
		minScaleZSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f,
				0.001f));
		minScaleZSpinner.setToolTipText("min scale z");
		minScalePanel.add(minScaleZSpinner);

		JPanel maxScalePanel = new JPanel();
		maxScalePanel.setLayout(new GridLayout(1, 4));
		JLabel maxScaleLabel = new JLabel("Max Scale:");
		maxScalePanel.add(maxScaleLabel);
		maxScaleXSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f,
				0.001f));
		maxScaleXSpinner.setToolTipText("max scale x");
		maxScalePanel.add(maxScaleXSpinner);
		maxScaleYSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f,
				0.001f));
		maxScaleYSpinner.setToolTipText("max scale y");
		maxScalePanel.add(maxScaleYSpinner);
		maxScaleZSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 10000f,
				0.001f));
		maxScaleZSpinner.setToolTipText("max scale z");
		maxScalePanel.add(maxScaleZSpinner);

		JPanel infoSignPanel = new JPanel();
		infoSignPanel.setLayout(new GridLayout(1, 4));
		JLabel infoSignLabel = new JLabel("Info sign:");
		infoSignPanel.add(infoSignLabel);
		infoSignXSpinner = new JSpinner(new SpinnerNumberModel(0f, -1f, 1f, 1f));
		infoSignXSpinner.setToolTipText("info sign x");
		infoSignPanel.add(infoSignXSpinner);
		infoSignYSpinner = new JSpinner(new SpinnerNumberModel(0f, -1f, 1f, 1f));
		infoSignYSpinner.setToolTipText("info sign y");
		infoSignPanel.add(infoSignYSpinner);
		infoSignZSpinner = new JSpinner(new SpinnerNumberModel(0f, -1f, 1f, 1f));
		infoSignZSpinner.setToolTipText("info sign z");
		infoSignPanel.add(infoSignZSpinner);

		JPanel minInfoPanel = new JPanel();
		minInfoPanel.setLayout(new GridLayout(1, 4));
		JLabel minInfoLabel = new JLabel("Min Info:");
		minInfoPanel.add(minInfoLabel);
		minInfoXSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.001f));
		minInfoXSpinner.setToolTipText("min info x");
		minInfoPanel.add(minInfoXSpinner);
		minInfoYSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.001f));
		minInfoYSpinner.setToolTipText("min info y");
		minInfoPanel.add(minInfoYSpinner);
		minInfoZSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.001f));
		minInfoZSpinner.setToolTipText("min info z");
		minInfoPanel.add(minInfoZSpinner);

		JPanel maxInfoPanel = new JPanel();
		maxInfoPanel.setLayout(new GridLayout(1, 4));
		JLabel maxInfoLabel = new JLabel("Max Info:");
		maxInfoPanel.add(maxInfoLabel);
		maxInfoXSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.001f));
		maxInfoXSpinner.setToolTipText("max info x");
		maxInfoPanel.add(maxInfoXSpinner);
		maxInfoYSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.001f));
		maxInfoYSpinner.setToolTipText("max info y");
		maxInfoPanel.add(maxInfoYSpinner);
		maxInfoZSpinner = new JSpinner(new SpinnerNumberModel(1f, 0f, 100f,
				0.001f));
		maxInfoZSpinner.setToolTipText("max info z");
		maxInfoPanel.add(maxInfoZSpinner);

		propertiesPanel.add(seedPanel);
		propertiesPanel.add(colorIndexPanel);
		propertiesPanel.add(emissionPanel);
		propertiesPanel.add(bandsPanel);
		propertiesPanel.add(avgColorBlendPanel);
		propertiesPanel.add(blendPowerPanel);
		propertiesPanel.add(positionFactorPanel);
		propertiesPanel.add(invertPositionPanel);
		propertiesPanel.add(minScalePanel);
		propertiesPanel.add(maxScalePanel);
		propertiesPanel.add(infoSignPanel);
		propertiesPanel.add(minInfoPanel);
		propertiesPanel.add(maxInfoPanel);
	}

	public JSpinner getSeedSpinner() {
		return seedSpinner;
	}

	public JLabel getColorIndexLabel() {
		return colorIndexLabel;
	}

	public JSpinner getEmissionMinSpinner() {
		return emissionMinSpinner;
	}

	public JSpinner getEmissionMaxSpinner() {
		return emissionMaxSpinner;
	}

	public JSpinner getBandsMinSpinner() {
		return bandsMinSpinner;
	}

	public JSpinner getBandsMaxSpinner() {
		return bandsMaxSpinner;
	}

	public JSpinner getAvgColorBlendMinSpinner() {
		return avgColorBlendMinSpinner;
	}

	public JSpinner getAvgColorBlendMaxSpinner() {
		return avgColorBlendMaxSpinner;
	}

	public JSpinner getBlendPowerMinSpinner() {
		return blendPowerMinSpinner;
	}

	public JSpinner getBlendPowerMaxSpinner() {
		return blendPowerMaxSpinner;
	}

	public JSpinner getPositionFactorMinSpinner() {
		return positionFactorMinSpinner;
	}

	public JSpinner getPositionFactorMaxSpinner() {
		return positionFactorMaxSpinner;
	}

	public JCheckBox getInvertPositionCheckbox() {
		return invertPositionCheckbox;
	}

	public JSpinner getMinScaleXSpinner() {
		return minScaleXSpinner;
	}

	public JSpinner getMinScaleYSpinner() {
		return minScaleYSpinner;
	}

	public JSpinner getMinScaleZSpinner() {
		return minScaleZSpinner;
	}

	public JSpinner getMaxScaleXSpinner() {
		return maxScaleXSpinner;
	}

	public JSpinner getMaxScaleYSpinner() {
		return maxScaleYSpinner;
	}

	public JSpinner getMaxScaleZSpinner() {
		return maxScaleZSpinner;
	}

	public JSpinner getInfoSignXSpinner() {
		return infoSignXSpinner;
	}

	public JSpinner getInfoSignYSpinner() {
		return infoSignYSpinner;
	}

	public JSpinner getInfoSignZSpinner() {
		return infoSignZSpinner;
	}

	public JSpinner getMinInfoXSpinner() {
		return minInfoXSpinner;
	}

	public JSpinner getMinInfoYSpinner() {
		return minInfoYSpinner;
	}

	public JSpinner getMinInfoZSpinner() {
		return minInfoZSpinner;
	}

	public JSpinner getMaxInfoXSpinner() {
		return maxInfoXSpinner;
	}

	public JSpinner getMaxInfoYSpinner() {
		return maxInfoYSpinner;
	}

	public JSpinner getMaxInfoZSpinner() {
		return maxInfoZSpinner;
	}

	@Override
	public PlanetTerrainEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public PlanetTerrainEditorSettings getSettings() {
		return settings;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new PlanetTerrainEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
