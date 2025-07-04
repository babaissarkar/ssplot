/*
 * SystemInputFrame.java
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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.system.core.EquationSystem;
import com.babai.ssplot.math.system.core.SystemMode;
import com.babai.ssplot.math.system.parser.ParserManager;
import com.babai.ssplot.math.system.solver.Solver;
import com.babai.ssplot.ui.controls.StateVar;
import com.babai.ssplot.ui.controls.UIInput;

import static com.babai.ssplot.ui.controls.DUI.*;

/** 
 * This class takes the input from user, sends data to Solver backend,
 * gets processed data from backend, and send it back to MainFrame for plotting.
 * @author babaissarkar
 */

// TODO noOfEqns() is not a correct check, it triggers for any N eqns
// instead of only the first N
public class SystemInputFrame extends JInternalFrame {
	// Data global vars
	private StateVar<SystemMode> curMode;
	// TODO perhaps system should be a StateVar too?
	// But how to handle system's internal fields?
	// Regression: setting this from outside doesn't cause the fields to
	// update.
	private EquationSystem system;
	private PlotData curData;
	
	private Consumer<PlotData> updater;

	public SystemInputFrame() {
		system = new EquationSystem();
		curData = new PlotData();
		curMode = new StateVar<>(SystemMode.ODE);
		curMode.bind(() -> system.mode = curMode.get());
		initInputDialog();
	}

