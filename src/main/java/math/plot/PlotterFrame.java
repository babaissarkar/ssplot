package math.plot;

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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

@SuppressWarnings("serial")
public class PlotterFrame extends JFrame implements ActionListener {

	private PlotView pv;
    private SystemData odedata;
    private ODEInputFrame pp;
    private StatLogger logger;
    
	private JMenu mnuFile, mnuPlot;
	private JMenuItem jmSave, jmPaint, jmOpen, jmHelp, jmShowData, jmQuit;
    private JMenuItem jmPlotType, jmPhase, jmCol, jmClear, jmSvData, jmAxes;
    private JMenuItem jmAbout, jmLogs;
    private JMenuItem jmLineWidth;
    
    private DBViewer dbv = new DBViewer();
    private static final int MENUBAR_WIDTH = 60; /* Valid only for defaul Metal look and feel. */
	
	public PlotterFrame() {
		setTitle("SSPlotter");
		setBounds(40, 40, 640, 640 + PlotterFrame.MENUBAR_WIDTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		jmOpen = new JMenuItem("Open Datafile");
		jmSvData = new JMenuItem("Save current data");
		jmSave = new JMenuItem("Save Image");
		jmShowData = new JMenuItem("View/Edit Plot Data");
		jmPaint = new JMenuItem("Refresh");
		jmHelp = new JMenuItem("Keymaps Help");
        jmQuit = new JMenuItem("Quit");

        jmPhase = new JMenuItem("Setup System...");
        jmAxes = new JMenuItem("Show/hide axes");
        jmLineWidth = new JMenuItem("Set line width");
        jmCol = new JMenuItem("Set Plot Color");
        jmPlotType = new JMenuItem("Set Plot Type");
        jmClear = new JMenuItem("Clear plot");
        
        jmAbout = new JMenuItem("About");
        jmLogs = new JMenuItem("Logs");
        
        jmOpen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
        jmSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        jmSvData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,  ActionEvent.CTRL_MASK|ActionEvent.SHIFT_MASK));
        jmShowData.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        jmQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        
        jmPhase.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, ActionEvent.CTRL_MASK));
        jmClear.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        
		jmSave.addActionListener(this);
		jmSvData.addActionListener(this);
		jmPaint.addActionListener(this);
		jmOpen.addActionListener(this);
		jmHelp.addActionListener(this);
		jmShowData.addActionListener(this);
        jmQuit.addActionListener(this);
        jmClear.addActionListener(this);
        jmLogs.addActionListener(this);
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
        mnuFile.add(jmPhase);
        mnuFile.addSeparator();
        mnuFile.add(jmLogs);
		mnuFile.add(jmHelp);
		mnuFile.add(jmAbout);
        mnuFile.add(jmQuit);

        mnuPlot = new JMenu("Plot");
        mnuPlot.add(jmAxes);
        mnuPlot.addSeparator();
        mnuPlot.add(jmShowData);
        mnuPlot.addSeparator();
        mnuPlot.add(jmClear);
        mnuPlot.add(jmPlotType);
        mnuPlot.add(jmLineWidth);
        mnuPlot.add(jmCol);
		
		JMenuBar jmb = new JMenuBar();
		jmb.add(mnuFile);
        jmb.add(mnuPlot);
		this.setJMenuBar(jmb);
		
        
        logger = new StatLogger();
		
		pv = new PlotView(logger);
        odedata = new SystemData();
        pp = new ODEInputFrame(odedata, pv);
        pp.show();
        //pp.hide();

        pv.addMouseListener(
            new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent ev) {
                    if (ev.getButton() == MouseEvent.BUTTON3) {
                        /* Will be added later. */
                        int x = ev.getX()-20;
                        int y = ev.getY()-20;
                        
                        Point2D.Double p = pv.getCanvas().getInvTransformedPoint(new Point2D.Double(x, y));
                        //System.out.println(p.toString());
                        /* The plotting area starts from (20,20) in java graphics space, so we are substracting it. */
                        String label = String.format("(%3.1f, %3.1f)", p.getX(), p.getY());
                        showMsg("Point : " + label);
                        //pv.addNode(new Point2D.Double(x-20, y-20), label, Color.BLUE);
                        repaint();
                    } else if (ev.getButton() == MouseEvent.BUTTON2) {
                    	int x = ev.getX()-20;
                        int y = ev.getY()-20;
                        
                        Point2D.Double p = pv.getCanvas().getInvTransformedPoint(new Point2D.Double(x, y));
                        pv.setZoomCenter(p);
                        
                        String label = String.format("(%3.1f, %3.1f)", p.getX(), p.getY());
                        showMsg("Zoom Center set at " + label);
                        
                        repaint();
                    }
                }
                
            }
        );
