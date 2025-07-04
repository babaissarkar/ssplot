/*
 * EquationSystem.java
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

package com.babai.ssplot.math.system.core;

import java.util.Arrays;
import java.util.function.DoubleConsumer;

/**
 * Class that holds data for system of equations.
 * Do not add Getter/Setters, use public field access. Causes bloat.
 * (until some sort of automatic lang support for that exists)
 **/
public class EquationSystem {
	public static final int DIM = 3;             /* Maximum dimension of system (global)    */
	public static final int DEFAULT_N = 1000;    /* Default number of iterations for RK4    */
	public static final double DEFAULT_H = 0.05; /* Default stepsize for RK4                */
	public static final Range DEFAULT_RANGE =
			new Range(-10, 10, 0.1);             /* Default range for the variables x, y, z */
	
	public String[] eqns = new String[DIM];      /* The equations                 */
	public Range[] ranges = new Range[DIM];      /* Ranges for each variable      */
	public SystemMode mode = SystemMode.ODE;     /* Identifies the type of system */
	public int n = DEFAULT_N;                    /* Iteration count (ODE/DE)      */
	public double h = DEFAULT_H;                 /* Step Size (ODE)               */
	public double[] solnPoint = {0.0, 0.0, 0.0}; /* Point where the system is to be solved */
	
	public EquationSystem() {
		Arrays.fill(eqns, "");
		Arrays.fill(ranges, new Range(-10, 10, 0.1));
	}
	
	public int numberOfEqns() {
		return (int) Arrays.stream(eqns).filter(eqn -> !eqn.isEmpty() && !eqn.isBlank()).count();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("System Details:\n");

		sb.append("Equations:\n");
		for (int i = 0; i < numberOfEqns(); i++) {
			sb.append("  Equation ").append(i + 1).append(": ").append(eqns[i]).append("\n");
		}

		// NOTE no of eqns == number of ranges
		// TODO perhaps should be programmatically enforced?
		// like a variable object?
		// EqnVar x = new EqnVar("x", new Range(min, max, step));
		sb.append("Ranges:\n");
		for (int i = 0; i < numberOfEqns(); i++) {
			sb.append("  Range ").append(i + 1).append(": ").append(ranges[i]).append("\n");
		}

		sb.append("System Mode: ").append(mode).append("\n");
		sb.append("Iteration Count (n): ").append(n).append("\n");
		sb.append("Step Size (h): ").append(h).append("\n");

		return sb.toString();
	}

	
	/** Small record for storing range info for each independent variable */
	public record Range(double min, double max, double step) {
		// Allows looping over this range
		public void forEach(DoubleConsumer consumer) {
			double i = min;
			while (i <= max) {
				consumer.accept(i);
				i += step;
			}
		}
	}

}
