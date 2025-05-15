	package cli;

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

import java.nio.file.Path;
import java.nio.file.Paths;

import math.plot.NumParse;
import math.plot.PlotData;
import math.plot.Plotter;

public class SSPlotCLI {
    Plotter plt;

    public SSPlotCLI() {
        CLILogger clilogger = new CLILogger();
        clilogger.log("Welcome to SSPlot CLI!");

        plt = new Plotter(clilogger);
        plt.initPlot();
    }
	
	public static void main (String[] args) {
        SSPlotCLI cli = new SSPlotCLI();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            //System.out.println("Arg " + i + " " + arg);
            if (arg.equals("-i")) {
                System.out.println("Parsing...");
                cli.plot(args[i+1]);
            }
        }
//		if (MainFrame.hasArg("i", args)) {
//			System.out.println("Parsing...");
//            cli.plot(args[i+1]);
//		}

        System.exit(0);
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
        //~ try {
            //~ ImageIO.write(plt.getCanvas().getImage(), "png", f);
        //~ } catch (IOException e) {
            //~ e.printStackTrace();
        //~ }
        plt.save(p.toFile());
        System.out.println("Output written to : " + p.toAbsolutePath());
    }
}

