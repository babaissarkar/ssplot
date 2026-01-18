#!/bin/bash
# Copyright 2023-2025 Subhraman Sarkar
# AppImageTool and any other required build programs
# should exist on PATH
# build-deps : markdown
VERSION=2.3.3
echo "Running Maven: Compile and generate packages"
./mvnw -B -X clean package jpackage:jpackage@linux
echo "Generating Docs"
markdown README.md > README.html
echo "Creating AppImage"
cd packaging/appimage
./package.sh
