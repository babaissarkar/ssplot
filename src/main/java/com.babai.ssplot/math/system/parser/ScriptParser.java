package math.system.parser;

import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

public class ScriptParser implements Parser {
	private String name;
	private ScriptEngine engine;
	
	public ScriptParser(String name, ScriptEngine engine) {
		this.name = name;
		this.engine = engine;
	}

	@Override
	public double evaluate(String expression, Map<String, Double> variables) {
		variables.forEach((var, val) -> engine.put(var, val));
		try {
			engine.eval("answer = " + expression);
			return (double) engine.get("answer");
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

}
