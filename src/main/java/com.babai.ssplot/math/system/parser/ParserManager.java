package math.system.parser;

import java.util.HashMap;

import javax.script.ScriptEngineManager;

import math.system.parser.internal.tree.TreeParser;

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
			final String name = factory.getNames().getFirst();
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
