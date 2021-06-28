package parse;

public class AddOperator extends TreeOperator {
	/** Abstract representation of Addition operation */

	@Override
	public double applyTo(TreeNode... nodes) {
		double result = 0.0;
		double d1 = nodes[0].getValue();
		double d2 = nodes[1].getValue();
		result = d1 + d2;
		return result;
	}
	
	@Override
	public String toString() {
		return "+";
	}

}
