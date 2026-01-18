/*
 * ArgParse.java
 * 
 * Copyright 2025-2026 Subhraman Sarkar <suvrax@gmail.com>
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
 * 
 */

package com.babai.ssplot.cli;

import java.util.Arrays;
import java.util.Optional;

/**
 * Utility interface for simple command-line argument parsing.
 * Supports flags with -, --, or / prefixes.
 */
public interface ArgParse {

	/**
	 * Checks if a given argument flag is present in the args array.
	 * 
	 * @param target the name of the flag (without prefix), e.g., "help"
	 * @param args   the command-line arguments array
	 * @return true if the flag is present (e.g., -help, --help, /help), false otherwise
	 */
	static boolean hasArg(String target, String[] args) {
		return Arrays.stream(args)
				.map(ArgParse::normalizeArg)
				.anyMatch(arg -> arg.equalsIgnoreCase(target));
	}

	/**
	 * Returns a new array with the specified argument flag removed.
	 * 
	 * @param target the name of the flag (without prefix)
	 * @param args   the original args array
	 * @return a filtered array with the specified flag removed
	 */
	static String[] removeArg(String target, String[] args) {
		return Arrays.stream(args)
				.filter(arg -> !normalizeArg(arg).equalsIgnoreCase(target))
				.toArray(String[]::new);
	}

	/**
	 * Checks if a string looks like a valid argument flag.
	 * A valid flag starts with -, --, or / followed by at least one character.
	 * 
	 * @param arg the string to test
	 * @return true if the string looks like a flag, false otherwise
	 */
	static boolean isArg(String arg) {
		return arg.matches("^(--?|/).+");
	}

	/**
	 * Returns the argument value immediately following the specified flag.
	 * If the flag is found but followed by another flag (e.g., "-debug"), it is ignored.
	 * 
	 * @param target the name of the flag (without prefix)
	 * @param args   the command-line arguments array
	 * @return an Optional containing the next argument if present and valid, or empty otherwise
	 */
	static Optional<String> nextArg(String target, String[] args) {
		for (int i = 0; i < args.length - 1; i++) {
			if (normalizeArg(args[i]).equalsIgnoreCase(target)) {
				String next = args[i + 1];
				return isArg(next) ? Optional.empty() : Optional.of(next);
			}
		}
		return Optional.empty();
	}

	/**
	 * Internal helper to normalize a flag by removing -, --, or / prefixes.
	 * 
	 * @param arg the raw argument string
	 * @return the argument with any leading -, --, or / removed
	 */
	private static String normalizeArg(String arg) {
		return arg.replaceFirst("^--?|/", "");
	}
}
