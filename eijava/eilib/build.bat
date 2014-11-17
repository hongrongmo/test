@echo off

rem Execute ANT to perform the requird build target
rem set JAVA_HOME=c:\j2sdk1.4.2_03
rem set JAVA_HOME=C:\jdk1.6.0_29
set JAVA_HOME=C:\jdk1.7.0_51
%JAVA_HOME%\bin\java -version
%JAVA_HOME%\bin\java -classpath c:\baja\ant\ant.jar;c:\baja\ant\xerces.jar;%JAVA_HOME%\lib\tools.jar org.apache.tools.ant.Main -Dtomcat.home=%TOMCAT_HOME% %1 %2 %3 %4 %5 %6 %7 %8 %9


