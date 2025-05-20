package math.system.parser;

import java.util.Map;

public class ParserManager {
	private Map<String, Parser> parsers;
	
	public ParserManager() {
		
	}
	
	public Map<String, Parser> availableParsers() {
		return parsers;
	}
}
