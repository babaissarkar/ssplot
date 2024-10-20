#!/bin/bash
# Copyright 2023-2024 Subhraman Sarkar
# beta version, can't do everything yet.
# AppImageTool and any other required build programs
# should exist on PATH
# build-deps : maven, dpkg-deb, appimagetool, markdown
VERSION=2.2.2
echo "Running Maven: Compile and generate DEB"
mvn clean package -X
echo "Generating Docs"
markdown README.md > README.html
echo "Creating AppImage"
cp -v "jar/SSPlot.jar" "SSPlot.AppDir/usr/share/ssplot/ssplot.jar"
ARCH=x86_64 appimagetool "SSPlot.AppDir"
