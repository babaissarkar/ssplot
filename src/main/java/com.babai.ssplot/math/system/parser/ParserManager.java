package math.system.parser;

import java.util.HashMap;

import javax.script.ScriptEngineManager;

import math.system.parser.internal.tree.TreeParser;

public class ParserManager {
	private HashMap<String, Parser> parsers;
	private Parser currParser = null;
	
	public ParserManager() {
		parsers = new HashMap<>();
		
		populateEngineList();
		
		// Current engine selection
		// Engine set via envvar
		String engineName = System.getenv("SSPLOT_ENGINE");
		if (engineName.isEmpty()) {
			currParser = parsers.get(engineName);
		} else {
			currParser = parsers.get("Internal"); // TODO Magic string
		}
	}
	
	public HashMap<String, Parser> availableParsers() {
		return parsers;
	}
	
	public Parser getParser() {
		return currParser;
	}
	
	private void populateEngineList() {
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
}
