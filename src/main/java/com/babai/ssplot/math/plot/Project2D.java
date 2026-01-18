/*
 * Project2D.java
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

package com.babai.ssplot.math.plot;

import java.awt.geom.Point2D;

public class Project2D {
	private static final double defaultMoveAngle = Math.toRadians(10.0);

	private double a, b, c;
	private double moveAngle = defaultMoveAngle;

	// Store rotation matrix as primitive doubles
	// Only store 2 rows, since we don't need the rotated Z
	private double m00, m01, m02;
	private double m10, m11, m12;

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
	 * Set viewing angle for 3d to 2d projection
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
		double sC = Math.sin(c), cC = Math.cos(c);
		double sB = Math.sin(b), cB = Math.cos(b);
		double sA = Math.sin(a), cA = Math.cos(a);

		// rotZ
		double rZ00 = cC,  rZ01 = -sC;
		double rZ10 = sC,  rZ11 = cC;

		// rotY
		double rY00 = cB,  rY02 = sB;
		double rY20 = -sB, rY22 = cB;

		// rotX
		double rX11 = cA,  rX12 = -sA;
		double rX21 = sA,  rX22 = cA;

		// Multiply rotY * rotX (only rows we need)
		double t00 = rY00*1 + rY02*0; // simplifies to cB
		double t01 = rY00*0 + rY02*rX21; // = rY02*sA
		double t02 = rY00*0 + rY02*rX22; // = rY02*cA

		double t10 = 0; 
		double t11 = 1*rX11; // = cA
		double t12 = 1*rX12; // = -sA

		double t20 = rY20*1 + rY22*0; // = -sB
		double t21 = rY20*0 + rY22*rX21; // = rY22*sA
		double t22 = rY20*0 + rY22*rX22; // = rY22*cA

		// Multiply rotZ * (rotY*rotX), only first two rows
		m00 = rZ00*t00 + rZ01*t10;
		m01 = rZ00*t01 + rZ01*t11;
		m02 = rZ00*t02 + rZ01*t12;

		m10 = rZ10*t00 + rZ11*t10;
		m11 = rZ10*t01 + rZ11*t11;
		m12 = rZ10*t02 + rZ11*t12;
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
		double px = m00*x + m01*y + m02*z;
		double py = m10*x + m11*y + m12*z;
		// z component ignored for 2D projection
		return new Point2D.Double(px, py);
	}
}
