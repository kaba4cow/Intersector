package kaba4cow.editors.particlesystemeditor;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
import kaba4cow.engine.toolbox.maths.Easing;
import kaba4cow.files.ParticleSystemFile;
import kaba4cow.files.ParticleTextureFile;

public class ParticleSystemEditor extends AbstractEditor {

	private static final long serialVersionUID = 1L;

	public static final String NAME = "Particle System";

	private ParticleSystemEditorSettings settings;
	private ParticleSystemEditorViewport viewport;

	private JSpinner errorlifeSpinner;
	private JSpinner errorscaleSpinner;
	private JSpinner dscaleSpinner;
	private JSpinner drotationSpinner;
	private JButton textureButton;
	private JButton easingButton;

	private JSlider lifeSlider;
	private JSlider scaleSlider;
	private JSlider ppsSlider;

	private JSlider tintRSlider;
	private JSlider tintGSlider;
	private JSlider tintBSlider;

	private JMenu fileMenu;
	private JMenuItem openMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem exitMenuItem;

	public ParticleSystemEditor() {
		super(NAME);
	}

	@Override
	protected String getRootDirectory() {
		return "particlesystems";
	}

	@Override
	public void onNewFileLoaded() {
		ParticleSystemFile file = getViewport().getSystemFile();
		if (file == null) {
			errorlifeSpinner.setValue(0f);
			errorscaleSpinner.setValue(0f);
			dscaleSpinner.setValue(0f);
			drotationSpinner.setValue(1f);
			easingButton.setText("null");
			textureButton.setText(ParticleTextureFile.getList().get(0)
					.getFileName());
		} else {
			errorlifeSpinner.setValue(file.getErrorLife());
			errorscaleSpinner.setValue(file.getErrorScale());
			dscaleSpinner.setValue(file.getdScale());
			drotationSpinner.setValue(file.getdRotation());
			if (file.getEasing() == null)
				easingButton.setText("null");
			else
				easingButton.setText(file.getEasing().name());
			textureButton.setText(file.getTexture());
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
		settings = new ParticleSystemEditorSettings(this);
		viewport = new ParticleSystemEditorViewport(this);
	}

	@Override
	protected void initProperties() {
		propertiesPanel.setLayout(new GridLayout(16, 1));

		JPanel errorlifePanel = new JPanel();
		errorlifePanel.setLayout(new GridLayout(1, 2));
		JLabel errorlifeLabel = new JLabel("errorlife:");
		errorlifeSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 1f,
				0.01f));
		errorlifeSpinner.setToolTipText("errorlife");
		errorlifePanel.add(errorlifeLabel);
		errorlifePanel.add(errorlifeSpinner);

		JPanel errorscalePanel = new JPanel();
		errorscalePanel.setLayout(new GridLayout(1, 2));
		JLabel errorscaleLabel = new JLabel("errorscale:");
		errorscaleSpinner = new JSpinner(new SpinnerNumberModel(0f, 0f, 1f,
				0.01f));
		errorscaleSpinner.setToolTipText("errorscale");
		errorscalePanel.add(errorscaleLabel);
		errorscalePanel.add(errorscaleSpinner);

		JPanel dscalePanel = new JPanel();
		dscalePanel.setLayout(new GridLayout(1, 2));
		JLabel dscaleLabel = new JLabel("dscale:");
		dscaleSpinner = new JSpinner(new SpinnerNumberModel(0f, -10f, 10f,
				0.01f));
		dscaleSpinner.setToolTipText("dscale");
		dscalePanel.add(dscaleLabel);
		dscalePanel.add(dscaleSpinner);

		JPanel drotationPanel = new JPanel();
		drotationPanel.setLayout(new GridLayout(1, 2));
		JLabel drotationLabel = new JLabel("drotation:");
		drotationSpinner = new JSpinner(new SpinnerNumberModel(0f, -10f, 10f,
				0.01f));
		drotationSpinner.setToolTipText("drotation");
		drotationPanel.add(drotationLabel);
		drotationPanel.add(drotationSpinner);

