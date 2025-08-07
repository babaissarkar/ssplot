#!/bin/bash
# Copyright 2023-2025 Subhraman Sarkar
# AppImageTool and any other required build programs
# should exist on PATH
# build-deps : maven, dpkg-deb, markdown
VERSION=2.3
echo "Running Maven: Compile and generate DEB"
mvn clean package -X -f pom.xml
echo "Generating Docs"
markdown README.md > README.html
echo "Creating AppImage"
cp -v "jar/ssplot.jar" "SSPlot.AppDir/usr/share/ssplot/ssplot.jar"
# download appimagetool
wget -nc https://github.com/AppImage/AppImageKit/releases/latest/download/appimagetool-x86_64.AppImage
chmod +x appimagetool-x86_64.AppImage
./appimagetool-x86_64.AppImage --appimage-extract
mv squashfs-root appimagetool-dir
# run appimagetool
ARCH=x86_64 appimagetool-dir/AppRun "SSPlot.AppDir"
