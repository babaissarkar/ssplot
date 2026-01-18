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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Vector;

import com.babai.ssplot.math.system.core.EquationSystem;

/**
 * A data class that contains information about the plot
 * including the data, the plot properties and the system
 * of equations.
 */
public class PlotData implements Cloneable {
	public enum PlotType {
		LINES("2D Lines"),
		POINTS("2D Points"),
		LINES_POINTS("2D Lines with Points"),
		
		POINTS3("3D Points"), 
		LINES3("3D Lines"),
		
		VFIELD("Vector field");
		
		private final String type;

		PlotType(String type) {
			this.type = type;
		}
		
		@Override
		public String toString() {
			return this.type;
		}
	};
	
	// TODO PointTypes should have a method that draws themselves?
	// Also, ptX, ptY could be fields of PointType
	public enum PointType { SQUARE, CIRCLE };

	private double[][] data;
	private List<Node> nodes;
	private EquationSystem system;
	private Axis[] axes = Axis.Cartesian.values();
	
	private PlotType plotType;
	private PointType pointType;
	
	// TODO make this private
	/**
	 * Width and height of the plotted point's marker
	 * If Circle, ptX is the circle's radius, ptY is ignored
	 * If Square, ptX and ptY are the width and height of the square
	 */
	public int ptX, ptY;
	
	private List<String> axesLabels = new ArrayList<String>();
	private HashMap<Integer, Integer> dataColumnMapping = new HashMap<>();
	
	private Color fgColor, fgColor2;
	private String title;
	
	private boolean maxMinComputed = false;
	private double[][] maxMin;

	public PlotData(double[][] extData) {
		data = extData;
		nodes = new ArrayList<Node>();
		
		// Cosmetic point properties
		pointType = PointType.SQUARE;
		ptX = 2; ptY = 2;
		
		// Cosmetic plot properties
		title = "New Data " + System.nanoTime();
		fgColor = Color.RED;
		fgColor2 = Color.BLACK;
	}
	
	// ------------- PROPERTY METHODS -----------------
	// TODO may these should be moved into a helper properties class?
	
	public Optional<String> getAxisLabel(int i) {
		return Optional.ofNullable(i < axesLabels.size() ? axesLabels.get(i) : null);
	}
	public void setAxisLabels(List<String> labels) { this.axesLabels = labels; }
	
	public void setTitle(String title) { this.title = title; }
	public String getTitle() { return this.title; }
	public void setFgColor(Color c) { this.fgColor = (c != null) ? c : Color.RED; }
	public Color getFgColor() { return fgColor; }
	public void setFgColor2(Color c) { this.fgColor2 = (c != null) ? c : Color.BLUE; }
	public Color getFgColor2() { return fgColor2; }
	public void setPlotType(PlotType pltype) { this.plotType = pltype; }
	public PlotType getPlotType() { return plotType; }
	public PointType getPointType() { return pointType; }
	public void setPointType(PointType pttype) { this.pointType = pttype; }
	
	public EquationSystem getSystem() { return system; }
	public void setSystem(EquationSystem system) { this.system = system; }
	
	public Axis[] getAxes() { return axes; }
	public void setAxes(Axis... axes) { this.axes = axes; }


	// ------------- DATA METHODS -----------------
	public double[][] getData() { return data; }
	public void setData(double[][] data) { maxMinComputed = false; this.data = data; }
	public int getRowCount() { return data.length; }
	
	// NOTE: Right now, number of columns of data == number of axes. If that changes
	// we might need a dim() method again. Also note that there might be the case
	// that each column may not correspond to coordinate data, for example see the
	// Vector field case which has row format: x, y, dx, dy
	public int getColumnCount() { return data.length > 0 ? data[0].length : 0; }
	
	/**
	 * @return the index of the data column corresponding to axis with `axisName`.
	 */
	public int getDataCol(Axis axis) {
		for (int i = 0; i < axes.length; i++) {
			if (axes[i].equals(axis)) {
				return dataColumnMapping.get(i);
			}
		}
		throw new IllegalArgumentException("Invalid axis " + axis + " for current plot");
	}
	
	/**
	 * @param index
	 * @return the data column for `index`th axis.
	 */
	public int getDataCol(int index) {
		Integer mappedIndex = dataColumnMapping.get(index);
		if (mappedIndex != null) {
			return mappedIndex;
		}
		throw new IllegalArgumentException("Invalid/unmapped column number " + index + " for current plot");
	}
	
