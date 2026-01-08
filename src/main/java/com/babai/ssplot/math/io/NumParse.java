/*
 * NumParse.java
 * 
 * Copyright 2021-2025 Subhraman Sarkar <suvrax@gmail.com>
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
 
/* A simple class that reads data blocks from a datafile,
 * and stores them in a two dimension dynamic array.*/

package com.babai.ssplot.math.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Vector;

public class NumParse {
	private static final String sepWS = "\\s+";
	private static final String sepCM = ",";
	
	private static Vector<String> headers = new Vector<>();
	
	public static Vector<String> getHeaders() {
		return headers;
	}
	
	public static double[][] parse(Path fpath) throws IOException {
		var lines = Files.readAllLines(fpath);
		var arEntries = new double[lines.size()][];
		
		// Reading and parsing data.
		for (int i = 0, row = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			
			// If any lines starts with "#", ignore
			// except the first line, which may contain header data.
			if (line.startsWith("#")) {
				if (i != 0) {
					continue;
				} else {
					// first line, remove leading "#" and process header
					line = line.substring(1);
				}
			}
			
			String[] strEntries = null;
			if (line.contains(sepCM)) {
				strEntries = line.split(sepCM);
			} else if (line.contains(sepWS)) {
				strEntries = line.split(sepWS);
			}
			
			// Looks like this line doesn't contain any
			// separators, skip
			if (strEntries == null) {
				continue;
			}
			
			var entries = new double[strEntries.length];
			for (int col = 0; col < strEntries.length; col++) {
				try {
					entries[col] = Double.parseDouble(strEntries[col]);
				} catch(NumberFormatException noe) {
					// Files has headers, store
					// delete any " (dbl quotes) if exists
					if (i == 0) {
						headers.add(strEntries[col].strip().replace("\"", ""));
					}
				}
			}
			
			if (entries.length != 0) {
				arEntries[row] = entries;
				row++;
			}
		}
		return arEntries;
	}
	
	public static void write(double[][] data, Path p) throws FileNotFoundException {
		try (var print = new PrintStream(p.toFile())) {
			for (var v : data) {
				for (int i = 0; i < v.length; i++) {
					print.print(v[i]);
					if (i != v.length - 1) {
						print.print(" ");
					}
				}
				print.print("\n");
			}
		}
	}
}