		JPanel texturePanel = new JPanel();
		texturePanel.setLayout(new GridLayout(1, 2));
		JLabel textureLabel = new JLabel("texture:");
		textureButton = new JButton();
		textureButton.setToolTipText("particle texture");
		textureButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectAssetFileName(ParticleTextureFile.getList(),
						textureButton, "Select texture file");
			}
		});
		texturePanel.add(textureLabel);
		texturePanel.add(textureButton);
		texturePanel.add(createResetButton(textureButton));

		JPanel easingPanel = new JPanel();
		easingPanel.setLayout(new GridLayout(1, 2));
		JLabel easingLabel = new JLabel("easing:");
		easingButton = new JButton();
		easingButton.setToolTipText("easing");
		List<String> enumList = new ArrayList<String>();
		for (int i = 0; i < Easing.values().length; i++)
			enumList.add(Easing.values()[i].name());
		easingButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				selectEnum(enumList, easingButton, "Select easing");
			}
		});
		easingPanel.add(easingLabel);
		easingPanel.add(easingButton);
		easingPanel.add(createResetButton(easingButton));

		propertiesPanel.add(errorlifePanel);
		propertiesPanel.add(errorscalePanel);
		propertiesPanel.add(dscalePanel);
		propertiesPanel.add(drotationPanel);
		propertiesPanel.add(texturePanel);
		propertiesPanel.add(easingPanel);
	}

	@Override
	protected void addSettings(JMenu settingsMenu) {

	}

	@Override
	protected void initSettings1(JPanel settingsPanel) {
		scaleSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 0);
		scaleSlider.setToolTipText("scale");
		scaleSlider.setPaintTicks(true);
		scaleSlider.setSnapToTicks(true);
		scaleSlider.setMinorTickSpacing(1);
		scaleSlider.setMajorTickSpacing(5);

		lifeSlider = new JSlider(JSlider.HORIZONTAL, -10, 10, 0);
		lifeSlider.setToolTipText("life length");
		lifeSlider.setPaintTicks(true);
		lifeSlider.setSnapToTicks(true);
		lifeSlider.setMinorTickSpacing(1);
		lifeSlider.setMajorTickSpacing(5);

		ppsSlider = new JSlider(JSlider.HORIZONTAL, 1, 16, 1);
		ppsSlider.setToolTipText("pps");
		ppsSlider.setPaintTicks(true);
		ppsSlider.setSnapToTicks(true);
		ppsSlider.setMinorTickSpacing(1);
		ppsSlider.setMajorTickSpacing(5);

		settingsPanel.add(scaleSlider);
		settingsPanel.add(lifeSlider);
		settingsPanel.add(ppsSlider);
	}

	@Override
	protected void initSettings2(JPanel settingsPanel) {

	}

	@Override
	protected void initSettings3(JPanel settingsPanel) {
		tintRSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		tintRSlider.setToolTipText("tint red");
		tintRSlider.setPaintTicks(true);
		tintRSlider.setSnapToTicks(true);
		tintRSlider.setMinorTickSpacing(5);
		tintRSlider.setMajorTickSpacing(25);

		tintGSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		tintGSlider.setToolTipText("tint green");
		tintGSlider.setPaintTicks(true);
		tintGSlider.setSnapToTicks(true);
		tintGSlider.setMinorTickSpacing(5);
		tintGSlider.setMajorTickSpacing(25);

		tintBSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 255);
		tintBSlider.setToolTipText("tint blue");
		tintBSlider.setPaintTicks(true);
		tintBSlider.setSnapToTicks(true);
		tintBSlider.setMinorTickSpacing(5);
		tintBSlider.setMajorTickSpacing(25);

		settingsPanel.add(tintRSlider);
		settingsPanel.add(tintGSlider);
		settingsPanel.add(tintBSlider);
	}

	@Override
	protected void initSettings4(JPanel settingsPanel) {

	}

	@Override
	public ParticleSystemEditorViewport getViewport() {
		return viewport;
	}

	@Override
	public ParticleSystemEditorSettings getSettings() {
		return settings;
	}

	public JButton getTextureButton() {
		return textureButton;
	}

	public JSpinner getErrorlifeSpinner() {
		return errorlifeSpinner;
	}

	public JButton getEasingButton() {
		return easingButton;
	}

	public JSpinner getErrorscaleSpinner() {
		return errorscaleSpinner;
	}

	public JSpinner getDscaleSpinner() {
		return dscaleSpinner;
	}

	public JSpinner getDrotationSpinner() {
		return drotationSpinner;
	}

	public JSlider getScaleSlider() {
		return scaleSlider;
	}

	public JSlider getLifeSlider() {
		return lifeSlider;
	}

	public JSlider getPpsSlider() {
		return ppsSlider;
	}

	public JSlider getTintRSlider() {
		return tintRSlider;
	}

	public JSlider getTintGSlider() {
		return tintGSlider;
	}

	public JSlider getTintBSlider() {
		return tintBSlider;
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new ParticleSystemEditor();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}
