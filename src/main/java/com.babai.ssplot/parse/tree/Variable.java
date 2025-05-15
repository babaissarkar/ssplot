/*
 * Variable.java
 * 
 * Copyright 2021-2025 Subhraman Sarkar <suvrax@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 */

package parse.tree;

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

