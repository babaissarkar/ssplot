#!/bin/bash

if [ -z "$DISPLAY" ]; then
  echo "[INFO] No DISPLAY detected: Running in CLI mode."
  java -jar ssplot.jar "$@"
else
  echo "[INFO] DISPLAY detected: Launching GUI mode."
  java -jar ssplot.jar
fi

