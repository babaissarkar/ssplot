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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

public class PlotView extends JLabel implements MouseListener, MouseMotionListener {
	
	private Vector<PlotData> plots;
	//private PlotData curPlot;
//	private int col1, col2;
	//private Canvas canv;
	private Plotter plt;
	private StatLogger logger;
	
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
	
	
	private boolean overlayMode;
	private boolean animate;
	int i;
	private Timer refresher;
	private int[] mouseDragStart;
	private boolean dragOn;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1906949716987184760L;

	public PlotView(StatLogger logger, Plotter plt2) {
		
		this.plt = plt2;
		this.setLogger(logger);
		//curPlot = null;
		overlayMode = false;
		animate = false;
		i = 0;
		mouseDragStart = new int[2];
		Arrays.fill(mouseDragStart, 0);
		dragOn = false;
		
		ActionListener trigger = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				updateCanvas();
			}
		};
		refresher = new Timer(1000, trigger);
		
		clear();
		
		// Mouse Listener
		addMouseListener(this);
		addMouseMotionListener(this);
		
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
		
		if (overlayMode) {
			plt.clear();
			for (PlotData pdata : plots) {
				if (plots.size() > 0) {
					plt.plotData(pdata);
					plt.plotOthers(pdata);
				}
			} 
		} else if (animate) {
			plt.clear();
			
			if ((plots.size() > 0) && (i > 0)) {
				
				PlotData pdata = new PlotData(
						new Vector<Vector<Double>>(
								getCurPlot().data.subList(0, i)));
				
				if (i < getCurPlot().data.size()) {
					//log("t = " + i + " sec.");
					plt.plotData(pdata);
					plt.plotOthers(pdata);
				} else {
					animate = false;
					i = 0;
					refresher.stop();
				}
			}
		} else {
			plt.clear();
			if (plots.size() > 0) {
				plt.plotData(getCurPlot());
				plt.plotOthers(getCurPlot());
			}
		}
		
		
		
//		if (curPlot != null) {
//			plt.plotData(curPlot);
//			plt.plotOthers(curPlot);
//		}
		g.drawImage(plt.getImage(), 20, 20, null);
	}
	
	private void updateCanvas() {
		i++;
		repaint();
	}
	
	/*** Getting plots ***/
	public PlotData getCurPlot() {
		if (plots.size() > 0) {
			return plots.lastElement();
		} else {
			return null;
		}
	}
	
	public void setCurPlot(PlotData data) {
		//curPlot = data;
		plots.add(data);
		repaint();
	}
	
	/* Getter and Setters */
	public PlotData.PlotType getCurPlotType() {
		return getCurPlot().getPltype();
	}
	
	public void setCurPlotType(PlotData.PlotType pltype) {
		getCurPlot().setPltype(pltype);
		repaint();
		
		log("<b>Plot Type : </b>" + pltype);
	}
	
	public void setColor(Color col) {
		getCurPlot().setFgColor(col);
		repaint();
		
		Color c = getCurPlot().getFgColor();
		log(String.format("Color : (%d, %d, %d)", c.getRed(), c.getGreen(), c.getBlue()));
	}

	/* Reset canvas */
	public void clear() {
		plots = new Vector<PlotData>();
		//curPlot = null;
		//int i = 0;
		plt.initPlot();
		repaint();
	}

	public void log(String s) {
		logger.log(s);
	}
	
	public void setLogger(StatLogger logger) {
		this.logger = logger;
		//plt.setLogger(logger);
	}
	
	/* Actions */
	@SuppressWarnings("serial")
	public class ZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.zoomIn(0,0);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class ZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.zoomOut(0,0);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class SmallZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.rescale(plt.getScale() + 1);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class SmallZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (plt.getScale() > 1) {
				plt.rescale(plt.getScale() - 1);
			}
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class UpAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("up");
			plt.getCanvas().shift(0, -5);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class DownAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("down");
			plt.getCanvas().shift(0, 5);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class LeftAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("up");
			plt.getCanvas().shift(5, 0);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RightAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("down");
			plt.getCanvas().shift(-5, 0);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotAPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//p.setView(p.a + getMoveAngle(), p.b, p.c);
			plt.moveView(Project2D.Axis.X);
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotAMinusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.NX);
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotBPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.Y);
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotBMinusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.NY);
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotCPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.Z);
			
			repaint();
		}
	}
	
	@SuppressWarnings("serial")
	public class RotCMinusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.NZ);
		
			repaint();
		}
	}

	public void toggleOverlayMode() {
		stopAnimation();
		overlayMode = !overlayMode;
		
		log("<b>Overlay mode :</b> " + overlayMode);
	}
	
	public void toggleAnimate() {		
		if (animate) {
			stopAnimation();
		} else {
			startAnimation();
		}
	}
	
	public void startAnimation() {
		animate = true;
		refresher.start();
		log("<b>Animate :</b> " + "ON");
	}
	
	public void stopAnimation() {
		animate = false;
		refresher.stop();
		log("<b>Animate :</b> " + "OFF");
	}
	
	public void setNormal() {
		if (overlayMode) {
			toggleOverlayMode();
		}
		stopAnimation();
	}

	@Override
	public void mouseDragged(MouseEvent mout) {
//		logger.log("Mouse Dragged to : " + mout.getX() + ", " + mout.getY());
		dragOn = true;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent min) {
		this.mouseDragStart[0] = min.getX();
		this.mouseDragStart[1] = min.getY();
//		logger.log("Mouse Pressed : " + this.mouseDragStart[0] + ", " + this.mouseDragStart[1]);
	}

	@Override
	public void mouseReleased(MouseEvent mout) {
		if (dragOn) {
//			logger.log("Mouse Released to : " + mout.getX() + ", " + mout.getY());
			//Rudimentary view rotation using a mouse
			int x2 = mout.getX();
			int y2 = mout.getY();
			int x1 = this.mouseDragStart[0];
			int y1 = this.mouseDragStart[1];
			
			int dx = x2-x1;
			int dy = y2-y1;
			
			plt.setViewMoveAngle(30.0);
			
			if (dx > dy) {
				if (dx > 0) {
					plt.moveView(Project2D.Axis.Y);
				} else {
					plt.moveView(Project2D.Axis.NY);
				}
			} else {
				if (dy > 0) {
					plt.moveView(Project2D.Axis.X);
				} else {
					plt.moveView(Project2D.Axis.NX);
				}
			}
			
			plt.setViewMoveAngle(10.0);
			
			repaint();
			
		}
		
		dragOn = false;
	}

}

