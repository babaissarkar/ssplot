/*
 * FnOperator.java
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

public class FnOperator extends TreeOperator {
	/** Abstract representation of a function, like sine, cosine functions etc. */
    public enum FnType {SIN, COS, TAN, ASIN, ACOS, ATAN, EXP, LOG, LN};
    private FnType type;

    public FnOperator(FnType type) {
        this.type = type;
    }
    
    public static FnOperator forName(String name) {
    	for (var type : FnType.values()) {
    		if (type.name().equalsIgnoreCase(name)) {
    			return new FnOperator(type);
    		}
    	}
    	
    	return null;
    }

	@Override
	public double applyTo(TreeNode... nodes) {
		double result = 0.0;

        if (nodes.length >= 1) {
            switch(type) {
                case SIN :
                    result = Math.sin(nodes[0].getValue());
                    break;
                case COS :
                    result = Math.cos(nodes[0].getValue());
                    break;
                case TAN :
                    result = Math.tan(nodes[0].getValue());
                    break;
                case EXP :
                    result = Math.exp(nodes[0].getValue());
                    break;
                case LOG :
                    result = Math.log10(nodes[0].getValue());
                    break;
                case LN :
                    result = Math.log(nodes[0].getValue());
                    break;
                case ASIN :
                    result = Math.asin(nodes[0].getValue());
                    break;
                case ACOS :
                	result = Math.acos(nodes[0].getValue());
                    break;
                case ATAN :
                    result = Math.atan(nodes[0].getValue());
                    break;
                default :
                    // Identity function. Does nothing.
                    result = nodes[0].getValue();
                    
            }
        }
        
		return result;
	}
	
	@Override
	public String toString() {
		return type.toString().toLowerCase();
	}

}
