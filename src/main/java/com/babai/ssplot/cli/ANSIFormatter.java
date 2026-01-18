/*
 * ANSIFormatter.java
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

