package math.system.parser;

import java.util.Map;

import math.system.parser.internal.tree.TreeParser;

public class InternalParser implements Parser {
	private TreeParser parser = new TreeParser();

	@Override
	public double evaluate(String expression, Map<String, Double> variables) {
		return parser.parse(expression).evalAt(variables);
	}

	@Override
	public String getName() {
		return "Internal";
	}

}
