package math.system.parser;

import java.util.Map;

public interface Parser {
	// Evaluate an expression using this parser
	double evaluate(String expression, Map<String, Double> variables);
	
	public String getName();
}
