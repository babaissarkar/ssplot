package parse;

public class SubOperator extends TreeOperator {
	/** Abstract representation of Subtraction. */

	@Override
	public double applyTo(TreeNode... nodes) {
		double result = 0.0;
        if (nodes.length == 1) {
            double d1 = nodes[0].getValue();
            result = -d1;
        } else if (nodes.length >= 2) {
            double d1 = nodes[0].getValue();

            for (int i = 1; i < nodes.length; i++) {
                result += nodes[i].getValue();
            }

            result = d1 - result;
        }
		
		return result;
	}
	
	@Override
	public String toString() {
		return "-";
	}

}
