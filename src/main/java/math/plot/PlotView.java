/*
 * PlotView.java
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

/* 20.07.2021 : Functions drawNode and addNode and class Node added. Function paint() changed to add node drawing.*/
 
package math.plot;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class PlotView extends JLabel {
	
	private Vector<PlotData> plots;
	private PlotData curPlot;
	private int col1, col2;
	private Canvas canv;
	private Project2D p;

    private Vector<Node> nodes = new Vector<Node>();
	
	private double moveAngle = Math.toRadians(10);
	
	// Actions
		private LeftAction move_left = new LeftAction();
		private RightAction move_right = new RightAction();
		private UpAction move_up = new UpAction();
		private DownAction move_down = new DownAction();
		private ZoomInAction zoom_in = new ZoomInAction();
		private ZoomOutAction zoom_out = new ZoomOutAction();
		private SmallZoomInAction szoom_in = new SmallZoomInAction();
		private SmallZoomOutAction szoom_out = new SmallZoomOutAction();
		
		private RotAPlusAction rot_a_pos = new RotAPlusAction();
		private RotAMinusAction rot_a_min = new RotAMinusAction();
		private RotBPlusAction rot_b_pos = new RotBPlusAction();
		private RotBMinusAction rot_b_min = new RotBMinusAction();
		private RotCPlusAction rot_c_pos = new RotCPlusAction();
		private RotCMinusAction rot_c_min = new RotCMinusAction();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1906949716987184760L;

	public PlotView(StatLogger logger) {
		clear();
		
		p = new Project2D();
		
		setLogger(logger);
		
		p.setView(0, 0, 0);
		
		// Setting Keybinding for movement
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right");
		getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
		getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_J, 0, false), "plus");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0, false), "minus");
		
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_Q, 0, false), "rotAp");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "rotAm");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "rotBp");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "rotBm");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_E, 0, false), "rotCp");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "rotCm");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_H, 0, false), "splus");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_G, 0, false), "sminus");


		getActionMap().put("left", move_left);
		getActionMap().put("right", move_right);
		getActionMap().put("up", move_up);
		getActionMap().put("down", move_down);
		getActionMap().put("plus", zoom_in);
		getActionMap().put("minus", zoom_out);
		getActionMap().put("splus", szoom_in);
		getActionMap().put("sminus", szoom_out);
		
		getActionMap().put("rotAp", rot_a_pos);
		getActionMap().put("rotAm", rot_a_min);
		getActionMap().put("rotBp", rot_b_pos);
		getActionMap().put("rotBm", rot_b_min);
		getActionMap().put("rotCp", rot_c_pos);
		getActionMap().put("rotCm", rot_c_min);
    }
	
	
	@Override
	public void paint(Graphics g) {
		canv.initPlot();
		
		for (PlotData pdata : plots) {
			if ( plots.size() > 0 ) {
				plotData(canv, pdata);
			}
		}

        //System.out.println(nodes.size());
        
        for (Node node : nodes) {
            drawNode(canv, node);
        }
		
		g.drawImage(canv.getImage(), 20, 20, null);
	
	}

	private void plotData(Canvas canv, PlotData pdata) {
        
		Point2D.Double p1 = null, p2 = null;

		Vector<Vector<Double>> dataset = pdata.data;
		canv.setFGColor(pdata.fgColor);
		Color curPlotColor2 = pdata.fgColor2;

		for (Vector<Double> row : dataset) {
			if (pdata.pltype == PlotData.PlotType.VECTORS) {
				/* For now, it works for vector data in first four columns only */
				if (row.size() >= 4) {
					p1 = canv.getTransformedPoint(new Point2D.Double(row.get(0), row.get(1)));
					p2 = canv.getTransformedPoint(new Point2D.Double(row.get(2), row.get(3)));

					canv.drawVector(p1, p2, curPlotColor2);
				} else {
					System.err.println("Bad vector field data!");
				}
			} else if ((pdata.pltype == PlotData.PlotType.THREED)) {
				//System.out.println("3D");
				if (row.size() >= 3) {
					Point2D.Double pp = p.project(row.get(0), row.get(1), row.get(2));
					p1 = canv.getTransformedPoint(pp);
					canv.drawPoint(p1, PlotData.PointType.SQUARE, pdata.ptX, pdata.ptY);
				} else {
					System.err.println("Data is not three dimensional!");
				}
			} else if (pdata.pltype == PlotData.PlotType.TRLINE) {
				//System.out.println("3D");
				if (row.size() >= 3) {
					Point2D.Double pp = p.project(row.get(0), row.get(1), row.get(2));
					p2 = canv.getTransformedPoint(pp);
					if (p1 != null) {
						canv.setStroke(pdata.ptX);
						canv.drawLine(p1, p2);
					}
					p1 = p2;
				} else {
					System.err.println("Data is not three dimensional!");
				}
			} else {
				//System.out.println(col1 + " " + col2);
				p2 = canv.getTransformedPoint(new Point2D.Double(row.get(col1-1), row.get(col2-1)));
				if (p1 != null) {
					switch(pdata.pltype) {
					case LINES :
						canv.setStroke(pdata.ptX);
						canv.drawLine(p1, p2);
						break;
					case POINTS :
						canv.drawPoint(p1, PlotData.PointType.SQUARE, pdata.ptX, pdata.ptY);
						break;
					case LP :
						Color c = canv.getFGColor();
						Point2D.Double pback = new Point2D.Double( p1.getX() - (pdata.ptX+4)/2, p1.getY() - (pdata.ptY+4)/2 );
						
						canv.setStroke(pdata.ptX);
						canv.drawLine(p1, p2);
						
						canv.setFGColor(Color.BLACK);
						canv.drawPoint(pback, PlotData.PointType.CIRCLE, pdata.ptX+4, pdata.ptY+4);
						canv.setFGColor(c);
					default :
						// Nothing here.
						break;
					}
				}
				p1 = p2;
			}
		}
		
		canv.setStroke(1);
	}


    public void drawNode(Canvas canv, Node n) {
        Color fgc = canv.getFGColor();
        canv.setFGColor(n.col);
        canv.drawPoint(n.pNode, PlotData.PointType.CIRCLE, 3, 3);
        Point2D.Double pText = new Point2D.Double(n.pNode.getX()+2, n.pNode.getY()+2);
        canv.drawText(n.lbl, pText);
        canv.setFGColor(fgc);
    }

    public void addNode(Point2D.Double p, String str, Color c) {
        Node n = new Node(p, str, c);
        nodes.add(n);
    }

	/* Getter and Setters */
	public PlotData.PlotType getCurPlotType() {
		return getCurElement().pltype;
	}
	
	public void setCurPlotType(PlotData.PlotType pltype) {
		getCurElement().pltype = pltype;
		repaint();
		
		JOptionPane.showMessageDialog(this, "Plot Type : " + pltype);
	}
	
	public void setColor(Color c) {
		//System.out.println(plots.toString());
		if ((plots.size() > 0) && (getCurElement() != null)) {
			getCurElement().fgColor = c;
		}
		//System.out.println(getCurElement().fgColor.toString());
		repaint();
		
		JOptionPane.showMessageDialog(this, String.format("Color : (%d, %d, %d)", c.getRed(), c.getGreen(), c.getBlue()));
	}

	public BufferedImage getImage() {
		BufferedImage img = canv.getImage();
		return img;
	}

	private PlotData getCurElement() {
		return curPlot;
	}
	
	public void setCurElement(PlotData pdata) {
		curPlot = pdata;
	}
	
	public Vector<PlotData> getAllElements() {
		return plots;
	}

	public PlotData getData() {
		return getCurElement();
	}

	public void setData(PlotData data) {
		plots.add(data);
		setCurElement(data);
		this.repaint();
	}

	public void setCols(int col1, int col2) {
		this.col1 = col1;
		this.col2 = col2;
	}
	
	public void toggleAxes() {
		canv.setAxesVisible(!canv.isAxesVisible());
		repaint();
	}

	/* Reset canvas */
	public void clear() {
		plots = new Vector<PlotData>();
        nodes = new Vector<Node>();
		col1 = 1;
		col2 = 2;
		
		canv = new Canvas(600, 600);
		canv.initPlot();
		repaint();
	}
	
	public double getMoveAngle() {
		return moveAngle;
	}


	public void setMoveAngle(double moveAngle) {
		this.moveAngle = moveAngle;
	}

	public void setZoomCenter(Point2D.Double zc) {
		canv.setZoomCenter(zc);
		repaint();
	}
	
	public void zoomIn(double zc_x, double zc_y) {
		canv.setZoomCenter(new Point2D.Double(zc_x, zc_y));
		canv.setScaleFactor(canv.getScaleFactor()*2);
		repaint();
	}
	
	public void zoomOut(double zc_x, double zc_y) {
		canv.setZoomCenter(new Point2D.Double(zc_x, zc_y));
        if (canv.getScaleFactor() >= 2) {
            canv.setScaleFactor(canv.getScaleFactor()/2);
        }
		repaint();
	}
	
	/* Actions */
	@SuppressWarnings("serial")
	public class ZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			zoomIn(0,0);
		}
	}
	
	@SuppressWarnings("serial")
	public class ZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			zoomOut(0,0);
		}
	}
	
	@SuppressWarnings("serial")
	public class SmallZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			canv.setScaleFactor(canv.getScaleFactor() + 1);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class SmallZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (canv.getScaleFactor() > 1) {
				canv.setScaleFactor(canv.getScaleFactor() - 1);
			}
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class UpAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("up");
			canv.shift(0, -5);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class DownAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("down");
			canv.shift(0, 5);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class LeftAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("up");
			canv.shift(-5, 0);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RightAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("down");
			canv.shift(5, 0);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotAPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			p.setView(p.a + getMoveAngle(), p.b, p.c);
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotAMinusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			p.setView(p.a - getMoveAngle(), p.b, p.c);
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotBPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			p.setView(p.a, p.b + getMoveAngle(), p.c);
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotBMinusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			p.setView(p.a, p.b - getMoveAngle(), p.c);
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotCPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			p.setView(p.a, p.b, p.c + getMoveAngle());
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotCMinusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			p.setView(p.a, p.b, p.c - getMoveAngle());
		
			repaint();
		}
	}

    public class Node {
        /* A node is a point with a label */
        Point2D.Double pNode;
        String lbl;
        Color col;
        
        public Node(Point2D.Double p, String str, Color c) {
            this.lbl = str;
            this.pNode = p;
            this.col = c;
        }
    }

	public Canvas getCanvas() {
		return canv;
	}


	public void setLogger(StatLogger logger) {
		canv.setLogger(logger);
		p.setLogger(logger);
	}

}
