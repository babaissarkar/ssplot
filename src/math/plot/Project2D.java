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

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.RotationConvention;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

public class Project2D {
	double a, b, c;
	
	public void setView(double a, double b, double c) {
        /* Set viewing angle */
        /*this.a = Math.toRadians(a);
        this.b = Math.toRadians(b);
        this.c = Math.toRadians(c);*/
		this.a = a;
		this.b = b;
		this.c = c;
    }
	
	public Point2D.Double project(double x, double y, double z) {
		Vector3D v = new Vector3D(x, y, z); /* Point to be projected */
		
		Rotation rot = new Rotation(new Vector3D(0,0,1), c, RotationConvention.VECTOR_OPERATOR);
		Rotation rot2 = new Rotation(new Vector3D(0,1,0), b, RotationConvention.VECTOR_OPERATOR);
		Rotation rot3 = new Rotation(new Vector3D(1,0,0), a, RotationConvention.VECTOR_OPERATOR);
		
		Vector3D camPos = new Vector3D(0, 0, 0); /* Position of the Camera */
		Vector3D camFrmPos = rot.applyTo(
								rot2.applyTo(
									rot3.applyTo(
										v.subtract(camPos))));
		
        return new Point2D.Double(camFrmPos.getX(), camFrmPos.getY());
	}
}
