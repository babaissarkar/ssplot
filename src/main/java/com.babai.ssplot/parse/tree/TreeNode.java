/*
 * TreeNode.java
 * 
 * Copyright 2021-2025 Subhraman Sarkar <suvrax@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 * 
 */

package parse.tree;

import java.util.Vector;

public class TreeNode {
	private TreeOperator nodeOp = null;
	private Vector<TreeNode> nodes = new Vector<TreeNode>();
	
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
	
	public double evalAt(String var, double value) {
		scanVariables(var, value);
		return getValue();
	}
	
	public double evalAt(String var1, double value1, String var2, double value2) {
		scanVariables(var1, value1);
		scanVariables(var2, value2);
		return getValue();
	}
	
	public double evalAt(String var1, double value1, String var2, double value2, String var3, double value3) {
		scanVariables(var1, value1);
		scanVariables(var2, value2);
		scanVariables(var3, value3);
		return getValue();
	}
	
	public void scanVariables(String var, double value) {
		//System.out.println("Scanning Variables in node " + this.toString());
        if (!(this.nodeOp instanceof Variable)) {
            for (TreeNode node : nodes) {
                TreeOperator nodeOp = node.getNodeOperator();
                if ((nodeOp instanceof Variable) && (nodeOp.toString().equals(var))) {
                    if (node.getChildCount() > 0) {
                        node.removeChilds();
                    }
                    node.addChild(new TreeNode(new Constant(value)));
                } else if (nodeOp instanceof Constant) {
                    // Stop recursion
                } else {
                    node.scanVariables(var, value);
                }
            }
        } else {
            if ((this.nodeOp instanceof Variable) && (this.nodeOp.toString().equals(var))) {
                if (this.getChildCount() > 0) {
                    this.removeChilds();
                }
                this.addChild(new TreeNode(new Constant(value)));
            }
        }
	}
	
	public void addChild(TreeNode node) {
		nodes.add(node);
	}
	
	public void removeChilds() {
		nodes.removeAllElements();
	}
	
	public Vector<TreeNode> getChilds() {
		return nodes;
	}
	
	public int getChildCount() {
		return nodes.size();
	}
	
	public TreeOperator getNodeOperator() {
		return nodeOp;
	}
	
	/** Returns the complete tree in Lisp notation. */
	public String toString() {
		StringBuffer buff = new StringBuffer();
		if (!(nodeOp instanceof Constant) && !(nodeOp instanceof Variable)) {
			buff.append("(");
		} else {
            if (nodeOp instanceof Variable) {
                if (this.getChildCount() > 0) {
                    buff.append("(");
                    buff.append("setf ");
                }
            }
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
		if (!(nodeOp instanceof Constant) && !(nodeOp instanceof Variable)) {
			buff.append(")");
		} else {
            if (nodeOp instanceof Variable) {
                if (this.getChildCount() > 0) {
                    buff.append(")");
                }
            }
        }
        
		return buff.toString();
	}
}
