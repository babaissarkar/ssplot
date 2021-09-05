package parse;

public class FnOperator extends TreeOperator {
	/** Abstract representation of a function, like sine, cosine functions etc. */
    public enum FnType {SIN, COS, TAN, ASIN, ACOS, ATAN, EXP, LOG, LN};
    private FnType type;

    public FnOperator(FnType type) {
        this.type = type;
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
