#!/bin/bash

find . -name "*.class" -type f -delete

javac gmaths\*.java
javac helpers\*.java
javac material\*.java
javac objects\*.java
javac scenegraph\*.java
javac shapes\*.java
javac *.java

java Spacecraft