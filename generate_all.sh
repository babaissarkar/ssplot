#!/bin/bash
# Author : Subhraman Sarkar
# 2023
# beta version, can't do everything yet.
# I should move this to a makefile.
VERSION=2.2.1
echo "Running Maven"
mvn package
echo "Copying target jar"
rm -v "jar/SSPlot.jar"
cp -v "target/SSPlot-${VERSION}-shaded.jar" "jar/SSPlot.jar"
rm -v ssplot-deb/usr/share/java/SSPlot*.jar
cp -v jar/SSPlot.jar ssplot-deb/usr/share/java
echo "Generating Docs"
markdown README.md > README.html
echo "Generating DEB package"
dpkg-deb -b ssplot-deb
mv -v "ssplot-deb.deb" "ssplot-${VERSION}.deb"
echo "Creating AppImage"
cp -v "target/SSPlot-${VERSION}-shaded.jar" "SSPlot.AppDir/usr/share/ssplot/ssplot.jar"
ARCH=x86_64 "/home/ssarkar/Downloads/Software/AppImages/appimagetool-x86_64.AppImage" "SSPlot.AppDir"
