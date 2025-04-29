/*
* PlotterFrame.java
* 
* Copyright 2021-2024 Subhraman Sarkar <suvrax@gmail.com>
* 
* This program is free software; you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation; either version 2.1 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Lesser General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
* MA 02110-1301, USA.
* 
* 
*/
package math.plot;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JColorChooser;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme;
import com.formdev.flatlaf.util.SystemInfo;

import cli.SSPlotCLI;
import help.HelpFrame;
import math.plot.PlotData.PlotType;

public class MainFrame extends JFrame {
	
	private final Plotter plt;
	private final PlotView pv;
	private final DBViewer dbv;
	private final ODEInputFrame odeinput;
	private StatLogger logger;
	
	private final JMenuItem jmSave;
	private final JMenuItem jmOpen;
	private final JMenuItem jmShowData;
	private final JMenuItem jmQuit;
	private final JMenuItem jmPlotType;
	private final JMenuItem jmPhase;
	private final JMenuItem jmCol;
	private final JMenuItem jmClear;
	private final JMenuItem jmSvData;
	private final JCheckBoxMenuItem jmAxes;
	private final JMenuItem jmClearLogs;
	private final JMenuItem jmTitle;
	private final JMenuItem jmLineWidth;
	private final JMenuItem jmXLabel, jmYLabel;
	
	// FIXME should be a theme-independent way instead of relying on this
	/* Valid only for default Metal look and feel. */
	private static final int MENUBAR_WIDTH = 60;
	
