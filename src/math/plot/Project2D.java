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
