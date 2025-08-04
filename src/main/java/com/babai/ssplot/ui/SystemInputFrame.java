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
import java.awt.Insets;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;

import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.system.core.EquationSystem;
import com.babai.ssplot.math.system.core.SystemMode;
import com.babai.ssplot.math.system.parser.ParserManager;
import com.babai.ssplot.math.system.solver.Solver;
import com.babai.ssplot.ui.controls.StateVar;
import com.babai.ssplot.ui.controls.UIFrame;
import com.babai.ssplot.ui.controls.UIGrid;

import static com.babai.ssplot.ui.controls.DUI.*;

/**
 * This class takes the input from user, sends data to Solver backend,
 * gets processed data from backend, and send it back to MainFrame for plotting.
 * @author babaissarkar
 */

// TODO enable conditions need more edge case handling
// TODO noOfEqns() is not a correct check, it triggers for any N eqns
// instead of only the first N
public class SystemInputFrame extends UIFrame {
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
		curMode.onChange(() -> system.mode = curMode.get());
		initInputDialog();
	}

	private void initInputDialog() {
		// UI Data
		final String[] axes = {"X", "Y", "Z"};

		// Creating Gui
		this
			.title("System Parameters")
			.closeOperation(JFrame.HIDE_ON_CLOSE)
			.content(
				vbox(
					createToolbarUI(axes),
					radioGroup(SystemMode.class)
						.options(SystemMode.values(), SystemMode.ODE)
						.bindOneWay(curMode),
					createIterationParamUIPanel(),
					createEqnInputUIPanel(axes),
					createRangesUIPanel(axes)
				).emptyBorder(10)
			);
	}
	
	private JToolBar createToolbarUI(final String[] axes) {
		final String small_markup = "<html><body style='font-size:12'>%s</body></html>";
		
		// Enable conditions for various input fields
		List<StateVar<Boolean>> inputConditions = List.of(
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() >= 2),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() >= 2),
			curMode.when(mode -> (mode == SystemMode.ODE && noOfEqns() == 3))
		);

		var plot2dCondition = curMode.when(mode ->
			(mode == SystemMode.ODE && noOfEqns() == 2)
			|| (mode == SystemMode.DFE && noOfEqns() >= 1)
			|| (mode == SystemMode.FN1 && noOfEqns() == 1)
		);

		var plot3dCondition = curMode.when(mode ->
			(mode == SystemMode.ODE && noOfEqns() == 3)
			|| (mode == SystemMode.FN2 && noOfEqns() == 1)
		);
		
		return toolbar(
			hbox(
				// Plot Buttons
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
			).gap(5, 5),

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
		);
	}

	private UIGrid createIterationParamUIPanel() {
		// Iteration paramters entry
		var pnlCounts = grid()
			.anchor(GridBagConstraints.WEST)
			.insets(new Insets(5, 5, 5, 5))
			.fill(GridBagConstraints.NONE)
			.row()
				.column(label("Iteration count"))
				.weightx(1.0)
				.column(
					input()
						.columns(6)
						.text("" + EquationSystem.DEFAULT_N)
						.enabled(curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE)))
						.numeric(true)
						.onChange(text -> system.n = Integer.parseInt(text))
				)
			.row()
				.column(label("Iteration stepsize"))
				.weightx(1.0)
				.column(
					input()
						.columns(6)
						.text("" + EquationSystem.DEFAULT_H)
						.enabled(curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE)))
						.numeric(true)
						.onChange(text -> system.h = Double.parseDouble(text))
				);
		
		pnlCounts.setBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(127, 0, 140), 3),
				"Iteration Parameters",
				TitledBorder.LEFT,
				TitledBorder.TOP,
				new Font("Serif", Font.BOLD, 12),
				new Color(127, 0, 140).darker().darker()));
		
		return pnlCounts;
	}

	
	private UIGrid createEqnInputUIPanel(final String[] axes) {
		final String sub_markup = "<html><body>%s<sub>%s</sub>%s</body></html>";
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
		
		// Equations Entry
		List<StateVar<Boolean>> eqnCondition = List.of(
			new StateVar<Boolean>(true),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE)),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE))
		);
		var pnlMatrix = grid()
			.anchor(GridBagConstraints.WEST)
			.insets(new Insets(5, 5, 5, 5));

		for (int i = 0; i < axes.length; i++) {
			final int idx = i;
			pnlMatrix
				.row()
					.column(label().bind(curMode.when(mode -> eqnFieldLabels.get(mode)[idx])))
					.weightx(1)
					.fill(GridBagConstraints.HORIZONTAL)
					.column(
						input()
							.columns(10)
							.enabled(eqnCondition.get(idx))
							.onChange(text -> {
								system.eqns[idx] = text;
								curMode.set(curMode.get()); // FIXME find a better way than this to update UI
							})
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
		return pnlMatrix;
	}
	
	private UIGrid createRangesUIPanel(final String[] axes) {
		final String sub_markup = "<html><body>%s<sub>%s</sub>%s</body></html>";
		final String[] tags = {"min", "max", "step"};
		final double[] rangeAsArray = EquationSystem.DEFAULT_RANGE.toArray();
		
		// Ranges entry
		List<StateVar<Boolean>> rangeConditions = List.of(
			curMode.when(mode -> noOfEqns() > 0),
			curMode.when(mode -> (mode != SystemMode.FN1) && noOfEqns() >= 2),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() == 3)
		);

		var pnlRange = grid()
				.anchor(GridBagConstraints.WEST)
				.insets(new Insets(5, 5, 5, 5));

		for (int row = 0; row < axes.length; row++) {
			final int row_idx = row;
			pnlRange.row();
			for (int col = 0; col < tags.length; col++) {
				final int col_idx = col; 
				pnlRange
				.column(label(sub_markup.formatted(axes[row], tags[col], "")))
				.weightx(1)
				.column(
					input()
					.columns(5)
					.numeric(true)
					.text("" + rangeAsArray[col])
					.enabled(rangeConditions.get(row))
					.onChange(text -> {
						var range = system.ranges[row_idx];
						system.ranges[row_idx] = switch(col_idx % 3) {
						case 0 -> new EquationSystem.Range(
								Double.parseDouble(text), range.end(), range.step());
						case 1 -> new EquationSystem.Range(
								range.start(), Double.parseDouble(text), range.step());
						default -> new EquationSystem.Range(
								range.start(), range.end(), Double.parseDouble(text));
						};
					})
				)
				.column(Box.createHorizontalStrut(10));
			}
		}

		pnlRange.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(new Color(24, 110, 1), 3),
				"Ranges",
				TitledBorder.LEFT,
				TitledBorder.TOP,
				new Font("Serif", Font.BOLD, 12),
				new Color(24, 110, 1).darker().darker()));

		return pnlRange;
	}

	private int noOfEqns() {
		return system.numberOfEqns();
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
		curData = new PlotData(solver.directionField());
		curData.setPltype(PlotData.PlotType.VECTORS);
		curData.setSystem(system);
		updater.accept(curData);
	}

	private void plot2D() {
		switch (curMode.get()) {
		case FN1:
			plotFunction2D();
			break;
		default:
			plotTrajectory(system.solnPoint[0], system.solnPoint[1]);
		}
		updater.accept(curData);
	}

	private void plot3D() {
		switch (curMode.get()) {
		case FN2:
			plotFunction3D();
			break;
		default:
			plotODE3D(system.solnPoint[0], system.solnPoint[1], system.solnPoint[2]);
		}
		updater.accept(curData);
	}

	private void plotCobweb() {
		if (noOfEqns() >= 1) {
			var solver = new Solver(ParserManager.getParser(), system);
			curData = new PlotData(solver.cobweb(system.solnPoint[0]));
			curData.setPltype(PlotData.PlotType.LINES);
			curData.setFgColor(Color.BLACK);
			curData.setSystem(system);
		}
		updater.accept(curData);
	}

	private void plotTrajectory(double x, double y) {
		var solver = new Solver(ParserManager.getParser(), system);
		switch (curMode.get()) {
		case DFE:
			curData = new PlotData(solver.iterateMap(x, x));
			break;
		default:
			curData = new PlotData(solver.RK4Iterate(x, y));
			break;
		}
		curData.setPltype(PlotData.PlotType.LINES);
		curData.setFgColor(Color.BLACK);
		curData.setSystem(system);
	}

	private void plotODE3D(double x, double y, double z) {
		var solver = new Solver(ParserManager.getParser(), system);
		curData = new PlotData(solver.RK4Iterate3D(x, y, z));
		curData.setPltype(PlotData.PlotType.THREED);
		curData.setFgColor(Color.BLACK);
		curData.setSystem(system);
	}

	private void plotFunction2D() {
		var solver = new Solver(ParserManager.getParser(), system);
		curData = new PlotData(solver.functionData());
		curData.setPltype(PlotData.PlotType.LINES);
		curData.setFgColor(Color.BLACK);
		curData.setTitle(String.format("y = %s", system.eqns[0]));
		curData.setSystem(system);
	}

	private void plotFunction3D() {
		var solver = new Solver(ParserManager.getParser(), system);
		curData = new PlotData(solver.functionData2D());
		curData.setPltype(PlotData.PlotType.THREED);
		curData.setFgColor(Color.BLACK);
		curData.setTitle(String.format("z = %s", system.eqns[0]));
		curData.setSystem(system);
	}

	public void setUpdateCallback(Consumer<PlotData> update) {
		this.updater = update;
	}
}
