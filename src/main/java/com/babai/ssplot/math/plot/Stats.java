/*
 * Stats.java
 *
 * Copyright 2025-2026 Subhraman Sarkar <suvrax@gmail.com>
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
 */

package com.babai.ssplot.math.plot;

import java.util.*;

public class Stats {

	public static double mean(double[] data) {
		return Arrays.stream(data).average().orElse(Double.NaN);
	}

	public static double variance(double[] data) {
		double mean = mean(data);
		return Arrays.stream(data).map(d -> Math.pow(d - mean, 2)).sum() / data.length;
	}

	public static double stdDev(double[] data) {
		return Math.sqrt(variance(data));
	}

	public static double skewness(double[] data) {
		double mean = mean(data);
		double sd = stdDev(data);
		int n = data.length;
		return Arrays.stream(data).map(d -> Math.pow((d - mean) / sd, 3)).sum() * (double) n / ((n - 1) * (n - 2));
	}

	public static double kurtosis(double[] data) {
		double mean = mean(data);
		double sd = stdDev(data);
		int n = data.length;
		return (n * Arrays.stream(data).map(d -> Math.pow((d - mean) / sd, 4)).sum()) / ((double) (n - 1) * (n - 2) * (n - 3)) - 3.0;
	}

	public static double percentile(double[] data, double p) {
		double[] sorted = data;
		Arrays.sort(sorted);
		double index = p * (sorted.length - 1);
		int lo = (int) Math.floor(index);
		int hi = (int) Math.ceil(index);
		return (sorted[lo] + sorted[hi]) / 2.0;
	}
	
	public static double quartile(double[] vals, double q) {
		if (vals.length == 0) return Double.NaN;
		double pos = (vals.length - 1) * q;
		int base = (int) Math.floor(pos);
		double frac = pos - base;
		return (base + 1 < vals.length)
			? vals[base] * (1 - frac) + vals[base + 1] * frac
			: vals[base];
	}

	public static double q1(double[] data) { return percentile(data, 0.25); }
	public static double median(double[] data) { return percentile(data, 0.5); }
	public static double q3(double[] data) { return percentile(data, 0.75); }

	public static double max(double[] column) {
		double max = column[0];
		for (int i = 1; i < column.length; i++) {
			max = Math.max(column[i], max);
		}
		return max;
	}
	
	public static double min(double[] column) {
		double min = column[0];
		for (int i = 1; i < column.length; i++) {
			min = Math.min(column[i], min);
		}
		return min;
	}
	
	/** Calculate max + mins for all columns at once to avoid multiple loops */
	public static double[][] parallelMaxMin(double[][] data) {
		int cols = data[0].length;
		double[] max = Arrays.copyOf(data[0], cols);
		double[] min = Arrays.copyOf(data[0], cols);
		
		for (int i = 1; i < data.length; i++) {
			for (int j = 0; j < data[0].length; j++) {
				max[j] = Math.max(data[i][j], max[j]);
				min[j] = Math.min(data[i][j], min[j]);
			}
		}
		return new double[][] { max, min };
	}
}
