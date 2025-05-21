package math.system.core;

public record Equation(
		String eqn, /* The equation */
		double min, /* Upper limit to which this is to be solved */
		double max, /* Lower limit to which this is to be solved */
		double gap  /* Difference between consecutive solution points */)
{
	@Override
	public String toString() {
		return eqn;
	}
}
