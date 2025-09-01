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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

import com.babai.ssplot.math.system.core.EquationSystem;

/**
 * A data class that contains information about the plot
 * including the data, the plot properties and the system
 * of equations.
 */
public class PlotData implements Cloneable {
	public enum PlotType {
		LINES("Lines", "X", "Y"),
		POINTS("Points", "X", "Y"),
		LP("Both Lines and Points", "X", "Y"),
		
		VECTORS("3D Points", "X", "Y", "Z"), 
		THREED("3D Lines", "X", "Y", "Z"),
		
		TRLINE("Vector field", "X", "Y");
		
		private final String type;
		private final Vector<String> axes;

		PlotType(String type, String... axes) {
			this.type = type;
			this.axes = new Vector<String>();
			for (var axis : axes) {
				this.axes.add(axis);
			}
		}
		
		public Vector<String> axes() {
			return axes;
		}
		
		public int dim() {
			return axes.size();
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
	
	private PlotType pltype;
	private PointType pttype;
	
	// TODO make this private
	// TODO double-check if ptX is circle radius
	/**
	 * Width and height of the plotted point's marker
	 * If Circle, ptX is the circle's radius, ptY is ignored
	 * If Square, ptX and ptY are the width and height of the square
	 */
	public int ptX, ptY;
	
	private HashMap<String, String> axesLabels = new HashMap<>();
	private HashMap<String, Integer> axesDataColumns = new HashMap<>();
	
	private Color fgColor, fgColor2;
	private String title;
	
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
		for (var row : this.data.subList(from, to)) {
			pdata.data.add(new Vector<>(row)); // copy inner vectors too
		}
		return pdata;
	}

	public PlotData() {
		this(new Vector<Vector<Double>>());
	}

	public PlotData(Vector<Vector<Double>> extData) {
		data = extData;
		nodes = new Vector<Node>();
		
		// Plot Type
		pltype = PlotType.LINES;
		
		//  Axes properties
		setDataCols(0, 1);
		
		// Cosmetic point properties
		pttype = PointType.SQUARE;
		ptX = 2; ptY = 2;
		
		// Cosmetic plot properties
		title = "New Data " + System.nanoTime();
		fgColor = Color.RED;
		fgColor2 = Color.BLUE;
	}
	
	public int getRowCount() {
		return data.size();
	}
	
	public int getColumnCount() {
		return getRowCount() > 0 ? data.firstElement().size() : 0;
	}
	
	// Can return null
	public void getAxisLabel(String axisName) {
		this.axesLabels.get(axisName);
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
	 * @return the index of the data column corresponding to axis with `axisName`.
	 */
	public int getDataCol(String axisName) {
		return axesDataColumns.getOrDefault(axisName, pltype.axes().indexOf(axisName));
	}
	
	/**
	 * Sets which data column is associated with which axis
	 * 
	 * @param dataCols: the dataCols to set. An array of column indices.
	 * Column with index `dataCols[0]` will be associated with X axis, and so on.
	 */
	public void setDataCols(int... dataCols) {
		var axes = pltype.axes();
		for (int i = 0; i < dataCols.length; i++) {
			axesDataColumns.put(axes.get(i), dataCols[i]);
		}
	}
	
	public HashMap<String, Integer> getDataColMapping() {
		return this.axesDataColumns;
	}

	/**
	 * @param dataCol     index of a column
	 * 
	 * @return            maximum value among all data in the given column
	 */
	public double getMax(int dataCol) {
		return Collections.max(getColumn(dataCol));
	}

	/**
	 * @param dataCol     index of a column
	 * 
	 * @return            min value among all data in the given column
	 */
	public double getMin(int dataCol) {
		return Collections.min(getColumn(dataCol));
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
	
	public Vector<Double> getColumn(int i) {
		Vector<Double> colData = new Vector<>();
		for (var row : data) { // assuming `data` is your Vector<Vector<Double>>
			if (i < row.size()) colData.add(row.get(i));
		}
		return colData;
	}

	public String info() {
		var buff = new StringBuilder();
		if (getSystem() != null) {
			buff.append(getSystem().toString()).append("\n");
		}
		var mappings = getDataColMapping();
		for (int i = 0; i < getColumnCount(); i++) {
			boolean isKnownColumn = false;
			for (var entry : mappings.entrySet()) {
				if (entry.getValue() == i) {
					buff.append(formatStats(entry.getKey(), i));
					isKnownColumn = true;
					break;
				}
			}
			if (!isKnownColumn) {
				buff.append(formatStats("Col " + i, i));
			}
		}
		return buff.toString();
	}

	private String formatStats(String colName, int i) {
		Vector<Double> colData = getColumn(i);
		return String.format(
				"""
				%s Data:
					Count=%d, Min=%.4f, Max=%.4f,
					Mean=%.4f, Var=%.4f, SD=%.4f,
					Skew=%.4f, Kurt=%.4f,
					Q1=%.4f, Median=%.4f, Q3=%.4f
				""",
				colName,
				colData.size(),
				Collections.min(colData),
				Collections.max(colData),
				Stats.mean(colData),
				Stats.variance(colData),
				Stats.stdDev(colData),
				Stats.skewness(colData),
				Stats.kurtosis(colData),
				Stats.quartile(colData, 0.25),
				Stats.quartile(colData, 0.5),
				Stats.quartile(colData, 0.75));
	}

}
