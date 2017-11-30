#!/bin/bash

cd JavaClient

javac -d out src/main.java src/*/*.java

mkdir out/css &>/dev/null
cp src/css/main.css out/css/main.css

java -cp out main
