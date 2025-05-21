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

package math.system.parser.internal.tree;

import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import math.system.parser.Parser;
import math.system.parser.internal.SSMathParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class TreeParser implements Parser {
	public final String NAME = "Internal"; 
	
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

	public TreeNode parse2(String tokenWtSp) {
		String token;
		if (tokenWtSp.contains(" ")||tokenWtSp.contains("\t")) {
			token = tokenWtSp.replaceAll("(\\s)+", "");
		} else {
			token = tokenWtSp;
		}

		//println("Parsing token : " + token);

		TreeNode exprTree = new TreeNode(new Constant(0.0));

		String digitRegex = "\\d+(\\.)?(\\d)*";
		String varRegex = "[a-z]";

		if (token.matches(digitRegex)) {
			/* Decimal numbers */

			exprTree = new TreeNode(new Constant(Double.parseDouble(token)));
		} else if ( token.matches(varRegex) ) {

			exprTree = new TreeNode(new Variable(token));

		} else if ( isArithmaticExpr(token) ) {
			/* Add, subtract, multiply, divide and exponentiation operations */
			/* BIG PROBLEM : can't parse brackets properly. Find out a way to split things */
			/* Need to write another condition to parse something with brackets */

			TreeOperator op;
			if (containsOutsideBrackets(token, "-")) {
				op = new SubOperator();
			} else if (containsOutsideBrackets(token, "+")) {
				op = new AddOperator();
			} else if (containsOutsideBrackets(token, "*")) {
				op = new MultOperator();
			} else if (containsOutsideBrackets(token, "/")) {
				op = new DivOperator();
			} else {
				op = new PowOperator();
			}

			TreeNode root = new TreeNode(op);
			String[] sumNodes;

			if (op instanceof PowOperator) {
				sumNodes = bracketSplit(token, "^".charAt(0));
				//sumNodes = token.split("\\" + "^");
			} else {
				sumNodes = bracketSplit(token, op.toString().charAt(0));
				//sumNodes = token.split("\\" + op.toString());
			}

			for (String node : sumNodes) {
				//System.err.println(node);
				root.addChild(parse(node));
			}

			exprTree = root;

		} else if (token.startsWith("sin")||token.startsWith("cos")||token.startsWith("tan")) {
			/* Trigonometric functions */

			/* Even if there's another expression inside the brackets, it is parsed, and the value sent back*/ 
			TreeNode fnroot = new TreeNode(TreeOperator.forName(token));

			/* finding the value inside the leftmost and rightmost brackets*/ 
			int start = token.indexOf("(");
			int end = token.lastIndexOf(")");
			String s = token.substring(start+1, end);

			fnroot.addChild(parse(s));

			exprTree = fnroot;

		} else if (token.contains("(")) {
			/* Expressions with brackets */
			if (token.startsWith("(")) {
				if (token.endsWith(")") && (countChar(token, "(") == 1))  {
					//Just remove the brackets and parse the rest, if there is only one pair
					//surrounding the token.
					String s = token.substring(1, token.length()-1);
					//println(s);
					exprTree = parse(s);
				} else {
					exprTree = splitBracketLeft(token);
				}
			} else {
				exprTree = splitBracketRight(token);
			}

		} else {
			System.err.println("Unparsable token found : " + token);
		}

		return exprTree;
	}

	public boolean isArithmaticExpr(String s) {
		/* No brackets allowed */
		boolean result = s.contains("+") || s.contains("-") || s.contains("*") || s.contains("/") || s.contains("^");
		//result = result && (!s.contains("(")) && (!s.contains(")"));
		return result;
	}

	public boolean containsOutsideBrackets(String token, String op) {
		/* operator is outside brackets or not */
		/* Don't apply to a string containing more than one pair of brackets */
		int start = token.indexOf("(");
		int end = token.indexOf(")");
		int pos = token.indexOf(op);
		//format("Contain check : %d, %d, %d\n", start, pos, end);
		boolean contains = token.contains(op);
		boolean isInsideBracket = (start < pos) && (pos < end);

		return (contains && (!isInsideBracket));
	}

	public String[] bracketSplit(String token, char sep) {
		// Split by separator, but ignore separator if inside bracket
		char[] chars = token.toCharArray();
		Vector<String> parts = new Vector<String>();

		StringBuilder part = new StringBuilder();
		boolean isInsideBracket = false;

		for (char c : chars) {
			if (c == '(') {
				isInsideBracket = true;
				//part.append(c);
			} else if (c == ')') {
				isInsideBracket = false;
				//part.append(c);
			} else if (c == sep) {
				if (!isInsideBracket) {
					parts.add(part.toString());
					part = new StringBuilder();
				} else {
					part.append(c);
				}
			} else {
				part.append(c);
			}
		}

		if (!isInsideBracket) {
			parts.add(part.toString());
		}

		String[] res = new String[parts.size()];
		res = parts.toArray(res);
		return res;
	}

	public TreeNode splitBracketLeft(String token) {
		/* Splitting the brackets from the left side */
		String regex = "\\((.+?)\\)((.)(.+))?";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(token);
		String tok1, tok2;

		TreeNode opRoot;

		if (m.matches()) {
			/*println("Match");
            for (int i = 1; i <= m.groupCount(); i++) {
                if (m.group(i) != null) {
                    println("Group " + i + " : " + m.group(i));
                }
            }*/
			tok1 = m.group(1);
			TreeNode node1 = parse(tok1);

			if (m.group(3) != null) {
				TreeOperator op = TreeOperator.forName(m.group(3));

				if (m.group(4) != null) {
					tok2 = m.group(4);
					//split3(m.group(4));
					TreeNode node2 = parse(tok2);

					opRoot = new TreeNode(op);
					opRoot.addChild(node1);
					opRoot.addChild(node2);
				} else {
					opRoot = node1;
					System.err.println("Insufficient number of arguments to " + op.toString() + " .");
				}
			} else {
				opRoot = node1;
			}
			return opRoot;
		} else {
			System.err.println("Invalid bracket syntax!");
			return new TreeNode(new Constant(0));
		}
	}

	public TreeNode splitBracketRight(String token) {
		/* Splitting the brackets from the right side */
		String regex = "(.+)(.)\\((.+?)\\)";

		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(token);
		String tok1, tok2;

		TreeNode opRoot = new TreeNode(new Constant(0));

		if (m.matches()) {
			/*println("Match");
            for (int i = 1; i <= m.groupCount(); i++) {
                if (m.group(i) != null) {
                    println("Group " + i + " : " + m.group(i));
                }
            }*/
			tok1 = m.group(3);
			TreeNode node1 = parse(tok1);

			//~ if (m.group(2) != null) {
			TreeOperator op = TreeOperator.forName(m.group(2));

			if (m.group(1) != null) {
				tok2 = m.group(1);
				TreeNode node2 = parse(tok2);

				opRoot = new TreeNode(op);
				opRoot.addChild(node2);
				opRoot.addChild(node1);
			} else {
				opRoot = node1;
				System.err.println("Insufficient number of arguments to " + op.toString() + " .");
			}
			//~ } else {
			//~ opRoot = node1;
			//~ }
			return opRoot;
		} else {
			System.err.println("Invalid bracket syntax!");
			return new TreeNode(new Constant(0));
		}
	}

	public int countChar(String str, String match) {
		char cm = match.charAt(0);
		char[] chars = str.toCharArray();
		int count = 0;
		for (char ch : chars) {
			if (ch == cm) {
				count++;
			}
		}
		return count;
	}

	private static void println(String s) {
		System.out.println(s);
	}

	public static void main (String[] args) {
		TreeParser p = new TreeParser();
		try {
			BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			//TreeNode node = p.parseLine("25.201 + cos(5.0) + 4 - 6 + 2");
			//TreeNode node = p.parseLine("2.25 + 3 * 4.2 + 3 - 2"); // Can't parse 2nd level decimals. see PROB line
			//TreeNode node = p.parseLine("sin(3.14159)^3 + 2^2");
			String s;
			while (true) {
				s = r.readLine();
				if (s.startsWith("exit")) {
					break;
				}
				TreeNode node = p.parse(s);
				println(node.toString());
				println("=> " + node.getValue());
				//                for (int i = 0; i < 7; i++) {
				//                    double val = (double) i;
				//                    println("Value at x = " + val + " : " + node.evalAt("x", val));
				//                }
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

