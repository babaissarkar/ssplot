/*
 * PlotData.java
 * 
 * Copyright 2021-2024 Subhraman Sarkar <suvrax@gmail.com>
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

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Vector;

public class PlotData {
    /* This class is the Model.*/
    public enum PlotType { LINES, POINTS, LP, VECTORS, THREED, TRLINE };
    public enum PointType { SQUARE, CIRCLE };
    
    public Vector<Vector<Double>> data;
    public Vector<Node> nodes;
    private int dataCol1, dataCol2;
    private PlotType pltype;
    private Color fgColor, fgColor2;
    
    public PointType pttype;
    public int ptX, ptY;
    
    private String title;
	private String xlabel, ylabel;
	
	public SystemData sysData;

	public String getXLabel() {
		return xlabel;
	}

	public void setXLabel(String xlabel) {
		this.xlabel = xlabel;
	}

	public String getYLabel() {
		return ylabel;
	}

	public void setYLabel(String ylabel) {
		this.ylabel = ylabel;
	}

    public PlotData() {
        this(new Vector<Vector<Double>>());
    }

    public PlotData(Vector<Vector<Double>> extData) {
    	nodes = new Vector<Node>();
    	sysData = new SystemData();
        data = extData;
        pltype = PlotType.LINES;
        pttype = PointType.SQUARE;
        ptX = 2; ptY = 2;
        fgColor = Color.RED;
        fgColor2 = Color.BLUE;
        setDataCols(1, 2);
        title = "New Data " + System.nanoTime();
    }
    
    public void setTitle(String title) {
    	this.title = title;
    }
    
    public String getTitle() {
    	return this.title;
    }
    
    public void setFgColor(Color c) {
    	this.fgColor = (c != null) ? c : Color.RED;
    }
    
    public Color getFgColor() {
		return fgColor;
	}

	public void setFgColor2(Color fgColor2) {
		this.fgColor2 = (fgColor2 != null) ? fgColor2 : Color.BLUE;
	}
	
	public Color getFgColor2() {
		return fgColor2;
	}

	public void setPltype(PlotType pltype) {
		this.pltype = pltype;
	}
	
	public PlotType getPltype() {
		return pltype;
	}

	public void addNode(Point2D.Double p, String str, Color c) {
        Node n = new Node(p, str, c);
        nodes.add(n);
    }

	/**
	 * @return the dataCol1
	 */
	public int getDataCol1() {
		return dataCol1;
	}
	
	/**
	 * @return the dataCol2
	 */
	public int getDataCol2() {
		return dataCol2;
	}

	/**
	 * @param dataCol1, dataCol2 : the dataCols to set
	 */
	public void setDataCols(int dataCol1, int dataCol2) {
		this.dataCol1 = dataCol1;
		this.dataCol2 = dataCol2;
	}
	
	/** @return max value among all data in a column */
	public double getMax(int dataCol) {
		double max = this.data.get(0).get(dataCol);
		for (var row : this.data) {
			max = row.get(dataCol) > max ? row.get(dataCol) : max; 
		}
		return max;
	}
	
	/** @return min value among all data in a column */
	public double getMin(int dataCol) {
		double min = this.data.get(0).get(dataCol);
		for (var row : this.data) {
			min = row.get(dataCol) < min ? row.get(dataCol) : min; 
		}
		return min;
	}
}
