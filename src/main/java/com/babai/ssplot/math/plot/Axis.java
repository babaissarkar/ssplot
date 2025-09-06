package com.babai.ssplot.math.plot;

public interface Axis {
	public enum Cartesian implements Axis { X, Y, Z; }
	public enum Polar implements Axis {
		R("R"), THETA("θ"), PHI("φ");
		
		private final String label;
		private Polar(String label) {
			this.label = label;
		}
		
		@Override
		public String toString() {
			return this.label;
		}
	}
}
