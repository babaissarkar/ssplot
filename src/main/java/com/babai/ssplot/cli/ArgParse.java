/*
 * ArgParse.java
 * 
 * Copyright 2025 Subhraman Sarkar <suvrax@gmail.com>
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
