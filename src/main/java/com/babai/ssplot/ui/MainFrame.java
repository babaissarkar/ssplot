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
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.Properties;
import java.util.function.Function;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;

import com.babai.ssplot.cli.SSPlotCLI;
import com.babai.ssplot.math.io.NumParse;
import com.babai.ssplot.math.plot.*;
import com.babai.ssplot.math.plot.PlotData.PlotType;
import com.babai.ssplot.ui.controls.DUI;
import com.babai.ssplot.ui.controls.DUI.Text;
import com.babai.ssplot.ui.controls.UIButton;
import com.babai.ssplot.ui.controls.UIRadioItem;
import com.babai.ssplot.ui.help.HelpFrame;
import com.babai.ssplot.util.SwingFileChooser;
import com.babai.ssplot.util.FocusTracker;
import com.babai.ssplot.util.SystemInfo;

import static javax.swing.JOptionPane.*;

import static com.babai.ssplot.cli.ArgParse.*;
import static com.babai.ssplot.ui.controls.DUI.*;
import static com.babai.ssplot.util.UIHelper.*;

public class MainFrame extends JFrame {
	// Is dark theme enabled?
	private static boolean isDark = false;
	
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
			CrashFrame.showCrash(e);
		}
	}
	
	public MainFrame() {
		try {
			setIconImage(ImageIO.read(getClass().getResource("/ssplot.png")));
		} catch (IOException e) {
			CrashFrame.showCrash(e);
		}
		
		// Initialize logger
		logger = new StatLogger();
		logger.log(Text.tag("h1", "Welcome to SSPlot!"));
		
		var plt = new Plotter(logger);
		if (isDark) {
			plt.setFgColor(Color.WHITE);
			plt.setBgColor(Color.decode("#474c5b"));
			plt.setAxisColor(Color.WHITE);
			plt.setTicColor(Color.WHITE);
			plt.setTitleColor(Color.WHITE);
		}
		
		//
		// GUI
		//
	
		// Internal Windows
		var odeinput = new SystemInputFrame();
		var dbv = new DataView(logger, this);
		var pv = new PlotView(logger, plt);
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
		
		// Sizing/Position calculations for internal frames
		var ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		var screenBounds = ge.getMaximumWindowBounds();
		
		odeinput.setLocation(Plotter.DEFAULT_W + 35, 0); // FIXME magic number 35
		
		ifrmPlot.setSize(ifrmPlot.getWidth(), odeinput.getHeight());
		
		int dbvWidth = Math.min(dbv.getWidth(),
			screenBounds.width - odeinput.getWidth() - ifrmPlot.getWidth());
		if (dbvWidth > Plotter.DEFAULT_W/2) {
			dbv.setSize(new Dimension(dbvWidth, odeinput.getHeight()));
			dbv.setLocation(screenBounds.width - dbvWidth, 0);
			dbv.setVisible(true);
		} else {
			dbv.setVisible(false);
		}
		
		// Top area with internal windows
		var topPane = new JDesktopPane();
		topPane.setDragMode(JDesktopPane.OUTLINE_DRAG_MODE);
		topPane.add(ifrmPlot);
		topPane.add(odeinput);
		topPane.add(dbv);
		
		ifrmPlot.setVisible(true);
		odeinput.setVisible(true);
		
		// Bottom pane
		FocusTracker.install();
		
		var console = new ScriptConsole();
		var txtScratchpad = new HintTextArea()
			.hintText("You can write anything here.");
		
		// Helper lambda for the symbols input pane below
		// Creates a button that inserts text into the last focused input.
		// If the text ends with ")", caret goes just before it; otherwise caret at end.
		Function<String, UIButton> mathBtnMaker = (text) ->
			button()
				.font(DUI.Text.fallbackFont)
				.text(text).onClick(() -> {
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
		
		// Main Container
		setTitle("SSPlot");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setJMenuBar(createMenu(pv, dbv, odeinput));
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		getContentPane().add(splitPane()
			.type(JSplitPane.VERTICAL_SPLIT)
			.dividerLoc((int) (screenBounds.height * 0.7))
			.top(topPane)
			.bottom(
				tabPane()
					.tab("Logs", logger.getComponent())
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
	}
	
	private JMenuBar createMenu(PlotView pv, DataView dbv, SystemInputFrame sif) {
		var radioGroup = new ButtonGroup();
		UIRadioItem modeNormal, modeAnimate, modeOverlay;
		
		var bar = menuBar(
			menu("File").content(
					menu("New Plot").content(
						item("Load Data from File...")
							.hotkey("ctrl O")
							.onClick(() -> {
								Optional<PlotData> pdata = openFile();
								if (pdata.isPresent()) {
									pv.setCurPlot(pdata.get());
									pv.fit();
								}
							}),
							
						item("From Equation...")
							.hotkey("ctrl M")
							.onClick(() -> {
								sif.setVisible(true);
								sif.requestFocusInWindow();
							})
					),
					
					item("Save Image...")
						.hotkey("ctrl S")
						.onClick(() -> saveImage(pv.getImage())),
						
					item("Save Data...")
						.hotkey("ctrl shift S")
						.onClick(() -> saveFile(dbv.getData())),
						
//								separator(),
						
					item("Clear Logs")
						.onClick(logger::clear),
						
					item("Quit")
						.hotkey("ctrl Q")
						.onClick(() -> System.exit(0))
				),

				menu("Plot").content(
					checkItem("Show Axes").onClick(pv::toggleAxes)
						.selected(true),
						
					menu("Plot Mode").content(
						modeNormal = radioItem("Normal").onClick(pv::setNormal)
							.selected(true),
						modeOverlay = radioItem("Overlay").onClick(pv::toggleOverlayMode),
						modeAnimate = radioItem("Animate").onClick(pv::toggleAnimate)
					),
					
					item("Autoscale")
						.hotkey("ctrl F")
						.onClick(pv::fit),
						
					item("Clear plot")
						.hotkey("alt X")
						.onClick(() -> {
							pv.refresh();
							dbv.clear();
						}),
						
					menu("Plot Properties").content(
						item("Add title")
							.onClick(() -> {
								String title = showInputDialog("Title:");
								if (title != null) {
									pv.setTitle(title);
									dbv.setCurPlotTitle(title);
								}
							}),
						item("Add X axis label")
							.onClick(() -> pv.setXLabel(showInputDialog("X Label:"))),
						item("Add Y axis label")
							.onClick(() -> pv.setYLabel(showInputDialog("Y Label:"))),
						item("Set Plot Type")
							.onClick(() -> changePlotType(pv)),
						item("Set Line Width")
							.onClick(() -> setLineWidth(pv)),
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
							sif.setVisible(true);
							sif.requestFocusInWindow();
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
					item("Report Bug/Request Feature...")
						.onClick(() -> openLink("https://github.com/babaissarkar/ssplot/issues/new")),
					item("Contribute code...")
						.onClick(() -> openLink("https://github.com/babaissarkar/ssplot/pulls")),
//					item("Donate...")
//						.onClick(() -> openLink("https://ko-fi.com/lumiouse")),
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
	private void saveImage(BufferedImage img) {
		var optPath = SwingFileChooser.save(this);
		if (optPath.isPresent()) {
			try {
				ImageIO.write(img, "png", optPath.get().toFile());
			} catch (IOException e) {
				CrashFrame.showCrash(e);
			}
		}
	}
	
	public Optional<PlotData> openFile() {
		var optPath = SwingFileChooser.open(this);
		double[][] data = null;
		if (optPath.isPresent()) {
			try {
				data = NumParse.parse(optPath.get());
			} catch (IOException e) {
				CrashFrame.showCrash(e);
			}
		}
		
		if (data == null) return Optional.empty();
		
		var pdata = new PlotData(data);
		var headers = NumParse.getHeaders();
		if (!headers.isEmpty()) {
			pdata.setAxisLabels(headers);
		}
		
		return Optional.of(pdata);
	}

	public void saveFile(Optional<PlotData> optData) {
		var optPath = SwingFileChooser.save(this);
		if (optPath.isPresent() && optData.isPresent()) {
			try {
				NumParse.write(optData.get().getData(), optPath.get());
			} catch (FileNotFoundException e) {
				CrashFrame.showCrash(e);
			}
		}
	}
	
	//
	// UI Methods
	//
	private void setLineWidth(PlotView pv) {
		String strWidth = showInputDialog("Line Width:");
		if (strWidth != null) {
			try {
				int width = Integer.parseInt(strWidth);
				Optional<PlotData> pd = pv.getCurPlot();
				if (pd.isPresent()) {
					pd.get().ptX = width;
					pd.get().ptY = width;
				}
				pv.repaint();
			} catch(NumberFormatException ne) {
				showMessageDialog(null, "Non-numeric or invalid value found.", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	private void changePlotType(PlotView pv) {
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
	
	private void showAbout() {
		logger.log(ABOUT_MSG);
		
		String[] buttonStrs = {"License", "System Info", "Close"};
		int status = showOptionDialog(
			this,
			Text.tag("html", ABOUT_MSG),
			"About SSPlot",
			YES_NO_OPTION,
			INFORMATION_MESSAGE,
			new ImageIcon(getClass().getResource("/ssplot.png")),
			buttonStrs,
			buttonStrs[2]);
		
		switch(status) {
		case 0:
			new HelpFrame("License", "/docs/lgpl-2.1-standalone.html").setVisible(true);
			break;
		case 1:
			showMessageDialog(
				this,
				Text.htmlAndBody(SystemInfo.getSystemInfo()),
				"System Information",
				JOptionPane.INFORMATION_MESSAGE);
			break;
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

	public static boolean isDark() {
		return isDark;
	}
}
