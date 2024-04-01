# ssplot
A simple plotting utility for dynamical systems.

# Requirements
Any Java version >= 8.

# Installation
1. Install Java runtime (JRE).
2. Download the latest release of SSPlot from the releases section. (You can also clone this repository, either using `git clone` or using the Code menu in this page (Code -> Dropdown -> Download ZIP))
3. Extract the ZIP.
4. Enter the folder inside the ZIP (or the cloned folder) and open a terminal there.
5. Run using `./ssplot` from your terminal/command line (For linux), or go to the directory where the file `SSPlot.jar` is, open a terminal there and run `java -jar SSPlot.jar`
7. You may need to set the `CLASSPATH` environment variable if you want to use an alternate engine for math equation parsing, as explained in a later section.

A `DEB` package, a flatpak from flathub and an `AppImage` with bundled AdoptOpenJDK java runtime is available.

[![Download on Flathub](https://flathub.org/api/badge?locale=en)](https://flathub.org/apps/io.github.babaissarkar.ssplot)

# Screenshot
![Main Screen of SSPlot, version 2.2.4](https://babaissarkar.github.io/images/ssplot_screenshot.png)

# Themes
Four themes are available, current accessible only through command line options.
1. *Light Theme* -- The default (FlatLAF Arc Orange). No options needed.
2. *Dark Theme* -- Use `-dark` option (FlatLAF Arc Orange Dark)
3. *Nimbus Theme* -- Use `-nimbus`. Java's inbuilt Nimbus theme.
4. *Metal Theme* -- Use `-metal`. Java's inbuilt Metal theme.

Options only available when you run using the command line (i.e. `ssplot -dark` or `java -jar SSPlot.jar -dark`).

# Important notes
1. You may have to set the `CLASSPATH` yourself. You can search the internet for how to do that for your OS.
2. Make sure you can access the `java` command from the JRE on the terminal/command prompt. You probably have to set another variable called `PATH`. The `java` command may be called something different in your OS.

# Documentation
A manual is available inside the `docs/manual` directory (or its link, the `doc` directory at the root) in pdf and odt formats. You can generate the pdf from the odt using LibreOffice Writer or any other tool you prefer.

# Dependency libraries (included)
1. FlatLAF
2. FlatLAF Intellij Themes
3. Mozilla Rhino

# Compilation
You will require Java JDK and Apache Maven. You will also require Markdown if you want to generate a html version of this Readme.

1. Clone this repository.
2. `cd` into the cloned directory and run `mvn package`. It will build everything and put the generated jar file into the jar directory.
3. Link the final jar file into the jar directory.
3. You can run the generated `SSPlot.jar` file using the command `java -jar jar/SSPlot.jar` or simple `./ssplot`, if the link is correctly created.
4. Generate the README.html using markdown : `markdown README.md > README.html`.

# Quick Build
Run the `generate-all.sh` script.

# Using a external script engine for parsing math expressions

You can install a different JSR-223 compatible script engine in your Java CLASSPATH to use as an alternate Math expression parser.

A list of all available JSR-223 script engines can be found [here](https://web.archive.org/web/20070610234337/https://scripting.dev.java.net/).

Some of the scripting engines can be downloaded from [here](https://mvnrepository.com/) by searching with the names from the previous page.

## Jython
How to install the Jython script engine :
[1](https://wiki.python.org/jython/UserGuide#using-jsr-223) Newer one.
[2](https://jython.readthedocs.io/en/latest/JythonAndJavaIntegration/) Contains some useful information.

_Jython 2.7 from the first link is recommended, as it does not require a separate script engine. For Ubuntu users, don't use jython from the repositories. It somehow is not working for me._

## Rhino
This also works.
[Mozilla Rhino JavaScript Library](https://github.com/mozilla/rhino)

Once you have installed a JSR-223 compatible script engine, you just have to set the environment variable `SSPLOT_ENGINE` with the name of the engine as the value, (run `export SSPLOT_ENGINE="engine-name"` in Linux) before you launch the program, and SSPlot will try to use that engine. Whether loading the engine is sucessful or not can be seen in the command line message by the program.

# License
This software is available under the LGPL 2.1 license. Please see the `COPYING` and `COPYING.LESSER` files.

