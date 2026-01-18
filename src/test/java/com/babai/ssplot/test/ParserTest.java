/*
 * ParserTest.java
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

package com.babai.ssplot.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.babai.ssplot.math.system.parser.internal.tree.TreeParser;

class ParserTest {
	private static TreeParser parser;
	
	@BeforeAll
	static void initParser() {
		parser = new TreeParser();
	}
	
	@Test
	void testBasicOperations() {
		assertEquals(parser.parse("2 + 3").evalAt(), 5, "2 + 3 != 5");
		assertEquals(parser.parse("4 - 3").evalAt(), 1, "4 - 3 != 1");
		assertEquals(parser.parse("2 * 3").evalAt(), 6, "2 * 3 != 6");
		assertEquals(parser.parse("6 / 3").evalAt(), 2, "6 / 3 != 2");
		assertEquals(parser.parse("2 ^ 3").evalAt(), 8, "2 ^ 3 != 8");
		assertEquals(parser.parse("2 ** 3").evalAt(), 8, "2 ^ 3 != 8");
	}
	
	@Test
	void testAssociativity() {
		assertEquals(parser.parse("2 / 2 / 2 / 2").evalAt(), 0.25, "2 / 2 / 2 / 2 != 0.25");
		assertEquals(parser.parse("2 + 3 - 4 * 2").evalAt(), -3, "2 + 3 - 4 * 2 != -3");
	}
	
	@Test
	void testPower() {
		assertEquals(parser.parse("2 ^ 2").evalAt(), 4, "2^2 != 4");
		assertEquals(parser.parse("2 ^ -2").evalAt(), 0.25, "2^-2 != 0.25");
		assertEquals(parser.parse("-2 ^ 2").evalAt(), -4, "-2^2 != -4");
		assertEquals(parser.parse("-2 ^ -2").evalAt(), -0.25, "-2^-2 != -0.25");
		assertEquals(parser.parse("2 ^ -1 ^ -1").evalAt(), 0.5, "2^-1^-1 != 0.5");
	}
	
	@Test
	void testConstants() {
		assertEquals(parser.parse("pi").evalAt(), Math.PI, 1e-10, "pi constant mismatch");
		assertEquals(parser.parse("e").evalAt(), Math.E, 1e-10, "e constant mismatch");
	}

	@Test
	void testFunctions() {
		assertEquals(parser.parse("sin(pi / 2)").evalAt(), 1, 1e-10, "sin(pi / 2) != 1");
		assertEquals(parser.parse("cos(0)").evalAt(), 1, 1e-10, "cos(0) != 1");
		assertEquals(parser.parse("tan(pi / 4)").evalAt(), 1, 1e-10, "tan(pi / 4) != 1");
		assertEquals(parser.parse("asin(1)").evalAt(), Math.PI / 2, 1e-10, "asin(1) != pi/2");
		assertEquals(parser.parse("acos(0)").evalAt(), Math.PI / 2, 1e-10, "acos(0) != pi/2");
		assertEquals(parser.parse("atan(1)").evalAt(), Math.PI / 4, 1e-10, "atan(1) != pi/4");
		assertEquals(parser.parse("log(1000)").evalAt(), 3, 1e-10, "log(1000) != 3");
		assertEquals(parser.parse("ln(e)").evalAt(), 1, 1e-10, "ln(e) != 1");
		assertEquals(parser.parse("exp(1)").evalAt(), Math.E, 1e-10, "exp(1) != e");
	}

	@Test
	void testParenthesesAndPrecedence() {
		assertEquals(parser.parse("(2 + 3) * 4").evalAt(), 20, "Incorrect parentheses handling");
		assertEquals(parser.parse("2 + 3 * 4").evalAt(), 14, "Incorrect operator precedence");
		assertEquals(parser.parse("2 ^ 3 ^ 2").evalAt(), 512, "Right-associative power failed");
		assertEquals(parser.parse("(2 ^ 3) ^ 2").evalAt(), 64, "Explicit parenthesis on power failed");
	}

	@Test
	void testCombinedExpressions() {
		assertEquals(parser.parse("sin(pi / 4) ^ 2 + cos(pi / 4) ^ 2").evalAt(), 1, 1e-10, "Trig identity sin^2 + cos^2 != 1");
		assertEquals(parser.parse("log(100) + ln(e ^ 2)").evalAt(), 4, 1e-10, "log and ln combination failed");
	}
}
