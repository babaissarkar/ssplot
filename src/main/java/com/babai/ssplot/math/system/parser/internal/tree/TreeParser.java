/*
 * TreeParser.java
 * 
 * Copyright 2021-2025 Subhraman Sarkar <suvrax@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 * 
 */

package com.babai.ssplot.math.system.parser.internal.tree;

import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.babai.ssplot.math.system.parser.Parser;
import com.babai.ssplot.math.system.parser.internal.SSMathParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TreeParser implements Parser {
	public static final String NAME = "Internal"; 
	
	@Override
	public double evaluate(String expression, Map<String, Double> variables) {
		return parse(expression).evalAt(variables);
	}

	@Override
	public String getName() {
		return NAME;
	}

	public TreeNode parse(String token) {
		try {
			return SSMathParser.parseString(token);
		} catch(Exception e) {
			return new TreeNode(new Constant(0.0));
		}
	}
}

