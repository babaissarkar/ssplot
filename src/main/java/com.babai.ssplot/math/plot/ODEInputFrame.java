/*
 * ODEInputFrame.java
 * 
 * Copyright 2021 Subhraman Sarkar <subhraman@subhraman-Inspiron>
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

package math.plot;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
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
	private JRadioButton rbODE, rbIM, rbFunc, rbFunc2;

	// boolean modeODE, modeFunc;

	private SystemMode curMode;
	private JButton btnTR2;
	private PlotData curData;
	private MainFrame mainFrame;

	public ODEInputFrame(MainFrame main) {
//		this.getSysData() = new SystemData();
		this.mainFrame = main;
		this.curData = new PlotData();
		this.curMode = SystemMode.ODE;
		initInputDialog();
		updateInterface();
	}

	public void initInputDialog() {
		/* Creating Gui */
		this.setTitle("System Parameters");
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		JPanel pnlMain = new JPanel();
		pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.PAGE_AXIS));
		pnlMain.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel pnlRB = new JPanel();
		rbODE = new JRadioButton("Differential Equation");
		rbODE.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				curMode = SystemMode.ODE;
				updateInterface();
			}
		});
		rbIM = new JRadioButton("Difference Equation");
		rbIM.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				curMode = SystemMode.DFE;
				updateInterface();
			}
		});
		rbFunc = new JRadioButton("1D function");
		rbFunc.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				curMode = SystemMode.FN1;
				updateInterface();
			}
		});
		rbFunc2 = new JRadioButton("2D function");
		rbFunc2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				curMode = SystemMode.FN2;
				updateInterface();
			}
		});

		ButtonGroup bg = new ButtonGroup();
		bg.add(rbODE);
		bg.add(rbIM);
		bg.add(rbFunc);
		bg.add(rbFunc2);
		
		rbODE.setSelected(true);

		pnlRB.add(rbODE);
		pnlRB.add(rbIM);
		pnlRB.add(rbFunc);
		pnlRB.add(rbFunc2);

		JPanel pnlCounts = new JPanel();
//		pnlCounts.setLayout(new GridLayout(2, 1, 5, 5));
		pnlCounts.setLayout(new BoxLayout(pnlCounts, BoxLayout.Y_AXIS));
		JLabel lblCounts = new JLabel("Iteration count");
		tfCounts = new JTextField(10);
		JPanel pnlLoCounts = new JPanel();
		pnlLoCounts.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlLoCounts.add(lblCounts);
		pnlLoCounts.add(Box.createRigidArea(new Dimension(38, 0)));
		pnlLoCounts.add(tfCounts);
		
		JLabel lblStep = new JLabel("Iteration stepsize");
		tfStep = new JTextField(10);
		JPanel pnlLoStep = new JPanel();
		pnlLoStep.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlLoStep.add(lblStep);
		pnlLoStep.add(Box.createRigidArea(new Dimension(20, 0)));
		pnlLoStep.add(tfStep);
		
		pnlCounts.add(pnlLoCounts);
		pnlCounts.add(pnlLoStep);
		pnlCounts.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(127, 0, 140), 3),
				"Iteration Parameters"));

		JPanel pnlMatrix = new JPanel();
//		pnlMatrix.setLayout(new GridLayout(3, 1, 5, 5));
		pnlMatrix.setLayout(new BoxLayout(pnlMatrix, BoxLayout.Y_AXIS));

		lbls = new JLabel[3];
		lbls[0] = new JLabel("Equation 1");
		lbls[1] = new JLabel("Equation 2");
		lbls[2] = new JLabel("Equation 3");

		tfs = new JTextField[4];
		tfs[0] = new JTextField(20);
		tfs[1] = new JTextField(20);
		tfs[2] = new JTextField(20);
		
		JPanel[] pnlLayout = new JPanel[3];

		for (int i = 0; i < 3; i++) {
//			lbls[i].setFont(new Font("TeXGyreSchola", Font.PLAIN, 14));
			pnlLayout[i] = new JPanel();
			pnlLayout[i].setLayout(new FlowLayout(FlowLayout.LEFT));
			lbls[i].setAlignmentX(LEFT_ALIGNMENT);
			pnlLayout[i].add(lbls[i]);
			pnlLayout[i].add(Box.createRigidArea(new Dimension(20, 0)));
			
			pnlLayout[i].add(tfs[i]);
			pnlMatrix.add(pnlLayout[i]);
		}

		pnlMatrix.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(255, 90, 38), 3),
				"Equations"));

		JPanel pnlRange = new JPanel();
		pnlRange.setLayout(new GridLayout(3, 3, 5, 5));
