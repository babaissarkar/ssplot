package math.plot;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Vector;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Plotter {
	private Canvas canv;
	private final Project2D p;
	private final StatLogger logger;
	private static final int DEFAULT_W = 600, DEFAULT_H = 600;
	private int c1, c2;
	
	public Plotter(StatLogger logger) {
		this.logger = logger;
		p = new Project2D(logger);
		c1 = 1;
		c2 = 2;
	}
	
	public void initPlot() {
		initPlot(DEFAULT_W, DEFAULT_H);
	}
	
	public void initPlot(int W, int H) {
		canv = new Canvas(W, H, logger);
		canv.initPlot();
	}
	
	public void clear() {
		canv.initPlot();
	}
	
	/** If you don't set the size of the plot, it uses the default size.
	 * It will also initialize the plot if you forget. */
	public void plotData(PlotData pdata) {
		if (canv == null) {
			initPlot();
		}
		
		c1 = pdata.getDataCol1();
		c2 = pdata.getDataCol2();
		plotData(canv, pdata);
	}
	
	private void plotData(Canvas canv, PlotData pdata) {
		plotData(canv, pdata, c1, c2);
	}
	
	private void plotData(Canvas canv, PlotData pdata, int col1, int col2) {
        
		Point2D.Double p1 = null, p2 = null;

		Vector<Vector<Double>> dataset = pdata.data;
		canv.setFGColor(pdata.getFgColor());
		Color curPlotColor2 = pdata.getFgColor2();

		for (Vector<Double> row : dataset) {
			if (pdata.getPltype() == PlotData.PlotType.VECTORS) {
				/* For now, it works for vector data in first four columns only */
				if (row.size() >= 4) {
					p1 = canv.getTransformedPoint(new Point2D.Double(row.get(0), row.get(1)));
					p2 = canv.getTransformedPoint(new Point2D.Double(row.get(2), row.get(3)));

					canv.drawVector(p1, p2, curPlotColor2);
				} else {
					System.err.println("Bad vector field data!");
				}
			} else if (pdata.getPltype() == PlotData.PlotType.THREED) {
				//System.out.println("3D");
				if (row.size() >= 3) {
					Point2D.Double pp = p.project(row.get(0), row.get(1), row.get(2));
					p1 = canv.getTransformedPoint(pp);
					canv.drawPoint(p1, PlotData.PointType.SQUARE, pdata.ptX, pdata.ptY);
				} else {
					System.err.println("Data is not three dimensional!");
				}
			} else if (pdata.getPltype() == PlotData.PlotType.TRLINE) {
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
				
				p2 = canv.getTransformedPoint(new Point2D.Double(row.get(col1-1), row.get(col2-1)));
				//System.out.println("c1 " + c1 + " c2 " + c2);
				//logger.log("From plotData : " + " Col1 : " + col1 + " Col2 : " + col2 + "\n");
				if (p1 != null) {
					switch(pdata.getPltype()) {
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

		for (Node node : pdata.nodes) {
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
	
	public void rescale(double factor) {
		canv.setScaleFactor(factor);
	}
	
	public double getScale() {
		return canv.getScaleFactor();
	}
	
	public void moveView(Project2D.Axis axis) {
		p.moveView(axis);
	}
	
	public Canvas getCanvas() {
		return canv;
	}

    public void save(File outfile) {
        try {
            ImageIO.write(getCanvas().getImage(), "png", outfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
