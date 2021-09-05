package parse;

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
