package parse;

public class Constant extends TreeOperator {
	/** Abstract representation of Constant */
	
	public double val;
	
	public Constant(double val) {
		this.val = val;
	}

	@Override
	public double applyTo(TreeNode... nodes) {
		return val;
	}
	
	@Override
	public String toString() {
		return ""+val;
	}

}
