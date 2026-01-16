/*
 * Project2D.java
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

package com.babai.ssplot.math.plot;

import java.awt.geom.Point2D;

import com.babai.ssplot.math.prim.Matrix;

public class Project2D {
	private static final double defaultMoveAngle = Math.toRadians(10.0);
	
	private double a, b, c;
	private double moveAngle = defaultMoveAngle;

	private Matrix rotMatrix;
	
	public enum RotationAxis { X, Y, Z, NX, NY, NZ };
	
	public Project2D() {
		this.a = 0.0;
		this.b = 0.0;
		this.c = 0.0;
		recomputeRotMatrix(0, 0, 0);
	}
	
	public double getMoveAngle() {
		return moveAngle;
	}

	public void setMoveAngle(double moveAngle) {
		this.moveAngle = moveAngle;
	}
	
	/**
	 *  Set viewing angle for 3d to 2d projection
	 */
	public void setView(double a, double b, double c) {
		if (this.a != a || this.b != b || this.c != c) {
			recomputeRotMatrix(a, b, c);
		}
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	private void recomputeRotMatrix(double a, double b, double c) {		
		var rotZ = new Matrix(3, 3);
		var rotY = new Matrix(3, 3);
		var rotX = new Matrix(3, 3);
		
		double sC = Math.sin(c);
		double cC = Math.cos(c); 
		
		/* Rotation about Z */
		rotZ.set(cC, 0, 0);
		rotZ.set(-sC, 0, 1);
		rotZ.set(cC, 1, 1);
		rotZ.set(sC, 1, 0);
		rotZ.set(1, 2, 2);
		
		/* Rotation about Y */
		rotY.set(Math.cos(b), 0, 0);
		rotY.set(-Math.sin(b), 2, 0);
		rotY.set(Math.cos(b), 2, 2);
		rotY.set(Math.sin(b), 0, 2);
		rotY.set(1, 1, 1);
		
		/* Rotation about X */
		rotX.set(Math.cos(a), 1, 1);
		rotX.set(-Math.sin(a), 1, 2);
		rotX.set(Math.cos(a), 2, 2);
		rotX.set(Math.sin(a), 2, 1);
		rotX.set(1, 0, 0);
		
		this.rotMatrix = rotZ.multiply(rotY.multiply(rotX));
	}

	public void moveView(RotationAxis axis) {
		switch (axis) {
		case X -> setView(a + getMoveAngle(), b, c);
		case Y -> setView(a, b + getMoveAngle(), c);
		case Z -> setView(a, b, c + getMoveAngle());
		case NX -> setView(a - getMoveAngle(), b, c);
		case NY -> setView(a, b - getMoveAngle(), c);
		case NZ -> setView(a, b, c - getMoveAngle());
		}
	}
	
	public Point2D.Double project(double x, double y, double z) {
		Matrix R, R2;
		R = new Matrix(3, 1);
		R.set(x, 0, 0);
		R.set(y, 1, 0);
		R.set(z, 2, 0);
		R2 = this.rotMatrix.multiply(R);
		return new Point2D.Double(R2.get(0, 0), R2.get(1, 0));
	}
}
