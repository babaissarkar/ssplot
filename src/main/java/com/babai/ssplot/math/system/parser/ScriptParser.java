/*
 * ScriptParser.java
 * 
 * Copyright 2025-2026 Subhraman Sarkar <suvrax@gmail.com>
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

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import com.babai.ssplot.ui.CrashFrame;

public class ScriptParser implements Parser {
	private String name;
	private ScriptEngine engine;
	private String[] varNames;
	
	@Override
	public void setVariables(String... varNames) {
		this.varNames = varNames;
	}
	
	public ScriptParser(String name, ScriptEngine engine) {
		this.name = name;
		this.engine = engine;
	}

	@Override
	public double evaluate(String expression, double... variables) {
		if (expression.isEmpty()) {
			return 0;
		}
		
		for (int i = 0; varNames != null && i < varNames.length; i++) {
			engine.put(varNames[i], variables[i]);
		}
		
		try {
			engine.eval("answer = " + expression);
			return (double) engine.get("answer");
		} catch (ScriptException e) {
			CrashFrame.showCrash(e);
		}
		return 0;
	}

	@Override
	public String getName() {
		return name;
	}

}