	private void initInputDialog() {
		// UI Data
		final String sub_markup = "<html><body>%s<sub>%s</sub>%s</body></html>";
		final String small_markup = "<html><body style='font-size:12'>%s</body></html>";
		final String[] axes = {"X", "Y", "Z"};
		final String[] tags = {"min", "max", "step"};
		final double[] rangeAsArray = {
			EquationSystem.DEFAULT_RANGE.min(),
			EquationSystem.DEFAULT_RANGE.max(),
			EquationSystem.DEFAULT_RANGE.step()
		};
		
		var eqnFieldLabels = new HashMap<SystemMode, String[]>();
		eqnFieldLabels.put(
			SystemMode.ODE,
			forEach(axes, i -> "d%s/dt =".formatted(axes[i].toLowerCase()), String[]::new)
		);
		eqnFieldLabels.put(
			SystemMode.DFE,
			forEach(axes, i -> sub_markup.formatted(axes[i].toLowerCase(), "n+1", " ="), String[]::new)
		);
		eqnFieldLabels.put(
			SystemMode.FN1,
			new String[] { "y(x) =", "", "" }
		);
		eqnFieldLabels.put(
			SystemMode.FN2,
			new String[] { "z(x, y) =", "", "" }
		);
		
		// Creating Gui
		setTitle("System Parameters");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// Iteration paramters entry
		var pnlCounts = new JPanel();
		pnlCounts.setLayout(new GridBagLayout());

		var gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.NONE;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		pnlCounts.add(label("Iteration count"), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		pnlCounts.add(
			input()
				.columns(6)
				.text("" + EquationSystem.DEFAULT_N)
				.enabled(curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE)))
				.numeric(true)
				.onChange(text -> system.n = Integer.parseInt(text)),
			gbc
		);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		pnlCounts.add(label("Iteration stepsize"), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		pnlCounts.add(
			input()
				.columns(6)
				.text("" + EquationSystem.DEFAULT_H)
				.enabled(curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE)))
				.numeric(true)
				.onChange(text -> system.h = Double.parseDouble(text)),
			gbc
		);

		pnlCounts.setBorder(
				BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(new Color(127, 0, 140), 3),
						"Iteration Parameters",
						TitledBorder.LEFT,
						TitledBorder.TOP,
						new Font("Serif", Font.BOLD, 12),
						new Color(127, 0, 140).darker().darker()));


		// Equations Entry
		var pnlMatrix = new JPanel();
		pnlMatrix.setLayout(new GridBagLayout());

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		
		List<StateVar<Boolean>> eqnCondition = List.of(
			new StateVar<Boolean>(true),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE)),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE))
		);

		for (int i = 0; i < axes.length; i++) {
			final int idx = i;
			// Add the label (column 0)
			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE; // Don't stretch the label
			pnlMatrix.add(label().bind(curMode.when(mode -> eqnFieldLabels.get(mode)[idx])), gbc);

			// Add the text field (column 1)
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL; // Grow textfield to fill available space
			pnlMatrix.add(
				input()
					.columns(10)
					.enabled(eqnCondition.get(idx))
					// force triggers a curMode update
					// FIXME find a better way than a forced update
					.onChange(text -> {
						system.eqns[idx] = text; 
						curMode.set(curMode.get());
					}),
				gbc
			);
		}

		pnlMatrix.setBorder(
				BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(new Color(255, 90, 38), 3),
						"Equations",
						TitledBorder.LEFT,
						TitledBorder.TOP,
						new Font("Serif", Font.BOLD, 12),
						new Color(255, 90, 38).darker().darker()));

		// Ranges entry
		var pnlRange = new JPanel(new GridBagLayout());
		var tfsRange = new UIInput[axes.length * tags.length];
		
		List<StateVar<Boolean>> rangeConditions = List.of(
			curMode.when(mode -> noOfEqns() > 0),
			curMode.when(mode -> (mode != SystemMode.FN1) && noOfEqns() >= 2),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() == 3)
		);

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		for (int row = 0; row < axes.length; row++) {
			for (int i = 0; i < tags.length; i++) {
				int col = i * tags.length;

				gbc.gridx = col;
				gbc.gridy = row;
				gbc.weightx = 0;
				pnlRange.add(label(sub_markup.formatted(axes[row], tags[i], "")), gbc);

				gbc.gridx = col + 1;
				gbc.weightx = 1;
				final int row_idx = row;
				pnlRange.add(
					tfsRange[i] = input()
						.columns(5)
						.numeric(true)
						.text("" + rangeAsArray[i])
						.enabled(rangeConditions.get(row))
						.onChange(() -> {
							system.ranges[row_idx] = new EquationSystem.Range(
								tfsRange[3*row_idx  ].value(),
								tfsRange[3*row_idx+1].value(),
								tfsRange[3*row_idx+2].value()
							);
						}),
					gbc
				);

				gbc.gridx = col + 2;
				gbc.weightx = 0;
				pnlRange.add(Box.createHorizontalStrut(10), gbc);
			}
		}

		pnlRange.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(24, 110, 1), 3),
				"Ranges",
				TitledBorder.LEFT,
				TitledBorder.TOP,
				new Font("Serif", Font.BOLD, 12),
				new Color(24, 110, 1).darker().darker()));
		
		// Enable conditions for various input fields
		List<StateVar<Boolean>> inputConditions = List.of(
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() >= 2),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() >= 2),
			curMode.when(mode -> (mode == SystemMode.ODE && noOfEqns() == 3))
		);
		
		var plot2dCondition = curMode.when(mode ->
			(mode == SystemMode.ODE && noOfEqns() == 2)
			|| (mode == SystemMode.DFE && noOfEqns() >= 1)
			|| mode == SystemMode.FN1 
		);
		
		var plot3dCondition = curMode.when(mode ->
			(mode == SystemMode.ODE && noOfEqns() == 3)
			|| mode == SystemMode.FN2
		);
		
		// Plot Buttons
		var pnlButton = hbox(
			button()
				.icon("/2d.png")
				.tooltip("Draw 2d plot")
				.enabled(plot2dCondition)
				.onClick(this::plot2D),
				
			button()
				.icon("/3d.png")
				.tooltip("Draw 3d plot")
				.enabled(plot3dCondition)
				.onClick(this::plot3D),
				
			button()
				.icon("/cobweb.png")
				.tooltip("Draw Cobweb Plot")
				.enabled(curMode.when(mode -> (mode == SystemMode.DFE && noOfEqns() >= 1)))
				.onClick(this::plotCobweb),
				
			button()
				.icon("/vfield.png")
				.tooltip("Draw Direction Field")
				.enabled(curMode.when(mode -> (mode == SystemMode.ODE && noOfEqns() == 2)))
				.onClick(this::plotDirectionField)
		).gap(5, 5);

		add(
			vbox(
				toolbar(
					pnlButton,
					
					// Entry area for the point where the system is to be solved (X,Y,Z)
					// Which of these will be enabled depends on the system of equation's type
					borderPane()
						.north(label("Solve At:"))
						.center(
							hbox(
								forEach(axes, idx -> hbox(
									label(String.format(small_markup, axes[idx])),
									input()
										.columns(3)
										.enabled(inputConditions.get(idx))
										.onChange(text -> system.solnPoint[idx] = Double.parseDouble(text))
								))
							).gap(5, 5)
						),
					
					hbox(
						button()
							.icon("/cross.png")
							.tooltip("Clear changes")
							.onClick(this::hide) // TODO this should reset everything to default vals
					)
				),
				
				radioGroup(SystemMode.class)
					.options(SystemMode.values(), SystemMode.ODE)
					.bind(curMode),
					
				pnlCounts,
				pnlMatrix,
				pnlRange
			).emptyBorder(10)
		);
	}
	
	private int noOfEqns() {
		return system.numberOfEqns();
	}

	private void setData(PlotData pdata) {
		this.curData = pdata;
	}

	private PlotData getData() {
		return this.curData;
	}
	
	public EquationSystem getSystem() {
		return this.system;
	}

	public void setSystem(EquationSystem system) {
		this.system = system;
		curMode.set(system.mode);
	}

	// TODO these methods sort of violate MVC, perhaps there should be some sort
	// of controller class?
	// Plotting functions : Calculates PlotData from EquationSystem
	private void plotDirectionField() {
		var solver = new Solver(ParserManager.getParser(), system);
		PlotData pdata = new PlotData(solver.directionField());
		pdata.setPltype(PlotData.PlotType.VECTORS);
		pdata.setSystem(system);
		setData(pdata);
		updater.accept(pdata);
	}
	
	private void plot2D() {
		switch (curMode.get()) {
		case FN1:
			plotFunction2D();
			break;
		default:
			plotTrajectory(system.solnPoint[0], system.solnPoint[1]);
		}
		updater.accept(getData());
	}
	
	private void plot3D() {
		switch (curMode.get()) {
		case FN2:
			plotFunction3D();
			break;
		default:
			plotODE3D(system.solnPoint[0], system.solnPoint[1], system.solnPoint[2]);
		}
		updater.accept(getData());
	}
	
	private void plotCobweb() {
		if (noOfEqns() >= 1) {
			var solver = new Solver(ParserManager.getParser(), system);
			PlotData pdata = new PlotData(solver.cobweb(system.solnPoint[0]));
			pdata.setPltype(PlotData.PlotType.LINES);
			pdata.setFgColor(Color.BLACK);
			pdata.setSystem(system);
			setData(pdata);
		}
		updater.accept(getData());
	}
	
	private void plotTrajectory(double x, double y) {
		var solver = new Solver(ParserManager.getParser(), system);
		PlotData trjData;		
		switch (curMode.get()) {
		case DFE:
			trjData = new PlotData(solver.iterateMap(x, x));
			break;
		default:
			trjData = new PlotData(solver.RK4Iterate(x, y));
			break;
		}
		trjData.setPltype(PlotData.PlotType.LINES);
		trjData.setFgColor(Color.BLACK);
		trjData.setSystem(system);
		setData(trjData);
	}
	

	private void plotODE3D(double x, double y, double z) {
		var solver = new Solver(ParserManager.getParser(), system);
		PlotData trjData = new PlotData(solver.RK4Iterate3D(x, y, z));
		trjData.setPltype(PlotData.PlotType.THREED);
		trjData.setFgColor(Color.BLACK);
		trjData.setSystem(system);
		setData(trjData);
	}
	
	public void plotFunction2D() {
		var solver = new Solver(ParserManager.getParser(), system);
		PlotData trjData = new PlotData(solver.functionData());
		trjData.setPltype(PlotData.PlotType.LINES);
		trjData.setFgColor(Color.BLACK);
		trjData.setTitle(String.format("y = %s", system.eqns[0]));
		trjData.setSystem(system);
		setData(trjData);
	}

	public void plotFunction3D() {
		var solver = new Solver(ParserManager.getParser(), system);
		PlotData trjData = new PlotData(solver.functionData2D());
		trjData.setPltype(PlotData.PlotType.THREED);
		trjData.setFgColor(Color.BLACK);
		trjData.setTitle(String.format("z = %s", system.eqns[0]));
		trjData.setSystem(system);
		setData(trjData);
	}

	public void setUpdateCallback(Consumer<PlotData> update) {
		this.updater = update;
	}
}
