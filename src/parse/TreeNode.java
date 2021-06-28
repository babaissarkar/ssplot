package parse;

import java.util.Vector;

public class TreeNode {
	public TreeOperator nodeOp = null;
	public Vector<TreeNode> nodes = new Vector<TreeNode>();
	
	public TreeNode(TreeOperator op) {
		this.nodeOp = op;
	}
	
	/** 
	 * Calculates the value of this node by
	 * applying the node operation with all child nodes as operands.
	 * @return the value of this equation tree
	 */
	public double getValue() {
		double res = 0;
		TreeNode[] a = new TreeNode[nodes.size()];
		if (nodeOp != null) {
			res = nodeOp.applyTo(nodes.toArray(a));
		}
		return res;
	}
	
	public void addChild(TreeNode node) {
		nodes.add(node);
	}
	
	/** Returns the complete tree in Lisp notation. */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		if (!(nodeOp instanceof Constant)) {
			buff.append("(");
		}
		buff.append(this.nodeOp.toString());
		buff.append(" ");
		for (TreeNode node : nodes) {
			if (node != null) {
				buff.append(node.toString());
				buff.append(" ");
			}
		}
		buff.deleteCharAt(buff.length()-1);
		if (!(nodeOp instanceof Constant)) {
			buff.append(")");
		}
		return buff.toString();
	}
	
	public static void main(String... args) {
		TreeNode root = new TreeNode(new AddOperator());
		TreeNode root2 = new TreeNode(new SubOperator());
		TreeNode ch1 = new TreeNode(new Constant(5));
		TreeNode ch2 = new TreeNode(new Constant(6));
		TreeNode ch3 = new TreeNode(new Constant(7));
		root2.addChild(ch2);
		root2.addChild(ch3);
		root.addChild(ch1);
		root.addChild(root2);
		System.out.println(root.toString());
		System.out.println("Value : " + root.getValue());
	}
}
