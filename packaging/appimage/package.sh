#!/bin/bash -xe

set -euo pipefail

# create directory structure
./link.sh
mkdir -p SSPlot.AppDir/usr/share/ssplot
cp "../../jar/ssplot.jar" "SSPlot.AppDir/usr/share/ssplot/"
cp ssplot SSPlot.AppDir/usr/bin/
cp AppRun SSPlot.AppDir/
cp ssplot.png SSPlot.AppDir/
cp ssplot.desktop SSPlot.AppDir/

# download appimagetool
pwd && ls -lh
wget --verbose -O /tmp/appimagetool-x86_64.AppImage https://github.com/AppImage/appimagetool/releases/download/continuous/appimagetool-x86_64.AppImage
file /tmp/appimagetool-x86_64.AppImage
pwd && ls -lh /tmp
chmod +x /tmp/appimagetool-x86_64.AppImage
/tmp/appimagetool-x86_64.AppImage --appimage-extract

# run appimagetool
ARCH=x86_64 squashfs-root/AppRun "SSPlot.AppDir"
