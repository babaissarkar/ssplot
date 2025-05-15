/*
 * TreeOperator.java
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

public abstract class TreeOperator {
	
	public abstract double applyTo(TreeNode... nodes);

    public static TreeOperator forName(String op2) {
        String op = op2.toLowerCase();
        if (op.equals("+")) {
            return new AddOperator();
        } else if (op.equals("-")) {
            return new SubOperator();
        } else if (op.equals("*")) {
            return new MultOperator();
        } else if (op.equals("/")) {
            return new DivOperator();
        } else if (op.equals("^")) {
            return new PowOperator();
        } else if (op.startsWith("sin")) {
            return new FnOperator(FnOperator.FnType.SIN);
        } else if (op.startsWith("cos")) {
            return new FnOperator(FnOperator.FnType.COS);
        } else if (op.startsWith("tan")) {
            return new FnOperator(FnOperator.FnType.TAN);
        } else {
            System.err.println("Invalid operator : " + op);
            return null;
        }
    }
}
