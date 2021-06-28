package math.plot;

import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class SystemData {
    //Matrix A; // The coefficient matrix
	String eqn1, eqn2;
    double Xmax, Ymax, Xmin, Ymin, Xgap, Ygap; // The range
    int N = 1000; /* iteration count */
    ScriptEngine engine;
    boolean isODE;

    public SystemData() {
        /* Default values */
        Xmax = 10; Ymax = 10;
        Xmin = -10; Ymin = -10;
        Xgap = 1; Ygap = 1;
        
        /* Initialize script engine */
        ScriptEngineManager m = new ScriptEngineManager();
        engine = m.getEngineByName("nashorn");
    }
    
    public void setSystemType(boolean isODE) {
    	this.isODE = isODE;
    }
    public void setEqns(String eqn1, String eqn2) {
    	/* Setup equations */
        this.eqn1 = eqn1;
        this.eqn2 = eqn2;
    }

    public double dx_dt (double x, double y) {
    	double res = 0;
        engine.put("x", x);
        engine.put("y", y);
        try {
			engine.eval("dx_dt = " + eqn1);
			res = (double) engine.get("dx_dt");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
        
//        System.out.format("dx_dt at (%4.2f, %4.2f) = %4.2f", x, y, res);
        
        return res;
        
    }

    public double dy_dt (double x, double y) {
    	double res = 0;
        engine.put("x", x);
        engine.put("y", y);
        try {
			engine.eval("dy_dt = " + eqn2);
			res = (double) engine.get("dy_dt");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
        
        return res;
    }
    
    public double x2 (double x, double y) {
    	double res = 0;
        engine.put("x", x);
        engine.put("y", y);
        try {
        	engine.eval("x2 = " + eqn1);
        	res = (double) engine.get("x2");
        	//System.out.println("X2 = " + res);
        } catch (ScriptException e) {
        	e.printStackTrace();
        }

        return res;
    }
    
    public double y2 (double x, double y) {
    	double res = 0;
        engine.put("x", x);
        engine.put("y", y);
        try {
        	engine.eval("y2 = " + eqn2);
        	res = (double) engine.get("y2");
        	//System.out.println("Y2 = " + res);
        } catch (ScriptException e) {
        	e.printStackTrace();
        }

        return res;
    }

    /** Solve the system of equations by RK 4th order method */
    public Vector<Vector<Double>> RK4Iterate(double x0, double y0) {
        Vector<Vector<Double>> soln = new Vector<Vector<Double>>();
        double x, y;
        double h = 0.05;
        double k1, k2, k3, k4;
        double p1, p2, p3, p4;
        
        x = x0;
        y = y0;
        
        for (int i = 0; i < N; i++) {
            k1 = h * dx_dt(x, y);
            p1 = h * dy_dt(x, y);
            k2 = h * dx_dt(x + 0.5 * k1, y + 0.5 * p1);
            p2 = h * dy_dt(x + 0.5 * k1, y + 0.5 * p1);
            k3 = h * dx_dt(x + 0.5 * k2, y + 0.5 * p2);
            p3 = h * dy_dt(x + 0.5 * k2, y + 0.5 * p2);
            k4 = h * dx_dt(x + k3, y + p3);
            p4 = h * dy_dt(x + k3, y + p3);
            x += (k1 + 2*k2 + 2*k3 + k4)/6.0;
            y += (p1 + 2*p2 + 2*p3 + p4)/6.0;

            Vector<Double> row = new Vector<Double>();
            row.add(x);
            row.add(y);
            soln.add(row);
        }

        return soln;
    }

    /** Gets the data for the direction field. */
    public Vector<Vector<Double>> directionField() {
        Vector<Vector<Double>> data = new Vector<Vector<Double>>();
        double i, j;
        
        //System.out.println(""+Xgap+","+Ygap);	
        for (i = Xmin; i <= Xmax; i = i + Xgap) {
			for (j = Ymin; j <= Ymax; j = j + Ygap) {
				double Xdot, Ydot;
				double X1, Y1, X2, Y2;
				Xdot = dx_dt(i, j);
				Ydot = dy_dt(i, j);
				X1 = i;
				Y1 = j;
				//X2 = X1 + Xdot/10.0;
				//Y2 = Y1 + Ydot/10.0;
				X2 = X1 + Xdot;
				Y2 = Y1 + Ydot;
				Vector<Double> entries = new Vector<Double>();
				entries.add(X1);
				entries.add(Y1);
				entries.add(X2);
				entries.add(Y2);
				data.add(entries);
				//System.out.format("%f, %f\n", i, j);
			} 
		}
		return data;
    }
    
    public Vector<Vector<Double>> iterateMap(double x0, double y0) {
    	Vector<Vector<Double>> soln = new Vector<Vector<Double>>();
    	double x, y;
        
        x = x0;
        y = y0;
        
        for (int i = 0; i < N; i++) {
        	double tempX, tempY;
        	
        	tempX = x2(x, y);
        	tempY = y2(x, y);
        	x = tempX;
        	y = tempY;

        	Vector<Double> row = new Vector<Double>();
            row.add(x);
            row.add(y);
            soln.add(row);

        }
        
    	return soln;
    }
}
