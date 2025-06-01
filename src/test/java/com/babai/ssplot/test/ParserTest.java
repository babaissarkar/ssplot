package com.babai.ssplot.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

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
		assertEquals(parser.parse("2 + 3").evalAt(Map.of()), 5, "2 + 3 != 5");
		assertEquals(parser.parse("4 - 3").evalAt(Map.of()), 1, "4 - 3 != 1");
		assertEquals(parser.parse("2 * 3").evalAt(Map.of()), 6, "2 * 3 != 6");
		assertEquals(parser.parse("6 / 3").evalAt(Map.of()), 2, "6 / 3 != 2");
		assertEquals(parser.parse("2 ^ 3").evalAt(Map.of()), 8, "2 ^ 3 != 8");
	}
	
	@Test
	void testAssociativity() {
		assertEquals(parser.parse("2 + 3 - 4 * 2").evalAt(Map.of()), -3, "2 + 3 - 4 * 2 != -3");
	}

}
