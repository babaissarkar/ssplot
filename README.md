# ssplot
A simple plotting utility for dynamical systems.

# Requirements
Any Java version less than 15.

# Installation
1. Install Java runtime (JRE).
2. Download the `SSPlot.jar` file from the jar directory.
3. Run using `java -jar SSPlot-2D.jar` from your terminal/command line.

# Compilation
You will require Java JDK, Apache Ant and the Apache Commons Math library (version 3).

1. Clone this repository.
2. `cd` into the cloned directory and run `ant`. It will build everything and put the generated jar file into the jar directory.
3. You can run the generated `SSPlot.jar` file using the command `java -cp /usr/share/java/commons-math3.jar:jar/SSPlot.jar math.plot.PlotterFrame`, assuming the library is located in `/usr/share/java`.

# Notes
The precompiled `SSPlot-2D.jar` does not require the Apache Commons Math library, because it does not have the Experiment 3D support which is added in the latest version.
