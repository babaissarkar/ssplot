package parse;

public class AddOperator extends TreeOperator {
	/** Abstract representation of Addition operation */

	@Override
	public double applyTo(TreeNode... nodes) {
		double result = 0.0;
        
        for (TreeNode node : nodes) {
            result += node.getValue();
		}
        
		return result;
	}
	
	@Override
	public String toString() {
		return "+";
	}

}