	/**
	 * Defines how input data columns are mapped for plotting.
	 *
	 * This method specifies which columns from the input dataset are used
	 * when constructing the plot. The interpretation depends on the plot type:
	 * <ul>
	 *   <li>For simple 2D/3D plots, the first entries typically correspond
	 *       to coordinate axes (e.g. x, y, z).</li>
	 *   <li>For vector fields or other specialized plots, additional columns
	 *       may represent quantities such as direction components (dx, dy),
	 *       magnitudes, or other attributes.</li>
	 * </ul>
	 *
	 * The array {@code dataCols} provides the sequence of column indices
	 * to be consumed by the plot type. For example:
	 * <pre>
	 *   // Vector field: x, y, dx, dy
	 *   setDataCols(0, 1, 2, 3);
	 * </pre>
	 *
	 * Unlike a strict axis mapping, not every column corresponds directly
	 * to an axis; instead, the meaning of each position in {@code dataCols}
	 * is determined by the plot type, similar to gnuplot's 'using' command.
	 *
	 * @param dataCols an array of column indices, ordered according to the
	 *                 requirements of the current plot type
	 */
	public void setDataCols(int... dataCols) {
		for (int i = 0; i < dataCols.length; i++) {
			dataColumnMapping.put(i, dataCols[i]);
		}
	}
	
	public HashMap<Integer, Integer> getDataColMapping() { return this.dataColumnMapping; }
	
	// NOTE expensive method
	public double[] getColumn(int i) {
		double[] colData = new double[getRowCount()];
		if (i >= getColumnCount()) return colData;
		
		for (int rowIdx = 0; rowIdx < colData.length; rowIdx++) {
			colData[rowIdx] = this.data[rowIdx][i]; 
		}
		return colData;
	}

	/**
	 * @param dataCol     index of a column
	 * @return            maximum value among all data in the given column
	 */
	public double getMax(int dataCol) {
		if (!maxMinComputed) {
			this.maxMin = Stats.parallelMaxMin(data);
			maxMinComputed = true;
		}
		return this.maxMin[0][dataCol];
	}

	/**
	 * @param dataCol     index of a column
	 * @return            min value among all data in the given column
	 */
	public double getMin(int dataCol) {
		if (!maxMinComputed) {
			this.maxMin = Stats.parallelMaxMin(data);
			maxMinComputed = true;
		}
		return this.maxMin[1][dataCol];
	}
	
	
	// ------------------ NODE METHODS -------------------
	public void addNode(Point2D.Double p, String str, Color c) { nodes.add(new Node(p, str, c)); }
	public List<Node> getNodes() { return nodes; }
	public void setNodes(List<Node> nodes) { this.nodes = nodes; }
	

	// ------------------- STRING REPRESENTATION --------------
	public String info() {
		var buff = new StringBuilder();
		if (getSystem() != null) {
			buff.append(getSystem().toString()).append("\n");
		}
		
		var headers = getHeaders();
		for (int i = 0; i < getColumnCount(); i++) {
			buff.append(formatStats(headers.get(i), i));
		}
		return buff.toString();
	}

	private String formatStats(String colName, int i) {
		double[] colData = getColumn(i);
		return String.format(
				"""
				%s:
					Count=%d, Min=%.4f, Max=%.4f,
					Mean=%.4f, Var=%.4f, SD=%.4f,
					Skew=%.4f, Kurt=%.4f,
					Q1=%.4f, Median=%.4f, Q3=%.4f
				""",
				colName,
				getRowCount(),
				getMin(i),
				getMax(i),
				Stats.mean(colData),
				Stats.variance(colData),
				Stats.stdDev(colData),
				Stats.skewness(colData),
				Stats.kurtosis(colData),
				Stats.quartile(colData, 0.25),
				Stats.quartile(colData, 0.5),
				Stats.quartile(colData, 0.75));
	}

	public Vector<String> getHeaders() {
		var headers = new Vector<String>();
		var mappings = getDataColMapping();
		var axes = getAxes();
		for (int i = 0; i < getColumnCount(); i++) {
			boolean isKnownColumn = false;
			for (var entry : mappings.entrySet()) {
				var lbl = getAxisLabel(i);
				if (lbl.isPresent() || (entry.getValue() == i && i < axes.length)) {
					headers.add(lbl.orElse(axes[i].toString() + " Data"));
					isKnownColumn = true;
					break;
				}
			}
			
			if (!isKnownColumn) {
				headers.add("Column " + (i+1));
			}
		}
		return headers;
	}
}
