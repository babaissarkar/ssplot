# ssplot
A simple plotting utility for dynamical systems.

# Requirements
Any Java version less than 15. (and more than 6).

# Installation
1. Install Java runtime (JRE).
2. Optionally, install Apache Commons Math library (if you want the experimental 3d support).
3. Clone this repository. (either using `git clone` or using the Code menu in this page (Code -> Dropdown -> Download ZIP)
4. Extract the ZIP if you downloaded it.
5. Enter the folder inside the ZIP (or the cloned folder) and open a terminal there.
6. Run using `./ssplot` from your terminal/command line.

# Compilation
You will require Java JDK, Apache Ant and the Apache Commons Math library (version 3).

1. Clone this repository.
2. `cd` into the cloned directory and run `ant`. It will build everything and put the generated jar file into the jar directory.
3. You can run the generated `SSPlot.jar` file using the command `java -cp /usr/share/java/commons-math3.jar:jar/SSPlot.jar math.plot.PlotterFrame`, assuming the library is located in `/usr/share/java`. (Or using `./ssplot`)

# Detailed Installation
If you decide to use the newer `SSPlot.jar` with experimental 3d support, you will also need to install the Apache Commons Math library in your java CLASSPATH. As given in the **Compilation** section, if your Apache Commons Math library is installed in `/usr/share/java`, you can either run it with,

    java -cp /usr/share/java/commons-math3.jar:jar/SSPlot.jar math.plot.PlotterFrame
    
OR,

if your library is in `/usr/share/java` and `SSPlot.jar` is in current directory, (for Linux only. Please search the internet on setting the java classpath if you are using any other OS.)
 
    export CLASSPATH="/usr/share/java:."
    java math.plot.PlotterFrame

(Which is exactly what the `ssplot` script does.)

# Notes
Please adjust the `ssplot` script if anything is renamed or the Apache Commons Math library is installed in a different location.
