set CLASSPATH=.;c:\jdk1.3.1_06\bin;c:\javaplatform2\eijava\dist\eilib.jar;c:\javaplatform2\eijava\ext\oracle-jdbc.jar


java org.ei.data.LoadLookupNtis %1 %2 %3 %4 %5 %6 %7



rem Parm1: url         - jdbc:oracle:thin:@stage.ei.org:1521:apl2
rem Parm2: username    - u_ntis
rem Parm3: password    - team
rem Parm4: Driver      - oracle.jdbc.driver.OracleDriver
rem Parm5: Lookup        - AUS/AF/CVS/All
rem Parm6: Load Number1  - 200310/All
rem Parm7: Load Number2 - 200320


rem jdbc:oracle:thin:@stage.ei.org:1521:apl2 u_ntis team oracle.jdbc.driver.OracleDriver all all
rem jdbc:oracle:thin:@e3000.ei.org:1521:apli new_office devel oracle.jdbc.driver.OracleDriver all all




