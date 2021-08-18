# ssplot
A simple plotting utility for dynamical systems.

# Requirements
Any Java version less than 15. (and more than 6).

# Installation
1. Install Java runtime (JRE). Version 14 preferred. Versions 15 and higher require some more work, which is explained in the last section. I use OpenJDK 14, which can be downloaded from [here](https://jdk.java.net/java-se-ri/14).
2. Download and install Apache Commons Math library (version 3) in your `CLASSPATH`. (Make sure it is installed in `/usr/share/java` for Linux, which is the case if it is installed by the package manager such as `apt`. You don't have to set the `CLASSPATH` for Linux. It is automatically done by the `ssplot` script.)
3. Download the latest release of SSPlot from the releases section. (You can also clone this repository, either using `git clone` or using the Code menu in this page (Code -> Dropdown -> Download ZIP))
4. Extract the ZIP if you downloaded it.
5. Enter the folder inside the ZIP (or the cloned folder) and open a terminal there.
6. Run using `./ssplot` from your terminal/command line (For linux).
7. Alternatively, for other OSes, see the next section.

# Short installation for Ubuntu
1. Install the dependencies. `sudo apt install openjdk-14-jre libcommons-math3-java1`
2. Download the deb file from the packages section and install it `sudo dpkg -i ssplot-1.4.1.deb`

# Other OSes.
Steps are same, except that,

1. You have to set the `CLASSPATH` yourself. You can search the internet for how to do that for your OS. Make sure that the `CLASSPATH` contains the Apache Commons Math library (version 3) and `SSPlot.jar`.
2. Make sure you can access the `java` command from the JRE. You probably have to set another variable called `PATH`. The `java` command may be called something different in your OS.
3. Run the `java` command with argument `math.plot.PlotterFrame` (`java math.plot.PlotterFrame`). If everything is okay, the main window of SSPlot will open.
4. Please see the last section on how to set up Jython or any other script engine if you are using a Java version > 14.

# Documentation
A manual is available inside the usr/share/doc/ssplot/manual directory.

# Compilation
You will require Java JDK, Apache Ant and the Apache Commons Math library (version 3).

1. Clone this repository.
2. `cd` into the cloned directory and run `ant`. It will build everything and put the generated jar file into the jar directory.
3. You can run the generated `SSPlot.jar` file using the command `java -cp /usr/share/java/commons-math3.jar:jar/SSPlot.jar math.plot.PlotterFrame`, assuming the library is located in `/usr/share/java`. (Or using `./ssplot`)

# Manual Installation of Apache Math library
If you decide to use the newer `SSPlot.jar` with experimental 3d support, you will need to install the Apache Commons Math library in your java CLASSPATH. As given in the **Compilation** section, if your Apache Commons Math library is installed in `/usr/share/java`, you can either run SSPlot with,

    java -cp /usr/share/java/commons-math3.jar:jar/SSPlot.jar math.plot.PlotterFrame
    
OR,

if your library is in `/usr/share/java` and `SSPlot.jar` is in your current directory, (for Linux only. Please search the internet on setting the java classpath if you are using any other OS.)
 
    export CLASSPATH="/usr/share/java:."
    java math.plot.PlotterFrame

(Which is exactly what the `ssplot` script does.)

The main idea is that the math library and SSPlot.jar must be in your CLASSPATH before you can use `java math.plot.PlotterFrame` to launch the program.

Please adjust the `ssplot` script if anything is renamed or the Apache Commons Math library is installed in a different location.

# Installation with Java JRE >= 15
Since the Nashorn Java engine is not available in Java versions > 14, so you will need to install a different JSR-223 compatible script engine in your Java CLASSPATH, in order for the dynamical system solver to work.

A list of all available JSR-223 script engines can be found [here](https://web.archive.org/web/20070610234337/https://scripting.dev.java.net/).

Some of the scripting engines can be downloaded from [here](https://mvnrepository.com/) by searching with the names from the previous page.

How to install the Jython script engine :
[1](https://wiki.python.org/jython/UserGuide#using-jsr-223) Newer one.
[2](https://jython.readthedocs.io/en/latest/JythonAndJavaIntegration/) Contains some useful information.

_Jython 2.7 from the first link is recommended, as it does not require a separate script engine. For Ubuntu users, don't use jython from the repositories. It somehow is not working for me._

Once you have installed a JSR-223 compatible script engine, you just have to set the environment variable `SSPLOT_ENGINE` with the name of the engine as the value, (run `export SSPLOT_ENGINE="engine-name"` in Linux) before you launch the program, and SSPlot will try to use that engine. Whether loading the engine is sucessful or not can be seen in the command line message by the program.

# License
This software is available under the LGPL 2.1 license. Please see the `COPYING` and `COPYING.LESSER` files.
