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

import com.babai.ssplot.ui.CrashFrame;

public class NumParse {
	private static final String sepWS = "\\s+";
	private static final String sepCM = ",";
	
	private static Vector<String> headers = new Vector<>();
	
	public static Vector<String> getHeaders() {
		return headers;
	}
	
	public static Vector<Vector<Double>> parse(Path fpath) throws IOException {
		var arEntries = new Vector<Vector<Double>>();
		var lines = Files.readAllLines(fpath);
		
		// Reading and parsing data.
		for (int i = 0; i < lines.size(); i++) {
			var entries = new Vector<Double>();
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
			
			if (strEntries == null) {
				// Looks like this line doesn't contain any
				// separators, skip
				continue;
			}
			
			for (String str : strEntries) {
				try {
					entries.add(Double.parseDouble(str));
				} catch(NumberFormatException noe) {
					// Files has headers, store
					// delete any " (dbl quotes) if exists
					if (i == 0) {
						headers.add(str.strip().replace("\"", ""));
					}
				}
			}
			
			if (!entries.isEmpty()) {
				arEntries.add(entries);
			}
		}
		return arEntries;
	}
	
	public static void write(Vector<Vector<Double>> data, Path p) {
		try (var print = new PrintStream(p.toFile())) {
			for (var v : data) {
				for (int i = 0; i < v.size(); i++) {
					print.print(v.get(i));
					if (i != v.size()-1) {
						print.print(" ");
					}
				}
				print.print("\n");
			}
		} catch (FileNotFoundException e) {
			CrashFrame.showCrash(e);
		}
	}
}

