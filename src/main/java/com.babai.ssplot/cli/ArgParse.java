package cli;

public interface ArgParse {
	public static boolean hasArg(String arg, String[] args) {
		// Note: arg has no hyphen/slash
		boolean result = false;
		
		for (String a : args) {
			String option = a;
			if (a.startsWith("-")||a.startsWith("/")) {
				option = a.substring(1, a.length());
			} else if (a.startsWith("--")) {
				option = a.substring(2, a.length());
			}
			
			if (option.equalsIgnoreCase(arg)) {
				result = true;
				break;
			}
		}
		
		return result;
	}
}
