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

import java.util.Vector;
import java.util.function.DoubleConsumer;

/** Class that holds data for system of equations */
public class EquationSystem {
	public static final int DIM = 3;             /* Maximum dimension of system (global)    */
	
	public static final int DEFAULT_N = 1000;    /* Default number of iterations for RK4    */
	public static final double DEFAULT_H = 0.05; /* Default stepsize for RK4                */
	public static final Range DEFAULT_RANGE =
			new Range(-10, 10, 0.1);             /* Default range for the variables x, y, z */
	
	private Vector<String> eqns;     /* The equations                 */
	private Vector<Range> ranges;    /* Ranges for each variable      */
	private SystemMode mode;         /* Identifies the type of system */
	private int n;                   /* iteration count (ODE/DE)      */
	private double h;                /* stepsize (ODE)                */
	
	private EquationSystem() {
		eqns = new Vector<>(DIM);
		ranges = new Vector<>(DIM);
		n = DEFAULT_N;
		h = DEFAULT_H;
	}
	
	/* ***** GETTERS ******* */
	public String get(int index)     { return eqns.get(index); }
	public Range getRange(int index) { return ranges.get(index); }
	public SystemMode getMode()      { return mode; }
	public int numberOfEqns()        { return eqns.size(); }
	public int n()                   { return n; }
	public double h()                { return h; }
	
	/* ***** SETTERS ******* */
	public void set(int index, String eqn)       { eqns.set(index, eqn); }
	public void setRange(int index, Range range) { ranges.set(index, range); }
	public void setN(int n)                      { this.n = n; }
	public void setH(double h)                   { this.h = h; }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("System Details:\n");

		sb.append("Equations:\n");
		for (int i = 0; i < eqns.size(); i++) {
			sb.append("  Equation ").append(i + 1).append(": ").append(eqns.get(i)).append("\n");
		}

		sb.append("Ranges:\n");
		for (int i = 0; i < ranges.size(); i++) {
			sb.append("  Range ").append(i + 1).append(": ").append(ranges.get(i)).append("\n");
		}

		sb.append("System Mode: ").append(mode).append("\n");
		sb.append("Iteration Count (n): ").append(n).append("\n");
		sb.append("Step Size (h): ").append(h).append("\n");

		return sb.toString();
	}

	
	/** Small record for storing range info for each independent variable */
	public record Range(double min, double max, double step) {
		/** Allows looping over this range */
		public void forEach(DoubleConsumer consumer) {
			double i = min;
			while (i <= max) {
				consumer.accept(i);
				i += step;
			}
		}
	}
	
	// FIXME This will be removed soon.
	/** Builder class for the EquationSystem */
 	public static class Builder {
		private EquationSystem system;
		
		public Builder() {
			system = new EquationSystem();
		}
		
		public void addEquation(String eqn) {
			system.eqns.add(eqn);
		}
		
		public void addRange(double min, double max, double step) {
			system.ranges.add(new Range(min, max, step));
		}
		
		public void setMode(SystemMode mode) {
			system.mode = mode;
		}
		
 		public void setCount(int n) {
			system.n = n;
		}
		
		public void setStepSize(double h) {
			system.h = h;
		}
		
		public EquationSystem build() {
			return system;
		}
	}
}
