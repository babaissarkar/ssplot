package com.babai.ssplot.math.plot;

import java.util.*;

public class Stats {

	public static double mean(Vector<Double> data) {
		return data.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
	}

	public static double variance(Vector<Double> data) {
		double mean = mean(data);
		return data.stream().mapToDouble(d -> Math.pow(d - mean, 2)).sum() / data.size();
	}

	public static double stdDev(Vector<Double> data) {
		return Math.sqrt(variance(data));
	}

	public static double skewness(Vector<Double> data) {
		double mean = mean(data);
		double sd = stdDev(data);
		int n = data.size();
		return data.stream().mapToDouble(d -> Math.pow((d - mean) / sd, 3)).sum() * (double) n / ((n - 1) * (n - 2));
	}

	public static double kurtosis(Vector<Double> data) {
		double mean = mean(data);
		double sd = stdDev(data);
		int n = data.size();
		return (n * data.stream().mapToDouble(d -> Math.pow((d - mean) / sd, 4)).sum()) / ((double) (n - 1) * (n - 2) * (n - 3)) - 3.0;
	}

	public static double percentile(Vector<Double> data, double p) {
		Vector<Double> sorted = new Vector<>(data);
		Collections.sort(sorted);
		double index = p * (sorted.size() - 1);
		int lo = (int) Math.floor(index);
		int hi = (int) Math.ceil(index);
		return (sorted.get(lo) + sorted.get(hi)) / 2.0;
	}
	
	public static double quartile(Vector<Double> vals, double q) {
		if (vals.isEmpty()) return Double.NaN;
		double pos = (vals.size() - 1) * q;
		int base = (int) Math.floor(pos);
		double frac = pos - base;
		if (base + 1 < vals.size())
			return vals.get(base) * (1 - frac) + vals.get(base + 1) * frac;
		else
			return vals.get(base);
	}

	public static double q1(Vector<Double> data) { return percentile(data, 0.25); }
	public static double median(Vector<Double> data) { return percentile(data, 0.5); }
	public static double q3(Vector<Double> data) { return percentile(data, 0.75); }
}
