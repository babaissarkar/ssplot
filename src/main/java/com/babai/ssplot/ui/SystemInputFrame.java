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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

/* A class for drawing phase plot of two simultaneous 1nd order ode. */
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;

import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.system.core.EquationSystem;
import com.babai.ssplot.math.system.core.SystemMode;
import com.babai.ssplot.math.system.parser.ParserManager;
import com.babai.ssplot.math.system.solver.Solver;

/** This class takes the input from user, sends data to backend,
 *  get processed data from backend, and send it back to MainFrame
 *  for plotting.
 *  @author ssarkar
 */
public class SystemInputFrame extends JInternalFrame implements ActionListener {

	private JLabel[] lblsEquations;
	private CenteredField tfCounts, tfStep;
	private CenteredField[] tfsEquations, tfsRange, tfsSolnPoint;
	private JButton btnOK, btnCancel, btnDF, btnCW;
	private JButton btnPlot2D, btnPlot3D;
	
	private Consumer<PlotData> updater;

	private SystemMode curMode;
	private EquationSystem system;
	private PlotData curData;

	public SystemInputFrame() {
		this.curData = new PlotData();
		this.curMode = SystemMode.ODE;
		initInputDialog();
		updateInterface();
	}

	private void initInputDialog() {
		/* Creating Gui */
		setTitle("System Parameters");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		var pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.PAGE_AXIS));
		pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		var pnlRB = new JPanel();
		var rbODE = new JRadioButton("Differential Equation", true);
		var rbIM = new JRadioButton("Difference Equation");
		var rbFunc = new JRadioButton("2D function");
		var rbFunc2 = new JRadioButton("3D function");

		rbODE.addActionListener(e -> {
			curMode = SystemMode.ODE;
			updateInterface();
		});
		rbIM.addActionListener(e -> {
			curMode = SystemMode.DFE;
			updateInterface();
		});

		rbFunc.addActionListener(e -> {
			curMode = SystemMode.FN1;
			updateInterface();
		});
		rbFunc2.addActionListener(e -> {
			curMode = SystemMode.FN2;
			updateInterface();
		});

		var bg = new ButtonGroup();
		bg.add(rbODE);
		bg.add(rbIM);
		bg.add(rbFunc);
		bg.add(rbFunc2);

		pnlRB.add(rbODE);
		pnlRB.add(rbIM);
		pnlRB.add(rbFunc);
		pnlRB.add(rbFunc2);

		// Iteration paramters entry
		var pnlCounts = new JPanel();
		pnlCounts.setLayout(new GridBagLayout());

		var gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.WEST;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.fill = GridBagConstraints.NONE;

		var lblCounts = new JLabel("Iteration count");
		var lblStep = new JLabel("Iteration stepsize");
		tfCounts = new CenteredField("" + EquationSystem.DEFAULT_N, 6);
		tfStep = new CenteredField("" + EquationSystem.DEFAULT_H, 6);
		tfCounts.setNumeric(true);
		tfCounts.setNumeric(true);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		pnlCounts.add(lblCounts, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		pnlCounts.add(tfCounts, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		pnlCounts.add(lblStep, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		pnlCounts.add(tfStep, gbc);

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

		tfsEquations = new CenteredField[] {
				new CenteredField(10),
				new CenteredField(10),
				new CenteredField(10)
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
		
		final String sub_markup = "<html><body>%s<sub>%s</sub></body></html>";
		final String small_markup = "<html><body style='font-size:12'>%s</body></html>";
		final String[] axes = {"X", "Y", "Z"};
		final String[] tags = {"min", "max", "step"};
		final double[] rangeAsArray = {
			EquationSystem.DEFAULT_RANGE.min(),
			EquationSystem.DEFAULT_RANGE.max(),
			EquationSystem.DEFAULT_RANGE.step()
		};

		JLabel[] lbls2 = new JLabel[axes.length * tags.length];
		tfsRange = new CenteredField[axes.length * tags.length];

		for (int i = 0; i < lbls2.length; i++) {
			lbls2[i] = new JLabel(sub_markup.formatted(axes[i / 3], tags[i % 3]));
			tfsRange[i] = new CenteredField(5);
			tfsRange[i].setNumeric(true);
			tfsRange[i].setText("" + rangeAsArray[i % 3]);
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

		// Plot Buttons
		var pnlButton = new JPanel();
		pnlButton.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		btnPlot2D = new JButton();
		btnPlot3D = new JButton();
		btnPlot2D.setToolTipText("Draw 2d plot");
		btnPlot3D.setToolTipText("Draw 3d plot");
		btnPlot2D.setIcon(new ImageIcon(getClass().getResource("/2d.png")));
		btnPlot3D.setIcon(new ImageIcon(getClass().getResource("/3d.png")));
		btnPlot2D.setEnabled(false);
		btnPlot3D.setEnabled(false);
		btnPlot2D.addActionListener(this);
		btnPlot3D.addActionListener(this);
		pnlButton.add(btnPlot2D);
		pnlButton.add(btnPlot3D);
		
		// Point of Solution labels and textfields
		var pnlSolnInput = new JPanel();
		pnlSolnInput.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		JLabel[] lbls3 = new JLabel[axes.length];
		tfsSolnPoint = new CenteredField[axes.length];
		for (int i = 0; i < tfsSolnPoint.length; i++) {
			lbls3[i] = new JLabel(String.format(small_markup, axes[i]));
			tfsSolnPoint[i] = new CenteredField(3);
			tfsSolnPoint[i].setEnabled(false);
			pnlSolnInput.add(lbls3[i]);
			pnlSolnInput.add(tfsSolnPoint[i]);
		}
		var pnlSolnMain = new JPanel();
		pnlSolnMain.setLayout(new BorderLayout());
		pnlSolnMain.add(new JLabel("Solve At:"), BorderLayout.NORTH);
		pnlSolnMain.add(pnlSolnInput);

		btnDF = new JButton();
		btnCW = new JButton();
		btnDF.setToolTipText("Draw Vector Field");
		btnCW.setToolTipText("Draw Cobweb Plot");
		btnDF.setIcon(new ImageIcon(getClass().getResource("/vfield.png")));
		btnCW.setIcon(new ImageIcon(getClass().getResource("/cobweb.png")));
		btnDF.setEnabled(false);
		btnCW.setEnabled(false);
		btnDF.addActionListener(this);
		btnCW.addActionListener(this);
		pnlButton.add(btnCW);
		pnlButton.add(btnDF);
		
		var pnlButton2 = new JPanel();
		pnlButton2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		btnOK = new JButton();
		btnCancel = new JButton();
		btnOK.setToolTipText("Apply changes");
		btnCancel.setToolTipText("Cancel changes");
		btnOK.setIcon(new ImageIcon(getClass().getResource("/check.png")));
		btnCancel.setIcon(new ImageIcon(getClass().getResource("/cross.png")));
		btnOK.addActionListener(this);
		btnCancel.addActionListener(this);
		pnlButton2.add(btnOK);
		pnlButton2.add(btnCancel);

		var tools = new JToolBar("Plot Tools");
		tools.add(pnlButton);
		tools.add(pnlSolnMain);
		tools.add(pnlButton2);

		pnlMain.add(tools);
		pnlMain.add(pnlRB);
		pnlMain.add(pnlCounts);
		pnlMain.add(pnlMatrix);
		pnlMain.add(pnlRange);

		JScrollPane sp = new JScrollPane(
			pnlMain,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(sp);
	}

	private void updateSystemFromUI() {
		var builder = new EquationSystem.Builder();
		
		String input = tfCounts.getText();
		if (!input.isBlank()) {
			builder.setCount(Integer.parseInt(input));
		}
		input = tfStep.getText();
		if (!input.isBlank()) {
			builder.setStepSize(Double.parseDouble(input));
		}
		
		int noOfEqns = noOfEqns();
		if (noOfEqns < 1) {
			return;
		}
		
		for (int i = 0; i < noOfEqns; i++) {
			builder.addEquation(tfsEquations[i].getText());
			builder.addRange(
				Double.parseDouble(tfsRange[3*i].getText()),
				Double.parseDouble(tfsRange[3*i+1].getText()),
				Double.parseDouble(tfsRange[3*i+2].getText()));
		}
		builder.setMode(curMode);
		
		setSystem(builder.build());
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

	private void reloadUI() {
		var system = getSystem();
		if (system != null) {
			curMode = getSystem().getMode();
			for (int i = 0; i < noOfEqns(); i++) {
				tfsEquations[i].setText(system.get(i));
			}
		} else {
			curMode = SystemMode.ODE;
		}

		// TODO set other fields except Eqns
		if (curMode != null) {
			switchSystemMode();
			updateInterface();
		}
	}

	private void updateInterface() {
		if (curMode == SystemMode.ODE) {
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
		} else if (curMode == SystemMode.DFE) {
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
		} else if (curMode == SystemMode.FN1) {
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
		} else if (curMode == SystemMode.FN2) {
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

	private void switchSystemMode() {
		switch (curMode) {
		default:
			int noOfEqns = noOfEqns();
			if (noOfEqns >= 2) {
				if (noOfEqns == 3) {
					btnPlot3D.setEnabled(true);
				} else {
					btnPlot2D.setEnabled(true);
				}
				btnDF.setEnabled(true);
				btnCW.setEnabled(false);
				tfsSolnPoint[0].setEnabled(true);
				tfsSolnPoint[1].setEnabled(true);
				tfsSolnPoint[2].setEnabled(true);
				
				tfsSolnPoint[0].requestFocusInWindow();
			} else {
				JOptionPane.showMessageDialog(this, "Not enough equations!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
			}
			
			break;

		case FN1:
			btnDF.setEnabled(false);
			btnCW.setEnabled(false);
			btnPlot2D.setEnabled(true);
			btnPlot3D.setEnabled(false);

			tfsSolnPoint[0].setEnabled(false);
			tfsSolnPoint[1].setEnabled(false);
			tfsSolnPoint[2].setEnabled(false);
			break;

		case FN2:
			btnDF.setEnabled(false);
			btnCW.setEnabled(false);
			btnPlot2D.setEnabled(false);
			btnPlot3D.setEnabled(true);

			tfsSolnPoint[0].setEnabled(false);
			tfsSolnPoint[1].setEnabled(false);
			tfsSolnPoint[2].setEnabled(false);
			break;

		case DFE:
			btnDF.setEnabled(false);
			btnCW.setEnabled(true);
			btnPlot2D.setEnabled(true);
			btnPlot3D.setEnabled(false);

			tfsSolnPoint[0].setEnabled(true);
			tfsSolnPoint[1].setEnabled(true);
			tfsSolnPoint[2].setEnabled(false);
			
			tfsSolnPoint[0].requestFocusInWindow();
			break;
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
	
	public void plotTrajectory(double x, double y) {
		var solver = new Solver(ParserManager.getParser(), getSystem());
		PlotData trjData;		
		switch (curMode) {
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

	public void plotCobweb(double x) {
		var solver = new Solver(ParserManager.getParser(), getSystem());
		PlotData pdata = new PlotData(solver.cobweb(x));
		pdata.setPltype(PlotData.PlotType.LINES);
		pdata.setFgColor(Color.BLACK);
		
		pdata.setSystem(getSystem());

		setData(pdata);
	}

	public void plotTrajectory3D(double x, double y, double z) {
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

	public void plotDirectionField() {
		var solver = new Solver(ParserManager.getParser(), getSystem());
		PlotData pdata = new PlotData(solver.directionField());
		pdata.setPltype(PlotData.PlotType.VECTORS);
		pdata.setSystem(getSystem());
		
		setData(pdata);
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == btnOK) {
			// Sets up the System of Equations and validates,
			// but doesn't plot anything
			switchSystemMode();
			updateSystemFromUI();
		} else if (evt.getSource() == btnCancel) {
			// FIXME intuitively this should clear all fields instead of hiding this frame
			hide();
		} else {
			if (evt.getSource() == btnDF) {
				plotDirectionField();
			} else if (evt.getSource() == btnPlot2D) {
				switch (curMode) {
				case FN1:
					plotFunction2D();
					break;
				default:
					boolean hasValidPoint =
						!tfsSolnPoint[0].getText().isEmpty()
						&& !tfsSolnPoint[1].getText().isEmpty();
					
					if (hasValidPoint) {
						double x = Double.parseDouble(tfsSolnPoint[0].getText());
						double y = Double.parseDouble(tfsSolnPoint[1].getText());
						plotTrajectory(x, y);
					} else {
						JOptionPane.showMessageDialog(this, "Enter a solution point!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
						tfsSolnPoint[0].requestFocusInWindow();
						return;
					}
					break;
				}
			} else if (evt.getSource() == btnPlot3D) {
				switch (curMode) {
				case FN2:
					plotFunction3D();
					break;
				default:
					boolean hasValidPoint =
						!tfsSolnPoint[0].getText().isEmpty()
						&& !tfsSolnPoint[1].getText().isEmpty()
						&& !tfsSolnPoint[2].getText().isEmpty();
					
					if (hasValidPoint) {
						double x = Double.parseDouble(tfsSolnPoint[0].getText());
						double y = Double.parseDouble(tfsSolnPoint[1].getText());
						double z = Double.parseDouble(tfsSolnPoint[2].getText());
						plotTrajectory3D(x, y, z);
					} else {
						JOptionPane.showMessageDialog(this, "Enter a solution point!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
						tfsSolnPoint[0].requestFocusInWindow();
						return;
					}
				}	
			} else if (evt.getSource() == btnCW) {
				if (!tfsSolnPoint[0].getText().isEmpty()) {
					double x = Double.parseDouble(tfsSolnPoint[0].getText());
					plotCobweb(x);
				} else {
					JOptionPane.showMessageDialog(this, "Enter a solution point!", "Invalid Input", JOptionPane.ERROR_MESSAGE);
					tfsSolnPoint[0].requestFocusInWindow();
					return;
				}
			}
			
			updater.accept(getData());
		}
	}

	public void setUpdateCallback(Consumer<PlotData> update) {
		this.updater = update;
	}
}
