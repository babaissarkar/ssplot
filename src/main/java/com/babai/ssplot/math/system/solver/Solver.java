/*
 * Solver.java
 * 
 * Copyright 2025 Subhraman Sarkar <suvrax@gmail.com>
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

package com.babai.ssplot.math.system.solver;

import java.util.function.DoubleUnaryOperator;

import com.babai.ssplot.math.system.core.EquationSystem;
import com.babai.ssplot.math.system.parser.Parser;

public class Solver {
	private Parser parser;
	private EquationSystem system;
	
	public Solver(Parser p, EquationSystem sys) {
		this.parser = p;
		this.system = sys;
	};
	
	// TODO might need fixing. recheck. is it being consistently used?
	private boolean validate(int dim) {
		return parser != null
		    && system != null
		    && system.numberOfEqns() == dim;
	}
	
	@FunctionalInterface
	private interface Evaluator2D {
		public double of(double x, double y);
	}
	
	@FunctionalInterface
	private interface Evaluator3D {
		public double of(double x, double y, double z);
	}
	
	/**********************************************************************/
	/** Solve the system of differential equations by RK 4th order method */
	/**********************************************************************/
	public double[][] rk4Iterate(double x0, double y0) {
		var soln = new double[system.n()][2];
		if (!validate(2)) { // 2d system
			System.err.println("Invalid System for 2D RK4 iteration!");
			return soln;
		}
		
		final String eqn1 = system.eqn(0);
		final String eqn2 = system.eqn(1);
		
		parser.setVariables("x", "y");

		Evaluator2D dx_dt = (x, y) -> parser.evaluate(eqn1, x, y);
		Evaluator2D dy_dt = (x, y) -> parser.evaluate(eqn2, x, y);
		
		double x, y;
		double k1, k2, k3, k4;
		double p1, p2, p3, p4;
		double h = system.h();

		x = x0;
		y = y0;

		for (int i = 0; i < system.n(); i++) {
			k1 = h * dx_dt.of(x, y);
			p1 = h * dy_dt.of(x, y);
			k2 = h * dx_dt.of(x + 0.5 * k1, y + 0.5 * p1);
			p2 = h * dy_dt.of(x + 0.5 * k1, y + 0.5 * p1);
			k3 = h * dx_dt.of(x + 0.5 * k2, y + 0.5 * p2);
			p3 = h * dy_dt.of(x + 0.5 * k2, y + 0.5 * p2);
			k4 = h * dx_dt.of(x + k3, y + p3);
			p4 = h * dy_dt.of(x + k3, y + p3);
			x += (k1 + 2*k2 + 2*k3 + k4)/6.0;
			y += (p1 + 2*p2 + 2*p3 + p4)/6.0;

			soln[i][0] = x;
			soln[i][1] = y;
		}

		return soln;
	}

	public double[][] rk4Iterate3D(double x0, double y0, double z0) {
		var soln = new double[system.n()][3];
		if (!validate(3)) { // 3d system
			System.err.println("Invalid System for 3D RK4 iteration!");
			return soln;
		}
		
		final String eqn1 = system.eqn(0);
		final String eqn2 = system.eqn(1);
		final String eqn3 = system.eqn(2);
		
		parser.setVariables("x", "y", "z");
		
		Evaluator3D dx_dt = (x, y, z) -> parser.evaluate(eqn1, x, y, z);
		Evaluator3D dy_dt = (x, y, z) -> parser.evaluate(eqn2, x, y, z);
		Evaluator3D dz_dt = (x, y, z) -> parser.evaluate(eqn3, x, y, z);
		
		double x, y, z;
		double k1, k2, k3, k4;
		double p1, p2, p3, p4;
		double q1, q2, q3, q4;
		double h = system.h();

		x = x0;
		y = y0;
		z = z0;

		for (int i = 0; i < system.n(); i++) {
			k1 = h * dx_dt.of(x, y, z);
			p1 = h * dy_dt.of(x, y, z);
			q1 = h * dz_dt.of(x, y, z);

			k2 = h * dx_dt.of(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);
			p2 = h * dy_dt.of(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);
			q2 = h * dz_dt.of(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);

			k3 = h * dx_dt.of(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);
			p3 = h * dy_dt.of(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);
			q3 = h * dz_dt.of(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);

			k4 = h * dx_dt.of(x + k3, y + p3, z + q3);
			p4 = h * dy_dt.of(x + k3, y + p3, z + q3);
			q4 = h * dz_dt.of(x + k3, y + p3, z + q3);

			x += (k1 + 2*k2 + 2*k3 + k4)/6.0;
			y += (p1 + 2*p2 + 2*p3 + p4)/6.0;
			z += (q1 + 2*q2 + 2*q3 + q4)/6.0;

			soln[i][0] = x;
			soln[i][1] = y;
			soln[i][2] = z;
		}

		return soln;
	}

	/** Gets the data for the direction field. */
	public double[][] directionField() {
		var xrange = system.range(0);
		var yrange = system.range(1);
		
		int xcount = xrange.count();
		int ycount = yrange.count();
		var soln = new double[xcount * ycount][4];
		
		// TODO dimension validation?
		final String eqn1 = system.eqn(0);
		final String eqn2 = system.eqn(1);
		
		parser.setVariables("x", "y");

		Evaluator2D dx_dt = (x, y) -> parser.evaluate(eqn1, x, y);
		Evaluator2D dy_dt = (x, y) -> parser.evaluate(eqn2, x, y);
		
		for (int ix = 0; ix < xcount; ix++) {
			double x = xrange.at(ix);
			
			for (int iy = 0; iy < ycount; iy++) {
				double y = yrange.at(iy);
				
				double Xdot, Ydot;
				double X1, Y1, X2, Y2;
				double r;
				
				Xdot = dx_dt.of(x, y);
				Ydot = dy_dt.of(x, y);
				X1 = x;
				Y1 = y;
				X2 = X1 + Xdot;
				Y2 = Y1 + Ydot;
				// Normalizing
				r = Math.hypot(Xdot, Ydot);
				if (r != 0) {
					X2 = X1 + Xdot / r;
					Y2 = Y1 + Ydot / r;
				} else {
					X2 = X1;
					Y2 = Y1;
				}

				int i = ix * ycount + iy;
				soln[i][0] = X1;
				soln[i][1] = Y1;
				soln[i][2] = X2;
				soln[i][3] = Y2;
			}
		}
		return soln;
	}

	public double[][] iterateMap(double x0, double y0) {
		var soln = new double[system.n()][2];
		final String eqn1 = system.eqn(0);
		final String eqn2 = system.eqn(1);
		
		parser.setVariables("x", "y");

		Evaluator2D x2 = (x, y) -> parser.evaluate(eqn1, x, y);
		Evaluator2D y2 = (x, y) -> parser.evaluate(eqn2, x, y);
		
		double x = x0;
		double y = y0;

		for (int i = 0; i < system.n(); i++) {
			soln[i][0] = x;
			soln[i][1] = y;

			x = x2.of(x, y);
			y = y2.of(x, y);
		}

		return soln;
	}
	
	public double[][] functionData2D() {
		return system.isParametric()
			? functionData2DParametric()
			: functionData2DNonParametric(); 
	}
	
	private double[][] functionData2DParametric() {
		var soln = new double[system.range(0).count()][2];
		
		final String eqn1 = system.eqn(0);
		final String eqn2 = system.eqn(1);
		parser.setVariables("t");
		DoubleUnaryOperator fx = t -> parser.evaluate(eqn1, t);
		DoubleUnaryOperator fy = t -> parser.evaluate(eqn2, t);
		
		var trange = system.range(0);
		int tcount = trange.count();
		for (int i = 0; i < tcount; i++) {
			double t = trange.at(i);
			soln[i][0] = fx.applyAsDouble(t);
			soln[i][1] = fy.applyAsDouble(t);
		}
		
		return soln;
	}

	private double[][] functionData2DNonParametric() {
		var soln = new double[system.range(0).count()][2];
		
		final String eqn = system.eqn(0);
		parser.setVariables("x");
		DoubleUnaryOperator f = x -> parser.evaluate(eqn, x);
		
		var xrange = system.range(0);
		int xcount = xrange.count();
		for (int ix = 0; ix < xcount; ix++) {
			double x = xrange.at(ix);
			soln[ix][0] = x;
			soln[ix][1] = f.applyAsDouble(x);
		}
		
		return soln;
	}

	public double[][] functionData3D() {
		int rows = (system.range(0).count() * system.range(1).count()) + 1;
		var soln = new double[rows][3];
		
		final String eqn1 = system.eqn(0);
		
		parser.setVariables("x", "y");

		Evaluator2D f = (x, y) -> parser.evaluate(eqn1, x, y);

		var xrange = system.range(0);
		var yrange = system.range(1);
		int xcount = xrange.count();
		int ycount = yrange.count();
		
		int i = 0;
		for (int ix = 0; ix < xcount; ix++) {
			double x = xrange.at(ix);
			
			for (int iy = 0; iy < ycount; iy++) {
				double y = yrange.at(iy);
				soln[i][0] = x;
				soln[i][1] = y;
				soln[i][2] = f.of(x, y);
				i++;
			}
		}

		return soln;
	}

	// TODO eqn entry textfields 2 and 3 are not being used despite them being indicated as such in UI.
	public double[][] cobweb(double x0) {
		/* Works for 1D maps only */
		var soln = new double[system.n() * 3][2];
		
		final String eqn = system.eqn(0);
		parser.setVariables("x");
		DoubleUnaryOperator f = x -> parser.evaluate(eqn, x);
		
		double x = x0, y = 0;
		for (int i = 0; i < system.n(); i++) {
			soln[3*i][0] = x;
			soln[3*i][1] = y;
			
			y = f.applyAsDouble(x);
			soln[3*i+1][0] = x;
			soln[3*i+1][1] = y;
			
			x = y;
			soln[3*i+2][0] = x;
			soln[3*i+2][1] = y;
		}

		return soln;
	}
}
