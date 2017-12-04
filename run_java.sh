#!/bin/bash

# In terminal:
# 1. Be in edaf55-camera-project folder
# 2. Type ./run_java.sh PORT
# Where PORT is optional. If not given the default will be set to 6666
# Will complile all Java-files and run the main.class file

cd JavaClient

javac -d out src/main.java src/*/*.java

mkdir out/css &>/dev/null
cp src/css/main.css out/css/main.css

port=$([ -z "$1" ] && printf "6666" || printf "$1")

java -cp out main $port
