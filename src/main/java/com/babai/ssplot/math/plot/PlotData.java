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
		LINES("2D Lines", Axis.Cartesian.X, Axis.Cartesian.Y),
		POINTS("2D Points", Axis.Cartesian.X, Axis.Cartesian.Y),
		LINES_POINTS("2D Lines with Points", Axis.Cartesian.X, Axis.Cartesian.Y),
		
		POINTS3("3D Points", Axis.Cartesian.X, Axis.Cartesian.Y, Axis.Cartesian.Z), 
		LINES3("3D Lines", Axis.Cartesian.X, Axis.Cartesian.Y, Axis.Cartesian.Z),
		
		VFIELD("Vector field", Axis.Cartesian.X, Axis.Cartesian.Y);
		
		private final String type;
		private final List<Axis> axes;

		PlotType(String type, Axis... axes) {
			this.type = type;
			this.axes = List.of(axes);
		}
		
		public List<Axis> axes() {
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
	
	// TODO PointTypes should have a method that draws themselves?
	// Also, ptX, ptY could be fields of PointType
	public enum PointType { SQUARE, CIRCLE };

	private double[][] data;
	private Vector<Node> nodes;
	private EquationSystem system;
	
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
	private HashMap<Axis, Integer> axesDataColumns = new HashMap<>();
	
	private Color fgColor, fgColor2;
	private String title;

	public PlotData(double[][] extData) {
		data = extData;
		nodes = new Vector<Node>();		
		// Plot Type
		plotType = PlotType.LINES;
		
		// Active axes
		setDataCols(0, 1);
		
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


	// ------------- DATA METHODS -----------------
	public double[][] getData() { return data; }
	public void setData(double[][] data) { this.data = data; }
	public int getRowCount() { return data.length; }
	public int getColumnCount() { return data.length > 0 ? data[0].length : 0; }
	
	/**
	 * @return the index of the data column corresponding to axis with `axisName`.
	 */
	public int getDataCol(Axis axisName) {
		return axesDataColumns.getOrDefault(axisName, plotType.axes().indexOf(axisName));
	}
	
	/**
	 * @param index
	 * @return the `index`th data column.
	 */
	public int getDataCol(int index) {
		return getDataCol(getPlotType().axes().get(index));
	}
	
	/**
	 * Sets which data column is associated with which axis
	 * 
	 * @param dataCols    An array of column indices.
	 * Column with index `dataCols[0]` will be associated with the 0-th axis, and so on.
	 */
	public void setDataCols(int... dataCols) {
		var axes = plotType.axes();
		for (int i = 0; i < dataCols.length; i++) {
			axesDataColumns.put(axes.get(i), dataCols[i]);
		}
	}
	
	public HashMap<Axis, Integer> getDataColMapping() { return this.axesDataColumns; }
	
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
		to = Math.min(getRowCount(), to);

		double[][] dataCopy = new double[to - from][];
		for (int i = from; i < to; i++) {
			dataCopy[i] = this.data[i];
		}
		return new PlotData(dataCopy);
	}
	
	public double[] getColumn(int i) {
		double[] colData = new double[getRowCount()];
		for (int rowIdx = 0; rowIdx < colData.length; rowIdx++) {
			if (i < getColumnCount()) {
				colData[rowIdx] = this.data[rowIdx][i]; 
			}
		}
		return colData;
	}

	/**
	 * @param dataCol     index of a column
	 * @return            maximum value among all data in the given column
	 */
	public double getMax(int dataCol) { return Stats.max(getColumn(dataCol)); }

	/**
	 * @param dataCol     index of a column
	 * @return            min value among all data in the given column
	 */
	public double getMin(int dataCol) { return Stats.min(getColumn(dataCol)); }
	
	
	// ------------------ NODE METHODS -------------------
	public void addNode(Point2D.Double p, String str, Color c) { nodes.add(new Node(p, str, c)); }
	public Vector<Node> getNodes() { return nodes; }
	public void setNodes(Vector<Node> nodes) { this.nodes = nodes; }
	

	// ------------------- STRING REPRESENTATION --------------
	public String info() {
		var buff = new StringBuilder();
		if (getSystem() != null) {
			buff.append(getSystem().toString()).append("\n");
		}
		
		// FIXME Can be moved into a separate function
		var mappings = getDataColMapping();
		for (int i = 0; i < getColumnCount(); i++) {
			boolean isKnownColumn = false;
			for (var entry : mappings.entrySet()) {
				var lbl = getAxisLabel(i);
				if (lbl.isPresent() || entry.getValue() == i) {
					String colName = lbl.orElse(entry.getKey().toString() + " Data");
					buff.append(formatStats(colName, i));
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
				colData.length,
				Stats.min(colData),
				Stats.max(colData),
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