//		pnlRange.setLayout(new BoxLayout(pnlRange, BoxLayout.Y_AXIS));

		JLabel[] lbls2 = new JLabel[9];
		lbls2[0] = new JLabel("<html><body>X<sub>min</sub></body></html>");
		lbls2[1] = new JLabel("<html><body>X<sub>max</sub></body></html>");
		lbls2[2] = new JLabel("<html><body>X<sub>gap</sub></body></html>");

		lbls2[3] = new JLabel("<html><body>Y<sub>min</sub></body></html>");
		lbls2[4] = new JLabel("<html><body>Y<sub>max</sub></body></html>");
		lbls2[5] = new JLabel("<html><body>Y<sub>gap</sub></body></html>");

		lbls2[6] = new JLabel("<html><body>Z<sub>min</sub></body></html>");
		lbls2[7] = new JLabel("<html><body>Z<sub>max</sub></body></html>");
		lbls2[8] = new JLabel("<html><body>Z<sub>gap</sub></body></html>");

		tfs2 = new JTextField[9];

		int[] defVals = { -10, 10, 1, -10, 10, 1, -10, 10, 1 };

		for (int j = 0; j < 9; j++) {
			tfs2[j] = new JTextField(4);
			tfs2[j].setHorizontalAlignment(JTextField.CENTER);
			tfs2[j].setFont(new Font("LMRomanUnsl10-Regular", Font.PLAIN, 16));
			tfs2[j].setText("" + defVals[j]);
		}

		for (int i = 0; i < 9; i++) {
			
			pnlRange.add(lbls2[i]);
			pnlRange.add(tfs2[i]);
		}

		pnlRange.setBorder(
				BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(24, 110, 1), 3), "Ranges"));

		JPanel pnlButton2 = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();
		btnOK.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
    			getClass().getResource("/check.png"))));
		btnCancel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
    			getClass().getResource("/cross.png"))));
		
		JPanel pnlButton = new JPanel();
		btnTR = new JButton();
		btnTR2 = new JButton();
		btnTR.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
    			getClass().getResource("/2d.png"))));
		btnTR2.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
    			getClass().getResource("/3d.png"))));
		tfs3 = new JTextField[3];
		tfs3[0] = new JTextField(3);
		tfs3[1] = new JTextField(3);
		tfs3[2] = new JTextField(3);
		
//		JLabel lblAt = new JLabel("<html><body><font size='5'><u><i>Evaluate At :</i></u></font></html>");
//		JLabel lblAt = new JLabel("Evaluate At :");
		
		JLabel[] lbls3 = new JLabel[3];
		lbls3[0] = new JLabel("X");
		lbls3[1] = new JLabel("Y");
		lbls3[2] = new JLabel("Z");
		
		JPanel pnlButton3 = new JPanel();
		btnDF = new JButton();
		btnCW = new JButton();
		btnDF.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
    			getClass().getResource("/vfield.png"))));
		btnCW.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
    			getClass().getResource("/cobweb.png"))));

		btnDF.setEnabled(false);
		btnTR.setEnabled(false);
		btnTR2.setEnabled(false);
		btnCW.setEnabled(false);
		tfs3[0].setEnabled(false);
		tfs3[1].setEnabled(false);
		tfs3[2].setEnabled(false);
		
		for (int i = 0; i < 3; i++) {
			tfs[i].setHorizontalAlignment(JTextField.CENTER);
			tfs[i].setFont(new Font("LMRomanUnsl10-Regular", Font.PLAIN, 16));
			tfs3[i].setHorizontalAlignment(JTextField.CENTER);
			tfs3[i].setFont(new Font("LMRomanUnsl10-Regular", Font.PLAIN, 16));
		}
		tfCounts.setHorizontalAlignment(JTextField.CENTER);
		tfCounts.setFont(new Font("LMRomanUnsl10-Regular", Font.PLAIN, 16));
		tfStep.setHorizontalAlignment(JTextField.CENTER);
		tfStep.setFont(new Font("LMRomanUnsl10-Regular", Font.PLAIN, 16));

		FlowLayout f = new FlowLayout(FlowLayout.LEFT, 5, 5);
		pnlButton.setLayout(f);
		pnlButton.add(btnTR);
		pnlButton.add(btnTR2);
