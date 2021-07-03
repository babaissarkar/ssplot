package math.plot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

//import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
//import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
//import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/*
 * PlotModel.java
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


public class Canvas {
    /*This is the view helper*/
    private int W, H; /* Size of image */
    private BufferedImage img; /* The image */
    private Graphics2D g;
    public Color fgColor, bgColor;
    public double theta, phi, psi;
    private boolean axesVisible = true;
    public int curNoTics = 10;

    /* Transformation Params */
    private double scaleFactor;
    private int dx, dy, moveX, moveY;

    private void initParams() {
        scaleFactor = 1.0;
        dx = 0; dy = 0;
        moveX = 0; moveY = 0;
        fgColor = Color.BLACK;
        bgColor = Color.WHITE;
        theta = 0;
        phi = 0;
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

    public Canvas(int W, int H) {
        initParams();
        g = initImage(W, H);
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
    
    public void initPlot() {
    	g.setColor(bgColor);
    	g.fill(new Rectangle2D.Double(0, 0, W, H));
    	g.setColor(fgColor);
		resetAxes();
		shiftAxes(W/2,H/2);
		if (axesVisible) {
			drawAxes();
			drawTics(curNoTics);
		}
		
		drawBoundingBox();
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

    /* Draws a line from point q1 to point q2 */
	public void drawLine(Point2D.Double q10, Point2D.Double q20) {
        //Point2D.Double q1 = getTransformedPoint(q10);
        //Point2D.Double q2 = getTransformedPoint(q20);
		//Stroke s = g.getStroke();
        g.draw(new Line2D.Double(q10, q20));
        //g.setStroke(s);
	}

    /* Draws an vector, by drawing a line with a marker.*/
	public void drawVector(Point2D.Double q1, Point2D.Double q2, Color tipCol) {
        //Point2D.Double q1 = getTransformedPoint(q10);
        //Point2D.Double q2 = getTransformedPoint(q20);
        Color curPlotColor = g.getColor();
		//g.setStroke(new BasicStroke(1.0f));
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
	
	/* Write text at a specific point */
	public void drawText(String str, Point2D.Double p) {
        //Point2D.Double p = getTransformedPoint(p0);
		g.drawString(str, (int) p.x, (int) p.y);
	}


/*********************************** Auxiliary Drawing Methods ************************************************/
    /* Draw the X and Y axes */
	public void drawAxes() {
		Color curColor = g.getColor();
		g.setColor(Color.BLACK);

        g.drawLine(dx, 0, dx, W);
        g.drawLine(0, dy, H, dy);
		g.setColor(curColor);
	}

    /* Draw tics along the axes */
	public void drawTics(int noOfMajorTics) {
        Color curColor = g.getColor();
		g.setColor(Color.BLACK);
        
        FontMetrics m = g.getFontMetrics();
        
        for (int i = -(noOfMajorTics/2 - 1); i < noOfMajorTics/2; i++) {
            // X axis tics
            int x =  i * W/noOfMajorTics;
            double lbl = x/scaleFactor;
            String strLbl;
            
            if ((Math.abs(lbl) >= 0.1) && (Math.abs(lbl) < 10)) {
                strLbl = String.format("%2.1f", lbl);
            } else if ((Math.abs(lbl) >= 10) && (Math.abs(lbl) < 1000)) {
                strLbl = String.format("%3.0f", lbl);
            } else {
                strLbl = String.format("%2.1e", lbl);
            }
            
            int strWidth = m.stringWidth(strLbl);
            int strHeight = m.getHeight();
            
            g.drawLine(x + dx, W/2, x + dx, W/2 + 5);
            if (i != 0) {
                g.drawString(strLbl, x+dx - strWidth/2 - 2, W/2 + strHeight + 3);

                // Tic labels in scientific notation (ie. 1e13)
                // Different scale factors for X and Y axes
            }
            // Y axis tics
            int y = i * H/noOfMajorTics;
            g.drawLine(H/2 - 5, y + dy, H/2, y + dy);
            if (i != 0) {
                g.drawString(strLbl, H/2 - strWidth - 8, y+dy + strHeight/2 - 2);
            }
        }
        
        g.setColor(curColor);
	}

    /* Draws a box around the plot */
	public void drawBoundingBox() {
        int strokeWidth = 1;
        
        Color curColor = g.getColor();
        g.setColor(Color.BLUE);
        
        g.drawLine(0, 0, 0, H);
        /* Correction for finite thickness of the line */
        g.drawLine(0, H-strokeWidth, W, H-strokeWidth);
        g.drawLine(W-strokeWidth, H, W-strokeWidth, 0);
        g.drawLine(W, 0, 0, 0);

        g.setColor(curColor);
	}


/*********************************** Property Setters ************************************************/

	public void setStroke(int width) {
		g.setStroke(new BasicStroke(width));
	}
	
    public void setFGColor(Color c) {
        fgColor = c;
        g.setColor(c);
    }

    public void setBGColor(Color c) {
        bgColor = c;
    }
    
    public void setViewpor(double theta, double phi) {
    	this.theta = theta;
    	this.phi = phi;
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

/*********************************** Helper Methods **************************************************/

    /* Transforms from Cartesian space to Java Graphics space. */
    /* Takes care of scaling and translation */
	public Point2D.Double getTransformedPoint(Point2D.Double p) {
        double x = p.x;
        double y = p.y;
		double x1 = scaleFactor*x + dx + moveX;
		double y1 = H - (scaleFactor*y + dy + moveY);
//	    System.out.println(x + ", " + y); 
//	    System.out.println(x1 + ", " + y1 + ".");
		return new Point2D.Double(x1, y1);
	}

    /* Transforms from Java Graphics space to Cartesian space. */
    /* Takes care of scaling and translation */
    public Point2D.Double getInvTransformedPoint(Point2D.Double p1) {
        double x1 = p1.x;
        double y1 = p1.y;
        double x = (x1 - dx - moveX)/scaleFactor;
        double y = (H - y1 - dy - moveY)/scaleFactor;
        return new Point2D.Double(x, y);
    }

    
	/* Converts point in polar form to cartesian form.*/
	public Point2D.Double getCartersianPoint(double r, double theta) {
		double x = r * Math.cos(theta) + dx;
		double y = r * Math.sin(theta) - dy;
		Point2D.Double p = getTransformedPoint(new Point2D.Double(x, y));
		return p;
	}
	
	/** 3D */
	
//	public Point2D.Double persectiveProject2D(double x, double y, double z) {
//		Vector3D v = new Vector3D(x, y, z); /* Point to be projected */
//		
//		Rotation rot = new Rotation(new Vector3D(0,0,1), phi, RotationConvention.VECTOR_OPERATOR);
//		Rotation rot2 = new Rotation(new Vector3D(0,1,0), psi, RotationConvention.VECTOR_OPERATOR);
//		Rotation rot3 = new Rotation(new Vector3D(1,0,0), theta, RotationConvention.VECTOR_OPERATOR);
//		
//		Vector3D camPos = new Vector3D(0, 0, 0); /* Position of the Camera */
//		Vector3D camFrmPos = rot.applyTo(
//								rot2.applyTo(
//									rot3.applyTo(
//										v.subtract(camPos))));
//		
//		Vector3D dispSurfPos = new Vector3D(10,10,10);
//		
//		double bx = dispSurfPos.getZ()/camFrmPos.getZ() * camFrmPos.getX() + dispSurfPos.getX();
//		double by = dispSurfPos.getZ()/camFrmPos.getZ() * camFrmPos.getY() + dispSurfPos.getY();
//		return new Point2D.Double(bx, by);
//	}

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

	public void shift(int i, int j) {
		moveX += i;
		moveY -= j;
	}
    
}

