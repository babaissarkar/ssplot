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

package com.babai.ssplot.math.plot;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Vector;

import com.babai.ssplot.math.system.core.EquationSystem;


/**
 * A data class that contains information about the plot
 * including the data, the plot properties and the system
 * of equations.
 */
public class PlotData implements Cloneable {
	public enum PlotType {
		LINES("Lines"),
		POINTS("Points"),
		LP("Both Lines and Points"),
		VECTORS("3D Points"), 
		THREED("3D Lines"), 
		TRLINE("Vector field");
		
		private final String type;

		PlotType(String type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			return this.type;
		}
	};
	
	public enum PointType { SQUARE, CIRCLE };

	private Vector<Vector<Double>> data;
	private Vector<Node> nodes;
	private EquationSystem system;
	
	private PointType pttype;
	
	// TODO make this private
	// TODO double-check if ptX is circle radius
	/**
	 * Width and height of the plotted point's marker
	 * If Circle, ptX is the circle's radius, ptY is ignored
	 * If Square, ptX and ptY are the width and height of the square
	 */
	public int ptX, ptY;
	
	private int dataCol1, dataCol2;
	private PlotType pltype;
	private Color fgColor, fgColor2;

	private String title;
	private String xlabel, ylabel;
	
	// Sliences an exception in splice
	@Override
	public Object clone() {
		try {
			return super.clone(); // shallow clone
		} catch (CloneNotSupportedException e) {
			throw new AssertionError(); // Should never happen since we implement Cloneable
		}
	}
	
	/**
	 * Creates a new {@code PlotData} instance containing a subset of the data rows
	 * from the current instance, from index {@code from} (inclusive) to index {@code to} (exclusive).
	 * 
	 * <p>The method clones the original object, then replaces its data with a deep copy
	 * of the specified sublist of rows. Each inner vector of doubles is also copied
	 * to ensure data isolation.</p>
	 * 
	 * <p>If {@code from} is greater than {@code to}, an {@link IllegalArgumentException} is thrown.</p>
	 * 
	 * <p>Indices are clamped to valid ranges: {@code from} is set to at least 0,
	 * and {@code to} is capped at the current data size.</p>
	 * 
	 * @param from the start index (inclusive) of the rows to splice
	 * @param to the end index (exclusive) of the rows to splice
	 * @return a new {@code PlotData} instance containing the spliced data rows
	 * @throws IllegalArgumentException if {@code from} &gt; {@code to}
	 */
	public PlotData splice(int from, int to) {
		if (from > to) {
			throw new IllegalArgumentException(
				"Invalid splice range: from (" + from + ") must be less than to (" + to + ")");
		}
		
		from = Math.max(0, from);
		to = Math.min(this.data.size(), to);

		var pdata = (PlotData) this.clone();
		pdata.data = new Vector<>();
		for (Vector<Double> row : this.data.subList(from, to)) {
			pdata.data.add(new Vector<>(row)); // copy inner vectors too
		}
		return pdata;
	}


	public PlotData() {
		this(new Vector<Vector<Double>>());
	}

	public PlotData(Vector<Vector<Double>> extData) {
		nodes = new Vector<Node>();
		data = extData;
		pltype = PlotType.LINES;
		setPointType(PointType.SQUARE);
		ptX = 2; ptY = 2;
		fgColor = Color.RED;
		fgColor2 = Color.BLUE;
		setDataCols(1, 2);
		title = "New Data " + System.nanoTime();
	}
	
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
	 * @return the first active data column's index
	 */
	public int getDataCol1() {
		return dataCol1;
	}

	/**
	 * @return the second active data column's index
	 */
	public int getDataCol2() {
		return dataCol2;
	}

	/**
	 * Set the active data columns
	 * 
	 * @param dataCol1, dataCol2 : the dataCols to set
	 */
	public void setDataCols(int dataCol1, int dataCol2) {
		this.dataCol1 = dataCol1;
		this.dataCol2 = dataCol2;
	}

	/**
	 * @param dataCol     index of a column
	 * 
	 * @return            max value among all data in the given column
	 */
	public double getMax(int dataCol) {
		double max = this.data.get(0).get(dataCol);
		for (var row : this.data) {
			max = row.get(dataCol) > max ? row.get(dataCol) : max; 
		}
		return max;
	}

	/**
	 * @param dataCol     index of a column
	 * 
	 * @return            min value among all data in the given column
	 */
	public double getMin(int dataCol) {
		double min = this.data.get(0).get(dataCol);
		for (var row : this.data) {
			min = row.get(dataCol) < min ? row.get(dataCol) : min; 
		}
		return min;
	}

	/**
	 * @return the data
	 */
	public Vector<Vector<Double>> getData() {
		return data;
	}

	/**
	 * @return the nodes
	 */
	public Vector<Node> getNodes() {
		return nodes;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(Vector<Vector<Double>> data) {
		this.data = data;
	}

	/**
	 * @param nodes the nodes to set
	 */
	public void setNodes(Vector<Node> nodes) {
		this.nodes = nodes;
	}

	/**
	 * @return the PointType
	 */
	public PointType getPointType() {
		return pttype;
	}

	/**
	 * @param pttype the PointType to set
	 */
	public void setPointType(PointType pttype) {
		this.pttype = pttype;
	}

	/**
	 * @return the system of equations
	 */
	public EquationSystem getSystem() {
		return system;
	}

	/**
	 * Sets the system of equations
	 * @param system the system to set
	 */
	public void setSystem(EquationSystem system) {
		this.system = system;
	}
}
