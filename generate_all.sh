#!/bin/bash
echo "Running Maven"
mvn package
echo "Copying target jar"
cp -v target/SSPlot.jar jar/
echo "Generating Docs"
markdown README.md > README.html
