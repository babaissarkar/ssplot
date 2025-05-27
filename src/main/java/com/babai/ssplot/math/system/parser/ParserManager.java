/*
 * ParserManager.java
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

import java.util.HashMap;

import javax.script.ScriptEngineManager;

import com.babai.ssplot.math.system.parser.internal.tree.TreeParser;

public class ParserManager {
	private static HashMap<String, Parser> parsers = new HashMap<>();
	private static Parser currParser = null;
	
	private static void init() {
		populateEngineList();
		
		// Current engine selection
		// Engine set via envvar
		String engineName = System.getenv("SSPLOT_ENGINE");
		currParser = parsers.get(engineName == null ? TreeParser.NAME : engineName);
	}
	
	private static void populateEngineList() {
		// Internal parser is always available
		final var internalParser = new TreeParser();
		System.out.println("Found engine: " + internalParser.getName());
		parsers.put(internalParser.getName(), internalParser);
		
		// Initialize script engines
		final var m = new ScriptEngineManager();
		for (var factory : m.getEngineFactories()) {
			final String name = factory.getNames().get(0);
			final Parser parser = new ScriptParser(name, factory.getScriptEngine());
			System.out.println("Found engine: " + name);
			parsers.put(name, parser);
		}
	}
	
	public static HashMap<String, Parser> availableParsers() {
		if (currParser == null) {
			init();
		}
		return parsers;
	}
	
	public static Parser getParser() {
		if (currParser == null) {
			init();
		}
		return currParser;
	}
}
