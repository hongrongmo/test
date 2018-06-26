
set CLASSPATH=.;c:\jdk1.3.1_06\bin;c:\javaplatform2\eijava\dist\eilib.jar;c:\javaplatform2\eijava\ext\oracle-jdbc.jar;c:\javaplatform2\eijava\eilib\ext\oro.jar

java org.ei.data.LoadLookup jdbc:oracle:thin:@stage.ei.org:1521:apl2 ev_search team oracle.jdbc.driver.OracleDriver ibf ST 193000 200501



rem Parm1: url 		- jdbc:oracle:thin:@stage.ei.org:1521:apl2
rem Parm2: username 	- e_java
rem Parm3: password 	- team 
rem Parm4: Driver 	- oracle.jdbc.driver.OracleDriver 
rem Parm5: Database 	- cpx / ins /ntis /ibf
rem Parm6: Lookup	- AUS/AF/PUB/ST/All/CVS
rem Parm7: Load Number1	- 200310 
rem Parm8: Load Number2 - 200320


rem Compendex
rem jdbc:oracle:thin:@stage.ei.org:1521:apl2 e_java team oracle.jdbc.driver.OracleDriver cpx all 200310 200320

rem Inspec
rem jdbc:oracle:thin:@stage.ei.org:1521:apl2 i_java team oracle.jdbc.driver.OracleDriver ins all 200310 200320

