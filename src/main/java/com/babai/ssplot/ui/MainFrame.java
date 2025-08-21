/*
* MainFrame.java
* 
* Copyright 2021-2025 Subhraman Sarkar <suvrax@gmail.com>
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
package com.babai.ssplot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;

import com.babai.ssplot.cli.SSPlotCLI;
import com.babai.ssplot.math.plot.*;
import com.babai.ssplot.math.plot.PlotData.PlotType;
import com.babai.ssplot.ui.controls.UIRadioItem;
import com.babai.ssplot.ui.help.HelpFrame;
import com.babai.ssplot.util.UIHelper;

import static javax.swing.JOptionPane.*;

import static com.babai.ssplot.cli.ArgParse.*;
import static com.babai.ssplot.ui.controls.DUI.*;

public class MainFrame extends JFrame {
	
	private final Plotter plt;
	private final PlotView pv;
	private final DataViewer dbv;
	private final SystemInputFrame odeinput;
	private final StatLogger logger;
	
	private static String ABOUT_MSG;
	private static String KEY_HELP_MSG;
	static {
		final Properties prop = new Properties();
		try (InputStream is = MainFrame.class.getResourceAsStream("/project.properties")) {
			prop.load(is);
			ABOUT_MSG = prop.getProperty("about_msg")
				.formatted(prop.getProperty("version"));
			KEY_HELP_MSG = prop.getProperty("key_help_msg");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public MainFrame() {
		try {
			setIconImage(ImageIO.read(getClass().getResource("/ssplot.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Initialize logger
		logger = new StatLogger();
		logger.log("<h1>Welcome to SSPlot!</h1>");
		
		plt = new Plotter(logger);
		plt.initPlot();
		
		pv = new PlotView(logger, plt);
		pv.setPadding(10);
		
		odeinput = new SystemInputFrame();
		odeinput.setResizable(true);
		odeinput.setClosable(true);
		odeinput.setIconifiable(true);
		
		dbv = new DataViewer(logger);
		dbv.setClosable(true);
		dbv.setResizable(true);
		dbv.setIconifiable(true);
		dbv.setMaximizable(true);
		
		// Create GUI
		setTitle("SSPlot");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(createMenu());
		
		var ifrmPlot = new JInternalFrame("Plot", true, false, true, true);
		ifrmPlot.setSize(Plotter.DEFAULT_W + 35, Plotter.DEFAULT_H + 100);
		ifrmPlot.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent ce) {
				// FIXME more embedded magic numbers
				pv.resize(ifrmPlot.getWidth() - 35, ifrmPlot.getHeight() - 100);
			}
		});
		
		// Plow View window: Keybindings for movement and actions
		bindAction(ifrmPlot, "left",   "LEFT",  pv::moveLeft);
		bindAction(ifrmPlot, "right",  "RIGHT", pv::moveRight);
		bindAction(ifrmPlot, "up",     "UP",    pv::moveUp);
		bindAction(ifrmPlot, "down",   "DOWN",  pv::moveDown);

		bindAction(ifrmPlot, "plus",   "J", pv::zoomIn);
		bindAction(ifrmPlot, "minus",  "F", pv::zoomOut);
		bindAction(ifrmPlot, "splus",  "H", pv::smallZoomIn);
		bindAction(ifrmPlot, "sminus", "G", pv::smallZoomOut);

		bindAction(ifrmPlot, "rotAp",  "Q", pv::rotateXPlus);
		bindAction(ifrmPlot, "rotAm",  "A", pv::rotateXMinus);
		bindAction(ifrmPlot, "rotBp",  "W", pv::rotateYPlus);
		bindAction(ifrmPlot, "rotBm",  "S", pv::rotateYMinus);
		bindAction(ifrmPlot, "rotCp",  "E", pv::rotateZPlus);
		bindAction(ifrmPlot, "rotCm",  "D", pv::rotateZMinus);
		
		// --- Zoom Section ---
		var zoomField = input().text("1").chars(4).numeric(true);
		var zoomLabel = label(" X");
		var zoomInBtn = button()
			.icon("/zoom-in.png")
			.tooltip("Zoom In (x2)")
			.onClick(() -> {
				zoomField.setText(String.format("%3.1f", pv.getScale()));
				pv.zoomIn();
			});
		var zoomOutBtn = button()
			.icon("/zoom-out.png")
			.tooltip("Zoom In (x0.5)")
			.onClick(() -> {
				zoomField.setText(String.format("%3.1f", pv.getScale()));
				pv.zoomOut();
			});
		
		// disable growing
		zoomField.setMaximumSize(zoomField.getPreferredSize());

		// --- Rotation Section ---
		JComboBox<String> axisSelector = new JComboBox<>(new String[]{"X", "Y", "Z"});
		// disable growing
		axisSelector.setMaximumSize(axisSelector.getPreferredSize());
		var rotateCWBtn = button()
			.icon("/rotate-cw.png")
			.tooltip("Rotate Clockwise")
			.onClick(() -> {
				String axis = (String) axisSelector.getSelectedItem();
				if (axis.equals("X")) {
					pv.rotateXPlus();
				} else if (axis.equals("Y")) {
					pv.rotateYPlus();
				} else {
					pv.rotateZPlus();
				}
			});
		var rotateCCWBtn = button()
			.icon("/rotate-ccw.png")
			.tooltip("Rotate Counter-clockwise")
			.onClick(() -> {
				String axis = (String) axisSelector.getSelectedItem();
				if (axis.equals("X")) {
					pv.rotateXMinus();
				} else if (axis.equals("Y")) {
					pv.rotateYMinus();
				} else {
					pv.rotateZMinus();
				}
			});

		var toolbar = toolbar(
			zoomInBtn,
			zoomField,
			zoomLabel,
			zoomOutBtn,
			
			// --- Separator ---
			Box.createRigidArea(new Dimension(10, 0)),
			
			rotateCWBtn,
			label("Axis:"),
			axisSelector,
			rotateCCWBtn
		);
		
		toolbar.setFloatable(false);
		toolbar.setRollover(true);

		ifrmPlot.add(toolbar, BorderLayout.NORTH);
		ifrmPlot.add(pv, BorderLayout.CENTER);
		
		// Update callbacks: these are called to replace Plow View
		// when data in SystemInputFrame or DataViewer changes.
		odeinput.setUpdateCallback(data -> {
			if (data == null) return;
			pv.setCurPlot(data);
			pv.setCurPlotType(data.getPltype());
			pv.fit();
			zoomField.setText(String.format("%3.1f", pv.getScale()));
			axisSelector.setEnabled(data.getColumnCount() >= 3);
			dbv.setData(data);
			dbv.show();
		});
		
		dbv.setUpdateCallback(data -> {
			if (data == null) return;
			pv.setCurPlot(data);
			pv.fit();
			zoomField.setText(String.format("%3.1f", pv.getScale()));
			axisSelector.setEnabled(data.getColumnCount() >= 3);
			odeinput.setSystem(data.getSystem());
			ifrmPlot.show();
		});
		
		// Sizing calculations for internal frames
		var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		var screenBounds = ge.getMaximumWindowBounds();
		
		// FIXME magic number
		odeinput.setLocation(Plotter.DEFAULT_W + 35, 0);
		odeinput.pack();
		
		ifrmPlot.setSize(ifrmPlot.getWidth(), odeinput.getHeight());
		
		dbv.pack();
		int dbvWidth = Math.min(dbv.getWidth(), screenBounds.width - odeinput.getWidth() - ifrmPlot.getWidth());
		if (dbvWidth > Plotter.DEFAULT_W/2) {
			dbv.setSize(new Dimension(dbvWidth, odeinput.getHeight()));
			dbv.setLocation(screenBounds.width - dbvWidth, 0);
			dbv.setVisible(true);
		} else {
			dbv.setVisible(false);
		}
		
		// Main panel
		var mainPane = new JDesktopPane();
		mainPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		mainPane.add(ifrmPlot);
		mainPane.add(odeinput);
		mainPane.add(dbv);
		
		ifrmPlot.setVisible(true);
		odeinput.setVisible(true);
		
		var ifrmLogs = new JInternalFrame("Logs", true, true, true, true);
		ifrmLogs.add(logger.getComponent());
		
		// Bottom pane
		var console = new ScriptConsole();
		var txtScratchpad = new HintTextArea();
		txtScratchpad.setHintText("You can write anything here.");
		
		var statusPane = tabPane()
			.tab("Logs", ifrmLogs.getContentPane())
			.tab("Console", console)
			.tab("Notes", txtScratchpad)
			.onChange(tabIdx -> {
				switch(tabIdx) {
				case 1:
					SwingUtilities.invokeLater(console::focusInput);
					break;
				case 2:
					SwingUtilities.invokeLater(txtScratchpad::requestFocusInWindow);
					break;
				default:
					// Nothing
				}
			});
		
		getContentPane().add(splitPane()
			.type(JSplitPane.VERTICAL_SPLIT)
			.dividerLoc((int) (screenBounds.height * 0.7))
			.top(mainPane)
			.bottom(statusPane)
		);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	private JMenuBar createMenu() {
		var radioGroup = new ButtonGroup();
		UIRadioItem modeNormal, modeAnimate, modeOverlay;
		
		var bar =menuBar(
			menu("File").content(
					menu("New Plot").content(
						item("Load Data from File...")
							.hotkey("ctrl O")
							.onClick(() -> {
								if (dbv.openFile()) {
									Optional<PlotData> pdata = dbv.getData();
									if (pdata.isPresent()) {
										pv.setCurPlot(pdata.get());
										pv.fit();
									}
								}
							}),
							
						item("From Equation...")
							.hotkey("ctrl M")
							.onClick(() -> {
								odeinput.setVisible(true);
								odeinput.requestFocusInWindow();
							})
					),
					
					item("Save Image...")
						.hotkey("ctrl S")
						.onClick(this::saveImage),
						
					item("Save Data...")
						.hotkey("ctrl shift S")
						.onClick(dbv::saveFile),
						
//								separator(),
						
					item("Clear Logs")
						.onClick(logger::clear),
						
					item("Quit")
						.hotkey("ctrl Q")
						.onClick(() -> System.exit(0))
				),

				menu("Plot").content(
					item("Toggle Axes")
						.onClick(() -> {
							plt.toggleAxes();
							pv.repaint();
						}),
						
					menu("Plot Mode").content(
						modeNormal = radioItem("Normal")
							.selected(true)
							.onClick(() -> {
								pv.setNormal();
								pv.repaint();
							}),
						modeOverlay = radioItem("Overlay")
							.onClick(() -> {
								pv.toggleOverlayMode();
								pv.repaint();
							}),
						modeAnimate = radioItem("Animate")
							.onClick(() -> {
								pv.toggleAnimate();
								pv.repaint();
							})
					),
					
					item("Autoscale")
						.hotkey("ctrl F")
						.onClick(() -> pv.fit()),
						
					item("Clear plot")
						.hotkey("ctrl X")
						.onClick(() -> {
							pv.clear();
							dbv.clear();
						}),
						
					menu("Plot Properties").content(
						item("Add title")
							.onClick(() -> {
								Optional<PlotData> pdata = pv.getCurPlot();
								if (pdata.isPresent()) {
									pdata.get().setTitle(showInputDialog("Title:"));
									pv.repaint();
								}
							}),
						item("Add X axis label")
							.onClick(() -> {
								plt.getCanvas().setXLabel(showInputDialog("X Label:"));
								pv.repaint();
							}),
						item("Add Y axis label")
							.onClick(() -> {
								plt.getCanvas().setYLabel(showInputDialog("Y Label:"));
								pv.repaint();
							}),
						item("Set Plot Type")
							.onClick(() -> changePlotType()),
						item("Set Line Width")
							.onClick(() -> setLineWidth()),
						item("Set Plot Color")
							.onClick(() -> pv.setColor(JColorChooser.showDialog(this, "Plot Color 1", Color.RED)))
					)
				),

				menu("Window").content(
					item("Data Editor...")
						.onClick(() -> {
							dbv.setVisible(true);
							dbv.requestFocusInWindow();
						}),
					item("Equation Editor...")
						.onClick(() -> {
							odeinput.setVisible(true);
							odeinput.requestFocusInWindow();
						})
				),

				menu("Help").content(
					item("Help...")
						.onClick(() -> new HelpFrame("Parser Reference", "/docs/parser_guide.html").setVisible(true)),
					item("Keymaps Help")
						.onClick(() -> {
							logger.log(KEY_HELP_MSG);
							showMessageDialog(this, "<html>" + KEY_HELP_MSG + "</html>");
						}),
					item("Homepage...")
						.onClick(() -> openLink("https://github.com/babaissarkar/ssplot")),
					item("Report An Issue...")
						.onClick(() -> openLink("https://github.com/babaissarkar/ssplot/issues")),
					item("Contribute code...")
						.onClick(() -> openLink("https://github.com/babaissarkar/ssplot/pulls")),
					item("Donate...")
						.onClick(() -> openLink("https://ko-fi.com/lumiouse")),
					item("About")
						.onClick(() -> showAbout())
				)
		);
		
		radioGroup.add(modeNormal);
		radioGroup.add(modeOverlay);
		radioGroup.add(modeAnimate);
		
		return bar;
	}
	
	/* Keybinding management helper */
	private void bindAction(JComponent control, String actionName, String hotkey, Runnable action) {
		control.getInputMap(WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
		       .put(KeyStroke.getKeyStroke(hotkey), actionName);
		control.getActionMap().put(actionName, new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				action.run();
			}
		});
	}
	
	/* Menu Actions */
	private void saveImage() {
		var files = new JFileChooser();
		int stat = files.showSaveDialog(this);
		if (stat == JFileChooser.APPROVE_OPTION) {
			File f = files.getSelectedFile();
			plt.save(f);
		}
	}
	
	private void setLineWidth() {
		String strWidth = showInputDialog("Line Width:");
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
	
	private void changePlotType() {
		PlotType type = (PlotType) showInputDialog(
			this,
			"Choose Plot Type :",
			"Plot Type",
			QUESTION_MESSAGE,
			null,
			PlotType.values(),
			PlotType.LINES);
		
		if (type != null) {
			pv.setCurPlotType(type);
		}
	}
	
	private void openLink(String url) {
		if (url.isBlank()) {
			logger.log("Empty link, not opening.");
			return;
		}
		
		if (!Desktop.isDesktopSupported()) {
			logger.log("Browsing links not supported on this platform!");
			return;
		}
		
		try {
			Desktop.getDesktop().browse(new URI(url));
		} catch (URISyntaxException|IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showAbout() {
		logger.log(ABOUT_MSG);
		
		String[] buttonStrs = {"License", "Close"};
		int status = showOptionDialog(
			this,
			"<html>" + ABOUT_MSG + "</html>",
			"About SSPlot",
			YES_NO_OPTION,
			INFORMATION_MESSAGE,
			new ImageIcon(getClass().getResource("/ssplot.png")),
			buttonStrs,
			buttonStrs[1]);
		
		if (status == 0) {
			new HelpFrame("License", "/docs/lgpl-2.1-standalone.html").setVisible(true);
		}
	}	
	
	public static void main(String[] args) {
		if (hasArg("cli", args)) {
			SSPlotCLI.main(removeArg("cli", args));
			return;
		}
		
		// Global UI Theme Configuration
		if (hasArg("dark", args)) {
			UIHelper.setDarkLF();
		} else if(hasArg("nimbus", args)) {
			UIHelper.setNimbusLF();
		} else if (!hasArg("metal", args)) {
			UIHelper.setLightLF();
		}
		
		Properties prop = new Properties();
		try {
			prop.load(MainFrame.class.getResourceAsStream("/com/babai/ssplot/ui/FlatLaf.properties"));
			// After loading properties (customProps)
			prop.forEach((key, value) -> { 
				String val = value.toString();
				if (val.startsWith("#")) {
					UIManager.put(key, Color.decode(val));
				} else {
					try {
						UIManager.put(key, Integer.parseInt(val));
					} catch (Exception e) {
						UIManager.put(key, val);
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Reduce tooltip times so user gets quick feedback
		var tooltipManager = ToolTipManager.sharedInstance();
		tooltipManager.setInitialDelay(50);
		tooltipManager.setDismissDelay(5000);
		tooltipManager.setReshowDelay(100);
		
		new MainFrame().setVisible(true);
	}
}
