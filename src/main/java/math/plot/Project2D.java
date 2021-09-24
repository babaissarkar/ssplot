/*
 * Project2D.java
 * 
 * Copyright 2021 Subhraman Sarkar <subhraman@subhraman-Inspiron>
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

import java.awt.geom.Point2D;

import math.prim.Matrix;

public class Project2D {
	double a, b, c;
	private StatLogger logger;
	
	public void setView(double a, double b, double c) {
        /* Set viewing angle */
        double a2 = Math.toDegrees(a) % 360;
        double b2 = Math.toDegrees(b) % 360;
        double c2 = Math.toDegrees(c) % 360;
        String threedpos = String.format("%f, %f, %f\n", a2, b2, c2);
        log(threedpos);
		this.a = a;
		this.b = b;
		this.c = c;
    }
	
	public void setLogger(StatLogger logger) {
		this.logger = logger;
	}
	
	private void log(String string) {
		this.logger.log(string + "\n");
		//System.out.println("log : " + string);
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
}
