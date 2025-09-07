/*
 * SSPlotCLI.java
 *
 * Copyright 2021-2025 Subhraman Sarkar <suvrax@gmail.com>
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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Scanner;

import javax.imageio.ImageIO;

import com.babai.ssplot.math.io.NumParse;
import com.babai.ssplot.math.plot.PlotData;
import com.babai.ssplot.math.plot.Plotter;
import com.babai.ssplot.math.system.parser.Parser;
import com.babai.ssplot.math.system.parser.ParserManager;

public class SSPlotCLI {
	Plotter plt;

	public SSPlotCLI() {
		CLILogger clilogger = new CLILogger();
		clilogger.log("Welcome to SSPlot CLI!");

		plt = new Plotter(clilogger);
		plt.initPlot();
	}

	public static void main (String[] args) {
		var cli = new SSPlotCLI();
		ArgParse.nextArg("i", args).ifPresentOrElse(
			inputFile -> {
				System.out.println("Parsing...");
				cli.plot(inputFile);
				System.exit(0);
			},
			() -> startREPL()
		);
	}

	private static void startREPL() {
		try (var input = new Scanner(System.in)) {
			Parser parser = ParserManager.getParser();
			while (true) {
				System.out.print(
					ANSIFormatter.fg(255, 255, 0)
					+ "(" + parser.getName() + ")"
					+ ANSIFormatter.RESET
					+ ANSIFormatter.fg(255, 255, 255)
					+ ">> ");
				String line = input.nextLine();
				if (line.equalsIgnoreCase("exit")) {
					break;
				} else {
					System.out.println(parser.evaluate(line, Map.of()));
				}
			}
		} catch (Exception e) {
			System.err.println("Exception raised, exiting!");
		}
	}

	public void plot(String fname) {
		System.out.println("Plotting...");
		try {
			Path dfpath = Paths.get(fname);
			System.out.println("Path : " + dfpath.toString());
			PlotData pdata = new PlotData(NumParse.parse(dfpath));
			plt.plotData(pdata);
			save();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void save() {
		Path p = Paths.get("out.png");
		try {
			ImageIO.write(plt.getImage(), "png", p.toFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Output written to : " + p.toAbsolutePath());
	}
}

