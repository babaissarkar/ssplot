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
import java.util.Iterator;

/**
 * Class that holds data for system of equations.
 **/
public record EquationSystem (
		String[] eqns,      /* The equations                 */
		Range[] ranges,     /* Ranges for each variable      */
		SystemMode mode,    /* Identifies the type of system */
		int n,              /* Iteration count (ODE/DE)      */
		double h,           /* Step Size (ODE)               */
		double[] solnPoint  /* Point where the system is to be solved */
		)
{
	public static final int DIM = 3;             /* Maximum dimension of system (global)    */
	public static final int DEFAULT_N = 1000;    /* Default number of iterations for RK4    */
	public static final double DEFAULT_H = 0.05; /* Default stepsize for RK4                */
	public static final Range DEFAULT_RANGE =
			new Range(-10, 10, 0.1);             /* Default range for the variables x, y, z */
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

	public static class Builder {
		private String[] eqns = new String[DIM];
		private Range[] ranges = new Range[DIM];
		private SystemMode mode = SystemMode.ODE;
		private int n = DEFAULT_N;
		private double h = DEFAULT_H;
		private double[] solnPoint = {0.0, 0.0, 0.0};

		public Builder() {
			Arrays.fill(eqns, "");
			Arrays.fill(ranges, DEFAULT_RANGE);
		}
		
		public int numberOfEqns() {
			return (int) Arrays.stream(eqns).filter(eqn -> !eqn.isEmpty() && !eqn.isBlank()).count();
		}
		
		public Range[] ranges() {
			return Arrays.copyOf(this.ranges, this.ranges.length);
		}
		
		public Builder fromSystem(EquationSystem sys) {
			eqns = Arrays.copyOf(sys.eqns(), sys.eqns().length);
			ranges = Arrays.copyOf(sys.ranges(), sys.ranges().length);
			solnPoint = Arrays.copyOf(sys.solnPoint(), sys.solnPoint().length);
			mode = sys.mode();
			n = sys.n();
			h = sys.h();
			return this;
		}

		public Builder eqn(int index, String eqn) {
			if (index < 0 || index >= DIM) throw new IllegalArgumentException("Invalid equation index");
			this.eqns[index] = eqn;
			return this;
		}

		public Builder range(int index, Range range) {
			if (index < 0 || index >= DIM) throw new IllegalArgumentException("Invalid range index");
			this.ranges[index] = range;
			return this;
		}

		public Builder mode(SystemMode mode) {
			this.mode = mode;
			return this;
		}

		public Builder n(int n) {
			this.n = n;
			return this;
		}

		public Builder h(double h) {
			this.h = h;
			return this;
		}
		
		public Builder solnPoint(int idx, double val) {
			this.solnPoint[idx] = val;
			return this;
		}

		public Builder solnPoint(double x, double y, double z) {
			this.solnPoint = new double[] {x, y, z};
			return this;
		}

		public EquationSystem build() {
			return new EquationSystem(
				Arrays.copyOf(eqns, eqns.length),
				ranges.clone(),
				mode,
				n,
				h,
				Arrays.copyOf(solnPoint, solnPoint.length)
			);
		}
	}

	/** Small record for storing range info for each independent variable */
	public record Range(double start, double end, double step) implements Iterable<Double> {

		public Range(double start, double end) {
			this(start, end, start < end ? 1 : -1);
		}

		public Range(double end) {
			this(0, end, end > 0 ? 1 : -1);
		}

		@Override
		public Iterator<Double> iterator() {
			return new Iterator<Double>() {
				Double current = start;

				@Override
				public boolean hasNext() {
					if (step == 0) throw new IllegalArgumentException("Step cannot be zero!");
					return step > 0 ? current < end : current > end;
				}

				@Override
				public Double next() {
					current += step;
					return current;
				}

			};
		}

		/** NOTE: does not include endpoint */
		public int count() {
			return (int) Math.floor((end - start) / step);
		}

		public double[] toArray() {
			return new double[] { start(), end(), step() };
		}


		public void printAllPoints() {
			for (double d : this) {
				System.out.println(d);
			}
		}
	}

}
