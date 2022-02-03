#!/bin/bash

echo "Copying targer jar"
cp -v target/SSPlot.jar jar/
echo "Generating Docs"
markdown README.md > README.html
