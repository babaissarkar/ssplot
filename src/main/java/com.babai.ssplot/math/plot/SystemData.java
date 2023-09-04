/*
 * SystemData.java
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

import java.util.Vector;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import parse.TreeNode;
import parse.TreeParser;

public class SystemData {
    //Matrix A; // The coefficient matrix
	String[] eqns;
    double[] max, min, gap; // The range
    int dim = 3;
    int N = 1000; /* iteration count */
    double h = 0.05; /* stepsize for RK4 */
    ScriptEngine engine;
    String engineName;
    TreeParser parser;
    TreeNode[] eqnNodes;
    boolean usingInternalParser;
    boolean isODE;
    
    public SystemMode curMode;

    public SystemData() {
    	eqns = new String[dim];
    	eqnNodes = new TreeNode[3];
    	max = new double[dim];
    	min = new double[dim];
    	gap = new double[dim];
        /* Default values */
		for (int i = 0; i < 3; i++) {
			max[i] = 10;
			min[i] = -10;
			gap[i] = 1;
		}
        /* Initialize script engine */
        ScriptEngineManager m = new ScriptEngineManager();
        engineName = System.getenv("SSPLOT_ENGINE");
        
        //Fallback to Rhino for now, until the internal parser is done.
        engineName = "rhino";
        
        if (engineName == null) {
        	//engine = m.getEngineByName("nashorn");
        	usingInternalParser = true;
        	parser = new TreeParser();
        } else {
        	usingInternalParser = false;
        	System.out.println("Trying to use engine " + engineName);
        	engine = m.getEngineByName(engineName);
        	if (engine == null) {
        		System.out.println("Unable to load " + engineName + ".");
        		System.out.println("Using default engine.");
        		engine = m.getEngineByName("nashorn");
        		if (engine == null) {
        			System.out.println("Default engine not found!");
        			System.out.println("ODE/DE plotter won't work!");
        			System.out.println("You need to install a JSR-223 compatible ScriptEngine.");
        		}
        	}
        }
        
        if (engine != null) {
        	System.out.println("Using Parsing engine : " + engine.getFactory().getEngineName());
        }
    }
    
    public void setSystemType(boolean isODE) {
    	this.isODE = isODE;
    }
    
    /*
    public void setEqns(String eqn1, String eqn2) {
    	// Setup equations
        this.eqns[0] = eqn1;
        this.eqns[1] = eqn2;
        
        if (usingInternalParser) {
        	for (int i = 0; i < 2; i++) {
        		eqnNodes[i] = parser.parse(eqns[i]);
        	}
        }
    }
    
    public void setEqns(String eqn1, String eqn2, String eqn3) {
    	// Setup equations
        this.eqns[0] = eqn1;
        this.eqns[1] = eqn2;
        this.eqns[2] = eqn3;
        
        if (usingInternalParser) {
        	for (int i = 0; i < 3; i++) {
        		eqnNodes[i] = parser.parse(eqns[i]);
        	}
        }
    }
    */
    
    public String[] getEqns() {
    	return this.eqns;
    }
    
    public void setEqns(String... eqns) {
    	for (int i = 0; i < 3; i++) { // Max variables : 3
    		if (eqns[i] != null) {
    			this.eqns[i] = eqns[i];
    			if (usingInternalParser) {
    				this.eqnNodes[i] = parser.parse(eqns[i]);
    			}
    		}
		}
    }
    /* Should reduce the numbers of methods here. Lot of code duplication */

    public double dx_dt(double x, double y, double z) {
    	double res = 0;
        if (!usingInternalParser) {
			engine.put("x", x);
			engine.put("y", y);
			engine.put("z", z);
			try {
				engine.eval("dx_dt = " + eqns[0]);
				res = (double) engine.get("dx_dt");
				//			System.out.format("dx_dt = %f at (%f, %f, %f).", res, x, y, z);

			} catch (ScriptException e) {
				e.printStackTrace();
			} 
		} else {
			res = eqnNodes[0].evalAt("x", x, "y", y, "z", z);
		}
        
		return res;
        
    }

    public double dy_dt(double x, double y, double z) {
    	double res = 0;
    	
    	if (!usingInternalParser) {
			engine.put("x", x);
			engine.put("y", y);
			engine.put("z", z);
			try {
				engine.eval("dy_dt = " + eqns[1]);
				res = (double) engine.get("dy_dt");
				//System.out.format("dy_dt = %f at (%f, %f, %f).", res, x, y, z);

			} catch (ScriptException e) {
				e.printStackTrace();
			} 
    	} else {
			res = eqnNodes[1].evalAt("x", x, "y", y, "z", z);
		}
    	
		return res;
    }
    
    public double dz_dt(double x, double y, double z) {
    	double res = 0;
        if (!usingInternalParser) {
			engine.put("x", x);
			engine.put("y", y);
			engine.put("z", z);
			try {
				engine.eval("dz_dt = " + eqns[2]);
				res = (double) engine.get("dz_dt");
				//System.out.format("dz_dt = %f at (%f, %f, %f).", res, x, y, z);

			} catch (ScriptException e) {
				e.printStackTrace();
			} 
        } else {
			res = eqnNodes[2].evalAt("x", x, "y", y, "z", z);
		}
		return res;
    }
    
    public double dx_dt(double x, double y) {
    	return dx_dt(x, y, 0);
    }
    
    public double dy_dt(double x, double y) {
    	return dy_dt(x, y, 0);
    }
    
    public double x2(double x) {
    	double res = 0;
        if (!usingInternalParser) {
			engine.put("x", x);
			try {
				engine.eval("x2 = " + eqns[0]);
				res = (double) engine.get("x2");
				//System.out.println("X2 = " + res);
			} catch (ScriptException e) {
				e.printStackTrace();
			} 
        } else {
        	res = eqnNodes[0].evalAt("x", x);
		}
        
		return res;
    }
    
    public double x2(double x, double y) {
    	double res = 0;
        if (!usingInternalParser) {
			engine.put("x", x);
			engine.put("y", y);
			try {
				engine.eval("x2 = " + eqns[0]);
				res = (double) engine.get("x2");
				//System.out.println("X2 = " + res);
			} catch (ScriptException e) {
				e.printStackTrace();
			} 
        } else {
        	res = eqnNodes[0].evalAt("x", x, "y", y);
		}
        
		return res;
    }
    
    public double y2(double x, double y) {
    	double res = 0;
        if (!usingInternalParser) {
			engine.put("x", x);
			engine.put("y", y);
			try {
				engine.eval("y2 = " + eqns[1]);
				res = (double) engine.get("y2");
				//System.out.println("Y2 = " + res);
			} catch (ScriptException e) {
				e.printStackTrace();
			} 
        } else {
        	res = eqnNodes[1].evalAt("x", x, "y", y);
		}
		return res;
    }

    /******************************************************************/
    /** Solve the system of equations by RK 4th order method */
    public Vector<Vector<Double>> RK4Iterate(double x0, double y0) {
    	// Set Mode
    	this.curMode = SystemMode.ODE;
    	
        Vector<Vector<Double>> soln = new Vector<Vector<Double>>();
        double x, y;
        //double h = 0.05;
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
    
    public Vector<Vector<Double>> RK4Iterate3D(double x0, double y0, double z0) {
    	// Set Mode
    	this.curMode = SystemMode.ODE;
    	
    	System.out.println("3D rk4 started.");
        Vector<Vector<Double>> soln = new Vector<Vector<Double>>();
        double x, y, z;
        //double h = 0.01;
        double k1, k2, k3, k4;
        double p1, p2, p3, p4;
        double q1, q2, q3, q4;
        
        x = x0;
        y = y0;
        z = z0;
        
        for (int i = 0; i < N; i++) {
            k1 = h * dx_dt(x, y, z);
            p1 = h * dy_dt(x, y, z);
            q1 = h * dz_dt(x, y, z);
            
//            System.out.println(k1 + "," + p1 + "," + q1);
            
            k2 = h * dx_dt(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);
            p2 = h * dy_dt(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);
            q2 = h * dz_dt(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);
            
//            System.out.println(k2 + "," + p2 + "," + q2);
            
            k3 = h * dx_dt(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);
            p3 = h * dy_dt(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);
            q3 = h * dz_dt(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);
            
//            System.out.println(k3 + "," + p3 + "," + q3);
            
            k4 = h * dx_dt(x + k3, y + p3, z + q3);
            p4 = h * dy_dt(x + k3, y + p3, z + q3);
            q4 = h * dz_dt(x + k3, y + p3, z + q3);
            
//            System.out.println(k4 + "," + p4 + "," + q4);
            
            x += (k1 + 2*k2 + 2*k3 + k4)/6.0;
            y += (p1 + 2*p2 + 2*p3 + p4)/6.0;
            z += (q1 + 2*q2 + 2*q3 + q4)/6.0;

            Vector<Double> row = new Vector<Double>();
            row.add(x);
            row.add(y);
            row.add(z);
            soln.add(row);
        }

        return soln;
    }


    /** Gets the data for the direction field. */
    public Vector<Vector<Double>> directionField() {
    	// Set Mode
    	this.curMode = SystemMode.ODE;
    	
        Vector<Vector<Double>> data = new Vector<Vector<Double>>();
        double i, j;
        
        //System.out.println(""+gap[0]+","+gap[1]);	
        for (i = min[0]; i <= max[0]; i = i + gap[0]) {
			for (j = min[1]; j <= max[1]; j = j + gap[1]) {
				double Xdot, Ydot;
				double X1, Y1, X2, Y2;
                double r;
				Xdot = dx_dt(i, j);
				Ydot = dy_dt(i, j);
				X1 = i;
				Y1 = j;
				X2 = X1 + Xdot;
				Y2 = Y1 + Ydot;
                /* Normalizing */
                r = Math.sqrt((X2-X1)*(X2-X1) + (Y2-Y1)*(Y2-Y1));
				X2 = X1 + Xdot/r;
				Y2 = Y1 + Ydot/r;
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
    	//Set Mode
    	curMode = SystemMode.DFE;
    	
    	Vector<Vector<Double>> soln = new Vector<Vector<Double>>();
    	double x, y;
        
        x = x0;
        y = y0;
        
        for (int i = 0; i < N; i++) {
        	double tempX, tempY;
        	
        	Vector<Double> row = new Vector<Double>();
            row.add(x);
            row.add(y);
            soln.add(row);
        	
        	tempX = x2(x, y);
        	tempY = y2(x, y);
        	x = tempX;
        	y = tempY;

        }
        
    	return soln;
    }
    
    public Vector<Vector<Double>> functionData() {
    	// Set Mode
    	this.curMode = SystemMode.FN1;
    	
    	Vector<Vector<Double>> soln = new Vector<Vector<Double>>();
    	double i, x;
        
        for (i = min[0]; i <= max[0]; i = i + gap[0]) {
			//for (j = min[1]; j <= max[1]; j = j + gap[1]) {
				Vector<Double> row = new Vector<Double>();
				x = x2(i);
	            row.add(i);
	            row.add(x);
	            soln.add(row);
			//}
        }
    	return soln;
    }
    
    public Vector<Vector<Double>> functionData2D() {
    	// Set Mode
    	this.curMode = SystemMode.FN2;
    	
    	Vector<Vector<Double>> soln = new Vector<Vector<Double>>();
    	double i, j, z;
        
        for (i = min[0]; i <= max[0]; i = i + gap[0]) {
			for (j = min[1]; j <= max[1]; j = j + gap[1]) {
				Vector<Double> row = new Vector<Double>();
				z = x2(i, j);
	            row.add(i);
	            row.add(j);
	            row.add(z);
	            soln.add(row);
			}
        }
    	return soln;
    }
    
    public Vector<Vector<Double>> cobweb(double x0) {
    	// Set Mode
    	curMode = SystemMode.DFE;
    	
    	/* Works for 1D maps only */
    	Vector<Vector<Double>> soln = new Vector<Vector<Double>>();
    	double x, y;
        
        x = x0;
        y = 0;
        
        for (int i = 0; i < N; i++) {

        	Vector<Double> row = new Vector<Double>();
            row.add(x);
            row.add(y);
            soln.add(row);
            
            y = x2(x);
        	
            Vector<Double> row2 = new Vector<Double>();
            row2.add(x);
            row2.add(y);
            soln.add(row2);
            
            x = y;
            
            Vector<Double> row3 = new Vector<Double>();
            row3.add(x);
            row3.add(y);
            soln.add(row3);
        }
        
    	return soln;
    }
}
