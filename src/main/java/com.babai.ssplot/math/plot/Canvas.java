/*
 * Canvas.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.*;
import java.awt.image.BufferedImage;

public class Canvas {
    /*This is the view helper*/
    private int W, H; /* Size of image */
    private BufferedImage img; /* The image */
    private Graphics2D g;
    private Color fgColor, bgColor, axesColor, titleColor;
    private boolean axesVisible = true;
    private boolean axes3d = false;
    public int curNoTics = 10;

    /* Transformation Params */
    private double scaleFactor;
    private int dx, dy, moveX, moveY;
    private Point2D.Double zc = new Point2D.Double(0, 0); /* Center of Zoom */
	private StatLogger logger;
    private String xlbl, ylbl;
	private Project2D project;

    public void setXLabel(String xlbl) {
        this.xlbl = xlbl;
    }

    public void setYLabel(String ylbl) {
        this.ylbl = ylbl;
    }

    private void initParams() {
        scaleFactor = 1.0;
        dx = 0; dy = 0;
        moveX = 0; moveY = 0;
        fgColor = Color.BLACK;
        bgColor = Color.WHITE;
        axesColor = Color.BLACK;
        titleColor = Color.BLACK;
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

    public Canvas(int W, int H, StatLogger logger) {
    	this.logger = logger;
    	this.project = new Project2D(this.logger);
        initParams();
        g = initImage(W, H);
    }
    
    /** Initializes the plot by creating an empty plotting area.
     * You need to initialize the Canvas first by calling its constructor. */
    public void initPlot() {
    	g.setColor(bgColor);
    	g.fill(new Rectangle2D.Double(0, 0, W, H));
    	g.setColor(fgColor);
    	//zc = new Point2D.Double(0, 0);
    	//zc = getInvTransformedPoint(new Point2D.Double(W/2, H/2));
    	//System.out.println(zc.toString());
    	resetAxes();
    	
    	if (isAxesVisible()) {
    		if (isAxes3d()) {
//    			Point2D.Double shTrP = project.project(W/2, H/2, 0);
//    			Point2D.Double shTrP2 = getInvTransformedPoint(project.projectInv(W/2, H/2, 0));
//    			System.out.format("%d, %d, %d -> %f, %f\n", W/2, H/2, 0, shTrP.x, shTrP.y);
//    			shiftAxes((int) shTrP.x, (int) shTrP.y);
    			shiftAxes(W/2,H/2);
//    			drawAxes3D();
    		} else {
    			shiftAxes(W/2,H/2);
    			drawAxes();
    		}
    		drawTics(curNoTics);
    	}

    	drawBoundingBox();
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

    /* Draws a line from point q1 to point q2 */
	public void drawLine(Point2D.Double q10, Point2D.Double q20) {
//		Color curPlotColor = g.getColor();
        g.draw(new Line2D.Double(q10, q20));
        
	}

    /* Draws an vector, by drawing a line with a marker.*/
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
	
	/* Write text at a specific point */
	public void drawText(String str, Point2D.Double p) {
		int x = (int) p.x;
		int y = (int) p.y;
		
		g.drawString(str, x, y);
	}
	
/********************** Complex drawing methods ***************************************/
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

/*********************************** Plot Specific Drawing Methods ************************************************/
    /* Draw the X and Y axes */
	public void drawAxes() {
		Color curColor = g.getColor();
		g.setColor(axesColor);

        g.drawLine(dx+moveX, 0, dx+moveX, W);
        g.drawLine(0, dy-moveY, H, dy-moveY);
		g.setColor(curColor);
	}

    /* Draw tics along the axes */
	public void drawTics(int noOfMajorTics) {
        Color curColor = g.getColor();
		g.setColor(Color.BLACK);
        
        FontMetrics m = g.getFontMetrics();
        int strHeight = m.getHeight();
        
        // When the screen is shifted, the axes need to be redrawn
        int offsetTicsX = (int) ((moveX*noOfMajorTics)/(W*scaleFactor));
        int offsetTicsY = (int) ((moveY*noOfMajorTics)/(H*scaleFactor));
        //System.out.println(offsetTics);
        
        for (int i = (-(noOfMajorTics/2 - 1) - offsetTicsX - (int) scaleFactor); i < (noOfMajorTics/2 - offsetTicsX + (int) scaleFactor); i++) {
            // X axis tics
            int x =  i * W/noOfMajorTics;
            double lbl = x/scaleFactor;
            String strLbl = getLabelFromDouble(lbl);
            int strWidth = m.stringWidth(strLbl);
            
            drawLine(
            		getTransformedPoint2(new Point2D.Double(x, 0)),
            		getTransformedPoint2(new Point2D.Double(x, -5))
            		);
            
            if (i != 0) {
            	drawText(strLbl, getTransformedPoint2(new Point2D.Double(x- (strWidth/2 + 2), -(strHeight+2))));
            }
            
        }

        // draw X Label
        if (xlbl != null) {
            drawText(xlbl, getTransformedPoint2(new Point2D.Double(50, 5)));
        }

        for (int j = (-(noOfMajorTics/2 - 1) - offsetTicsY - (int) scaleFactor); j < (noOfMajorTics/2 - offsetTicsY + (int) scaleFactor); j++) {
			// Y axis tics
			int y = j * H/noOfMajorTics;
			double lbl = y/scaleFactor;
			String strLbl = getLabelFromDouble(lbl);
			int strWidth = m.stringWidth(strLbl);
			
			drawLine(
					getTransformedPoint2(new Point2D.Double(0, y)),
					getTransformedPoint2(new Point2D.Double(-5, y))
					);
			
			if (j != 0) {
				drawText(strLbl, getTransformedPoint2(new Point2D.Double(-(strWidth + 8), y - (strHeight / 2 - 2))));
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
            drawText(ylbl, getTransformedPoint2(new Point2D.Double(15, 50)));
            g.setFont(f);
        }
        //g.rotate(-90);
        
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
		Font f = new Font("Serif", Font.BOLD, 22);
		Font prevFont = g.getFont();
		Color prevColor = g.getColor();
		
		g.setFont(f);
		FontMetrics fm = g.getFontMetrics();
		double textH = fm.getHeight();
		double textW = fm.stringWidth(title);
		
		Point2D.Double p = new Point2D.Double(20, 30); /* Need to be changed. (20,30) is Magic no. */
        logger.log("" + H/2);
		
		g.setColor(Color.WHITE);
		g.fill(new Rectangle2D.Double(p.x, p.y - textH, textW + 2, textH + 2));
		//g.setColor(prevColor);
		g.setColor(titleColor);
		drawText(title, p);
		
		Point2D.Double p2 = getInvTransformedPoint(p);
		logger.log(String.format("Added title \"%s\" at (%6.2f, %6.2f)", title, p2.x, p2.y));
		
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
		//System.out.println("Zoom : " + getScaleFactor() + " x");
		log("Zoom : " + getScaleFactor() + " x");
	}

	public boolean isAxesVisible() {
		return axesVisible;
	}

	public void setAxesVisible(boolean axesVisible) {
		this.axesVisible = axesVisible;
	}
	
	public boolean isAxes3d() {
		return axes3d;
	}
	
	public void setAxes3d(boolean axes3d) {
		this.axes3d = axes3d;
	}
	
	public void toggleAxes() {
		setAxesVisible(!isAxesVisible());
		if (isAxesVisible()) {
			log("Axes visible.");
		} else {
			log("Axes hidden.");
		}
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
		//moveX += i*scaleFactor;
		//moveY -= j*scaleFactor;
        moveX += i;
        moveY += j;
        String out = String.format("dx = %d, dy = %d\n", moveX, -moveY);
        log(out);
	}

/*********************************** Helper Methods **************************************************/

    /* Transforms from Cartesian space to Java Graphics space. */
    /* Takes care of scaling and translation */
	public Point2D.Double getTransformedPoint(Point2D.Double p) {
        double x = p.x;
        double y = p.y;
		double x1 = scaleFactor*(x-zc.getX()) + dx + moveX + zc.getX();
		double y1 = H - (scaleFactor*(y-zc.getY()) + dy + moveY + zc.getY());
//      double x1 = scaleFactor*x + dx + moveX;
//      double y1 = H - (scaleFactor*y + dy + moveY);
		return new Point2D.Double(x1, y1);
	}
	
	private Point2D.Double getTransformedPoint2(Point2D.Double p) {
        double x = p.x;
        double y = p.y;
		double x1 = x + dx + moveX;
		double y1 = H - (y + dy + moveY);
		return new Point2D.Double(x1, y1);
	}

    /* Transforms from Java Graphics space to Cartesian space. */
    /* Takes care of scaling and translation */
    public Point2D.Double getInvTransformedPoint(Point2D.Double p1) {
        double x1 = p1.x;
        double y1 = p1.y;
        double x = (x1 - dx - moveX)/scaleFactor;
        double y = ((H - y1) - dy - moveY)/scaleFactor;
        return new Point2D.Double(x, y);
    }

    
	/* Converts point in polar form to cartesian form.*/
	public Point2D.Double getCartersianPoint(double r, double theta) {
		double x = r * Math.cos(theta) + dx;
		double y = r * Math.sin(theta) - dy;
		Point2D.Double p = getTransformedPoint(new Point2D.Double(x, y));
		return p;
	}
	
	private void log(String string) {
		this.logger.log(string + "\n");
		//System.out.println("log : " + string);
	}
	
/************************************ 3D *********************************************/
	/**Set the projector from 3d to 2d. */
	public void setProjection(Project2D p) {
		this.project = p;
	}

/********************* Nodes *******************************/
	public void drawNode(Node n) {
        Color fgc = getFGColor();
        setFGColor(n.col);
        drawPoint(n.pNode, PlotData.PointType.CIRCLE, 3, 3);
        Point2D.Double pText = new Point2D.Double(n.pNode.getX()+2, n.pNode.getY()+2);
        drawText(n.lbl, pText);
        setFGColor(fgc);
    }
	
	public void drawAxes3D() {
		Color curColor = g.getColor();
		g.setColor(axesColor);
		
		// Origin Shift
		Point2D.Double shTrP = project.project(W/2, H/2, 0);
		double shX = -(shTrP.x - W/2);
		double shY = -(shTrP.y - H/2);
		
//		Point2D.Double px1 = project.projectInv(dx+moveX, 0, 0);
//		Point2D.Double px2 = project.projectInv(dx+moveX, W, 0);
//		Point2D.Double py1 = project.projectInv(0, dy-moveY, 0);
//		Point2D.Double py2 = project.projectInv(H, dy-moveY, 0);
		Point2D.Double px1 = project.projectInv(0, 0, 0);
		Point2D.Double px2 = project.projectInv(0, W, 0);
		Point2D.Double py1 = project.projectInv(0, 0, 0);
		Point2D.Double py2 = project.projectInv(H, 0, 0);
		px1 = new Point2D.Double(px1.x+dx+moveX+shX, px1.y+shY);
		px2 = new Point2D.Double(px2.x+dx+moveX+shX, px2.y+shY);
		py1 = new Point2D.Double(py1.x+shX, py1.y+dy-moveY+shY);
		py2 = new Point2D.Double(py2.x+shX, py2.y+dy-moveY+shY);
		
		drawLine(px1, px2);
		drawLine(py1, py2);
		
		g.setColor(curColor);
	}
	
}

