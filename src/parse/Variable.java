package parse;

public class Variable extends TreeOperator {
	private String sym;
	
	public Variable(String sym) {
		this.sym = sym;
	}
	
	@Override
	public double applyTo(TreeNode... nodes) {
		double val = 0.0;
		
		if (nodes.length > 0) {
			for (TreeNode node : nodes) {
				val += node.getValue();
			}
		}
		
		return val;
	}

	@Override
	public String toString() {
		return sym;
	}
	
	
}

