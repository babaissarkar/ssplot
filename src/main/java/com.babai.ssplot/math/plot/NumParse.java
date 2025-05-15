/*
 * NumParse.java
 * 
 * Copyright 2021 Subhraman Sarkar <subhraman@subhraman-Inspiron>
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

package math.plot;

import java.io.*;
import java.nio.file.*;
import java.util.Vector;

public class NumParse {
	public static final String sep = "\\s+";
	
	public static Vector<Vector<Double>> parse(Path fpath) throws IOException {
		/* Opening data file.*/
		Vector<Vector<Double>> arEntries = new Vector<Vector<Double>>();
		BufferedReader bin = Files.newBufferedReader(fpath);
		
		/* Reading and parsing data.*/
		String line;
		while ((line = bin.readLine()) != null) {
			Vector<Double> entries = new Vector<Double>();
			String[] strEntries = line.split(sep);
			for (String str : strEntries) {
				entries.add(Double.parseDouble(str));
			}
			arEntries.add(entries);
		}
		
		bin.close();
		return arEntries;
	}
	
	public static void write(Vector<Vector<Double>> data, Path p) {
		try (PrintStream print = new PrintStream(p.toFile())) {
			for (Vector<Double> v : data) {
				for (int i = 0; i < v.size(); i++) {
					print.print(v.get(i));
					if (i != v.size()-1) {
						print.print(" ");
					}
				}
				print.print("\n");
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}

