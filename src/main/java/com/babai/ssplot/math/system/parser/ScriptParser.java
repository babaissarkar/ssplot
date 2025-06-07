/*
 * ScriptParser.java
 * 
 * Copyright 2025 Subhraman Sarkar <suvrax@gmail.com>
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

package com.babai.ssplot.math.system.parser;

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
		if (expression.isEmpty()) {
			return 0;
		}
		
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
