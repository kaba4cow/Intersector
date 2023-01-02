package kaba4cow.editors;

import java.awt.Canvas;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

import kaba4cow.engine.MainProgram;
import kaba4cow.files.GameFile;

public abstract class AbstractEditor extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	public static int WIDTH = 1400;
	public static int HEIGHT = 720;

	protected final JPanel contentPanel;
	protected final JPanel propertiesPanel;
	private final JPanel settingsPanel;
	protected final JPanel settingsPanel1;
	protected final JPanel settingsPanel2;
	protected final JPanel settingsPanel3;
	protected final JPanel settingsPanel4;
	protected final JMenuBar menuBar;
	protected final Canvas canvas;

	private JMenu settingsMenu;
	private JCheckBoxMenuItem staticLightMenuItem;
	private JCheckBoxMenuItem renderSkyboxMenuItem;
	private JCheckBoxMenuItem renderWireframeMenuItem;

	private final String title;

	public AbstractEditor(String type) {
		title = "Intersector : " + type + " Editor - ";

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setVisible(true);
		setEnabled(false);
		setResizable(false);

		contentPanel = new JPanel();
		contentPanel.setBorder(new BevelBorder(EditorUtils.BEVEL_BORDER, null,
				null, null, null));
		contentPanel.setLayout(null);
		setContentPane(contentPanel);

		canvas = new Canvas();
		canvas.setBounds(0, 0, 860, 480);
		// canvas.setBounds(0, 0, 1360, 700);
		canvas.setBackground(null);

		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		initSettingsAndViewport();
		initMenuBar();
		initSettingsMenu();

		propertiesPanel = new JPanel();
		initPropertiesPanel();
		initProperties();
		contentPanel.add(propertiesPanel);

		settingsPanel = new JPanel();
		settingsPanel.setBounds(0, 480, 860, 180);
		settingsPanel.setLayout(new GridLayout(1, 4));

		settingsPanel1 = new JPanel();
		initSettingsPanel1();
		initSettings1(settingsPanel1);
		settingsPanel.add(settingsPanel1);

		settingsPanel2 = new JPanel();
		initSettingsPanel2();
		initSettings2(settingsPanel2);
		settingsPanel.add(settingsPanel2);

		settingsPanel3 = new JPanel();
		initSettingsPanel3();
		initSettings3(settingsPanel3);
		settingsPanel.add(settingsPanel3);

		settingsPanel4 = new JPanel();
		initSettingsPanel4();
		initSettings4(settingsPanel4);
		settingsPanel.add(settingsPanel4);

		contentPanel.add(settingsPanel);
		contentPanel.add(canvas);

		EditorUtils.setFont(getComponents());

		new EditorWindowListener(this);
		MainProgram.start(getViewport());
		setEnabled(true);
	}

	protected abstract String getRootDirectory();

	public void updateTitle(GameFile file) {
		if (file == null)
			setTitle(title + "empty");
		else
			setTitle(title + file.getFileName());
	}

	protected abstract void initSettingsAndViewport();

	public abstract void onNewFileLoaded();

	protected abstract void onActionPerformed(Object source);

	protected void selectAssetFileName(List<? extends GameFile> fileList,
			JButton source, String title) {
		JDialog dialog = new JDialog(this, title);
		dialog.setLocationRelativeTo(null);
		dialog.setSize(400, 500);
		dialog.setLayout(null);
		dialog.setVisible(true);
		dialog.setResizable(false);
		EditorUtils.setFont(dialog.getComponents());

		GameFile.sort(fileList);
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (int i = 0; i < fileList.size(); i++)
			listModel.addElement(fileList.get(i).getFileName());
		JList<String> list = new JList<String>(listModel);
		list.setBounds(0, 0, 400, 400);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 0, 385, 410);

		JButton button = new JButton("Select");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				source.setText(list.getSelectedValue());
				onItemSelected(source);
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		button.setBounds(0, 410, 400, 50);

		dialog.add(scrollPane);
		dialog.add(button);
	}

	protected void selectEnum(List<String> enumNames, JButton source,
			String title) {
		JDialog dialog = new JDialog(this, title);
		dialog.setLocationRelativeTo(null);
		dialog.setSize(400, 500);
		dialog.setLayout(null);
		dialog.setVisible(true);
		dialog.setResizable(false);
		EditorUtils.setFont(dialog.getComponents());

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (int i = 0; i < enumNames.size(); i++)
			listModel.addElement(enumNames.get(i));
		JList<String> list = new JList<String>(listModel);
		list.setBounds(0, 0, 400, 400);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 0, 385, 410);

		JButton button = new JButton("Select");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				source.setText(list.getSelectedValue());
				onItemSelected(source);
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		button.setBounds(0, 410, 400, 50);

		dialog.add(scrollPane);
		dialog.add(button);
	}

	protected void selectItem(String[] items, JButton source, String title) {
		JDialog dialog = new JDialog(this, title);
		dialog.setLocationRelativeTo(null);
		dialog.setSize(400, 500);
		dialog.setLayout(null);
		dialog.setVisible(true);
		dialog.setResizable(false);
		EditorUtils.setFont(dialog.getComponents());

		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for (int i = 0; i < items.length; i++)
			listModel.addElement(items[i]);
		JList<String> list = new JList<String>(listModel);
		list.setBounds(0, 0, 400, 400);
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane
				.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane
				.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(0, 0, 385, 410);

		JButton button = new JButton("Select");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				source.setText(list.getSelectedValue());
				onItemSelected(source);
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
		button.setBounds(0, 410, 400, 50);

		dialog.add(scrollPane);
		dialog.add(button);
	}

	protected void onItemSelected(JButton source) {

	}

	protected static JButton createResetButton(JButton src) {
		JButton resetButton = new JButton("RESET");
		resetButton.setToolTipText("reset");
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				src.setText("null");
			}
		});
		return resetButton;
	}

	protected abstract void initMenuBar();

	private void initSettingsMenu() {
		settingsMenu = new JMenu("Settings");
		settingsMenu.addActionListener(this);
		menuBar.add(settingsMenu);

		staticLightMenuItem = new JCheckBoxMenuItem("Static light");
		staticLightMenuItem.addActionListener(this);
		settingsMenu.add(staticLightMenuItem);

		renderSkyboxMenuItem = new JCheckBoxMenuItem("Skybox");
		renderSkyboxMenuItem.addActionListener(this);
		settingsMenu.add(renderSkyboxMenuItem);

		renderWireframeMenuItem = new JCheckBoxMenuItem("Wireframe");
		renderWireframeMenuItem.addActionListener(this);
		settingsMenu.add(renderWireframeMenuItem);

		addSettings(settingsMenu);
	}

	protected abstract void addSettings(JMenu settingsMenu);

	private final void initPropertiesPanel() {
		propertiesPanel.setBounds(860, 0, 400, 660);
		propertiesPanel.setBorder(new BevelBorder(EditorUtils.BEVEL_BORDER,
				null, null, null, null));
		propertiesPanel.setLayout(null);
	}

	protected abstract void initProperties();

	private void initSettingsPanel1() {
		settingsPanel1.setBorder(new BevelBorder(EditorUtils.BEVEL_BORDER,
				null, null, null, null));
		settingsPanel1.setLayout(new GridLayout(4, 1));
	}

	protected abstract void initSettings1(JPanel settingsPanel);

	protected final void initSettingsPanel2() {
		settingsPanel2.setBorder(new BevelBorder(EditorUtils.BEVEL_BORDER,
				null, null, null, null));
		settingsPanel2.setLayout(new GridLayout(4, 1));
	}

	protected abstract void initSettings2(JPanel settingsPanel);

	protected final void initSettingsPanel3() {
		settingsPanel3.setBorder(new BevelBorder(EditorUtils.BEVEL_BORDER,
				null, null, null, null));
		settingsPanel3.setLayout(new GridLayout(4, 1));
	}

	protected abstract void initSettings3(JPanel settingsPanel);

	protected final void initSettingsPanel4() {
		settingsPanel4.setBorder(new BevelBorder(EditorUtils.BEVEL_BORDER,
				null, null, null, null));
		settingsPanel4.setLayout(new GridLayout(4, 1));
	}

	protected abstract void initSettings4(JPanel settingsPanel);

	@Override
	public void actionPerformed(ActionEvent event) {
		onActionPerformed(event.getSource());
	}

	public abstract AbstractEditorViewport getViewport();

	public abstract AbstractEditorSettings getSettings();

	public final Canvas getCanvas() {
		return canvas;
	}

	public JCheckBoxMenuItem getStaticLightMenuItem() {
		return staticLightMenuItem;
	}

	public JCheckBoxMenuItem getRenderSkyboxMenuItem() {
		return renderSkyboxMenuItem;
	}

	public JCheckBoxMenuItem getRenderWireframeMenuItem() {
		return renderWireframeMenuItem;
	}
}
