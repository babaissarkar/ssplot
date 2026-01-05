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

import java.util.LinkedHashMap;
import java.util.Map;
import com.babai.ssplot.math.system.parser.Parser;
import com.babai.ssplot.math.system.parser.internal.SSMathParser;

public class TreeParser implements Parser {
	private static final String NAME = "Internal";
	private String[] varNames;
	
	private static final Map<String, TreeNode> exprCache = new LinkedHashMap<>(16, 0.75f, true) {
		private static final int MAX_CACHE_ENTRIES = 100;
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, TreeNode> eldest) {
			return size() > MAX_CACHE_ENTRIES;
		}
	};
	
	public static String internalParserName() {
		return NAME;
	}
	
	@Override
	public double evaluate(String expression, double... vals) {
		// get the cached TreeNode or parse it if missing
		TreeNode tree = exprCache.computeIfAbsent(expression, this::parse);

		// evaluate the cached AST with the provided variables
		return tree.evalAt(vals);
	}

	@Override
	public String getName() {
		return internalParserName();
	}

	public TreeNode parse(String token) {
		TreeNode exprNode; 
		try {
			exprNode = SSMathParser.parseString(token);
		} catch(Exception e) {
			exprNode = new TreeNode(new Constant(0.0));
		}
		exprNode.setVarNames(varNames);
		return exprNode;
	}

	@Override
	public void setVariables(String... varNames) {
		this.varNames = varNames;
	}
}

