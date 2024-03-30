#!/bin/bash
# Copyright 2023-2024 Subhraman Sarkar
# beta version, can't do everything yet.
# AppImageTool and any other required build programs
# should exist on PATH
# build-deps : maven, dpkg-deb, appimagetool, markdown
VERSION=2.2.2
echo "Running Maven"
mvn package
echo "Copying target jar"
rm -v "jar/SSPlot.jar"
cp -v "target/ssplot.jar" "jar/SSPlot.jar"
rm -v ssplot-deb/usr/share/java/SSPlot*.jar
cp -v jar/SSPlot.jar ssplot-deb/usr/share/java
echo "Generating Docs"
markdown README.md > README.html
echo "Generating DEB package"
dpkg-deb -b ssplot-deb
mv -v "ssplot-deb.deb" "ssplot-${VERSION}.deb"
echo "Creating AppImage"
cp -v "jar/SSPlot.jar" "SSPlot.AppDir/usr/share/ssplot/ssplot.jar"
ARCH=x86_64 appimagetool "SSPlot.AppDir"
