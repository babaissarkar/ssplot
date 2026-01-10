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
import java.awt.GridBagConstraints;
import java.util.List;
import java.util.EnumMap;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JToolBar;

import com.babai.ssplot.math.plot.Axis;
import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.system.core.EquationSystem;
import com.babai.ssplot.math.system.core.SystemMode;
import com.babai.ssplot.math.system.parser.ParserManager;
import com.babai.ssplot.math.system.solver.Solver;
import com.babai.ssplot.ui.controls.DUI.Text;
import com.babai.ssplot.ui.controls.StateVar;
import com.babai.ssplot.ui.controls.UIFrame;
import com.babai.ssplot.ui.controls.UIGrid;
import com.babai.ssplot.ui.controls.UIInput;

import static com.babai.ssplot.ui.controls.DUI.*;

/**
 * This class takes the input from user, sends data to Solver backend,
 * gets processed data from backend, and send it back to MainFrame for plotting.
 * @author babaissarkar
 */

// TODO split class into UI and Controller
// TODO enable conditions need more edge case handling
// TODO noOfEqns() is not a correct check, it triggers for any N eqns
// instead of only the first N
public class SystemInputFrame extends UIFrame {
	private StateVar<SystemMode> curMode;
	private StateVar<Boolean> isParametric, isPolar;
	
	private EquationSystem.Builder builder;
	private Consumer<PlotData> updater;
	
	private int solnPointNum = 0;
	private UIInput[] inputEqns;

	public SystemInputFrame() {
		inputEqns = new UIInput[EquationSystem.DIM];
		builder = new EquationSystem.Builder();
		
		curMode = new StateVar<>(SystemMode.ODE);
		curMode.onChange(mode -> builder.mode(mode));
		isParametric = new StateVar<Boolean>(false);
		isPolar = new StateVar<Boolean>(false);
		
		initInputDialog();
	}

	private void initInputDialog() {
		final var axes = PlotData.PlotType.LINES3.axes();

		// Creating Gui
		this.title("System Parameters")
			.closeOperation(JFrame.HIDE_ON_CLOSE)
			.resizable(true)
			.iconifiable(true)
			.closable(true)
			.maximizable(false)
			.content(
				vbox(10,
					createToolbarUI(axes),
					radioGroup(SystemMode.class)
						.options(SystemMode.values(), SystemMode.ODE)
						.bindToSelection(curMode),
					createEqnInputUIPanel(axes),
					createRangesUIPanel(axes),
					createIterationParamUIPanel()
				)
				.bg(MainFrame.isDark() ? Color.decode("#474c5b") : Color.WHITE) // TODO: very theme specific
				.emptyBorder(15)
			)
			.packFrame();
	}
	
	private JToolBar createToolbarUI(final List<Axis> axes) {
		var plot2dCondition = curMode.when(mode ->
			(mode == SystemMode.ODE && noOfEqns() == 2 && solnPointNum == 2)
			|| (mode == SystemMode.DFE && noOfEqns() >= 1)
			|| (mode == SystemMode.FN1 && noOfEqns() == 1)
		);

		var plot3dCondition = curMode.when(mode ->
			(mode == SystemMode.ODE && noOfEqns() == 3 && solnPointNum == 3)
			|| (mode == SystemMode.FN2 && noOfEqns() == 1)
		);
		
		var bar = toolbar(
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
					.onClick(this::plotDirectionField),
					
				button()
					.icon("/cross.png")
					.tooltip("Clear changes")
					.onClick(this::hide) // TODO this should reset everything to default vals
			)
			.gap(0, 0)
			.bg(MainFrame.isDark() ? Color.decode("#474c5b") : Color.WHITE) // TODO: very theme specific
		);
		