//        pv.addMouseWheelListener(
//        	new MouseWheelListener() {
//        		public void mouseWheelMoved(MouseWheelEvent ev) {
//        			int x = ev.getX()-20;
//                    int y = ev.getY()-20;
//                    
//                    Point2D.Double p = pv.getCanvas().getInvTransformedPoint(new Point2D.Double(x, y));
//                    
//                    if (ev.getWheelRotation() < 0) {
//                    	pv.zoomIn(p.getX(), p.getY());
//                    } else if (ev.getWheelRotation() > 0) {
//                    	pv.zoomOut(p.getX(), p.getY());
//                    }
//                }
//        	}
//        );
        
		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(pv, BorderLayout.CENTER);
		//this.pack();
		this.setResizable(false);
		this.setVisible(true);
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
		dbv.openFile();
		pv.setData(new PlotData(dbv.getData()));
		pv.repaint();
	}
	
	public void saveData() {
		dbv.saveFile();
	}
	
	public void showData() {
		if (pv.getData() != null) {
			dbv.setData(pv.getData().data);
		}
        dbv.addListener(this);
        dbv.setVisible(true);
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
			 String str = "<html><body>"
					 +    "<h1>SSPlot</h1>"
			 		 +    "Created by : Subhraman Sarkar, 2021<br>"
					 +    "Available under the LGPL 2.1 license or, (at your choice)"
					 +    " any later version.<br>"
					 +    "Homepage : <a href='https://github.com/babaissarkar/ssplot'>"
					 +    "https://github.com/babaissarkar/ssplot</a>"
					 +    "</body></html>";
			 JOptionPane.showMessageDialog(this, str, "About", JOptionPane.INFORMATION_MESSAGE);
        } else if (ae.getSource() == jmQuit) {
            System.exit(0);
        } else if (ae.getSource() == jmClear) {
            pv.clear();
        } else if (ae.getSource() == jmPhase) {
            pp.show();
        } else if (ae.getSource() == jmPlotType) {
            changePlotType();
        } else if (ae.getSource() == jmCol) {
        	Color c = JColorChooser.showDialog(this, "Plot Color 1", Color.RED);
        	if (c != null) {
        		pv.setColor(c);
        	} else {
        		c = Color.BLACK;
        	}
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
        } else if (ae.getSource() == jmLogs) {
        	logger.showLogs();
		} else if (ae.getSource() == dbv.btnPlot) {
			pv.setData(new PlotData(dbv.getData()));
			
			pv.setCols(dbv.getCol1(), dbv.getCol2());
            //JOptionPane.showMessageDialog(this, "Changes applied.");
            pv.repaint();
        }
	}
	
	public static void main(String[] args) {
		UIManager.put("Label.font", new FontUIResource("Cantarell", Font.PLAIN, 15));
		UIManager.put("RadioButton.font", new FontUIResource("Cantarell", Font.PLAIN, 16));
		
		UIManager.put("RadioButton.foreground", new Color(80,28,0));
		
		UIManager.put("Menu.selectionBackground", new Color(255,247,132));
		UIManager.put("MenuItem.selectionBackground", new Color(255,247,132));
		UIManager.put("MenuItem.acceleratorForeground", new Color(5,132,37));
		UIManager.put("MenuItem.foreground", new Color(4,88,25));
		UIManager.put("MenuItem.background", Color.WHITE);
		
		new PlotterFrame();
	}

}
