package math.system.core;

import java.util.Vector;

public class EquationSystem {
	private final int dim = 3;      /* Dimension of this system     */
	
	private Vector<Equation> eqns;
	private int n;                  /* iteration count (ODE/DE)     */
	private double h;               /* stepsize (ODE)               */
	
	// Class that holds data for an equation system
	private EquationSystem() {
		eqns = new Vector<>(dim);
		
		n = 1000;
		h = 0.05;

// TODO what about default values for equations?
//		for (int i = 0; i < dim; i++) {
//			eqns.add(new Equation("", 10, -10, 1));
//		}
	}
	
	/* ***** GETTERS ******* */
	public Equation get(int index) { return eqns.get(index); }
	public int n()    { return n; }
	public double h() { return h; }
	
	// Builds the EquationSystem
	public static class Builder {
		private EquationSystem system;
		
		public Builder() {
			system = new EquationSystem();
		}
		
		public void addEquation(Equation eqn) {
			system.eqns.add(eqn);
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
