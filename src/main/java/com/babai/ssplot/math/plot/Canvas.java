/*
 * Canvas.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Canvas {
	private final static Font titleFont = new Font("Inter", Font.BOLD, 22);
	
	private int W, H; /* Size of image */
	private boolean axesVisible = true;
	private boolean axes3d = false;
	private int curNoTics = 10;
	private BufferedImage img; /* The image */
	private Graphics2D g;
	private Color fgColor, bgColor, axesColor, ticColor, titleColor;
	private Color borderColor;

	/* Transformation Params */
	private double scaleFactor;
	private int dx, dy, moveX, moveY;
	private Point2D.Double zc = new Point2D.Double(0, 0); /* Center of Zoom */
	private String xlbl, ylbl;
	private Project2D projector;
	
	/*
	 * This only sets properties of the plot
	 * but does not start drawing. */
	public Canvas() {
		this.projector = new Project2D();
		scaleFactor = 1.0;
		dx = 0; dy = 0;
		moveX = 0; moveY = 0;
		ticColor = Color.BLACK;
		fgColor = Color.BLACK;
		bgColor = Color.WHITE;
		axesColor = Color.BLACK;
		titleColor = Color.BLACK;
		borderColor = Color.BLUE;
	}

	/**
	 * Initializes the plot by creating an empty plotting area.
	 * You need to initialize the Canvas first by calling its constructor.
	 */
	public void initPlot(int W, int H) {
		g = initImage(W, H);
		g.setColor(bgColor);
		g.fill(new Rectangle2D.Double(0, 0, W, H));
		g.setColor(fgColor);

		resetAxes();

		if (isAxesVisible()) {
			shiftAxes(W/2,H/2);
			if (!isAxes3d()) {
				drawAxes();
				drawTics(curNoTics);
			} else {
				drawAxes3D();
			}
		}

		if (isBoundingBoxVisible()) {
			drawBoundingBox();
		}
	}
	
	public void refresh() {
		initPlot(this.W, this.H);
	}
	
	private Graphics2D initImage(int W, int H) {
		this.W = W;
		this.H = H;
		img = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = img.createGraphics();
		RenderingHints rh = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHints(rh);
		return g2;
	}

	public Graphics2D getGraphics() {
		return g;
	}

	public BufferedImage getImage() {
		return img;
	}

	public void dispose() {
		g.dispose();
	}



	/*********************************** Drawing Methods ************************************************/

	/* Draws a point, at the specified point and of the specified type.*/
	public void drawPoint(Point2D.Double p, PlotData.PointType ptype, double sizeX, double sizeY) {
		if (ptype == PlotData.PointType.SQUARE) {
			g.fill(new Rectangle2D.Double(p.x, p.y, sizeX, sizeY));
		} else if (ptype == PlotData.PointType.CIRCLE) {
			g.fill(new Ellipse2D.Double(p.getX(), p.getY(), sizeX, sizeY));
		}
	}

	/** Draws a line from point q1 to point q2 */
	public void drawLine(Point2D.Double q10, Point2D.Double q20) {
		g.draw(new Line2D.Double(q10, q20));

	}

	/** Draws an vector, by drawing a line with a marker.*/
	public void drawVector(Point2D.Double q1, Point2D.Double q2, Color tipCol) {
		Color curPlotColor = g.getColor();
		g.setColor(tipCol);
		g.draw(new Ellipse2D.Double(q1.x - 1, q1.y - 1, 2, 2));
		g.setColor(curPlotColor);
		drawArrowTip(q1, q2);
		drawLine(q1, q2);
		g.setStroke(new BasicStroke(1f));
	}

	private void drawArrowTip(Point2D.Double q1, Point2D.Double q2) {
		double tipLen = (q1.distance(q2))/10;
		double tipAngle = Math.toRadians(15);

		double arrowAngle = Math.atan2((q2.y - q1.y), (q2.x - q1.x));

		double tipEndX1 = q2.x - tipLen * Math.cos(tipAngle - arrowAngle);
		double tipEndY1 = q2.y + tipLen * Math.sin(tipAngle - arrowAngle);
		Point2D.Double tipEnd1 = new Point2D.Double(tipEndX1, tipEndY1);
		g.draw(new Line2D.Double(q2, tipEnd1));

		double tipEndX2 = q2.x - tipLen * Math.cos(-tipAngle - arrowAngle);
		double tipEndY2 = q2.y + tipLen * Math.sin(-tipAngle - arrowAngle);
		Point2D.Double tipEnd2 = new Point2D.Double(tipEndX2, tipEndY2);
		g.draw(new Line2D.Double(q2, tipEnd2));
	}

	/** Write text at a specific point */
	public void drawText(String str, Point2D.Double p) {
		g.drawString(str, (int) p.x, (int) p.y);
	}

	/********************** Complex drawing methods ***************************************/
	/* Draws a box around the plot */
	public void drawBoundingBox() {
		int strokeWidth = 1;

		Color curColor = g.getColor();
		g.setColor(borderColor);

		g.drawLine(0, 0, 0, H);
		
		// Correction for finite thickness of the line
		g.drawLine(0, H-strokeWidth, W, H-strokeWidth);
		g.drawLine(W-strokeWidth, H, W-strokeWidth, 0);
		g.drawLine(W, 0, 0, 0);

		g.setColor(curColor);
	}

	/*********************************** Plot Specific Drawing Methods ************************************************/
	/** Draw the X and Y axes */
	public void drawAxes() {
		Color curColor = g.getColor();
		g.setColor(axesColor);

		// X Axis
		g.drawLine(0, dy-moveY, W, dy-moveY);
		// Y Axis
		g.drawLine(dx+moveX, 0, dx+moveX, H);

		g.setColor(curColor);
	}
	
	public void drawAxes3D() {
		final var xAxisColor = Color.RED;
		final var yAxisColor = Color.BLUE;
		final var zAxisColor = new Color(4, 121, 0);
		
		Point2D.Double p1 = null, p2 = null;
		
		var oldFgColor = getFGColor();
		
		// draw rotated axis
		setStroke(2);
		
		setFGColor(xAxisColor);
		Point2D.Double pp1 = projector.project(-225, 0, 0);
		p1 = cartesianToJava(pp1);
		Point2D.Double pp2 = projector.project(225, 0, 0);
		p2 = cartesianToJava(pp2);
		drawLine(p1, p2);
		
		setFGColor(yAxisColor);
		pp1 = projector.project(0, 225, 0);
		p1 = cartesianToJava(pp1);
		pp2 = projector.project(0, -225, 0);
		p2 = cartesianToJava(pp2);
		drawLine(p1, p2);
		
		setFGColor(zAxisColor);
		pp1 = projector.project(0, 0, 225);
		p1 = cartesianToJava(pp1);
		pp2 = projector.project(0, 0, -225);
		p2 = cartesianToJava(pp2);
		drawLine(p1, p2);
		
		setFGColor(oldFgColor);
	}

	/* Draw tics along the axes */
	public void drawTics(int noOfMajorTics) {
		Color curColor = g.getColor();
		g.setColor(ticColor);

		FontMetrics m = g.getFontMetrics();
		int strHeight = m.getHeight();

		// When the screen is shifted, the axes need to be redrawn
		int offsetTicsX = (int) ((moveX*noOfMajorTics)/(W*scaleFactor));
		int offsetTicsY = (int) ((moveY*noOfMajorTics)/(H*scaleFactor));

		for (int i = (-(noOfMajorTics/2 - 1) - offsetTicsX - (int) scaleFactor);
				i < (noOfMajorTics/2 - offsetTicsX + (int) scaleFactor); i++) {
			// X axis tics
			int x =  i * W/noOfMajorTics;
			double lbl = x/scaleFactor;
			String strLbl = getLabelFromDouble(lbl);
			int strWidth = m.stringWidth(strLbl);

			drawLine(cartesianToJavaUnscaled(x, 0), cartesianToJavaUnscaled(x, -5));

			if (i != 0) {
				drawText(strLbl, cartesianToJavaUnscaled(x - (strWidth/2 + 2), -(strHeight+2)));
			}

		}

		// draw X Label
		if (xlbl != null) {
			drawText(xlbl, cartesianToJavaUnscaled(50, 5));
		}

		for (int j = (-(noOfMajorTics/2 - 1) - offsetTicsY - (int) scaleFactor); j < (noOfMajorTics/2 - offsetTicsY + (int) scaleFactor); j++) {
			// Y axis tics
			int y = j * H/noOfMajorTics;
			double lbl = y/scaleFactor;
			String strLbl = getLabelFromDouble(lbl);
			int strWidth = m.stringWidth(strLbl);

			drawLine(cartesianToJavaUnscaled(0, y), cartesianToJavaUnscaled(-5, y));

			if (j != 0) {
				drawText(strLbl, cartesianToJavaUnscaled(-(strWidth + 8), y - (strHeight / 2 - 2)));
			} 
		}

		// draw Y Label
		if (ylbl != null) {
			Font f = g.getFont();
			AffineTransform trans = new AffineTransform();
			trans.setToIdentity();
			trans.rotate(Math.toRadians(-90));
			Font f2 = f.deriveFont(trans);
			g.setFont(f2);
			drawText(ylbl, cartesianToJavaUnscaled(15, 50));
			g.setFont(f);
		}

		g.setColor(curColor);
	}

	private String getLabelFromDouble(double lbl) {
		// Tic labels in scientific notation (ie. 1e13)
		// Different scale factors for X and Y axes
		String strLbl;
		if ((Math.abs(lbl) >= 0.1) && (Math.abs(lbl) < 10)) {
			strLbl = String.format("%2.1f", lbl);
		} else if ((Math.abs(lbl) >= 10) && (Math.abs(lbl) < 1000)) {
			strLbl = String.format("%3.0f", lbl);
		} else {
			strLbl = String.format("%2.1e", lbl);
		}
		return strLbl;
	}

	/** Plot Specific! */
	/* Add a title to the plot */
	public void drawTitle(String title) {
		/* Draw the title in top center */
		Font prevFont = g.getFont();
		Color prevColor = g.getColor();

		g.setFont(titleFont);
		FontMetrics fm = g.getFontMetrics();
		double textH = fm.getHeight();
		double textW = fm.stringWidth(title);

		Point2D.Double titlePos = new Point2D.Double(20, 30);

		g.setColor(bgColor);
		g.fill(new Rectangle2D.Double(titlePos.x, titlePos.y - textH, textW + 2, textH + 2));
		g.setColor(titleColor);
		drawText(title, titlePos);

		g.setColor(prevColor);
		g.setFont(prevFont);
	}


	/*********************************** Property Getters/Setters ************************************************/

	public void setStroke(int width) {
		g.setStroke(new BasicStroke(width));
	}

	public void setFGColor(Color c) {
		fgColor = c;
		g.setColor(c);
	}

	public Color getFGColor() {
		return fgColor;
	}

	public void setBGColor(Color c) {
		bgColor = c;
	}

	public Color getBGColor() {
		return bgColor;
	}

	public double getScaleFactor() {
		return scaleFactor;
	}

	public void setScaleFactor(double scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	public boolean isAxesVisible() {
		return axesVisible;
	}

	public void setAxesVisible(boolean axesVisible) {
		this.axesVisible = axesVisible;
	}

	public boolean isBoundingBoxVisible() {
		return true;
	}

	public void setBoundingBoxVisible() {};

	public boolean isAxes3d() {
		return axes3d;
	}

	public void setAxes3d(boolean axes3d) {
		this.axes3d = axes3d;
	}

	public void toggleAxes() {
		setAxesVisible(!isAxesVisible());
	}

	public int getPlotWidth() {
		return this.W;
	}

	public int getPlotHeight() {
		return this.H;
	}

	/*********************************** Transformation Methods **************************************************/
	/* Shifts the axes */
	public void shiftAxes(int x, int y) {
		dx = x;
		dy = y;
	}

	/* Sets the axes at x=0 and y=0 */
	public void resetAxes() {
		dx = 0;
		dy = 0;
	}

	public void setZoom(double zoom) {
		scaleFactor = zoom;
	}

	public void setZoomCenter(Point2D.Double zc) {
		this.zc = zc;
	}

	public void shift(int i, int j) {
		moveX += i;
		moveY += j;
	}

	/*********************************** Helper Methods **************************************************/

	// NOTE: these mutate argument (passed point) to avoid allocating new Point2D.Double
	
	/**
	 * Transforms from Cartesian space to Java Graphics space.
	 * Takes care of scaling and translation
	 */
	public Point2D.Double cartesianToJava(Point2D.Double p) {
		double sF = 1 - scaleFactor;
		p.x = scaleFactor*p.x + sF*zc.x + dx + moveX;
		p.y = H - (scaleFactor*p.y + sF*zc.y + dy + moveY);
		return p;
	}

	private Point2D.Double cartesianToJavaUnscaled(double x, double y) {
		double x1 = x + dx + moveX;
		double y1 = H - (y + dy + moveY);
		return new Point2D.Double(x1, y1);
	}

	/* Transforms from Java Graphics space to Cartesian space. */
	/* Takes care of scaling and translation */
	// FIXME does not work with non Zero Zoom Center
	public Point2D.Double javaToCartesian(Point2D.Double p1) {
		p1.x = (p1.x - dx - moveX)/scaleFactor;
		p1.y = ((H - p1.y) - dy - moveY)/scaleFactor;
		return p1;
	}


	/* Converts point in polar form to cartesian form.*/
	public Point2D.Double polarToCartesian(double r, double theta) {
		double x = r * Math.cos(theta) + dx;
		double y = r * Math.sin(theta) - dy;
		Point2D.Double p = cartesianToJava(new Point2D.Double(x, y));
		return p;
	}

	/************************************ 3D *********************************************/
	/**Set the projector from 3d to 2d. */
	public void setProjection(Project2D p) {
		this.projector = p;
	}

	/********************* Nodes *******************************/
	public void drawNode(Node n) {
		Color fgc = getFGColor();
		setFGColor(n.c());
		drawPoint(n.p(), PlotData.PointType.CIRCLE, 3, 3);
		Point2D.Double pText = new Point2D.Double(n.p().x + 2, n.p().y + 2);
		drawText(n.str(), pText);
		setFGColor(fgc);
	}


	/********************** Getters and Setters ******************/
	
	//TODO odd public api: we have setX/YLabel but drawTitle!
	
	public void setXLabel(String xlbl) {
		this.xlbl = xlbl;
	}

	public void setYLabel(String ylbl) {
		this.ylbl = ylbl;
	}
	
	/**
	 * Set the axis color to the given color
	 * @param axesColor the new axis color 
	 */
	public void setAxesColor(Color axesColor) {
		this.axesColor = axesColor;
	}

	/**
	 * @param ticColor sets the color of the tics on the axes to `ticColor`.
	 */
	public void setTicColor(Color ticColor) {
		this.ticColor = ticColor;
	}

	/**
	 * @param titleColor sets the color of plot title
	 */
	public void setTitleColor(Color titleColor) {
		this.titleColor = titleColor;
	}	
	
}

