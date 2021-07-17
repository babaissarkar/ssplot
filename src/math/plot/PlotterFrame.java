/*
 * PlotterFrame.java
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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import javax.swing.JColorChooser;

@SuppressWarnings("serial")
public class PlotterFrame extends JFrame implements ActionListener {

	private PlotView pv;
    private SystemData odedata;
    private ODEInputFrame pp;
    //private PlotData pd;
    
	private JMenu mnuFile, mnuPlot;
	private JMenuItem jmSave, jmPaint, jmOpen, jmHelp, jmShowData, jmQuit;
    private JMenuItem jmPlotType, jmPhase, jmCol, jmClear, jmSvData, jmAxes;
    private JMenuItem jmAbout;
    private JMenuItem jmLineWidth;
	private DBViewer dbv = null;
    private static final int MENUBAR_WIDTH = 60; /* Valid only for defaul Metal look and feel. */
	
	public PlotterFrame() {
		setTitle("SSPlotter");
		setBounds(40, 40, 640, 640 + PlotterFrame.MENUBAR_WIDTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jmOpen = new JMenuItem("Open Datafile");
		jmSvData = new JMenuItem("Save current data");
		jmSave = new JMenuItem("Save Image");
		jmShowData = new JMenuItem("Show Plot Data");
		jmPaint = new JMenuItem("Refresh");
		jmHelp = new JMenuItem("Keymaps Help");
        jmQuit = new JMenuItem("Quit");

        jmPhase = new JMenuItem("Setup System ...");
        jmAxes = new JMenuItem("Show/hide axes");
        jmLineWidth = new JMenuItem("Set line width");
        jmCol = new JMenuItem("Set Plot Color");
        jmPlotType = new JMenuItem("Set Plot Type");
        jmClear = new JMenuItem("Clear plot");
        
        jmAbout = new JMenuItem("About");
        
		jmSave.addActionListener(this);
		jmSvData.addActionListener(this);
		jmPaint.addActionListener(this);
		jmOpen.addActionListener(this);
		jmHelp.addActionListener(this);
		jmShowData.addActionListener(this);
        jmQuit.addActionListener(this);
        jmClear.addActionListener(this);
        jmAbout.addActionListener(this);

        jmCol.addActionListener(this);
        jmLineWidth.addActionListener(this);
        jmAxes.addActionListener(this);
        jmPhase.addActionListener(this);
        jmPlotType.addActionListener(this);
        
		mnuFile = new JMenu("File");
		mnuFile.add(jmOpen);
		mnuFile.add(jmSave);
		mnuFile.add(jmSvData);
        mnuFile.addSeparator();
        mnuFile.add(jmClear);
		mnuFile.add(jmShowData);
        mnuFile.addSeparator();
        mnuFile.add(jmPhase);
        mnuFile.addSeparator();
		mnuFile.add(jmHelp);
		mnuFile.add(jmAbout);
        mnuFile.add(jmQuit);

        mnuPlot = new JMenu("Plot");
        mnuPlot.add(jmAxes);
        mnuPlot.add(jmPlotType);
        mnuPlot.add(jmLineWidth);
        mnuPlot.add(jmCol);
		
		JMenuBar jmb = new JMenuBar();
		jmb.add(mnuFile);
        jmb.add(mnuPlot);
		this.setJMenuBar(jmb);
		
		pv = new PlotView();
        odedata = new SystemData();
        pp = new ODEInputFrame(odedata, pv);
        pp.hide();

        pv.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent ev) {
                	/* Will be added later. */
                	int x = ev.getX();
            		int y = ev.getY();
            		
            		Point2D.Double p = pv.getCanvas().getInvTransformedPoint(new Point2D.Double(x, y));
            		//System.out.println(p.toString());
            		/* The plotting area starts from (20,20) in java graphics space, so we are substracting it. */
            		String label = String.format("(%d, %d)", (int) p.getX() - 20, (int) p.getY() + 20);
            		//showMsg("Point : " + label);
                    repaint();
                }
            }
        );
        
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(pv, BorderLayout.CENTER);
		this.setResizable(false);
		this.setVisible(true);
	}
	
	public static void main(String[] args) {
		new PlotterFrame();
	}

	public void saveImage() {
		JFileChooser files = new JFileChooser();
		int stat = files.showSaveDialog(this);
		if (stat == JFileChooser.APPROVE_OPTION) {
			File f = files.getSelectedFile();
			try {
				ImageIO.write(pv.getImage(), "png", f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void openFile() {
		JFileChooser files = new JFileChooser();
		int stat = files.showOpenDialog(this);
		File f = null;
		if (stat == JFileChooser.APPROVE_OPTION) {
			f = files.getSelectedFile();
            Path dpath = f.toPath();
            if (dpath != null) {
                try {
					Vector<Vector<Double>> data = NumParse.parse(dpath);
					PlotData pd = new PlotData(data);
					pv.setData(pd);
					pv.repaint();
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
//            canv.repaint();
		}
		
	}
	
	public void showData() {
        Vector<Vector<Double>> data = pv.getData().data;
        if (data != null) {
            dbv = new DBViewer(data);
            dbv.addListener(this);
            dbv.setVisible(true);
        }
	}
	
	public void saveData() {
		Vector<Vector<Double>> data = pv.getData().data;
		JFileChooser files = new JFileChooser();
		int stat = files.showSaveDialog(this);
		File f = null;
		if (stat == JFileChooser.APPROVE_OPTION) {
			f = files.getSelectedFile();
            Path dpath = f.toPath();
            if (dpath != null) {
                NumParse.write(data, dpath);
            }
//            canv.repaint();
		}
	}
	
	public void showHelp() {
		// Shows help message
		String msg = "<html><body>arrow keys : translate graph,"
				   + "<br> j and f : zoom in and out"
				   + "<br> g and h : fine zoom adjustment"
				   + "<br>q, a; w, s; e, d : 3d rotation keys"
				   + "</body></html>";
        showMsg(msg);
	}
	
	public void showMsg(String str) {
		JOptionPane.showMessageDialog(this, str);
	}
	 
    public void changePlotType() {
        Object[] types = {"Lines", "Points", "Both Lines and Points", "3D Points", "3D Lines", "Vector field"};

        String type = (String) JOptionPane.showInputDialog(
            this,
            "Choose Plot Type :",
            "Plot Type",
            JOptionPane.QUESTION_MESSAGE,
            null,
            types,
            types[0]
        );

        if (type != null) {
            for (int i = 0; i < 6; i++) {
                if (type == types[i]) {
                    switch(i) {
                        case 1 :
                            pv.setCurPlotType(PlotData.PlotType.POINTS);
                            break;
                        case 2 :
                            pv.setCurPlotType(PlotData.PlotType.LP);
                            break;
                        case 3 :
                            pv.setCurPlotType(PlotData.PlotType.THREED);
                            break;
                        case 4 :
                            pv.setCurPlotType(PlotData.PlotType.TRLINE);
                            break;
                        case 5 :
                            pv.setCurPlotType(PlotData.PlotType.VECTORS);
                            break;
                        default :
                            pv.setCurPlotType(PlotData.PlotType.LINES);
                    }
                }
            }
        }
    }

	@Override
	public void actionPerformed(ActionEvent ae) {
		/* Triggers the actions associated with the menus */
		if (ae.getSource() == jmSave) {
			saveImage();
		} else if (ae.getSource() == jmPaint) {
//			canv.refresh();
		} else if (ae.getSource() == jmOpen) {
			openFile();
        } else if (ae.getSource() == jmShowData) {
            showData();
//            canv.showMsg("Show datasets.");
		} else if (ae.getSource() == jmHelp) {
			showHelp();
		} else if (ae.getSource() == jmAbout) {
			 String str = "<html><body>Created by : Subhraman Sarkar, 2021<br>"
					 +    "Available under the LGPL 2.1 license.</body></html>";
			 JOptionPane.showMessageDialog(this, str, "About", JOptionPane.INFORMATION_MESSAGE);
        } else if (ae.getSource() == jmQuit) {
            System.exit(0);
        } else if (ae.getSource() == jmClear) {
            pv.clear();
        } else if (ae.getSource() == jmPhase) {
           //if (pp == null) {
             //   pp = new ODEInputFrame(odedata, canv);
                //canv.addMouseListener(pp);
           //}
            pp.show();
        } else if (ae.getSource() == jmPlotType) {
            changePlotType();
        } else if (ae.getSource() == jmCol) {
        	Color c = JColorChooser.showDialog(this, "Plot Color 1", Color.RED);
    		pv.setColor(c);
        } else if (ae.getSource() == jmSvData) {
        	saveData();
        } else if (ae.getSource() == jmAxes) {
        	pv.toggleAxes();
        } else if (ae.getSource() == jmLineWidth) {
        	String strWidth = JOptionPane.showInputDialog("Line Width :");
        	if (strWidth != null) {
        		int width = Integer.parseInt(strWidth);
        		PlotData pd = pv.getData();
        		if (pd != null) {
        			pd.ptX = width;
        			pd.ptY = width;
        		}
        		pv.repaint();
        	}
		} else if (ae.getSource() == dbv.btnPlot) {
			pv.setCols(dbv.getCol1(), dbv.getCol2());
            JOptionPane.showMessageDialog(this, "Changes applied.");
            pv.refresh();
        }
	}

}
