#!/bin/bash

# create directory structure
./link.sh
mkdir -p SSPlot.AppDir/usr/share/ssplot
cp -v "../../jar/ssplot.jar" "SSPlot.AppDir/usr/share/ssplot/"
cp ssplot SSPlot.AppDir/usr/bin/
cp AppRun SSPlot.AppDir/
cp ssplot.png SSPlot.AppDir/
cp ssplot.desktop SSPlot.AppDir/

# download appimagetool
wget -nc https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage
chmod +x appimagetool-x86_64.AppImage
./appimagetool-x86_64.AppImage --appimage-extract

# run appimagetool
ARCH=x86_64 squashfs-root/AppRun "SSPlot.AppDir"
