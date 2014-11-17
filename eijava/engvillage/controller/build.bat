@echo off

rem Execute ANT to perform the requird build target
%JAVA_HOME%\bin\java -version
%JAVA_HOME%\bin\java -classpath ..\..\ant\ant.jar;..\..\ant\xerces.jar;%JAVA_HOME%\lib\tools.jar org.apache.tools.ant.Main -Dtomcat.home=%TOMCAT_HOME% %1 %2 %3 %4 %5 %6 %7 %8 %9


