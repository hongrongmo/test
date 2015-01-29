set JAVA_HOME=C:\Baja\j2sdk1.4.2_11
%JAVA_HOME%\bin\java -version

java -Xms128m -Xmx1024m -classpath c:\eijava\ext\xerces.jar;c:\eijava\ext\ojdbc14.jar;c:\eijava\ext\oro.jar;C:\Baja-Hongrong-new-eilib-05-12-2014\eijava\dist\eilib.jar;c:\eijava\ext\jdom.jar org.ei.data.inspec.loadtime.INSPECCombiner jdbc:oracle:thin:@localhost:1521:eid oracle.jdbc.driver.OracleDriver db_inspec ny5av 197004 50000 add ins_1969_master prod

