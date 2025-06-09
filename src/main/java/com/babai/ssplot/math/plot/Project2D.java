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
import com.babai.ssplot.util.InfoLogger;

public class Project2D {
	private double a, b, c;
	private static final double defaultMoveAngle = 10.0;
	private double moveAngle = Math.toRadians(defaultMoveAngle);
	private InfoLogger logger;
	
	public enum Axis {X, Y, Z, NX, NY, NZ};
	
	public Project2D(InfoLogger logger) {
		this.logger = logger;
	}
	
	/**
	 *  Set viewing angle for 3d plots
	 */
	public void setView(double a, double b, double c) {
		double a2 = Math.toDegrees(a) % 360;
		double b2 = Math.toDegrees(b) % 360;
		double c2 = Math.toDegrees(c) % 360;
		// TODO: this creates too much noise on the log window.
		// Perhaps various log modes (Info/Debug) are needed?
		if (logger != null) {
			String threedpos = String.format("3D Rotation angles : %f, %f, %f\n", a2, b2, c2);
			logger.log(threedpos);
		}
		this.a = a;
		this.b = b;
		this.c = c;
	}
	
	public double getMoveAngle() {
		return moveAngle;
	}

	public void setMoveAngle(double moveAngle) {
		this.moveAngle = moveAngle;
	}
	
	public Point2D.Double project(double x, double y, double z) {
		Matrix rotX, rotY, rotZ, R, R2;
		rotZ = new Matrix(3, 3);
		rotY = new Matrix(3, 3);
		rotX = new Matrix(3, 3);
		
		R = new Matrix(3, 1);
		
		R.set(x, 0, 0);
		R.set(y, 1, 0);
		R.set(z, 2, 0);
		
		/* Rotation about Z */
		rotZ.set(Math.cos(c), 0, 0);
		rotZ.set(-Math.sin(c), 0, 1);
		rotZ.set(Math.cos(c), 1, 1);
		rotZ.set(Math.sin(c), 1, 0);
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
		
		R2 = rotZ.times(rotY.times(rotX.times(R)));
		
		return new Point2D.Double(R2.get(0, 0), R2.get(1, 0));
	}
	
	public void moveView(Axis axis) {
		switch (axis) {
		case X:
			setView(a + getMoveAngle(), b, c);
			break;

		case Y:
			setView(a, b + getMoveAngle(), c);
			break;
			
		case Z:
			setView(a, b, c + getMoveAngle());
			break;
			
		case NX:
			setView(a - getMoveAngle(), b, c);
			break;

		case NY:
			setView(a, b - getMoveAngle(), c);
			break;
			
		case NZ:
			setView(a, b, c - getMoveAngle());
			break;
			
		default:
			break;
		}
	}

	public Point2D.Double projectInv(double a, double b, double c) {
		this.a = -this.a;
		this.b = -this.b;
		this.c = -this.c;
		Point2D.Double tP = project(a, b, c);
		this.a = -this.a;
		this.b = -this.b;
		this.c = -this.c;
		return tP;
	}
}
