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
	private EquationSystem.Builder builder;
	private PlotData curData;
	
	private Consumer<PlotData> updater;
	private UIInput[] inputEqns;

	public SystemInputFrame() {
		inputEqns = new UIInput[EquationSystem.DIM];
		builder = new EquationSystem.Builder();
		curData = new PlotData();
		curMode = new StateVar<>(SystemMode.ODE);
		curMode.onChange(() -> builder.mode(curMode.get()));
		initInputDialog();
	}

	private void initInputDialog() {
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
						.bindFromUI(curMode),
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
								.chars(3)
								.enabled(inputConditions.get(idx))
								.onChange(text -> builder.solnPoint(idx, Double.parseDouble(text)))
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

	// Iteration paramters entry panel
	private UIGrid createIterationParamUIPanel() {
		return grid()
			.anchor(GridBagConstraints.WEST)
			.insets(new Insets(5, 5, 5, 5))
			.fill(GridBagConstraints.NONE)
			.row()
				.column(label("Iteration count"))
				.weightx(1.0)
				.column(
					input()
						.chars(6)
						.text("" + EquationSystem.DEFAULT_N)
						.enabled(curMode.when(mode ->
							(mode == SystemMode.DFE || mode == SystemMode.ODE)))
						.numeric(true)
						.onChange(text -> builder.n(Integer.parseInt(text)))
				)
			.row()
				.column(label("Iteration stepsize"))
				.weightx(1.0)
				.column(
					input()
						.chars(6)
						.text("" + EquationSystem.DEFAULT_H)
						.enabled(curMode.when(mode ->
							(mode == SystemMode.DFE || mode == SystemMode.ODE)))
						.numeric(true)
						.onChange(text -> builder.h(Double.parseDouble(text)))
				)
			.titledBorder()
				.lineBorder(new Color(127, 0, 140), 3)
				.title("Iteration Parameters")
					.justify(TitledBorder.LEFT)
					.position(TitledBorder.TOP)
					.font(new Font("Serif", Font.BOLD, 12))
					.color(new Color(127, 0, 140).darker().darker())
				.apply();
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
					.column(
						label().bindToUI(curMode.when(mode -> eqnFieldLabels.get(mode)[idx])))
					.weightx(1)
					.fill(GridBagConstraints.HORIZONTAL)
					.column(
						inputEqns[i] = input()
							.chars(10)
							.enabled(eqnCondition.get(idx))
							.onChange(text -> {
								builder.eqn(idx, text);
								curMode.set(curMode.get()); // FIXME find a better way than this to update UI
							})
					);
		}
		
		pnlMatrix.titledBorder()
			.lineBorder(new Color(255, 90, 38), 3)
			.title("Equations")
				.justify(TitledBorder.LEFT)
				.position(TitledBorder.TOP)
				.font(new Font("Serif", Font.BOLD, 12))
				.color(new Color(255, 90, 38).darker().darker())
			.apply();
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
					.column(Box.createHorizontalStrut(10));
			}
		}
		
		pnlRange.titledBorder()
			.lineBorder(new Color(24, 110, 1), 3)
			.title("Ranges")
				.justify(TitledBorder.LEFT)
				.position(TitledBorder.TOP)
				.font(new Font("Serif", Font.BOLD, 12))
				.color(new Color(24, 110, 1).darker().darker())
			.apply();

		return pnlRange;
	}

	private int noOfEqns() {
		return builder.numberOfEqns();
	}

	public EquationSystem getSystem() {
		return this.builder.build();
	}

	public void setSystem(EquationSystem system) {
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
	private void plotDirectionField() {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		curData = new PlotData(solver.directionField());
		curData.setPltype(PlotData.PlotType.VECTORS);
		curData.setSystem(system);
		updater.accept(curData);
	}

	private void plot2D() {
		var system = getSystem();
		switch (curMode.get()) {
		case FN1:
			plotFunction2D();
			break;
		default:
			plotTrajectory(system.solnPoint()[0], system.solnPoint()[1]);
		}
		updater.accept(curData);
	}

	private void plot3D() {
		var system = getSystem();
		switch (curMode.get()) {
		case FN2:
			plotFunction3D();
			break;
		default:
			plotODE3D(system.solnPoint());
		}
		updater.accept(curData);
	}

	private void plotCobweb() {
		var system = getSystem();
		if (noOfEqns() >= 1) {
			var solver = new Solver(ParserManager.getParser(), system);
			curData = new PlotData(solver.cobweb(system.solnPoint()[0]));
			curData.setPltype(PlotData.PlotType.LINES);
			curData.setFgColor(Color.BLACK);
			curData.setSystem(system);
		}
		updater.accept(curData);
	}

	private void plotTrajectory(double x, double y) {
		var system = getSystem();
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

	private void plotODE3D(double[] solnPoint) {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		curData = new PlotData(solver.RK4Iterate3D(solnPoint[0], solnPoint[1], solnPoint[2]));
		curData.setPltype(PlotData.PlotType.THREED);
		curData.setFgColor(Color.BLACK);
		curData.setSystem(system);
	}

	private void plotFunction2D() {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		curData = new PlotData(solver.functionData());
		curData.setPltype(PlotData.PlotType.LINES);
		curData.setFgColor(Color.BLACK);
		curData.setTitle(String.format("y = %s", system.eqns()[0]));
		curData.setSystem(system);
	}

	private void plotFunction3D() {
		var system = getSystem();
		var solver = new Solver(ParserManager.getParser(), system);
		curData = new PlotData(solver.functionData2D());
		curData.setPltype(PlotData.PlotType.THREED);
		curData.setFgColor(Color.BLACK);
		curData.setTitle(String.format("z = %s", system.eqns()[0]));
		curData.setSystem(system);
	}

	public void setUpdateCallback(Consumer<PlotData> update) {
		this.updater = update;
	}
}
