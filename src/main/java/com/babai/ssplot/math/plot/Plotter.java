/*
 * Plotter.java
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

package com.babai.ssplot.math.plot;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Vector;
import com.babai.ssplot.util.InfoLogger;

public final class Plotter {
	private final Project2D p;
	private final InfoLogger logger;
	private Canvas canv;
	
	public static final int DEFAULT_W = 450, DEFAULT_H = 450;
	
	public Plotter(InfoLogger logger) {
		this.logger = logger;
		p = new Project2D(logger);
		initPlot();
	}
	
	public void initPlot() {
		initPlot(DEFAULT_W, DEFAULT_H);
	}
	
	public void initPlot(int W, int H) {
		p.setView(0, 0, 0);
		if (canv == null) {
			canv = new Canvas(logger);
		}
		canv.initPlot(W, H);
	}
	
	public void clear() {
		canv.refresh();
	}
	
	/* If you don't set the size of the plot, it uses the default size.
	 * It will also initialize the plot if you forget. */
	public void plotData(PlotData pdata) {
		if (canv == null) {
			initPlot();
		}
		
		plotData(canv, pdata);
	}
	
	private void plotData(Canvas canv, PlotData pdata) {
		var dataCols = new ArrayList<Integer>(pdata.getDataColMapping().values());
		Point2D.Double p1 = null, p2 = null;

		Vector<Vector<Double>> dataset = pdata.getData();
		canv.setFGColor(pdata.getFgColor());
		canv.setAxes3d(pdata.getPlotType().dim() == 3);
		clear();

		for (Vector<Double> row : dataset) {
			switch(pdata.getPlotType()) {
			case VFIELD:
				/* For now, it works for vector data in first four columns only */
				if (row.size() >= 4) {
					p1 = canv.getTransformedPoint(new Point2D.Double(row.get(dataCols.get(0)), row.get(dataCols.get(1))));
					p2 = canv.getTransformedPoint(new Point2D.Double(row.get(dataCols.get(2)), row.get(dataCols.get(3))));

					canv.drawVector(p1, p2, pdata.getFgColor2());
				} else {
					System.err.println("Bad vector field data!");
				}
				break;
				
			case LINES3:
				if (row.size() >= 3) {
					Point2D.Double pp = p.project(row.get(dataCols.get(0)), row.get(dataCols.get(1)), row.get(dataCols.get(2)));
					p1 = canv.getTransformedPoint(pp);
					canv.setProjection(p);
					canv.drawPoint(p1, PlotData.PointType.SQUARE, pdata.ptX, pdata.ptY);
				} else {
					System.err.println("Data is not three dimensional!");
				}
				break;
				
			case POINTS3:
				if (row.size() >= 3) {
					Point2D.Double pp = p.project(row.get(dataCols.get(0)), row.get(dataCols.get(1)), row.get(dataCols.get(2)));
					p2 = canv.getTransformedPoint(pp);
					if (p1 != null) {
						canv.setProjection(p);
						canv.setStroke(pdata.ptX);
						canv.drawLine(p1, p2);
					}
					p1 = p2;
				} else {
					System.err.println("Data is not three dimensional!");
				}
				break;
				
			default:
				canv.setAxes3d(false);
				p2 = canv.getTransformedPoint(
					new Point2D.Double(row.get(dataCols.get(0)), row.get(dataCols.get(1))));
				if (p1 != null) {
					switch(pdata.getPlotType()) {
					case LINES :
						canv.setStroke(pdata.ptX);
						canv.drawLine(p1, p2);
						break;
					case POINTS :
						canv.drawPoint(p1, PlotData.PointType.SQUARE, pdata.ptX, pdata.ptY);
						break;
					case LINES_POINTS :
						Color c = canv.getFGColor();
						Point2D.Double pback = new Point2D.Double(
							p1.getX() - (pdata.ptX+4)/2,
							p1.getY() - (pdata.ptY+4)/2);
						// The line is drawn with plot FGcolor 1
						// the points on the top is drawn with plot FGcolor 2
						// FIXME generalization needed
						canv.setStroke(pdata.ptX);
						canv.drawLine(p1, p2);
						
						canv.setFGColor(pdata.getFgColor2());
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

	public void plotPoint(Point2D.Double p0, int ptX, int ptY) {
		if (canv == null) {
			initPlot();
		}
		canv.drawPoint(canv.getTransformedPoint(p0), PlotData.PointType.SQUARE, ptX, ptY);
	}

	public void plotOthers(PlotData pdata) {
		if (pdata.getTitle() != null) {
			canv.drawTitle(pdata.getTitle());
		}

		for (Node node : pdata.getNodes()) {
			canv.drawNode(node);
		}
	}

	public BufferedImage getImage() {
		return canv.getImage();
	}

	public void toggleAxes() {
		canv.toggleAxes();
	}
	
	public void setZoomCenter(Point2D.Double zc) {
		canv.setZoomCenter(zc);
	}
	
	public void zoomIn(double zc_x, double zc_y) {
		canv.setZoomCenter(new Point2D.Double(zc_x, zc_y));
		canv.setScaleFactor(canv.getScaleFactor()*2);
	}
	
	public void zoomOut(double zc_x, double zc_y) {
		canv.setZoomCenter(new Point2D.Double(zc_x, zc_y));
		if (canv.getScaleFactor() >= 2) {
			canv.setScaleFactor(canv.getScaleFactor()/2);
		}
	}
	
	/* Scale the plot by the give factor */
	public void rescale(double factor) {
		canv.setScaleFactor(factor);
	}
	
	/**  Scale plot to minimize empty area */
	// TODO update method so that it works for 3d case
	public void fit(PlotData pdata) {
		double Xmax = pdata.getMax(0);
		double Xmin = pdata.getMin(0);
		double Ymax = pdata.getMax(1);
		double Ymin = pdata.getMin(1);
		double maxWidth = Math.max(Ymax-Ymin, Xmax - Xmin);
		canv.setScaleFactor(canv.getPlotWidth()/maxWidth);
	}
	
	public double getScale() {
		return canv.getScaleFactor();
	}
	
	public void setViewMoveAngle(double th) {
		p.setMoveAngle(Math.toRadians(th));
	}
	
	public void moveView(Project2D.Axis axis) {
		p.moveView(axis);
	}
	
	public void setXLabel(String label) {
		canv.setXLabel(label);
	}
	
	public void setYLabel(String label) {
		canv.setYLabel(label);
	}
	
	public void setFgColor(Color fgColor) {
		canv.setFGColor(fgColor);
	}

	public void setBgColor(Color bgcolor) {
		canv.setBGColor(bgcolor);
	}

	public void setAxisColor(Color axisColor) {
		canv.setAxesColor(axisColor);
	}
	
	public void setTicColor(Color ticColor) {
		canv.setTicColor(ticColor);
	}

	public void setTitleColor(Color titleColor) {
		canv.setTitleColor(titleColor);
	}

	public void shift(int dx, int dy) {
		canv.shift(dx, dy);
	}
	
	/** Converts from internal Java coordinates to Cartesian coordinates */
	public Point2D.Double toCartesianPoint(Point2D.Double internalPoint) {
		return canv.getInvTransformedPoint(internalPoint);
	}
}
