/*
 * ODEInputFrame.java
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

package ui;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.border.TitledBorder;

import math.plot.*;

/** This class takes the input from user, sends data to backend,
 *  get processed data from backend, and send it back to MainFrame
 *  for plotting.
 *  @author ssarkar
 */
public class ODEInputFrame extends JInternalFrame implements ActionListener {

	//	private SystemData getSysData();

	//	private JInternalFrame frmMain = null;
	private JLabel[] lbls;
	private JTextField tfCounts, tfStep;
	private JTextField[] tfs, tfs2, tfs3;
	private JButton btnOK, btnCancel, btnDF, btnTR, btnCW;

	private SystemMode curMode;
	private JButton btnTR2;
	private PlotData curData;
	private UpdateCallback updater;

	public ODEInputFrame() {
		this.curData = new PlotData();
		this.curMode = SystemMode.ODE;
		initInputDialog();
		updateInterface();
	}

	public void initInputDialog() {
		/* Creating Gui */
		setTitle("System Parameters");
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		var pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.PAGE_AXIS));
		pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		var pnlRB = new JPanel();
		var rbODE = new JRadioButton("Differential Equation", true);
		var rbIM = new JRadioButton("Difference Equation");
		var rbFunc = new JRadioButton("1D function");
		var rbFunc2 = new JRadioButton("2D function");

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
		tfCounts = new JTextField(10);
		var lblStep = new JLabel("Iteration stepsize");
		tfStep = new JTextField(10);

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
						new Font("Serif", Font.BOLD, 14),
						new Color(127, 0, 140).darker().darker()));


		// Equations Entry
		var pnlMatrix = new JPanel();
		pnlMatrix.setLayout(new GridBagLayout());

		lbls = new JLabel[] {
				new JLabel("Equation 1"),
				new JLabel("Equation 2"),
				new JLabel("Equation 3")
		};

		tfs = new JTextField[] {
				new JTextField(20),
				new JTextField(20),
				new JTextField(20)
		};

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;

		for (int i = 0; i < lbls.length; i++) {
			// Add the label (column 0)
			gbc.gridx = 0;
			gbc.gridy = i;
			gbc.weightx = 0;
			gbc.fill = GridBagConstraints.NONE; // Don't stretch the label
			pnlMatrix.add(lbls[i], gbc);

			// Add the text field (column 1)
			gbc.gridx = 1;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL; // Make text field stretch to fill available space
			pnlMatrix.add(tfs[i], gbc);
		}

		pnlMatrix.setBorder(
				BorderFactory.createTitledBorder(
						BorderFactory.createLineBorder(new Color(255, 90, 38), 3),
						"Equations",
						TitledBorder.LEFT,
						TitledBorder.TOP,
						new Font("Serif", Font.BOLD, 14),
						new Color(255, 90, 38).darker().darker()));

		// Ranges entry
		var pnlRange = new JPanel(new GridBagLayout());
		final String sub_markup = "<html><body>%s<sub>%s</sub></body></html>";

		String[] axes = {"X", "Y", "Z"};
		String[] tags = {"min", "max", "gap"};

		JLabel[] lbls2 = new JLabel[9];
		tfs2 = new JTextField[9];
		double[] defVals = {-10, 10, 0.1, -10, 10, 0.1, -10, 10, 0.1};

		for (int i = 0; i < 9; i++) {
			lbls2[i] = new JLabel(sub_markup.formatted(axes[i / 3], tags[i % 3]));
			tfs2[i] = new JTextField(9);
			tfs2[i].setHorizontalAlignment(JTextField.CENTER);
			tfs2[i].setFont(new Font("monospace", Font.PLAIN, 16));
			tfs2[i].setText("" + defVals[i]);
		}

		gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		for (int row = 0; row < 3; row++) {
			for (int i = 0; i < 3; i++) {
				int idx = row * 3 + i;
				int col = i * 3;

				gbc.gridx = col;
				gbc.gridy = row;
				gbc.weightx = 0;
				pnlRange.add(lbls2[idx], gbc);

				gbc.gridx = col + 1;
				gbc.weightx = 1;
				pnlRange.add(tfs2[idx], gbc);

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
				new Font("Serif", Font.BOLD, 14),
				new Color(24, 110, 1).darker().darker()));

		var pnlButton2 = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();
		btnOK.setToolTipText("Apply changes");
		btnCancel.setToolTipText("Cancel changes");
		btnOK.setIcon(new ImageIcon(getClass().getResource("/check.png")));
		btnCancel.setIcon(new ImageIcon(getClass().getResource("/cross.png")));

		var pnlButton = new JPanel();
		btnTR = new JButton();
		btnTR2 = new JButton();
		btnTR.setToolTipText("Draw 2d plot");
		btnTR2.setToolTipText("Draw 3d plot");
		btnTR.setIcon(new ImageIcon(getClass().getResource("/2d.png")));
		btnTR2.setIcon(new ImageIcon(getClass().getResource("/3d.png")));
		tfs3 = new JTextField[3];
		tfs3[0] = new JTextField(3);
		tfs3[1] = new JTextField(3);
		tfs3[2] = new JTextField(3);

		JLabel[] lbls3 = new JLabel[3];
		lbls3[0] = new JLabel("X");
		lbls3[1] = new JLabel("Y");
		lbls3[2] = new JLabel("Z");

		btnDF = new JButton();
		btnCW = new JButton();
		btnDF.setToolTipText("Draw Vector Field");
		btnCW.setToolTipText("Draw Cobweb Plot");
		btnDF.setIcon(new ImageIcon(getClass().getResource("/vfield.png")));
		btnCW.setIcon(new ImageIcon(getClass().getResource("/cobweb.png")));

		btnDF.setEnabled(false);
		btnTR.setEnabled(false);
		btnTR2.setEnabled(false);
		btnCW.setEnabled(false);
		tfs3[0].setEnabled(false);
		tfs3[1].setEnabled(false);
		tfs3[2].setEnabled(false);

		for (int i = 0; i < 3; i++) {
			tfs[i].setHorizontalAlignment(JTextField.CENTER);
			tfs[i].setFont(new Font("monospace", Font.PLAIN, 16));
			tfs3[i].setHorizontalAlignment(JTextField.CENTER);
			tfs3[i].setFont(new Font("monospace", Font.PLAIN, 16));
		}
		tfCounts.setHorizontalAlignment(JTextField.CENTER);
		tfCounts.setFont(new Font("monospace", Font.PLAIN, 16));
		tfStep.setHorizontalAlignment(JTextField.CENTER);
		tfStep.setFont(new Font("monospace", Font.PLAIN, 16));

		pnlButton.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		pnlButton.add(btnTR);
		pnlButton.add(btnTR2);
		for (int i = 0; i < 3; i++) {
			pnlButton.add(lbls3[i]);
			pnlButton.add(tfs3[i]);
		}
		pnlButton.add(btnCW);
		pnlButton.add(btnDF);

		pnlButton2.setLayout(new FlowLayout(FlowLayout.RIGHT));
		pnlButton2.add(btnOK);
		pnlButton2.add(btnCancel);

		btnTR.addActionListener(this);
		btnTR2.addActionListener(this);
		btnDF.addActionListener(this);
		btnCW.addActionListener(this);
		btnOK.addActionListener(this);
		btnCancel.addActionListener(this);

		JToolBar tools = new JToolBar("Plot Tools");
		tools.add(pnlButton);
		tools.add(pnlButton2);

		pnlMain.add(tools);
		pnlMain.add(pnlRB);
		pnlMain.add(pnlCounts);
		pnlMain.add(pnlMatrix);
		pnlMain.add(pnlRange);

		JScrollPane sp = new JScrollPane(pnlMain, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(sp);
	}

	public void setODERange() {
		try {
			getSysData().min[0] = Double.parseDouble(tfs2[0].getText());
			getSysData().max[0] = Double.parseDouble(tfs2[1].getText());
			getSysData().gap[0] = Double.parseDouble(tfs2[2].getText());
			getSysData().min[1] = Double.parseDouble(tfs2[3].getText());
			getSysData().max[1] = Double.parseDouble(tfs2[4].getText());
			getSysData().gap[1] = Double.parseDouble(tfs2[5].getText());
			getSysData().min[2] = Double.parseDouble(tfs2[6].getText());
			getSysData().max[2] = Double.parseDouble(tfs2[7].getText());
			getSysData().gap[2] = Double.parseDouble(tfs2[8].getText());

			String input;
			if ( !(input = tfCounts.getText()).isBlank() ) {
				getSysData().n = Integer.parseInt(input);
			}
			
			if ( !(input = tfStep.getText()).isBlank() ) {
				getSysData().h = Double.parseDouble(input);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setData(PlotData pdata) {
		this.curData = pdata;
	}

	private PlotData getData() {
		return this.curData;
	}

	// set the UI from DBViewer
	public void setSystemData(SystemData data) {
		//		this.getSysData() = data;
		this.curData.sysData = data;
		reloadUI();
	}

	// Problematic method
	private void reloadUI() {
		// Update UI, changed
		//		curMode = this.getSysData().curMode;
		if (this.curData.sysData != null) {
			curMode = this.curData.sysData.curMode;
			for (int i = 0; i < 3; i++) {
				tfs[i].setText(this.curData.sysData.eqns[i]);
			}
		} else {
			curMode = SystemMode.ODE;
		}

		// TODO set other fields except Eqns
		if (curMode != null) {
			switchStates();
			updateInterface();
		}
	}

	private void updateInterface() {
		if (curMode == SystemMode.ODE) {
			lbls[0].setText("dx/dt =");
			lbls[1].setText("dy/dt =");
			lbls[2].setText("dz/dt =");
			tfs[1].setEditable(true);
			tfs[2].setEditable(true);
			tfs2[6].setEditable(true);
			tfs2[7].setEditable(true);
			tfs2[8].setEditable(true);
			tfStep.setEditable(true);
			tfCounts.setEditable(true);
		} else if (curMode == SystemMode.DFE) {
			lbls[0].setText("<html><body>x<sub>n+1</sub> =</body></html>");
			lbls[1].setText("<html><body>y<sub>n+1</sub> =</body></html>");
			lbls[2].setText("<html><body>z<sub>n+1</sub> =</body></html>");
			tfs[1].setEditable(true);
			tfs[2].setEditable(true);
			tfs2[6].setEditable(true);
			tfs2[7].setEditable(true);
			tfs2[8].setEditable(true);
			tfStep.setEditable(true);
			tfCounts.setEditable(true);
		} else if (curMode == SystemMode.FN1) {
			lbls[0].setText("y(x) =");
			lbls[1].setText("");
			lbls[2].setText("");
			tfs[1].setEditable(false);
			tfs[2].setEditable(false);
			tfs2[6].setEditable(false);
			tfs2[7].setEditable(false);
			tfs2[8].setEditable(false);
			tfStep.setEditable(false);
			tfCounts.setEditable(false);
		} else if (curMode == SystemMode.FN2) {
			lbls[0].setText("z(x, y) =");
			lbls[1].setText("");
			lbls[2].setText("");
			tfs[1].setEditable(false);
			tfs[2].setEditable(false);
			tfs2[6].setEditable(true);
			tfs2[7].setEditable(true);
			tfs2[8].setEditable(true);
			tfStep.setEditable(false);
			tfCounts.setEditable(false);
		}

	}

	/** Plotting functions : Calculated PlotData from Equation System */
	public void plotTrajectory(double x, double y) {
		PlotData trjData;
		switch (curMode) {
		case DFE:
			trjData = new PlotData(getSysData().iterateMap(x, x));
			break;
		default:
			trjData = new PlotData(getSysData().RK4Iterate(x, y));
			break;
		}
		trjData.setPltype(PlotData.PlotType.LINES);
		trjData.setFgColor(Color.BLACK);

		setData(trjData);
	}

	/* TODO remove setFgColor, it is forcing plot color to black always,
	 * should use existing fgcolor, and use black as fallback instead. */

	/* FIXME setPltype is not having any effect. */

	public void plotCobweb(double x) {
		PlotData pdata = new PlotData(getSysData().cobweb(x));
		pdata.setPltype(PlotData.PlotType.LINES);
		pdata.setFgColor(Color.BLACK);

		setData(pdata);
	}

	public void plotTrajectory3D(double x, double y, double z) {
		PlotData trjData = new PlotData(getSysData().RK4Iterate3D(x, y, z));
		trjData.setPltype(PlotData.PlotType.THREED);
		trjData.setFgColor(Color.BLACK);

		setData(trjData);
	}

	public void plotFunction() {
		PlotData trjData = new PlotData(getSysData().functionData());
		trjData.setPltype(PlotData.PlotType.LINES);
		trjData.setFgColor(Color.BLACK);
		trjData.setTitle(String.format("y = %s", tfs[0].getText()));

		setData(trjData);
	}

	public void plotFunction2D() {
		PlotData trjData = new PlotData(getSysData().functionData2D());
		trjData.setPltype(PlotData.PlotType.THREED);
		trjData.setFgColor(Color.BLACK);
		trjData.setTitle(String.format("z = %s", tfs[0].getText()));

		setData(trjData);
	}

	public void vectorPlot(Vector<Vector<Double>> data) {
		PlotData pdata = new PlotData(data);
		pdata.setPltype(PlotData.PlotType.VECTORS);
		setData(pdata);
	}

	public void switchStates() {
		switch (curMode) {
		default:
			btnDF.setEnabled(true);
			btnCW.setEnabled(false);
			btnTR.setEnabled(true);
			btnTR2.setEnabled(true);

			tfs3[0].setEnabled(true);
			tfs3[1].setEnabled(true);
			tfs3[2].setEnabled(true);
			break;

		case FN1:
			btnDF.setEnabled(false);
			btnCW.setEnabled(false);
			btnTR.setEnabled(true);
			btnTR2.setEnabled(false);

			tfs3[0].setEnabled(false);
			tfs3[1].setEnabled(false);
			tfs3[2].setEnabled(false);
			break;

		case FN2:
			btnDF.setEnabled(false);
			btnCW.setEnabled(false);
			btnTR.setEnabled(false);
			btnTR2.setEnabled(true);

			tfs3[0].setEnabled(false);
			tfs3[1].setEnabled(false);
			tfs3[2].setEnabled(false);
			break;

		case DFE:
			btnDF.setEnabled(false);
			btnCW.setEnabled(true);
			btnTR.setEnabled(true);
			btnTR2.setEnabled(false);

			tfs3[0].setEnabled(true);
			tfs3[1].setEnabled(true);
			tfs3[2].setEnabled(false);
			break;
		}
	}

	@Override
	public void actionPerformed(ActionEvent evt) {
		if (evt.getSource() == btnOK) {
			/* Just sets up the System of Equations, but doesn't plot anything */
			setEqnSystem();
			switchStates();
			setODERange();

		} else if (evt.getSource() == btnDF) {

			vectorPlot(getSysData().directionField());
			updater.update(getData());

		} else if (evt.getSource() == btnTR) {

			switch (curMode) {
			case FN1:
				plotFunction();
				break;
			default:
				double x = Double.parseDouble(tfs3[0].getText());
				double y = Double.parseDouble(tfs3[1].getText());
				plotTrajectory(x, y);
				break;
			}
			updater.update(getData());

		} else if (evt.getSource() == btnTR2) {
			switch (curMode) {
			case FN2:
				plotFunction2D();
				break;
			default:
				double x = Double.parseDouble(tfs3[0].getText());
				double y = Double.parseDouble(tfs3[1].getText());
				double z = Double.parseDouble(tfs3[2].getText());

				plotTrajectory3D(x, y, z);
			}
			updater.update(getData());

		} else if (evt.getSource() == btnCW) {

			double x = Double.parseDouble(tfs3[0].getText());
			plotCobweb(x);
			updater.update(getData());

		} else if (evt.getSource() == btnCancel) {
			hide();
		}
	}

	public SystemData getSysData() {
		return this.curData.sysData;
	}

	public void setUpdateCallback(UpdateCallback update) {
		this.updater = update;
	}

	private void setEqnSystem() {
		this.curData.sysData.setEqns(tfs[0].getText(), tfs[1].getText(), tfs[2].getText());
	}

	@FunctionalInterface
	interface UpdateCallback {
		void update(PlotData data);
	}
}
