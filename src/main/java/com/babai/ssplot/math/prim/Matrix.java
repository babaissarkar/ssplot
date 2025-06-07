/*
 * Matrix.java
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

package com.babai.ssplot.math.prim;

public class Matrix {
	private double[][] elems;
	private int rows, columns;
	private Matrix result;
	
	/**
     * Creates a matrix with given data.
	 * @param data
	 * @param rows
	 * @param columns
	 */
	public Matrix(double[][] data, int rows, int columns) {
		this.elems = data;
		this.rows = rows;
		this.columns = columns;
	}

    /** Creates an empty matrix.
     * @param rows
	 * @param columns */
    public Matrix(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.elems = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                elems[i][j] = 0.0;
            }
        }
    }

    public double get(int i, int j) {
        return elems[i][j];
    }

    public void set(double elem, int i, int j) {
        elems[i][j] = elem;
    }
	/**
	 * @param m
	 * @return resultant matrix
	 */
	public Matrix add(Matrix m) {
		double[][] mdata = m.toArray();
		double[][] res = new double[this.rows][this.columns];
		for(int i = 0; i < m.getRows(); i++) {
			for(int j = 0; j < m.getColumns(); j++) {
				res[i][j] = elems[i][j] + mdata[i][j];
			}
		}
		result = new Matrix(res, rows, columns);
		return result;
	}
	
	/**
	 * @param m
	 * @return resultant matrix
	 */
	public Matrix subtract(Matrix m) {
		double[][] mdata = m.toArray();
		double[][] res = elems;
		for(int i = 0; i < m.getRows(); i++) {
			for(int j = 0; j < m.getColumns(); j++) {
				res[i][j] = elems[i][j] - mdata[i][j];
			}
		}
		result = new Matrix(res, rows, columns);
		return result;
	}

	/**
	 * @param k
	 * @return resultant matrix
	 */
	public Matrix times(double k) {
		double[][] mdata = new double[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				mdata[i][j] = k*elems[i][j];
			}
		}
		result = new Matrix(mdata, rows, columns);
		return result;
	}
	
	/**
	 * @param m
	 * @return resultant matrix
	 */
	public Matrix times(Matrix m) {
		if(columns == m.rows) { //Changed
		double b[][] = m.toArray();
		double c[][] = new double[this.rows][m.columns];
		for (int i = 0; i < this.rows; i++) {
			for (int k = 0; k < m.columns; k++) {
				for (int j = 0; j < this.columns; j++) {
					c[i][k] += elems[i][j]*b[j][k];
				}
			}
		}
		result = new Matrix(c, this.rows, m.columns);
		} else {
			//Throw MatrixException
			result = null;
			System.err.println("Error!");
		}
		return result;
	}

    /* Convenience method. */
    public Matrix multiply(Matrix m) {
        return this.times(m);    
    }

    /**
	 * @param m
	 * @return direct product of this matrix and m
	 */
	public Matrix dp(Matrix m) {
        int res_rows = this.rows * m.rows;
        int res_cols = this.columns * m.columns;
        double[][] res = new double[res_rows][res_cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                for (int p = 0; p < m.rows; p++) {
                    for (int n = 0; n < m.columns; n++) {
                        res[m.rows * i + p ][m.columns * j + n] = this.elems[i][j] * m.elems[p][n];
                    }        
                }
            }        
        }

        return new Matrix(res, res_rows, res_cols);
    }

	/**
	 * @return resultant matrix
	 */
	public Matrix getLastResult() {
		return result;
	}
	
	/**
	 * @return the transposed Matrix
	 */
	public Matrix transpose() {		
		double[][] elems2 = new double[columns][rows];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				double buf;
				buf = elems[i][j];
				elems2[i][j] = elems[j][i];
				elems2[j][i] = buf;
			}
		}
		result = new Matrix(elems2, columns, rows);
		return result;	
	}
	
	/**
	 * @param omittedrow
	 * @param omittedcol
	 * @return the matrix formed by removing the specified row and column
	 */
	public Matrix submatrix(int omittedrow, double omittedcol) {
		double sub_elems[][] = new double[rows - 1][columns - 1];
		for (int i = 0, m = 0; i < rows; i++) {
			if (i != omittedrow) {
				for (int j = 0, n = 0; j < columns; j++) {
					if (j != omittedcol) {
						sub_elems[m][n] = this.elems[i][j];
						n++;
					} else {
					}
				}
				m++;
			} else {
			}
		}
		result = new Matrix(sub_elems, rows - 1, columns - 1);
		return result;
	}


    /* ************************************************************ */
    /* This part is not a good idea */
    /* Should use LU or some other decomposition scheme. */
	/**
	 * @return the Determinant formed from this Matrix
	 */
    /*
	public Determinant determinant() {
		if (rows == columns) {
			return new Determinant(this.toArray());
		} else {
			//Throw MatrixException
			Determinant d =  new Determinant(1);
			d.put(0, 0, 0);
			return d;
		}
	}*/
    /* ************************************************************ */
	
	/**
	 * @return the adjoint of this matrix
	 */
    /*
	public Matrix adjoint() {
		double[][] mdata = new double[rows][columns];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < columns; j++) {
				mdata[i][j] = this.submatrix(i, j).determinant().value() * Math.pow(-1, i+j);
			}
		}
		result = new Matrix(mdata, rows, columns).transpose();
		return result;
	}
    */
	
	/**
	 * @return the inverse of this Matrix, if it is non-singular
	 */
    /*
	public Matrix inverse() {
		if (rows == columns) {
			result = this.adjoint().times(1.0/this.determinant().value());
		} else {
			//throw MatrixException
		}
		return result;
	}
    */
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer mat = new StringBuffer("");
		for(int i = 0; i < rows; i++) {
			mat.append("[");
			for(int j = 0; j < columns; j++) {
				mat.append(elems[i][j]);
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
	 * @return array created from this Matrix
	 */
	public double[][] toArray() {
		return elems;
	}
	
	/**
	 * @return number of rows
	 */
	private double getRows() {
		return this.rows;
	}
	
	/**
	 * @return number of columns
	 */
	private double getColumns() {
		return this.columns;
	}

    /*
	public static void main(String[] args) {
		double[][] data1 = { {1, 2, 3}, {1, 5, 6}, {7, 8, 9} };
		//double[][] data2 = { {3, 6}, {4, 5} };
		Matrix m1 = new Matrix(data1, 3, 3);
		//Matrix m2 = new Matrix(data2, 2, 2);
		System.out.println(m1.toString() + "\n");
		//mi = m1.adjoint();
		System.out.println(m1.determinant().value() + "\n");
		System.out.println(m1.adjoint().toString() + "\n");
		//m2 = m1.times(m2);
		//System.out.println(m2.toString() + "\n");
		//System.out.println(m1.determinant().value() + "\n");
	}
    */
}

