package math.system.core;

import java.util.Vector;

/** Class that holds data for system of equations */
public class EquationSystem {
	public static final int DIM = 3; /* Max dimension (global)        */
	
	private Vector<Equation> eqns;   /* The equations                 */
	private SystemMode mode;         /* Identifies the type of system */
	private int n;                   /* iteration count (ODE/DE)      */
	private double h;                /* stepsize (ODE)                */
	
	private EquationSystem() {
		eqns = new Vector<>(DIM);
		
		n = 1000;
		h = 0.05;

// TODO what about default values for equations?
//		for (int i = 0; i < dim; i++) {
//			eqns.add(new Equation("", 10, -10, 1));
//		}
	}
	
	/* ***** GETTERS ******* */
	public Equation get(int index) { return eqns.get(index); }
	public SystemMode getMode()    { return mode; }
	public int n()    { return n; }
	public double h() { return h; }
	public int dim()  { return eqns.size(); }
	
	/** Builder class for the EquationSystem */
 	public static class Builder {
		private EquationSystem system;
		
		public Builder() {
			system = new EquationSystem();
		}
		
		public void addEquation(Equation eqn) {
			system.eqns.add(eqn);
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
