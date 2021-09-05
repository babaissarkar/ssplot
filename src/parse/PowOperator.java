package parse;

public class PowOperator extends TreeOperator {
	/** Abstract representation of exponentiation operation */

	@Override
	public double applyTo(TreeNode... nodes) {
		double result = nodes[0].getValue();

        for (int i = 1; i < nodes.length; i++) {
            result = Math.pow(result, nodes[i].getValue());
        }
        
		return result;
	}
	
	@Override
	public String toString() {
		return "expt";
	}

}
