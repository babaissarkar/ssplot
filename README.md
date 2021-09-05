# ssplot
A simple plotting utility for dynamical systems.

# Requirements
Any Java version >= 7.

# Installation
1. Install Java runtime (JRE).
3. Download the latest release of SSPlot from the releases section. (You can also clone this repository, either using `git clone` or using the Code menu in this page (Code -> Dropdown -> Download ZIP))
4. Extract the ZIP.
5. Enter the folder inside the ZIP (or the cloned folder) and open a terminal there.
6. Run using `./ssplot-quick-run` from your terminal/command line (For linux), or go to the directory where the file `SSPlot.jar` is, open a terminal there and run `java -jar SSPlot.jar`
7. Alternatively, for other OSes, see the section __Other OSes__.
8. You may need to set the `CLASSPATH` environment variable if you want to use an alternate engine for math equation parsing, as explained in a later section.

# Short installation for Ubuntu
1. Install Java `sudo apt install default-jre`.
2. Get the zip file from the Releases section and install the deb file from it `sudo dpkg -i ssplot-1.5.deb`
3. You can now run SSPlot from the terminal by using the command `ssplot`.

# Other OSes.
Steps are same, except that,

1. You may have to set the `CLASSPATH` yourself if you are using an external script engine. You can search the internet for how to do that for your OS. Make sure that the `CLASSPATH` contains the Apache Commons Math library (version 3) and `SSPlot.jar`. Not really needed as of version 1.5.
2. Make sure you can access the `java` command from the JRE. You probably have to set another variable called `PATH`. The `java` command may be called something different in your OS.
3. Please see the last section on how to set up Jython or any other script engine if you are using a Java version > 14.

# Documentation
A manual is available inside the `usr/share/doc/ssplot/manual` directory (or its link, the `doc` directory at the root) in pdf and odt formats. You can generate the pdf from the odt using LibreOffice Writer or any other tool you prefer.

# Compilation
You will require Java JDK and Apache Ant. You will also require Markdown if you want to generate a html version of this Readme.

1. Clone this repository.
2. `cd` into the cloned directory and run `ant`. It will build everything and put the generated jar file into the jar directory.
3. You can run the generated `SSPlot.jar` file using the command `java -jar jar/SSPlot.jar` or just `./ssplot-quick-run`

# Using a external script engine for parsing math expressions

You can install a different JSR-223 compatible script engine in your Java CLASSPATH to use as an alternate Math expression parser.

A list of all available JSR-223 script engines can be found [here](https://web.archive.org/web/20070610234337/https://scripting.dev.java.net/).

Some of the scripting engines can be downloaded from [here](https://mvnrepository.com/) by searching with the names from the previous page.

How to install the Jython script engine :
[1](https://wiki.python.org/jython/UserGuide#using-jsr-223) Newer one.
[2](https://jython.readthedocs.io/en/latest/JythonAndJavaIntegration/) Contains some useful information.

_Jython 2.7 from the first link is recommended, as it does not require a separate script engine. For Ubuntu users, don't use jython from the repositories. It somehow is not working for me._

Once you have installed a JSR-223 compatible script engine, you just have to set the environment variable `SSPLOT_ENGINE` with the name of the engine as the value, (run `export SSPLOT_ENGINE="engine-name"` in Linux) before you launch the program, and SSPlot will try to use that engine. Whether loading the engine is sucessful or not can be seen in the command line message by the program.

# License
This software is available under the LGPL 2.1 license. Please see the `COPYING` and `COPYING.LESSER` files.
