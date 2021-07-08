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
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

/* A class for drawing phase plot of two simultaneous 1nd order ode. */
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

//import math.prim.Matrix;

public class ODEInputFrame implements ActionListener {

//    Matrix A = new Matrix(2, 2);
	

    /* MVC architecture */
    SystemData odedata; /* Model */
    PlotView view; /* View */
    /* This class is the controller. */

    JFrame frmMain = null;
    JTextField[] tfs, tfs2, tfs3;
    JButton btnOK, btnCancel, btnDF, btnTR;
    JRadioButton rbODE, rbIM;
    JTextField tfCounts;
    
    boolean modeODE;
	JButton btnTR2;

    public ODEInputFrame(SystemData odedata, PlotView view) {
        this.odedata = odedata;
        this.view = view;
        initInputDialog();
	}

    public void initInputDialog() {
        /* Creating Gui */

        frmMain = new JFrame("System Parameters");
        frmMain.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frmMain.setBounds(50, 50, 400, 400);
        frmMain.setResizable(false);
        frmMain.getContentPane().setLayout(
            new GridLayout(5, 1, 2, 2)
        );
        
        JPanel pnlRB = new JPanel();
        rbODE = new JRadioButton("Differential Equation");
        rbODE.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		modeODE = true;
        	}
        });
        rbIM = new JRadioButton("Difference Equation");
        rbIM.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent evt) {
        		modeODE = false;
        	}
        });
        ButtonGroup bg = new ButtonGroup();
        bg.add(rbODE);
        bg.add(rbIM);
        pnlRB.add(rbODE);
        pnlRB.add(rbIM);
        
        JPanel pnlCounts = new JPanel();
        JLabel lblCounts = new JLabel("Iteration count :");
        tfCounts = new JTextField(10);
        pnlCounts.add(lblCounts);
        pnlCounts.add(tfCounts);

        JPanel pnlMatrix = new JPanel();
        pnlMatrix.setLayout(new GridLayout(3, 2, 5, 5));
        
        JLabel[] lbls = new JLabel[4];        
        
        lbls[0] = new JLabel("Eqn. 1: ");
        lbls[1] = new JLabel("Eqn. 2: ");
        lbls[2] = new JLabel("Eqn. 3: ");
        
        tfs = new JTextField[4];
        tfs[0] = new JTextField(20);
        tfs[1] = new JTextField(20);
        tfs[2] = new JTextField(20);

        for (int i = 0; i < 3; i++) {
            pnlMatrix.add(lbls[i]);
            pnlMatrix.add(tfs[i]);
        }

        pnlMatrix.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                "ODEs"
            )
        );

        JPanel pnlRange = new JPanel();
        pnlRange.setLayout(new GridLayout(3, 3, 5, 5));
        
        JLabel[] lbls2 = new JLabel[9];
        lbls2[0] = new JLabel("Xmin :");
        lbls2[1] = new JLabel("Xmax :");
        lbls2[2] = new JLabel("Xgap :");
        
        lbls2[3] = new JLabel("Ymin :");
        lbls2[4] = new JLabel("Ymax :");
        lbls2[5] = new JLabel("Ygap :");
        
        lbls2[6] = new JLabel("Zmin :");
        lbls2[7] = new JLabel("Zmax :");
        lbls2[8] = new JLabel("Zgap :");
        
        tfs2 = new JTextField[9];

        int[] defVals = {-10, 10, 1, -10, 10, 1, -10, 10, 1};
        
        for (int j = 0; j < 9; j++) {
            tfs2[j] = new JTextField(""+defVals[j] , 4);
        }

        for (int i = 0; i < 9; i++) {
            pnlRange.add(lbls2[i]);
            pnlRange.add(tfs2[i]);
        }

        pnlRange.setBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                "Range"
            )
        );

        JPanel pnlButton = new JPanel();
        btnOK = new JButton("Apply");
        btnCancel = new JButton("Close");
        btnDF = new JButton("Plot VField");
        btnTR = new JButton("Plot 2D");
        btnTR2 = new JButton("Plot 3D");
        tfs3 = new JTextField[3];
        tfs3[0] = new JTextField(3);
        tfs3[1] = new JTextField(3);
        tfs3[2] = new JTextField(3);
        JLabel[] lbls3 = new JLabel[3];
        lbls3[0] = new JLabel("X :");
        lbls3[1] = new JLabel("Y :");
        lbls3[2] = new JLabel("Z :");
        
        btnDF.setEnabled(false);
        btnTR.setEnabled(false);
        btnTR2.setEnabled(false);
        tfs3[0].setEnabled(false);
        tfs3[1].setEnabled(false);
        
        FlowLayout f = new FlowLayout(FlowLayout.RIGHT, 5, 5);
        pnlButton.setLayout(f);
        pnlButton.add(btnTR);
        pnlButton.add(btnTR2);
        for (int i = 0; i < 3; i++) {
        	pnlButton.add(lbls3[i]);
        	pnlButton.add(tfs3[i]);
        }

        pnlButton.add(btnDF);
        pnlButton.add(btnOK);
        pnlButton.add(btnCancel);

        btnTR.addActionListener(this);
        btnTR2.addActionListener(this);
        btnDF.addActionListener(this);
        btnOK.addActionListener(this);
        btnCancel.addActionListener(this);

        frmMain.getContentPane().add(pnlRB);
        frmMain.getContentPane().add(pnlCounts);
        frmMain.getContentPane().add(pnlMatrix);
        frmMain.getContentPane().add(pnlRange);
        frmMain.getContentPane().add(pnlButton);
        frmMain.pack();
    }

    public void setODERange() {
        try {
            odedata.min[0] = Double.parseDouble(tfs2[0].getText());
            odedata.max[0] = Double.parseDouble(tfs2[1].getText());
            odedata.gap[0] = Double.parseDouble(tfs2[2].getText());
            odedata.min[1] = Double.parseDouble(tfs2[3].getText());
            odedata.max[1] = Double.parseDouble(tfs2[4].getText());
            odedata.gap[1] = Double.parseDouble(tfs2[5].getText());
            odedata.min[2] = Double.parseDouble(tfs2[6].getText());
            odedata.max[2] = Double.parseDouble(tfs2[7].getText());
            odedata.gap[2] = Double.parseDouble(tfs2[8].getText());
            
            odedata.N = Integer.parseInt(tfCounts.getText());
        } catch(Exception e) {
            
        }
    }

    public void updateView(Vector<Vector<Double>> data) {
    	PlotData pdata = new PlotData(data);
    	pdata.pltype = PlotData.PlotType.VECTORS;
        view.setData(pdata);
    }

    public void plotTrajectory(double x, double y) {
    	PlotData trjData;
    	if (modeODE) {
    		trjData = new PlotData(odedata.RK4Iterate(x, y));
    	} else {
    		trjData = new PlotData(odedata.iterateMap(x, x));
    	}
    	trjData.pltype = PlotData.PlotType.LINES;
    	trjData.fgColor = Color.BLACK;
    	
    	view.setData(trjData);
    }
    
    public void plotTrajectory3D(double x, double y, double z) {
    	PlotData trjData = new PlotData(odedata.RK4Iterate3D(x, y, z));
    	trjData.pltype = PlotData.PlotType.THREED;
    	trjData.fgColor = Color.BLACK;
    	
    	view.setData(trjData);
    }

    @Override
    public void actionPerformed(ActionEvent evt) {
        if (evt.getSource() == btnOK) {
        	/* Just sets up the System of Equations, but doesn't plot anything */
        	setEqnSystem();
            if (modeODE) {
				btnDF.setEnabled(true);
			}
			setODERange();
			tfs3[0].setEnabled(true);
            tfs3[1].setEnabled(true);
            btnTR.setEnabled(true);
            btnTR2.setEnabled(true);
            
        } else if (evt.getSource() == btnDF) {
        	
            updateView(odedata.directionField());
            
        } else if (evt.getSource() == btnTR) {
        	
        	double x = Double.parseDouble(tfs3[0].getText());
        	double y = Double.parseDouble(tfs3[1].getText());
        	this.plotTrajectory(x, y);
        	
        } else if (evt.getSource() == btnTR2) {
        	
        	double x = Double.parseDouble(tfs3[0].getText());
        	double y = Double.parseDouble(tfs3[1].getText());
        	double z = Double.parseDouble(tfs3[2].getText());
        	
        	
        	this.plotTrajectory3D(x, y, z);
        	
        } else if (evt.getSource() == btnCancel) {
        	
            hide();
            
        }
    }

	private void setEqnSystem() {
		
		odedata.setEqns(tfs[0].getText(), tfs[1].getText(), tfs[2].getText());
		
	}

	public void show() {
        if (frmMain != null) {
            frmMain.setVisible(true);
        }
    }

    public void hide() {
        if (frmMain != null) {
            frmMain.setVisible(false);
        }
    }

}

