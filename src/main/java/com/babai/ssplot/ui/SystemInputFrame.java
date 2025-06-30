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
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
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

/** This class takes the input from user, sends data to backend,
 *  get processed data from backend, and send it back to MainFrame
 *  for plotting.
 *  @author ssarkar
 */
public class SystemInputFrame extends JInternalFrame {

	private JLabel[] lblsEquations;
	private UIInput tfCounts, tfStep;
	private UIInput[] tfsEquations, tfsRange;
	private UIInput[] tfsSolnPoint;
	
	private Consumer<PlotData> updater;

	private StateVar<SystemMode> curMode;
	private EquationSystem system;
	private PlotData curData;

	public SystemInputFrame() {
		this.curData = new PlotData();
		this.curMode = new StateVar<>(SystemMode.ODE);
		this.curMode.bind(this::updateInterface);
		initInputDialog();
		updateInterface();
	}

	private void initInputDialog() {
		/* Creating Gui */
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
			tfCounts = input()
				.columns(6)
				.text("" + EquationSystem.DEFAULT_N)
				.numeric(true),
			gbc
		);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		pnlCounts.add(label("Iteration stepsize"), gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		pnlCounts.add(
			tfStep = input()
				.columns(6)
				.text("" + EquationSystem.DEFAULT_H)
				.numeric(true),
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

		lblsEquations = new JLabel[] {
				new JLabel("Equation 1"),
				new JLabel("Equation 2"),
				new JLabel("Equation 3")
		};

		tfsEquations = new UIInput[] {
				input().columns(10),
				input().columns(10),
				input().columns(10)
		};

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		for (int i = 0; i < lblsEquations.length; i++) {
			// Add the label (column 0)
			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE; // Don't stretch the label
			pnlMatrix.add(lblsEquations[i], gbc);

			// Add the text field (column 1)
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL; // Grow textfield to fill available space
			pnlMatrix.add(tfsEquations[i], gbc);
			tfsEquations[i].onChange(() -> {
				// force triggers a curMode update
				// FIXME find a better way
				curMode.set(curMode.get());
			});
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
		final String sub_markup = "<html><body>%s<sub>%s</sub></body></html>";
		final String small_markup = "<html><body style='font-size:12'>%s</body></html>";
		final String[] axes = {"X", "Y", "Z"};
		final String[] tags = {"min", "max", "step"};
		final double[] rangeAsArray = {
			EquationSystem.DEFAULT_RANGE.min(),
			EquationSystem.DEFAULT_RANGE.max(),
			EquationSystem.DEFAULT_RANGE.step()
		};

		var pnlRange = new JPanel(new GridBagLayout());
		JLabel[] lbls2 = new JLabel[axes.length * tags.length];
		tfsRange = new UIInput[axes.length * tags.length];

		for (int i = 0; i < lbls2.length; i++) {
			lbls2[i] = label(sub_markup.formatted(axes[i / 3], tags[i % 3]));
			tfsRange[i] = input().columns(5).numeric(true).text("" + rangeAsArray[i % 3]);
		}

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		for (int row = 0; row < axes.length; row++) {
			for (int i = 0; i < tags.length; i++) {
				int idx = row * axes.length + i;
				int col = i * tags.length;

				gbc.gridx = col;
				gbc.gridy = row;
				gbc.weightx = 0;
				pnlRange.add(lbls2[idx], gbc);

				gbc.gridx = col + 1;
				gbc.weightx = 1;
				pnlRange.add(tfsRange[idx], gbc);

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
		
		var plot2dCondition = curMode.when(mode -> (
			(mode == SystemMode.ODE && noOfEqns() == 2)
			|| mode == SystemMode.FN1
			|| mode == SystemMode.DFE
		));
		
		var plot3dCondition = curMode.when(mode -> (
			(mode == SystemMode.ODE && noOfEqns() == 3)
			|| mode == SystemMode.FN2
		));
		
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
		
		tfsSolnPoint = new UIInput[axes.length];

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
									tfsSolnPoint[idx] = input().columns(3).enabled(inputConditions.get(idx))
								))
							).gap(5, 5)
						),
					
					hbox(
						button()
							.icon("/check.png")
							.tooltip("Apply changes")
							.onClick(() -> {
								// Sets up the System of Equations and validates,
								// but doesn't plot anything
								updateSystemFromUI();
							}),
						
						// TODO this still hides instead of clearing input
						button()
							.icon("/cross.png")
							.tooltip("Clear changes")
							.onClick(this::hide)
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
		int noOfEqns = 0;
		for (var tf : tfsEquations) {
			if (!tf.getText().isEmpty()) {
				noOfEqns++;
			}
		}
		return noOfEqns;
	}
		
	private void updateSystemFromUI() {
		var builder = new EquationSystem.Builder();
		
		String input = tfCounts.getText();
		if (!input.isBlank()) {
			builder.setCount(tfCounts.intValue());
		}
		input = tfStep.getText();
		if (!input.isBlank()) {
			builder.setStepSize(tfStep.value());
		}
		
		int noOfEqns = noOfEqns();
		if (noOfEqns < 1) {
			return;
		}
		
		for (int i = 0; i < noOfEqns; i++) {
			builder.addEquation(tfsEquations[i].getText());
			builder.addRange(
				tfsRange[3*i  ].value(),
				tfsRange[3*i+1].value(),
				tfsRange[3*i+2].value()
			);
		}
		builder.setMode(curMode.get());
		
		setSystem(builder.build());
	}
	
	private void reloadUI() {
		var system = getSystem();
		if (system != null) {
			curMode.set(getSystem().getMode());
			for (int i = 0; i < noOfEqns(); i++) {
				tfsEquations[i].setText(system.get(i));
			}
		} else {
			curMode.set(SystemMode.ODE);
		}

		// TODO set other fields except Eqns
		if (curMode != null) {
			updateInterface();
		}
	}

	private void updateInterface() {
		if (curMode.get() == SystemMode.ODE) {
			lblsEquations[0].setText("dx/dt =");
			lblsEquations[1].setText("dy/dt =");
			lblsEquations[2].setText("dz/dt =");
			tfsEquations[1].setEditable(true);
			tfsEquations[2].setEditable(true);
			tfsRange[6].setEditable(true);
			tfsRange[7].setEditable(true);
			tfsRange[8].setEditable(true);
			tfStep.setEditable(true);
			tfCounts.setEditable(true);
		} else if (curMode.get() == SystemMode.DFE) {
			lblsEquations[0].setText("<html><body>x<sub>n+1</sub> =</body></html>");
			lblsEquations[1].setText("<html><body>y<sub>n+1</sub> =</body></html>");
			lblsEquations[2].setText("<html><body>z<sub>n+1</sub> =</body></html>");
			tfsEquations[1].setEditable(true);
			tfsEquations[2].setEditable(true);
			tfsRange[6].setEditable(true);
			tfsRange[7].setEditable(true);
			tfsRange[8].setEditable(true);
			tfStep.setEditable(true);
			tfCounts.setEditable(true);
		} else if (curMode.get() == SystemMode.FN1) {
			lblsEquations[0].setText("y(x) =");
			lblsEquations[1].setText("");
			lblsEquations[2].setText("");
			tfsEquations[1].setEditable(false);
			tfsEquations[2].setEditable(false);
			tfsRange[6].setEditable(false);
			tfsRange[7].setEditable(false);
			tfsRange[8].setEditable(false);
			tfStep.setEditable(false);
			tfCounts.setEditable(false);
		} else if (curMode.get() == SystemMode.FN2) {
			lblsEquations[0].setText("z(x, y) =");
			lblsEquations[1].setText("");
			lblsEquations[2].setText("");
			tfsEquations[1].setEditable(false);
			tfsEquations[2].setEditable(false);
			tfsRange[6].setEditable(true);
			tfsRange[7].setEditable(true);
			tfsRange[8].setEditable(true);
			tfStep.setEditable(false);
			tfCounts.setEditable(false);
		}
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
		reloadUI();
	}

	// TODO these methods sort of violate MVC, perhaps there should be some sort
	// of controller class?
	// Plotting functions : Calculates PlotData from EquationSystem
	private void plotDirectionField() {
		var solver = new Solver(ParserManager.getParser(), getSystem());
		PlotData pdata = new PlotData(solver.directionField());
		pdata.setPltype(PlotData.PlotType.VECTORS);
		pdata.setSystem(getSystem());
		setData(pdata);
		updater.accept(pdata);
	}
	
	
	private void plot2D() {
		switch (curMode.get()) {
		case FN1:
			plotFunction2D();
			break;
		default:
			if (!tfsSolnPoint[0].empty() && !tfsSolnPoint[1].empty())
			{
				plotTrajectory(
					tfsSolnPoint[0].value(),
					tfsSolnPoint[1].value()
				);
			} else {
				JOptionPane.showMessageDialog(this, "Enter a solution point!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
				tfsSolnPoint[0].requestFocusInWindow();
				return;
			}
			break;
		}
		updater.accept(getData());
	}
	
	private void plot3D() {
		switch (curMode.get()) {
		case FN2:
			plotFunction3D();
			break;
		default:
			if (noOfEqns() == 3) {
				plotODE3D(
					tfsSolnPoint[0].value(),
					tfsSolnPoint[1].value(),
					tfsSolnPoint[2].value()
				);
			} else {
				JOptionPane.showMessageDialog(this, "Enter a solution point!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
				tfsSolnPoint[0].requestFocusInWindow();
				return;
			}
		}
		updater.accept(getData());
	}
	
	private void plotCobweb() {
		if (noOfEqns() >= 1) {
			double x = tfsSolnPoint[0].value();
			var solver = new Solver(ParserManager.getParser(), getSystem());
			PlotData pdata = new PlotData(solver.cobweb(x));
			pdata.setPltype(PlotData.PlotType.LINES);
			pdata.setFgColor(Color.BLACK);
			pdata.setSystem(getSystem());
			setData(pdata);
		} else {
			JOptionPane.showMessageDialog(this, "Enter a solution point!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
			tfsSolnPoint[0].requestFocusInWindow();
			return;
		}
		updater.accept(getData());
	}
	
	private void plotTrajectory(double x, double y) {
		var solver = new Solver(ParserManager.getParser(), getSystem());
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
		trjData.setSystem(getSystem());
		setData(trjData);
	}
	

	private void plotODE3D(double x, double y, double z) {
		var solver = new Solver(ParserManager.getParser(), getSystem());
		PlotData trjData = new PlotData(solver.RK4Iterate3D(x, y, z));
		trjData.setPltype(PlotData.PlotType.THREED);
		trjData.setFgColor(Color.BLACK);
		trjData.setSystem(getSystem());
		setData(trjData);
	}
	

	public void plotFunction2D() {
		var solver = new Solver(ParserManager.getParser(), getSystem());
		PlotData trjData = new PlotData(solver.functionData());
		trjData.setPltype(PlotData.PlotType.LINES);
		trjData.setFgColor(Color.BLACK);
		trjData.setTitle(String.format("y = %s", tfsEquations[0].getText()));
		trjData.setSystem(getSystem());
		setData(trjData);
	}

	public void plotFunction3D() {
		var solver = new Solver(ParserManager.getParser(), getSystem());
		PlotData trjData = new PlotData(solver.functionData2D());
		trjData.setPltype(PlotData.PlotType.THREED);
		trjData.setFgColor(Color.BLACK);
		trjData.setTitle(String.format("z = %s", tfsEquations[0].getText()));
		trjData.setSystem(getSystem());
		setData(trjData);
	}

	public void setUpdateCallback(Consumer<PlotData> update) {
		this.updater = update;
	}
}
