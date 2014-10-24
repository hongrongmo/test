@echo off
rem build.bat -- Build Script for the "Hello, World" Application
rem $Id: build.bat,v 1.3.2.3 2000/12/27 17:20:11 marcsaeg Exp $


rem Execute ANT to perform the requird build target
java -classpath ..\..\ant\ant.jar;..\..\ant\xerces.jar;%JAVA_HOME%\lib\tools.jar org.apache.tools.ant.Main -Dtomcat.home=%TOMCAT_HOME% %1 %2 %3 %4 %5 %6 %7 %8 %9


