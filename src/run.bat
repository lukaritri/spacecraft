@echo off

del /S *.class

javac gmaths\*.java
javac helpers\*.java
javac material\*.java
javac objects\*.java
javac scenegraph\*.java
javac shapes\*.java
javac *.java

java Spacecraft