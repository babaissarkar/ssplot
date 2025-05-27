/*
 * PlotView.java
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

/* 20.07.2021 : Functions drawNode and addNode and class Node added. Function paint() changed to add node drawing.*/

package com.babai.ssplot.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.Objects;
import java.util.Optional;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.KeyStroke;
import javax.swing.Timer;

import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.plot.Plotter;
import com.babai.ssplot.math.plot.Project2D;

public class PlotView extends JLabel implements MouseListener, MouseMotionListener {

	private Vector<PlotData> plots;
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
	private boolean dragOn;
	private int frameCounter;
	private int[] mouseDragStart = {0, 0};
	private Timer refresher;
	/**
	 * 
	 */
	private static final long serialVersionUID = -1906949716987184760L;

	public PlotView(StatLogger logger, Plotter plt) {

		this.plt = plt;
		setLogger(logger);

		overlayMode = false;
		animate = false;
		dragOn = false;

		frameCounter = 0;
		// TODO make the time customizable
		refresher = new Timer(100, e -> updateCanvas());

		clear();

		// Mouse Listener
		addMouseListener(this);
		addMouseMotionListener(this);

		// Setting Keybinding for movement
		// This temporary avoids too many calls to getInputMap()
		var inputMap = getInputMap();
		inputMap.put(KeyStroke.getKeyStroke("LEFT"), "left");
		inputMap.put(KeyStroke.getKeyStroke("RIGHT"), "right");
		inputMap.put(KeyStroke.getKeyStroke("UP"), "up");
		inputMap.put(KeyStroke.getKeyStroke("DOWN"), "down");
		inputMap.put(KeyStroke.getKeyStroke("J"), "plus");
		inputMap.put(KeyStroke.getKeyStroke("F"), "minus");
		inputMap.put(KeyStroke.getKeyStroke("H"), "splus");
		inputMap.put(KeyStroke.getKeyStroke("G"), "sminus");
		inputMap.put(KeyStroke.getKeyStroke("Q"), "rotAp");
		inputMap.put(KeyStroke.getKeyStroke("A"), "rotAm");
		inputMap.put(KeyStroke.getKeyStroke("W"), "rotBp");
		inputMap.put(KeyStroke.getKeyStroke("S"), "rotBm");
		inputMap.put(KeyStroke.getKeyStroke("E"), "rotCp");
		inputMap.put(KeyStroke.getKeyStroke("D"), "rotCm");

		// This temporary avoids too many calls to getActionMap()
		var actionMap = getActionMap();
		actionMap.put("left", move_left);
		actionMap.put("right", move_right);
		actionMap.put("up", move_up);
		actionMap.put("down", move_down);
		actionMap.put("plus", zoom_in);
		actionMap.put("minus", zoom_out);
		actionMap.put("splus", szoom_in);
		actionMap.put("sminus", szoom_out);
		actionMap.put("rotAp", rot_a_pos);
		actionMap.put("rotAm", rot_a_min);
		actionMap.put("rotBp", rot_b_pos);
		actionMap.put("rotBm", rot_b_min);
		actionMap.put("rotCp", rot_c_pos);
		actionMap.put("rotCm", rot_c_min);
	}

	@Override
	public void paint(Graphics g) {
		plt.clear();
		
		if (overlayMode) {
			for (PlotData pdata : plots) {
				if (plots.size() > 0) {
					plt.plotData(pdata);
					plt.plotOthers(pdata);
				}
			} 
		} else if (animate) {
			Optional<PlotData> optCurPlot = getCurPlot();
			if ((plots.size() > 0) && (frameCounter > 0) && optCurPlot.isPresent()) {
				if (frameCounter >= optCurPlot.get().getData().size()) {
					frameCounter = 0;
				}
				var pdata = optCurPlot.get().splice(0, frameCounter);
				plt.plotData(pdata);
				plt.plotOthers(pdata);
			}
		} else {
			Optional<PlotData> optCurPlot = getCurPlot();
			if ((plots.size() > 0) && optCurPlot.isPresent()) {
				var pdata = optCurPlot.get();
				plt.plotData(pdata);
				plt.plotOthers(pdata);
			}
		}

		g.drawImage(plt.getImage(), 20, 20, null);
	}

	private void updateCanvas() {
		frameCounter++;
		repaint();
	}

	/*** Get current plots ***/
	public Optional<PlotData> getCurPlot() {
		return (plots.size() > 0) ? Optional.of(plots.lastElement()) : Optional.empty();
	}

	public void setCurPlot(PlotData data) {
		Objects.requireNonNull(data, "(setCurPlot) : null PlotData");
		plots.add(data);
		fit();
	}

