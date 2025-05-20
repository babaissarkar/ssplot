package math.system.core;

public record Equation(
		String eqn, /* The equation */
		double max, /* Upper limit to which this is to be solved */
		double min, /* Lower limit to which this is to be solved */
		double gap  /* Difference between consecutive solution points */)
{}
