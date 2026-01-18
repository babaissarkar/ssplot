/*
 * Matrix.java
 * 
 * Copyright 2021-2026 Subhraman Sarkar <suvrax@gmail.com>
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

package com.babai.ssplot.math.prim;

/**
 * A mathematical matrix with a mostly functional (non-mutating) API.
 * <p>
 * All arithmetic operations such as {@code add}, {@code subtract},
 * {@code multiply}, {@code transpose}, etc. return new {@code Matrix}
 * instances and do <b>not</b> modify the receiver.
 * </p>
 *
 * <p>
 * The {@link #set(double, int, int)} method is the <b>only</b> supported
 * mutator and exists solely for controlled, low-level construction or
 * performance-sensitive initialization. No other mutating operations
 * should be added.
 * </p>
 *
 * <p>
 * This design preserves referential transparency for matrix algebra
 * while still allowing explicit, intentional mutation when required.
 * Any future mutating methods would break these guarantees and are
 * therefore considered API violations.
 * </p>
 */
public class Matrix {
	private double[][] data;
	private int rows, columns;

	/**
	 * Creates a matrix with given data.
	 * @param data the data given via a 2d double array.
	 */
	public Matrix(double[][] data) {
		if (data == null || data.length == 0) {
			throw new IllegalArgumentException("Matrix data is empty");
		}
		
		if (data[0].length == 0) {
			throw new IllegalArgumentException("Matrix has zero columns");
		}

		this.rows = data.length;
		this.columns = data[0].length;
		
		this.data = new double[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				if (data[i].length < columns) {
					throw new IllegalArgumentException("Jagged row with length " + data[i].length + " != length " + columns);
				}
				this.data[i][j] = data[i][j];
			}
		}
	}

	/**
	 * Creates an empty matrix filled with zeroes.
	 * @param rows
	 * @param columns
	 */
	public Matrix(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
		this.data = new double[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				data[i][j] = 0.0;
			}
		}
	}

	/**
	 * @return the i-jth matrix element
	 */
	public double get(int i, int j) {
		return data[i][j];
	}

	/**
	 * Sets a single matrix element.
	 * <p>
	 * This is the only mutating operation intentionally exposed by
	 * {@code Matrix}. All other operations return new instances.
	 * </p>
	 */
	public void set(double value, int i, int j) {
		data[i][j] = value;
	}
	/**
	 * @param m Matrix to be added.
	 * @return resultant matrix
	 */
	public Matrix add(Matrix m) {
		if(rows != m.getRows() || columns != m.getColumns()) {
			throw new IllegalArgumentException("Matrix dimensions don't match for addition");
		}
		
		double[][] res = new double[rows][columns];
		for(int i = 0; i < m.getRows(); i++) {
			for(int j = 0; j < m.getColumns(); j++) {
				res[i][j] = data[i][j] + m.get(i, j);
			}
		}
		return new Matrix(res);
	}

	/**
	 * @param m Matrix to be subtracted
	 * @return resultant matrix
	 */
	public Matrix subtract(Matrix m) {
		if(rows != m.getRows() || columns != m.getColumns()) {
			throw new IllegalArgumentException("Matrix dimensions don't match for subtraction");
		}
		
		double[][] res = new double[rows][columns];
		for(int i = 0; i < m.getRows(); i++) {
			for(int j = 0; j < m.getColumns(); j++) {
				res[i][j] = data[i][j] - m.get(i, j);
			}
		}
		return new Matrix(res);
	}

	/**
	 * @param k the constant to be multiplied with this matrix
	 * @return resultant matrix
	 */
	public Matrix times(double k) {
		double[][] newData = new double[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				newData[i][j] = k * this.data[i][j];
			}
		}
		return new Matrix(newData);
	}

	/**
	 * @param m the matrix to be multiplied with this matrix
	 * @return resultant matrix
	 */
	public Matrix multiply(Matrix m) {
		if(columns != m.getRows()) {
			throw new IllegalArgumentException("Matrix 1 columns " + columns + " != matrix 2 rows " + m.getRows());
		}
		
		double c[][] = new double[this.rows][m.columns];
		for (int i = 0; i < this.rows; i++) {
			for (int k = 0; k < m.columns; k++) {
				for (int j = 0; j < this.columns; j++) {
					c[i][k] += data[i][j] * m.get(j, k);
				}
			}
		}
		return new Matrix(c);
	}

	/**
	 * @param m another matrix to have direct product with this.
	 * @return direct product of this matrix and m
	 */
	public Matrix directProduct(Matrix m) {
		int resRows = this.rows * m.rows;
		int resCols = this.columns * m.columns;
		double[][] res = new double[resRows][resCols];

		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				for (int p = 0; p < m.rows; p++) {
					for (int n = 0; n < m.columns; n++) {
						res[m.rows * i + p ][m.columns * j + n] = this.data[i][j] * m.get(p, n);
					}
				}
			}
		}

		return new Matrix(res);
	}

	/**
	 * @return the transposed Matrix
	 */
	public Matrix transpose() {
		double[][] newData = new double[columns][rows];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				newData[j][i] = data[i][j];
			}
		}
		return new Matrix(newData);
	}

	/**
	 * @param omittedrow the row to remove
	 * @param omittedcol the column to remove
	 * @return the matrix formed by removing the specified row and column
	 */
	public Matrix submatrix(int omittedrow, int omittedcol) {
		double sub_elems[][] = new double[rows - 1][columns - 1];
		for (int i = 0, m = 0; i < rows; i++) {
			if (i != omittedrow) {
				for (int j = 0, n = 0; j < columns; j++) {
					if (j != omittedcol) {
						sub_elems[m][n] = this.data[i][j];
						n++;
					} else {
					}
				}
				m++;
			} else {
			}
		}
		return new Matrix(sub_elems);
	}

	@Override
	public String toString() {
		StringBuilder mat = new StringBuilder();
		for(int i = 0; i < rows; i++) {
			mat.append("[");
			for(int j = 0; j < columns; j++) {
				mat.append(data[i][j]);
				if (j != columns - 1) {
					mat.append(", ");
				}
			}
			mat.append("]");
			if (i != rows-1) {
				mat.append("\n");
			}
		}
		return mat.toString();
	}

	/**
	 * @return number of rows
	 */
	public int getRows() {
		return this.rows;
	}

	/**
	 * @return number of columns
	 */
	public int getColumns() {
		return this.columns;
	}
}