	/** Gets current plot type */
	public Optional<PlotData.PlotType> getCurPlotType() {
		Optional<PlotData> optCurPlot = getCurPlot();
		return (optCurPlot.isPresent())
				? Optional.of(optCurPlot.get().getPltype())
						: Optional.empty();
	}

	public void setCurPlotType(PlotData.PlotType pltype) {
		Optional<PlotData> optCurPlot = getCurPlot();

		if (optCurPlot.isPresent()) {
			optCurPlot.get().setPltype(pltype);
			repaint();
			log("<b>Plot Type : </b>" + pltype);
		}
	}

	public void setColor(Color col) {
		Optional<PlotData> optCurPlot = getCurPlot();

		if (optCurPlot.isPresent()) {
			optCurPlot.get().setFgColor(col);
			repaint();
			Color c = optCurPlot.get().getFgColor();
			log(String.format("Color : (%d, %d, %d)", c.getRed(), c.getGreen(), c.getBlue()));
		}
	}

	/* Fit to plot size */
	public void fit() {
		if (getCurPlot().isPresent()) {
			plt.fit(getCurPlot().get());
			repaint();
		}
	}

	/* Reset canvas */
	public void clear() {
		plots = new Vector<PlotData>();
		//curPlot = null;
		//int i = 0;
		plt.initPlot();
		repaint();
	}

	/* Resize canvas */
	public void resize(int w, int h) {
		plt.initPlot(w, h);
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
	public class ZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.zoomIn(0,0);
			repaint();
		}
	}

	public class ZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.zoomOut(0,0);
			repaint();
		}
	}

	public class SmallZoomInAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.rescale(plt.getScale() + 1);
			repaint();
		}
	}

	public class SmallZoomOutAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			if (plt.getScale() > 1) {
				plt.rescale(plt.getScale() - 1);
			}
			repaint();
		}
	}

	public class UpAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("up");
			plt.getCanvas().shift(0, -5);
			repaint();
		}
	}

	public class DownAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("down");
			plt.getCanvas().shift(0, 5);
			repaint();
		}
	}

	public class LeftAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("up");
			plt.getCanvas().shift(5, 0);
			repaint();
		}
	}

	public class RightAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//System.out.println("down");
			plt.getCanvas().shift(-5, 0);
			repaint();
		}
	}

	public class RotAPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			//p.setView(p.a + getMoveAngle(), p.b, p.c);
			plt.moveView(Project2D.Axis.X);

			repaint();
		}
	}

	public class RotAMinusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.NX);

			repaint();
		}
	}

	public class RotBPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.Y);

			repaint();
		}
	}

	public class RotBMinusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.NY);
			repaint();
		}
	}

	public class RotCPlusAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			plt.moveView(Project2D.Axis.Z);

			repaint();
		}
	}

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
		frameCounter = 0;
		refresher.start();
		log("<b>Animate :</b> " + "ON");
	}

	public void stopAnimation() {
		animate = false;
		frameCounter = 0;
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
		dragOn = true;
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {}

	@Override
	public void mouseClicked(MouseEvent arg0) {}

	@Override
	public void mouseEntered(MouseEvent arg0) {}

	@Override
	public void mouseExited(MouseEvent arg0) {}

	@Override
	public void mousePressed(MouseEvent ev) {
		this.mouseDragStart[0] = ev.getX();
		this.mouseDragStart[1] = ev.getY();

		// FIXME origin is a magic number
		if (ev.getButton() != MouseEvent.BUTTON1) {
			/* The plotting area starts from (20,20) in java graphics space,
			 * so we are substracting it. */
			Point2D.Double clickedAt = new Point2D.Double(ev.getX() - 20, ev.getY() - 20);
			Point2D.Double p = plt.getCanvas().getInvTransformedPoint(clickedAt);
			//			String label = String.format("(%3.1f, %3.1f)", p.getX(), p.getY());
			String label = p.toString();

			if (ev.getButton() == MouseEvent.BUTTON3) {
				//pv.addNode(new Point2D.Double(x-20, y-20), label, Color.BLUE);
				log("Point : " + label);
			} else if (ev.getButton() == MouseEvent.BUTTON2) {
				plt.setZoomCenter(p);
				log("Zoom Center set at " + label);
			}

			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent mout) {
		if (dragOn) {
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

	/*
	pv.addMouseWheelListener(
		new MouseWheelListener() {
			public void mouseWheelMoved(MouseWheelEvent ev) {
				int x = ev.getX()-20;
				int y = ev.getY()-20;

				Point2D.Double p = pv.getCanvas().getInvTransformedPoint(new Point2D.Double(x, y));

				if (ev.getWheelRotation() < 0) {
					pv.zoomIn(p.getX(), p.getY());
				} else if (ev.getWheelRotation() > 0) {
					pv.zoomOut(p.getX(), p.getY());
				}
			}
		}
		);
	 */

}