//		pnlButton.add(lblAt);
//		pnlButton.add(Box.createRigidArea(new Dimension(10, 10)));
		for (int i = 0; i < 3; i++) {
			pnlButton.add(lbls3[i]);
			pnlButton.add(tfs3[i]);
		}

		pnlButton3.setLayout(new FlowLayout(FlowLayout.LEFT));
		pnlButton3.add(btnCW);
		pnlButton3.add(btnDF);
		
		pnlButton2.setLayout(new FlowLayout(FlowLayout.CENTER));
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
		tools.add(pnlButton3);
		tools.add(pnlButton2);
		
		pnlMain.add(tools);
		pnlMain.add(pnlRB);
		pnlMain.add(pnlCounts);
		pnlMain.add(pnlMatrix);
		pnlMain.add(pnlRange);
//		pnlMain.add(pnlButton);
//		pnlMain.add(pnlButton3);
//		pnlMain.add(pnlButton2);
		
//		JMenuBar tools = new JMenuBar();
//		tools.add(pnlButton);
//		tools.add(pnlButton3);
//		tools.add(pnlButton2);
//		this.setJMenuBar(tools);
		
		JScrollPane sp = new JScrollPane(pnlMain, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		this.add(sp);
		// frmMain.pack();
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

			getSysData().N = Integer.parseInt(tfCounts.getText());
			getSysData().h = Double.parseDouble(tfStep.getText());
		} catch (Exception e) {

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
		this.curData.sysData = data; //ch
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
		} else if (curMode == SystemMode.DFE) {
			lbls[0].setText("<html><body>x<sub>n+1</sub> =</body></html>");
			lbls[1].setText("<html><body>y<sub>n+1</sub> =</body></html>");
			lbls[2].setText("<html><body>z<sub>n+1</sub> =</body></html>");
			tfs[1].setEditable(true);
			tfs[2].setEditable(true);
			tfs2[6].setEditable(true);
			tfs2[7].setEditable(true);
			tfs2[8].setEditable(true);
		} else if (curMode == SystemMode.FN1) {
			lbls[0].setText("y(x) =");
			lbls[1].setText("");
			lbls[2].setText("");
			tfs[1].setEditable(false);
			tfs[2].setEditable(false);
			tfs2[6].setEditable(false);
			tfs2[7].setEditable(false);
			tfs2[8].setEditable(false);
		} else if (curMode == SystemMode.FN2) {
			lbls[0].setText("z(x, y) =");
			lbls[1].setText("");
			lbls[2].setText("");
			tfs[1].setEditable(false);
			tfs[2].setEditable(false);
			tfs2[6].setEditable(true);
			tfs2[7].setEditable(true);
			tfs2[8].setEditable(true);
		}

	}

	/** Plotting functions : Calculated PlotData from Equation System */
	public void plotTrajectory(double x, double y) {
		PlotData trjData;
		switch (curMode) {
		default:
			trjData = new PlotData(getSysData().RK4Iterate(x, y));
			break;
		case DFE:
			trjData = new PlotData(getSysData().iterateMap(x, x));
			break;
		}
		trjData.setPltype(PlotData.PlotType.LINES);
		trjData.setFgColor(Color.BLACK);

		setData(trjData);
	}

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
		
		setData(trjData);
	}

	public void plotFunction2D() {
		PlotData trjData = new PlotData(getSysData().functionData2D());
		trjData.setPltype(PlotData.PlotType.THREED);
		trjData.setFgColor(Color.BLACK);

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
			updateView();

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
			updateView();

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
			
			updateView();

		} else if (evt.getSource() == btnCW) {

			double x = Double.parseDouble(tfs3[0].getText());
			plotCobweb(x);
			updateView();

		} else if (evt.getSource() == btnCancel) {
			hide();
		}
	}
	
	public SystemData getSysData() {
		return this.curData.sysData;
	}

	private void updateView() {
		mainFrame.setPlotData(getData());
	}

	private void setEqnSystem() {      
    	this.curData.sysData.setEqns(tfs[0].getText(), tfs[1].getText(), tfs[2].getText());
	}


}