	// Note: html string does not show up correctly in JOptionPane
	// unless the \n -> <br/> replacement is done.
	private static String VERSION;
	static {
		final Properties prop = new Properties();
		try(InputStream is = MainFrame.class.getResourceAsStream("/project.properties")) {
			prop.load(is);
			VERSION = prop.getProperty("version");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static final String ABOUT_MSG ="""
	<h1>SSPlot %s</h1>
	Copyright 2021-2024 Subhraman Sarkar
	Available under the LGPL 2.1 license or, (at your choice) any later version.
	<b>Homepage :</b> https://github.com/babaissarkar/ssplot
	""".replace("\n", "<br/>").formatted(VERSION);
	
	private static final String KEY_HELP_MSG = """
	<h1>Key bindings</h1>
	<p>(Can be used when the plot window is selected.)</p>
	<b>Arrow keys :</b> translate graph,
	<b>j</b> and <b>f</b> : zoom in and out (2x or 0.5x)
	<b>g</b> and <b>h</b> : fine zoom adjustment
	<b>q, a; w, s; e, d</b> : 3d rotation keys
	""".replace("\n", "<br/>");
	
	
	public MainFrame() {
		// Set icon
		this.setIconImage(
				Toolkit.getDefaultToolkit().getImage(
						getClass().getResource("/ssplot.png")));
		
		// Initialize logger
		this.setLogger(new StatLogger());
		this.getLogger().log("<h1>Welcome to SSPlot!</h1>");
		
		plt = new Plotter(logger);
		plt.initPlot();
		
		pv = new PlotView(this.getLogger(), plt);
		
		odeinput = new ODEInputFrame(this);
		odeinput.setResizable(true);
		odeinput.setClosable(true);
		odeinput.setIconifiable(true);
		
		dbv = new DBViewer(odeinput, pv);
		dbv.setClosable(true);
		dbv.setResizable(true);
		dbv.setIconifiable(true);
		dbv.setMaximizable(true);
		
		// Create GUI
		
		setTitle("SSPlot");
		// FIXME this MENUBAR_WIDTH should really be removed
		setBounds(100, 20, 1300, 700 + MainFrame.MENUBAR_WIDTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JInternalFrame ifrmPlot = new JInternalFrame("Plot", true, false, true, true);
		JInternalFrame ifrmLogs = new JInternalFrame("Logs", true, true, true, true);
		
		// Initialize menu variables
		jmOpen = new JMenuItem("From File...");
		jmPhase = new JMenuItem("From Equation...");
		jmSvData = new JMenuItem("Save Data...");
		jmSave = new JMenuItem("Save Image...");
		jmShowData = new JMenuItem("View/Edit Plot Data");
		jmQuit = new JMenuItem("Quit");
		
		JRadioButtonMenuItem jcmNormal = new JRadioButtonMenuItem("Normal mode");
		JRadioButtonMenuItem jcmOverlay = new JRadioButtonMenuItem("Overlay mode");
		JRadioButtonMenuItem jcmAnimate = new JRadioButtonMenuItem("Animate");
		jcmNormal.setSelected(true);
		ButtonGroup bg = new ButtonGroup();
		bg.add(jcmNormal);
		bg.add(jcmAnimate);
		bg.add(jcmOverlay);
		
		jmClearLogs = new JMenuItem("Clear Logs");
		jmTitle = new JMenuItem("Add title");
		jmXLabel = new JMenuItem("Add X axis label");
		jmYLabel = new JMenuItem("Add Y axis label");
		jmAxes = new JCheckBoxMenuItem("Toggle axes");
		jmAxes.setSelected(true);
		jmLineWidth = new JMenuItem("Set Line Width");
		jmCol = new JMenuItem("Set Plot Color");
		jmPlotType = new JMenuItem("Set Plot Type");
		jmClear = new JMenuItem("Clear plot");
		JMenuItem jmFit = new JMenuItem("Autoscale");
		
		JMenuItem jmShowDBV = new JMenuItem("Data Editor...");
		JMenuItem jmShowEqn = new JMenuItem("Equation Editor...");
		
		JMenuItem jmShowHelp = new JMenuItem("Help...");
		JMenuItem jmKeyHelp = new JMenuItem("Keymaps Help");
		JMenuItem jmAbout = new JMenuItem("About");
		
		// Add keybindings
		jmOpen.setAccelerator(
		KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		jmSave.setAccelerator(
		KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		jmSvData.setAccelerator(
		KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
		jmShowData.setAccelerator(
		KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
		jmQuit.setAccelerator(
		KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
		
		jmPhase.setAccelerator(
		KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
		jmFit.setAccelerator(
		KeyStroke.getKeyStroke(KeyEvent.VK_F, ActionEvent.CTRL_MASK));
		jmClear.setAccelerator(
		KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		
		// Add Listener
		jmOpen.addActionListener(e -> {
			dbv.openFile();
			pv.setCurPlot(dbv.getData());
		});
		jmSave.addActionListener(e -> saveImage());
		jmKeyHelp.addActionListener(e -> {
			// Shows help message
			showMsg(KEY_HELP_MSG);
			JOptionPane.showMessageDialog(this, "<html>" + KEY_HELP_MSG + "</html>");
		});
		jmShowData.addActionListener(e -> dbv.setVisible(true));
		jmQuit.addActionListener(e -> System.exit(0));
		jmSvData.addActionListener(e -> dbv.saveFile());
		jmClear.addActionListener(e -> {
			pv.clear();
			dbv.clear();
		});
		jmClearLogs.addActionListener(e -> logger.clear());
		jmAbout.addActionListener(e -> showAbout());
		jmTitle.addActionListener(e -> {
			Optional<PlotData> pdata = pv.getCurPlot();
			if (pdata.isPresent()) {
				pdata.get().setTitle(JOptionPane.showInputDialog("Title:"));
				pv.repaint();
			}
		});
		jmXLabel.addActionListener(e -> {
			plt.getCanvas().setXLabel(JOptionPane.showInputDialog("X Label:"));
			pv.repaint();
		});
		jmYLabel.addActionListener(e -> {
			plt.getCanvas().setYLabel(JOptionPane.showInputDialog("Y Label:"));
			pv.repaint();
		});
		jmCol.addActionListener(e -> {
			pv.setColor(JColorChooser.showDialog(this, "Plot Color 1", Color.RED));
		});
		jmLineWidth.addActionListener(e -> setLineWidth());
		jmAxes.addActionListener(e -> {
			plt.toggleAxes();
			pv.repaint();
		});
		jmPhase.addActionListener(e -> odeinput.setVisible(true));
		jmPlotType.addActionListener(e -> changePlotType());
		jmFit.addActionListener(e -> pv.fit());
		jmShowHelp.addActionListener(e -> {
			new HelpFrame("Parser Reference", "/docs/parser_guide.html").setVisible(true);
		});
		
		jmShowDBV.addActionListener(e -> {
			if (!dbv.isVisible()) {
				dbv.setVisible(true);
			}
		});
		
		jmShowEqn.addActionListener(e -> {
			if (!odeinput.isVisible()) {
				odeinput.setVisible(true);
			}
		});

		jcmNormal.addActionListener(e -> {
			pv.setNormal();
			pv.repaint();
		});
		jcmOverlay.addActionListener(e -> {
			pv.toggleOverlayMode();
			pv.repaint();
		});
		jcmAnimate.addActionListener(e -> {
			pv.toggleAnimate();
			pv.repaint();
		});
		
		// Setup menu
		JMenu mnuFile = new JMenu("File");
		JMenu sbmnNew = new JMenu("New Plot");
		sbmnNew.add(jmOpen);
		sbmnNew.add(jmPhase);
		
		mnuFile.add(sbmnNew);
		mnuFile.add(jmSave);
		mnuFile.add(jmSvData);
		mnuFile.addSeparator();
		mnuFile.add(jmClearLogs);
		mnuFile.add(jmQuit);
		
		JMenu mnuPlot = new JMenu("Plot");
		JMenu mnuMode = new JMenu("Plot Mode");
		mnuPlot.add(jmAxes);
		mnuMode.add(jcmNormal);
		mnuMode.add(jcmOverlay);
		mnuMode.add(jcmAnimate);
		mnuPlot.add(mnuMode);
		mnuPlot.add(jmFit);
		mnuPlot.add(jmClear);
		
		JMenu mnuProp = new JMenu("Plot Properties");
		mnuProp.add(jmTitle);
		mnuProp.add(jmXLabel);
		mnuProp.add(jmYLabel);
		mnuProp.add(jmPlotType);
		mnuProp.add(jmLineWidth);
		mnuProp.add(jmCol);
		mnuPlot.add(mnuProp);
		
		JMenu mnuWindow = new JMenu("Window");
		mnuWindow.add(jmShowDBV);
		mnuWindow.add(jmShowEqn);
		
		JMenu mnuHelp = new JMenu("Help");
		mnuHelp.add(jmShowHelp);
		mnuHelp.add(jmKeyHelp);
		mnuHelp.add(jmAbout);
		
		JMenuBar jmb = new JMenuBar();
		jmb.add(mnuFile);
		jmb.add(mnuPlot);
		jmb.add(mnuWindow);
		jmb.add(mnuHelp);
		
		this.setJMenuBar(jmb);
		
		// Main layouting
		JDesktopPane mainPane = new JDesktopPane();
		mainPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		
		ifrmPlot.setSize(Plotter.DEFAULT_W+50, Plotter.DEFAULT_H + 80);
		ifrmPlot.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				// FIXME more embedded magic numbers
				pv.resize(ifrmPlot.getWidth() - 50, ifrmPlot.getHeight() - 50 - MENUBAR_WIDTH);
			}
		});
		
		// FIXME more embedded magic numbers
		dbv.setSize(500, 500);
		dbv.setLocation(Plotter.DEFAULT_W + 100, 180);
		dbv.setDefaultCloseOperation(JInternalFrame.HIDE_ON_CLOSE);
		
		odeinput.setSize(600, Plotter.DEFAULT_H + 80);
		odeinput.setLocation(Plotter.DEFAULT_W + 100, 0);
		
		ifrmPlot.add(pv);
		ifrmLogs.add(this.getLogger().getComponent());
		
		mainPane.add(ifrmPlot);
		mainPane.add(odeinput);
		mainPane.add(dbv);
		
		odeinput.pack();
		
		ifrmPlot.setVisible(true);
		odeinput.setVisible(true);
		
		JTabbedPane statusPane = new JTabbedPane();
		statusPane.addTab("Logs", ifrmLogs.getContentPane());
		ScriptConsole con = new ScriptConsole();
		statusPane.addTab("Console", con);
		JTextArea txtNotes = new JTextArea();
		txtNotes.setText("You can write anything here.");
		statusPane.addTab("Notes", txtNotes);
		
		JSplitPane mainPane2 = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		mainPane2.setDividerLocation(550);
		mainPane2.setTopComponent(mainPane);
		mainPane2.setBottomComponent(statusPane);
		
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(mainPane2, BorderLayout.CENTER);
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	public void startApp() {
		if (!this.isVisible()) {
			this.setVisible(true);
		}
	}
	
	/* Getters and Setters */
	public StatLogger getLogger() {
		return this.logger;
	}
	
	public void setLogger(StatLogger logger) {
		this.logger = logger;
	}
	
	/* Menu Actions */
	public void saveImage() {
		JFileChooser files = new JFileChooser();
		int stat = files.showSaveDialog(this);
		if (stat == JFileChooser.APPROVE_OPTION) {
			File f = files.getSelectedFile();
			plt.save(f);
		}
	}
	
	public void setLineWidth() {
		String strWidth = JOptionPane.showInputDialog("Line Width:");
		if (strWidth != null) {
			int width = Integer.parseInt(strWidth);
			Optional<PlotData> pd = pv.getCurPlot();
			if (pd.isPresent()) {
				pd.get().ptX = width;
				pd.get().ptY = width;
			}
			pv.repaint();
		}
	}
	
	public void changePlotType() {
		PlotType type = (PlotType) JOptionPane.showInputDialog(
		this,
		"Choose Plot Type :",
		"Plot Type",
		JOptionPane.QUESTION_MESSAGE,
		null,
		PlotType.values(),
		PlotType.LINES
		);
		
		if (type != null) {
			pv.setCurPlotType(type);
		}
	}
	
	public void showAbout() {
		String[] buttonStrs = {"Homepage", "Report a problem...", "License", "Close"};
		
		logger.log(ABOUT_MSG);
		int status = JOptionPane.showOptionDialog(
			this,
			"<html>" + ABOUT_MSG + "</html>",
			"About SSPlot",
			JOptionPane.YES_NO_CANCEL_OPTION,
			JOptionPane.INFORMATION_MESSAGE,
			new ImageIcon(
				Toolkit.getDefaultToolkit().getImage(
					MainFrame.class.getResource("/ssplot.png"))),
					buttonStrs,
					buttonStrs[3]);
		
		logger.log("Selected option: " + status);
		
		switch (status) {
			// TODO reduce duplicated code in case 0 and 1
			case 0:
			if (Desktop.isDesktopSupported()) {
				URI homepage;
				try {
					homepage = new URI("https://github.com/babaissarkar/ssplot");
					Desktop.getDesktop().browse(homepage);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				logger.log("Not supported on this platform!");
			}
			break;
			case 1:
			if (Desktop.isDesktopSupported()) {
				URI homepage;
				try {
					homepage = new URI("https://github.com/babaissarkar/ssplot/issues");
					Desktop.getDesktop().browse(homepage);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				logger.log("Not supported on this platform!");
			}
			break;
			case 2:
			new HelpFrame("License", "/docs/lgpl-2.1-standalone.html").setVisible(true);
			break;
			default:
			// Do nothing
		}
	}
	
	public void showMsg(String str) {
		logger.log(str);
	}
	
	public void setPlotData(PlotData pdata) {
		pv.setCurPlot(pdata);
		pv.setCurPlotType(pdata.getPltype());
		dbv.setData(pdata);
	}
	
	// Look and Feel
	public void setNimbusLF() {
		for (LookAndFeelInfo lafinf : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(lafinf.getName())) {
				try {
					UIManager.setLookAndFeel(lafinf.getClassName());
				} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException
				| UnsupportedLookAndFeelException e) {
					e.printStackTrace();
					setMetalLF();
				}
				SwingUtilities.updateComponentTreeUI(this);
			}
		}
	}
	
	public void setMetalLF() {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			//			UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		} catch (ClassNotFoundException | InstantiationException
		| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		SwingUtilities.updateComponentTreeUI(this);
	}
	
	public static void main(String[] args) {
		/* Global UI Configuration */
		//        UIManager.put("Label.font", new FontUIResource("Cantarell", Font.PLAIN, 15));
		//		UIManager.put("RadioButton.font", new FontUIResource("Cantarell", Font.PLAIN, 16));
		//		
		//		UIManager.put("RadioButton.foreground", new Color(80,28,0));
		//		
		UIManager.put("Menu.selectionBackground", new Color(255,156,95));
		UIManager.put("Menu.selectionForeground", new Color(0,0,0));
		UIManager.put("MenuItem.selectionBackground", new Color(255,156,95));
		UIManager.put("MenuItem.selectionForeground", new Color(0,0,0));
		UIManager.put("MenuItem.checkBackground", new Color(153, 204, 255));
		UIManager.put("MenuItem.acceleratorSelectionForeground", new Color(0,0,0));
		//		UIManager.put("MenuItem.foreground", new Color(4,88,25));
		//		UIManager.put("MenuItem.background", Color.WHITE);
		
		if (hasArg("dark", args)) {
			FlatArcDarkOrangeIJTheme.setup();
		} else if (hasArg("metal", args)) {
			// do nothing, default theme
		} else {
			FlatArcOrangeIJTheme.setup();
		}
		
		if (SystemInfo.isLinux) {
			JFrame.setDefaultLookAndFeelDecorated(true);
			JDialog.setDefaultLookAndFeelDecorated(true);
		}
		
		UIManager.put("Button.arc", 20);
		UIManager.put("TextComponent.arc", 50);
		
		MainFrame pframe = new MainFrame();
		
		if(hasArg("nimbus", args)) {
			pframe.setNimbusLF();
			pframe.pack();
		}
		
		if (hasArg("cli", args)) {
			SSPlotCLI.main(args);
		} else {
			pframe.startApp();
		}
	}
	
	/* Basic command line option parsing */
	public static boolean hasArg(String arg, String[] args) {
		// Note: arg has no hyphen/slash
		boolean result = false;
		
		for (String a : args) {
			String option = a;
			if (a.startsWith("-")||a.startsWith("/")) {
				option = a.substring(1, a.length());
			} else if (a.startsWith("--")) {
				option = a.substring(2, a.length());
			}
			
			if (option.equalsIgnoreCase(arg)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
	
}