		bar.setBackground(Color.WHITE);
		return bar;
	}

	// Equation Entry Panel
	private UIGrid createEqnInputUIPanel(final List<Axis> axes) {
		final String subMarkup = Text.htmlAndBody("%s" + Text.tag("sub", "%s") + "%s");
		final String smallMarkup = Text.tag("html", Text.tag("body", "style='font-size:12'", "%s"));
		
		StateVar<Boolean> isODEorDFE = curMode.when(
				mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE));
		StateVar<Boolean> isFN = curMode.when(
				mode -> (mode == SystemMode.FN1 || mode == SystemMode.FN2));
		
		var eqnFieldLabels = new EnumMap<SystemMode, String[]>(SystemMode.class);
		
		eqnFieldLabels.put(
			SystemMode.ODE,
			forEach(axes, i -> "d%s/dt =".formatted(axes.get(i).toString().toLowerCase()), String[]::new));
		
		eqnFieldLabels.put(
			SystemMode.DFE,
			forEach(axes, i -> subMarkup.formatted(axes.get(i).toString().toLowerCase(), "n+1", " ="), String[]::new));
		
		eqnFieldLabels.put(
			SystemMode.FN1,
			new String[] { "y(x) =", "", "" });
		
		eqnFieldLabels.put(
			SystemMode.FN2,
			new String[] { "z(x, y) =", "", "" });
		
		// Equations entry enable conditions
		var eqnCondition = List.of(
			new StateVar<Boolean>(true),
			isODEorDFE,
			isODEorDFE
		);
		
		// Solution point entry enable conditions
		var inputConditions = List.of(
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() >= 2),
			curMode.when(mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() >= 2),
			curMode.when(mode -> (mode == SystemMode.ODE && noOfEqns() == 3))
		);
		
		var pnlEquations = grid()
			.emptyBorder(5)
			.anchor(GridBagConstraints.WEST)
			.insets(3)
			.row()
				.spanx(4)
				.column(label("Equations").font(Text.headerFont))
	
			.row()
				.column(label("Parametric (x = x(t), y = y(t)):").visible(isFN))
				.weightx(1)
				.fill(GridBagConstraints.HORIZONTAL)
				.column(
					hbox(
						checkBox().bindSelection(isParametric)
					).visible(isFN)
				)
				
			.row()
				.column(label("Polar (r = r(θ)):").visible(isFN))
				.weightx(1)
				.fill(GridBagConstraints.HORIZONTAL)
				.column(
					hbox(
						checkBox().bindSelection(isPolar)
					)
					.visible(isFN));

		for (int i = 0; i < axes.size(); i++) {
			final int idx = i;
			pnlEquations.row()
				.column(label().bindText(curMode.when(mode -> eqnFieldLabels.get(mode)[idx])))
				.weightx(1)
				.fill(GridBagConstraints.HORIZONTAL)
				.column(
					inputEqns[i] = input()
						.chars(10)
						.visible(eqnCondition.get(idx))
						.onChange(text -> {
							builder.eqn(idx, text);
							// FIXME find a better way than this to update UI
							curMode.set(curMode.get());
						})
				);
		}
		
		pnlEquations.row()
			.column(label("Solve At:").visible(isODEorDFE))
			.weightx(1)
			.fill(GridBagConstraints.HORIZONTAL)
			.column(
				// Entry area for the point where the system is to be solved (X,Y,Z)
				hbox(
					forEach(axes, idx -> hbox(
						label(String.format(smallMarkup, axes.get(idx))),
						input()
							.chars(3)
							.enabled(inputConditions.get(idx))
							.onChange(text -> {
								if (!text.isEmpty()) {
									solnPointNum++;
									builder.solnPoint(idx, Double.parseDouble(text));
								} else {
									solnPointNum--;
									builder.solnPoint(idx, 0.0);
								}
								
								// FIXME find a better way than this to update UI
								curMode.set(curMode.get());
							})
					))
				).visible(isODEorDFE)
			);
		
		return pnlEquations;
	}
	
	// Range Entry Panel
	private UIGrid createRangesUIPanel(final List<Axis> axes) {
		final String subMarkup = Text.htmlAndBody("%s" + Text.tag("sub", "%s") + "%s");
		final String[] tags = {"min", "max", "step"};
		final double[] rangeAsArray = EquationSystem.DEFAULT_RANGE.toArray();
		
		// Ranges entry
		var rangeConditions = List.of(
			curMode.when(mode -> noOfEqns() > 0),
			curMode.when(mode ->
				(mode != SystemMode.FN1 && noOfEqns() >= 2)
				|| mode == SystemMode.FN1 && noOfEqns() >= 1
				|| mode == SystemMode.FN2 && noOfEqns() >= 1),
			curMode.when(mode ->
				((mode == SystemMode.DFE || mode == SystemMode.ODE) && noOfEqns() == 3)
				|| mode == SystemMode.FN2 && noOfEqns() >= 1)
		);

		var pnlRange = grid()
			.anchor(GridBagConstraints.WEST)
			.insets(3)
			.row()
				.spanx(9)
				.column(label("Ranges").font(Text.headerFont));

		for (int row = 0; row < axes.size(); row++) {
			final int row_idx = row;
			pnlRange.row();
			for (int col = 0; col < tags.length; col++) {
				final int col_idx = col; 
				pnlRange
					.weightx(0)
					.column(label(subMarkup.formatted(axes.get(row), tags[col], "")))
					.weightx(1)
					.column(input()
						.chars(5)
						.numeric(true)
						.text("" + rangeAsArray[col])
						.enabled(rangeConditions.get(row))
						.onChange(text -> {
							var range = builder.ranges()[row_idx];
							builder.range(row_idx,
								switch (col_idx % 3) {
								case 0 -> new EquationSystem.Range(
										Double.parseDouble(text), range.end(), range.step());
								case 1 -> new EquationSystem.Range(
										range.start(), Double.parseDouble(text), range.step());
								default -> new EquationSystem.Range(
										range.start(), range.end(), Double.parseDouble(text));
								}
							);
						})
					)
					.weightx(0)
					.column(Box.createHorizontalStrut(10));
			}
		}
		
		pnlRange.emptyBorder(5);
		return pnlRange;
	}
	
	// Iteration paramters entry panel
	private UIGrid createIterationParamUIPanel() {
		StateVar<Boolean> enableCond = curMode.when(
			mode -> (mode == SystemMode.DFE || mode == SystemMode.ODE));
		
		return grid()
			.anchor(GridBagConstraints.WEST)
			.insets(3)
			.fill(GridBagConstraints.NONE)
			.row()
				.spanx(4)
				.column(label("Iteration Parameters").font(Text.headerFont))
			.row()
				.column(label("Iteration count"))
				.weightx(1.0)
				.column(
					input()
						.chars(6)
						.text("" + EquationSystem.DEFAULT_N)
						.enabled(enableCond)
						.numeric(true)
						.onChange(text -> builder.n(Integer.parseInt(text)))
				)
				.weightx(0)
				.column(label("Iteration stepsize"))
				.weightx(1.0)
				.column(
					input()
						.chars(6)
						.text("" + EquationSystem.DEFAULT_H)
						.enabled(enableCond)
						.numeric(true)
						.onChange(text -> builder.h(Double.parseDouble(text)))
				)
			.emptyBorder(5)
			.visible(enableCond);
	}

	private int noOfEqns() {
		return builder.numberOfEqns();
	}

	public EquationSystem getSystem() {
		return this.builder.build();
	}

	public void setSystem(EquationSystem system) {
		if (system == null) return;
		String[] eqns = system.eqns();
		for (int i = 0; i < inputEqns.length; i++) {
			inputEqns[i].text(eqns[i]);
		}
		builder.fromSystem(system);
		curMode.set(system.mode());
	}

	// TODO these methods sort of violate MVC, perhaps there should be some sort
	// of controller class?
	// Plotting functions : Calculates PlotData from EquationSystem

	private void plot2D() {
		var system = getSystem();
		var plotData = switch (curMode.get()) {
			case FN1 -> plotFunction2D();
			default -> plotTrajectory(system.solnPoint()[0], system.solnPoint()[1]);
		};
		updater.accept(plotData);
	}

	private void plot3D() {
		var system = getSystem();
		var plotData = switch (curMode.get()) {
			case FN2 -> plotFunction3D();
			default -> plotODE3D(system.solnPoint());
		};
		updater.accept(plotData);
	}
	
	private void plotDirectionField() {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		var curData = new PlotData(solver.directionField());
		curData.setPlotType(PlotData.PlotType.VFIELD);
		curData.setSystem(system);
		updater.accept(curData);
	}

	private void plotCobweb() {
		var system = getSystem();
		if (noOfEqns() >= 1) {
			var solver = new Solver(ParserManager.getParser(), system);
			var curData = new PlotData(solver.cobweb(system.solnPoint()[0]));
			curData.setPlotType(PlotData.PlotType.LINES);
			curData.setSystem(system);
			updater.accept(curData);
		}
	}

	private PlotData plotTrajectory(double x, double y) {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		PlotData curData = switch (curMode.get()) {
			case DFE -> new PlotData(solver.iterateMap(x, x));
			default -> new PlotData(solver.RK4Iterate(x, y));
		};
		curData.setPlotType(PlotData.PlotType.LINES);
		curData.setSystem(system);
		return curData;
	}

	private PlotData plotODE3D(double[] solnPoint) {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		var curData = new PlotData(solver.RK4Iterate3D(solnPoint[0], solnPoint[1], solnPoint[2]));
		curData.setPlotType(PlotData.PlotType.LINES3);
		curData.setDataCols(0, 1, 2);
		curData.setSystem(system);
		return curData;
	}

	private PlotData plotFunction2D() {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		var curData = new PlotData(solver.functionData());
		curData.setPlotType(PlotData.PlotType.LINES);
		curData.setTitle(String.format("y = %s", system.eqns()[0]));
		curData.setSystem(system);
		return curData;
	}

	private PlotData plotFunction3D() {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		var curData = new PlotData(solver.functionData2D());
		curData.setPlotType(PlotData.PlotType.LINES3);
		curData.setDataCols(0, 1, 2);
		curData.setTitle(String.format("z = %s", system.eqns()[0]));
		curData.setSystem(system);
		return curData;
	}

	public void setUpdateCallback(Consumer<PlotData> update) {
		this.updater = update;
	}
}
