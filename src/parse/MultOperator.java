package parse;

public class MultOperator extends TreeOperator {
	/** Abstract representation of Multiplication operation */

	@Override
	public double applyTo(TreeNode... nodes) {
		double result = 1.0;
        
        for (TreeNode node : nodes) {
            result *= node.getValue();
		}
        
		return result;
	}
	
	@Override
	public String toString() {
		return "*";
	}

}
