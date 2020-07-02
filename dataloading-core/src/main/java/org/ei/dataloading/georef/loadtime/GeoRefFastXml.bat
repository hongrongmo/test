
set EV_HOME=c:\baja\eijava
set EV_CLASSPATH=%EV_HOME%\dist\eilib.jar;%EV_HOME%\ext\ojdbc14.jar;%EV_HOME%\ext\oro.jar;%EV_HOME%\ext\xerces.jar;%EV_HOME%\ext\saxon.jar;%EV_HOME%\ext\xmlParserAPIs.jar

java -cp %EV_CLASSPATH% org.ei.data.georef.loadtime.GeoRefCombiner jdbc:oracle:thin:@206.137.75.51:1521:EI  oracle.jdbc.driver.OracleDriver ap_ev_search  200601 50000 1500000 georef_master
