package com.babai.ssplot.cli;

/**
 * ANSI escape code utility for coloring terminal output using true RGB color.
 * 
 * Works on terminals that support 24-bit ("true color") ANSI codes.
 * Examples include modern Linux terminals, macOS Terminal, iTerm2, Windows Terminal, etc.
 */
public interface ANSIFormatter {

	/**
	 * Generates an ANSI escape sequence for setting the foreground (text) color.
	 *
	 * @param r Red component (0–255)
	 * @param g Green component (0–255)
	 * @param b Blue component (0–255)
	 * @return ANSI escape sequence for the given RGB foreground color
	 */
	public static String fg(int r, int g, int b) {
		return String.format("\033[38;2;%d;%d;%dm", r, g, b);
	}

	/**
	 * Generates an ANSI escape sequence for setting the background color.
	 *
	 * @param r Red component (0–255)
	 * @param g Green component (0–255)
	 * @param b Blue component (0–255)
	 * @return ANSI escape sequence for the given RGB background color
	 */
	public static String bg(int r, int g, int b) {
		return String.format("\033[48;2;%d;%d;%dm", r, g, b);
	}

	/**
	 * ANSI escape sequence to reset all terminal formatting (color, bold, etc.).
	 */
	public static final String RESET = "\033[0m";
}

