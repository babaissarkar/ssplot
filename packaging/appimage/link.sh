#! /bin/bash

jlink \
  --module-path "$JAVA_HOME/jmods" \
  --add-modules java.base,java.datatransfer,java.desktop,java.logging,java.scripting \
  --output SSPlot.AppDir/usr \
  --compress=2 \
  --strip-debug \
  --no-header-files \
  --no-man-pages

