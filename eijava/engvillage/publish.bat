@echo off
rem build.bat -- Build Script for the "Hello, World" Application
rem $Id: build.bat,v 1.3.2.3 2000/12/27 17:20:11 marcsaeg Exp $

rem set _CP=%CP%

rem Identify the custom class path components we need

rem Execute ANT to perform the requird build target

set ANT_HOME=C:\ant
%JAVA_HOME%\bin\java -version
%JAVA_HOME%\bin\java -classpath ..\ant\js.jar;..\ant\NetComponents.jar;..\ant\bsf.jar;..\ant\optional.jar;..\ant\ant.jar;..\ant\xerces.jar;%JAVA_HOME%\lib\tools.jar org.apache.tools.ant.Main -buildfile publish.xml %1 %2 %3 %4 %5 %6 %7 %8 %9

rem set CP=%_CP%
rem set _CP=
