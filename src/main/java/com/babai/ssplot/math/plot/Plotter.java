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

import com.babai.ssplot.util.InfoLogger;

public final class Plotter {
	private final Project2D p;
	private final InfoLogger logger;
	private Canvas canv;
	
	public static final int DEFAULT_W = 450, DEFAULT_H = 450;
	
	public Plotter(InfoLogger logger) {
		this.logger = logger;
		p = new Project2D();
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
	
	public void plot(PlotData data) {
		plot(data, data.getRowCount());
	}
	
	public void plot(PlotData data, int lastIndex) {
		plotData(data, lastIndex);
		plotOthers(data);
	}
	
	/* If you don't set the size of the plot, it uses the default size.
	 * It will also initialize the plot with default size if you forget. */
	private void plotData(PlotData pdata, int lastIndex) {
		if (canv == null) {
			initPlot(DEFAULT_W, DEFAULT_H);
		}
		
		var dataCols = new ArrayList<Integer>();
		for (int i = 0; i < pdata.getColumnCount(); i++) {
			dataCols.add(pdata.getDataCol(i));
		}
		
		Point2D.Double p1 = null, p2 = null;

		var dataset = pdata.getData();
		if (lastIndex > dataset.length) {
			lastIndex = dataset.length;
		}
		
		canv.setFGColor(pdata.getFgColor());
		canv.setAxes3d(pdata.getColumnCount() == 3);

		for (int i = 0; i < lastIndex; i++) {
			var row = dataset[i];
			
			switch(pdata.getPlotType()) {
				case VFIELD -> {
					// For now, it works for vector data in first four columns only
					// TODO custom column mapping
					if (row.length >= 4) {
						p1 = canv.getTransformedPoint(new Point2D.Double(row[0], row[1]));
						p2 = canv.getTransformedPoint(new Point2D.Double(row[2], row[3]));
	
						canv.drawVector(p1, p2, pdata.getFgColor2());
					} else {
						System.err.println("Bad vector field data!");
					}
				}
					
				case POINTS3 -> {
					if (row.length >= 3) {
						canv.setProjection(p);
						canv.drawPoint(getPoint3D(row, dataCols, p, 0, 1, 2), PlotData.PointType.SQUARE, pdata.ptX, pdata.ptY);
					} else {
						System.err.println("Data is not three dimensional!");
					}
				}
					
				case LINES3 -> {
					if (row.length >= 3) {
						p2 = getPoint3D(row, dataCols, p, 0, 1, 2);
						if (p1 != null) {
							canv.setProjection(p);
							canv.setStroke(pdata.ptX);
							canv.drawLine(p1, p2);
						}
						p1 = p2;
					} else {
						System.err.println("Data is not three dimensional!");
					}
				}
					
				case POINTS, LINES, LINES_POINTS -> {
					p2 = getPoint2D(row, dataCols, 0, 1);
					
					if (p1 != null) {
						switch(pdata.getPlotType()) {
							case LINES -> {
								canv.setStroke(pdata.ptX);
								canv.drawLine(p1, p2);
							}
							
							case POINTS -> canv.drawPoint(p1, PlotData.PointType.SQUARE, pdata.ptX, pdata.ptY);
							
							case LINES_POINTS -> {
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
							}
							
							default -> {}
						}
					}
					
					p1 = p2;
				}
			}
		}
		
		canv.setStroke(1);
	}
	
	private void plotOthers(PlotData pdata) {
		if (pdata.getTitle() != null) {
			canv.drawTitle(pdata.getTitle());
		}

		for (Node node : pdata.getNodes()) {
			canv.drawNode(node);
		}
	}

	private Point2D.Double getPoint3D(double[] row, ArrayList<Integer> dataCols, Project2D projector,
			int i, int j, int k)
	{
		return projector.project(row[dataCols.get(i)], row[dataCols.get(j)], row[dataCols.get(k)]);
	}

	private Point2D.Double getPoint2D(double[] row, ArrayList<Integer> dataCols, int c1, int c2) {
		return canv.getTransformedPoint(new Point2D.Double(row[dataCols.get(c1)], row[dataCols.get(c2)]));
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
	
	public void moveView(Project2D.RotationAxis axis) {
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
