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

import java.util.List;
import java.util.Map;
import java.util.Vector;
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
	
	/******************************************************************/
	/** Solve the system of equations by RK 4th order method */
	/******************************************************************/
	public Vector<Vector<Double>> RK4Iterate(double x0, double y0) {
		System.out.println("2D RK4 started.");
		var soln = new Vector<Vector<Double>>();
		if (!validate(2)) { // 2d system
			System.err.println("Invalid System for 2D RK4 iteration!");
			return soln;
		}
		
		Evaluator2D dx_dt = (x, y) -> parser.evaluate(system.get(0), Map.of("x", x, "y", y));		
		Evaluator2D dy_dt = (x, y) -> parser.evaluate(system.get(1), Map.of("x", x, "y", y));
		
		int n;
		double x, y, h;
		double k1, k2, k3, k4;
		double p1, p2, p3, p4;

		x = x0;
		y = y0;
		h = system.h();
		n = system.n();

		for (int i = 0; i < n; i++) {
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

			soln.add(new Vector<>(List.of(x, y)));
		}

		return soln;
	}

	public Vector<Vector<Double>> RK4Iterate3D(double x0, double y0, double z0) {
		System.out.println("3D RK4 started.");
		var soln = new Vector<Vector<Double>>();
		if (!validate(3)) { // 3d system
			System.err.println("Invalid System for 3D RK4 iteration!");
			return soln;
		}
		
		Evaluator3D dx_dt = (x, y, z) -> parser.evaluate(system.get(0), Map.of("x", x, "y", y, "z", z));
		Evaluator3D dy_dt = (x, y, z) -> parser.evaluate(system.get(1), Map.of("x", x, "y", y, "z", z));
		Evaluator3D dz_dt = (x, y, z) -> parser.evaluate(system.get(2), Map.of("x", x, "y", y, "z", z));
		
		int n;
		double x, y, z, h;
		double k1, k2, k3, k4;
		double p1, p2, p3, p4;
		double q1, q2, q3, q4;

		x = x0;
		y = y0;
		z = z0;
		h = system.h();
		n = system.n();

		for (int i = 0; i < n; i++) {
			k1 = h * dx_dt.of(x, y, z);
			p1 = h * dy_dt.of(x, y, z);
			q1 = h * dz_dt.of(x, y, z);

			//            System.out.println(k1 + "," + p1 + "," + q1);

			k2 = h * dx_dt.of(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);
			p2 = h * dy_dt.of(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);
			q2 = h * dz_dt.of(x + 0.5 * k1, y + 0.5 * p1, z + 0.5 * q1);

			//            System.out.println(k2 + "," + p2 + "," + q2);

			k3 = h * dx_dt.of(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);
			p3 = h * dy_dt.of(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);
			q3 = h * dz_dt.of(x + 0.5 * k2, y + 0.5 * p2, z + 0.5 * q2);

			//            System.out.println(k3 + "," + p3 + "," + q3);

			k4 = h * dx_dt.of(x + k3, y + p3, z + q3);
			p4 = h * dy_dt.of(x + k3, y + p3, z + q3);
			q4 = h * dz_dt.of(x + k3, y + p3, z + q3);

			//            System.out.println(k4 + "," + p4 + "," + q4);

			x += (k1 + 2*k2 + 2*k3 + k4)/6.0;
			y += (p1 + 2*p2 + 2*p3 + p4)/6.0;
			z += (q1 + 2*q2 + 2*q3 + q4)/6.0;

			soln.add(new Vector<>(List.of(x, y, z)));
		}

		return soln;
	}


	/** Gets the data for the direction field. */
	public Vector<Vector<Double>> directionField() {
		var data = new Vector<Vector<Double>>();
		// TODO dimension validation?
		String eqn1 = system.get(0);
		String eqn2 = system.get(1);
		
		Evaluator2D dx_dt = (x, y) -> parser.evaluate(eqn1, Map.of("x", x, "y", y));
		Evaluator2D dy_dt = (x, y) -> parser.evaluate(eqn2, Map.of("x", x, "y", y));
		
		system.getRange(0).forEach(i ->
			system.getRange(1).forEach(j -> {
				double Xdot, Ydot;
				double X1, Y1, X2, Y2;
				double r;
				Xdot = dx_dt.of(i, j);
				Ydot = dy_dt.of(i, j);
				X1 = i;
				Y1 = j;
				X2 = X1 + Xdot;
				Y2 = Y1 + Ydot;
				/* Normalizing */
				r = Math.hypot(X2 - X1, Y2 - Y1);
				X2 = X1 + Xdot/r;
				Y2 = Y1 + Ydot/r;
				data.add(new Vector<>(List.of(X1, Y1, X2, Y2)));
			})
		);
		return data;
	}

	public Vector<Vector<Double>> iterateMap(double x0, double y0) {
		var soln = new Vector<Vector<Double>>();
		Evaluator2D x2 = (x, y) -> parser.evaluate(system.get(0), Map.of("x", x, "y", y));
		Evaluator2D y2 = (x, y) -> parser.evaluate(system.get(1), Map.of("x", x, "y", y));
		
		double x = x0;
		double y = y0;
		int n = system.n();

		for (int i = 0; i < n; i++) {
			soln.add(new Vector<>(List.of(x, y)));

			double tempX = x2.of(x, y);
			double tempY = y2.of(x, y);
			x = tempX;
			y = tempY;
		}

		return soln;
	}

	public Vector<Vector<Double>> functionData() {
		var soln = new Vector<Vector<Double>>();
		String eqn = system.get(0);
		DoubleUnaryOperator x2 = x -> parser.evaluate(eqn, Map.of("x", x));
		system.getRange(0).forEach(i -> soln.add(new Vector<>(List.of(i, x2.applyAsDouble(i)))));
		return soln;
	}

	public Vector<Vector<Double>> functionData2D() {
		var soln = new Vector<Vector<Double>>();
		String eqn1 = system.get(0);		
		Evaluator2D f = (x, y) -> parser.evaluate(eqn1, Map.of("x", x, "y", y));

		system.getRange(0).forEach(i ->
			system.getRange(1).forEach(j ->
				soln.add(new Vector<>(List.of(i, j, f.of(i, j))))
			)
		);

		return soln;
	}

	public Vector<Vector<Double>> cobweb(double x0) {
		/* Works for 1D maps only */
		var soln = new Vector<Vector<Double>>();
		DoubleUnaryOperator x2 = x -> parser.evaluate(system.get(0), Map.of("x", x));
		
		int n = system.n();
		double x = x0, y = 0;

		for (int i = 0; i < n; i++) {
			soln.add(new Vector<>(List.of(x, y)));
			y = x2.applyAsDouble(x);
			soln.add(new Vector<>(List.of(x, y)));
			x = y;
			soln.add(new Vector<>(List.of(x, y)));
		}

		return soln;
	}
}
