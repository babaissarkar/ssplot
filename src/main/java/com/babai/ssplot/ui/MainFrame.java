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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import com.babai.ssplot.cli.SSPlotCLI;
import com.babai.ssplot.math.plot.*;
import com.babai.ssplot.math.plot.PlotData.PlotType;
import com.babai.ssplot.ui.controls.DUI.Text;
import com.babai.ssplot.ui.controls.UIButton;
import com.babai.ssplot.ui.controls.UIRadioItem;
import com.babai.ssplot.ui.help.HelpFrame;
import com.babai.ssplot.util.FocusTracker;

import static javax.swing.JOptionPane.*;

import static com.babai.ssplot.cli.ArgParse.*;
import static com.babai.ssplot.ui.controls.DUI.*;
import static com.babai.ssplot.util.UIHelper.*;

public class MainFrame extends JFrame {
	// Is dark theme enabled?
	public static boolean isDark = false;
	
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
		logger.log(Text.tag("h1", "Welcome to SSPlot!"));
		
		plt = new Plotter(logger);
		if (isDark) {
			plt.setFgColor(Color.WHITE);
			plt.setBgColor(Color.decode("#474c5b"));
			plt.setAxisColor(Color.WHITE);
			plt.setTicColor(Color.WHITE);
			plt.setTitleColor(Color.WHITE);
			plt.clear();
		}
		
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
		
		// Plot Area
		pv = new PlotView(logger, plt);
		var ifrmPlot = new PlotFrame(pv);
		ifrmPlot.title("Plot");
		
		// Update callbacks: these are called to update PlotView
		// when data in SystemInputFrame or DataViewer changes.
		odeinput.setUpdateCallback(data -> {
			if (data == null) return;
			ifrmPlot.updateView(data);
			dbv.setData(data);
			dbv.show();
		});
		
		dbv.setUpdateCallback(data -> {
			if (data == null) return;
			ifrmPlot.updateView(data);
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
		int dbvWidth = Math.min(dbv.getWidth(),
			screenBounds.width - odeinput.getWidth() - ifrmPlot.getWidth());
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
		
		var ifrmLogs = iframe("Logs")
			.resizable(true)
			.iconifiable(true)
			.maximizable(true)
			.closable(false)
			.content(logger.getComponent());
		
		// Bottom pane
		FocusTracker.install();
		
		var console = new ScriptConsole();
		var txtScratchpad = new HintTextArea()
			.hintText("You can write anything here.");
		
		// Helper lambda for the symbols input pane below
		// Creates a button that inserts text into the last focused input.
		// If the text ends with ")", caret goes just before it; otherwise caret at end.
		Function<String, UIButton> mathBtnMaker = (text) ->
			button().text(text).onClick(() -> {
				if (text.endsWith(")")) {
					FocusTracker.insertTextWithCaret(text, text.length() - 1);
				} else {
					FocusTracker.insertText(text);
				}
			});
			
		var symbolsPane = grid()
			.insets(5)
			.padx(10).pady(10)
			.row()
				.spanx(10)
				.column(label("Click a button to insert the corresponding symbol in an input area"))
				.row()
					.column(mathBtnMaker.apply("+"))
					.column(mathBtnMaker.apply("-"))
					.column(mathBtnMaker.apply("*"))
					.column(mathBtnMaker.apply("/"))
					.column(mathBtnMaker.apply("^"))
					.column(mathBtnMaker.apply("("))
					.column(mathBtnMaker.apply(")"))
					.column(mathBtnMaker.apply("π"))
					.column(mathBtnMaker.apply("e"))
				.row()
					.column(mathBtnMaker.apply("α"))
					.column(mathBtnMaker.apply("β"))
					.column(mathBtnMaker.apply("γ"))
					.column(mathBtnMaker.apply("δ"))
					.column(mathBtnMaker.apply("θ"))
					.column(mathBtnMaker.apply("φ"))
					.column(mathBtnMaker.apply("λ"))
					.column(mathBtnMaker.apply("μ"))
					.column(mathBtnMaker.apply("ψ"))
				.row()
					.column(mathBtnMaker.apply("sin()"))
					.column(mathBtnMaker.apply("cos()"))
					.column(mathBtnMaker.apply("tan()"))
					.column(mathBtnMaker.apply("asin()"))
					.column(mathBtnMaker.apply("acos()"))
					.column(mathBtnMaker.apply("atan()"))
					.column(mathBtnMaker.apply("log()"))
					.column(mathBtnMaker.apply("exp()"))
					.column(mathBtnMaker.apply("ln()"))
			.emptyBorder(5);
		
		getContentPane().add(splitPane()
			.type(JSplitPane.VERTICAL_SPLIT)
			.dividerLoc((int) (screenBounds.height * 0.7))
			.top(mainPane)
			.bottom(
				tabPane()
					.tab("Logs", ifrmLogs.getContentPane())
					.tab("Console", console)
					.tab("Notes", scrollPane(txtScratchpad))
					.tab("Math Symbols", scrollPane(symbolsPane))
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
					})
			)
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
							pv.refresh();
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
								plt.setXLabel(showInputDialog("X Label:"));
								pv.repaint();
							}),
						item("Add Y axis label")
							.onClick(() -> {
								plt.setYLabel(showInputDialog("Y Label:"));
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
							showMessageDialog(this, Text.tag("html", KEY_HELP_MSG));
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
			Text.tag("html", ABOUT_MSG),
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
		
		// Global UI Configuration
		if (hasArg("dark", args)) {
			MainFrame.isDark = true;
			setDarkLF();
		} else if(hasArg("nimbus", args)) {
			setNimbusLF();
		} else if (!hasArg("metal", args)) {
			setLightLF();
		}
		
		loadUIProperties();

		// Reduce tooltip times so user gets quick feedback
		var tooltipManager = ToolTipManager.sharedInstance();
		tooltipManager.setInitialDelay(50);
		tooltipManager.setDismissDelay(5000);
		tooltipManager.setReshowDelay(100);
		
		new MainFrame().setVisible(true);
	}
}
