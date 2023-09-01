#!/bin/bash
echo "Running Maven"
mvn package
echo "Copying target jar"
cp -v target/SSPlot-2.0-shaded.jar jar/SSPlot.jar
rm -v ssplot-deb/usr/share/java/SSPlot.jar
cp -v jar/SSPlot.jar ssplot-deb/usr/share/java
echo "Generating Docs"
markdown README.md > README.html
echo "Generating DEB package"
dpkg-deb -b ssplot-deb
