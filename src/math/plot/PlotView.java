package math.plot;
/*
 * PlotView.java
 * 
 * Copyright 2021 Subhraman Sarkar <subhraman@subhraman-Inspiron>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

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
	
	// Actions
		private LeftAction move_left = new LeftAction();
		private RightAction move_right = new RightAction();
		private UpAction move_up = new UpAction();
		private DownAction move_down = new DownAction();
		private ZoomInAction zoom_in = new ZoomInAction();
		private ZoomOutAction zoom_out = new ZoomOutAction();
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1906949716987184760L;

	public PlotView() {
		clear();
		
		// Setting Keybinding for movement
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "left");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "right");
		getInputMap().put(KeyStroke.getKeyStroke("UP"), "up");
		getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "down");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_J, 0, false), "plus");
		getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_F, 0, false), "minus");

		getActionMap().put("left", move_left);
		getActionMap().put("right", move_right);
		getActionMap().put("up", move_up);
		getActionMap().put("down", move_down);
		getActionMap().put("plus", zoom_in);
		getActionMap().put("minus", zoom_out);
    }
	
	
	@Override
	public void paint(Graphics g) {
		canv.initPlot();
		
		for (PlotData pdata : plots) {
			if ( plots.size() > 0 ) {
				plotData(canv, pdata);
			}
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
			} else {
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
					default :
						// TODO
						break;
					}
				}
				p1 = p2;
			}
		}
		
		canv.setStroke(1);
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
		col1 = 1;
		col2 = 2;
		
		canv = new Canvas(600, 600);
		canv.initPlot();
		repaint();
	}
	
	/* Actions */
	@SuppressWarnings("serial")
	public class ZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			canv.setScaleFactor(canv.getScaleFactor()*2);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class ZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			canv.setScaleFactor(canv.getScaleFactor()/2);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class UpAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("up");
			canv.shift(0, 5);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class DownAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("down");
			canv.shift(0, -5);
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
}

