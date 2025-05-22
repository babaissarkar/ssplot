/*
 * DivOperator.java
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

package math.system.parser.internal.tree;

public class DivOperator extends TreeOperator {
	/** Abstract representation of Division operation */

	@Override
	public double applyTo(TreeNode... nodes) {
		double result = 1.0;
		if (nodes.length == 1) {
            double d1 = nodes[0].getValue();
            result = 1.0/d1;
        } else if (nodes.length >= 2) {
            double d1 = nodes[0].getValue(); 

            for (int i = 1; i < nodes.length; i++) {
                result *= nodes[i].getValue();
            }

            result = d1/result;
        }
		return result;
	}
	
	@Override
	public String toString() {
		return "/";
	}

}
