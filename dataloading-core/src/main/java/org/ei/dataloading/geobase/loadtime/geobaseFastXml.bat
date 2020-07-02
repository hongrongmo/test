
set CLASSPATH=.;c:\jdk1.3.1_06\bin;c:\miami\eijava\dist\eilib.jar;c:\testData\eijava\ext\ojdbc14.jar;c:\miami\eijava\ext\oro.jar;c:\miami\eijava\ext\xerces.jar;c:\miami\eijava\ext\saxon.jar;c:\miami\eijava\ext\xmlParserAPIs.jar

java org.ei.data.geobase.loadtime.GeobaseCombiner jdbc:oracle:thin:@206.137.75.51:1521:EI  oracle.jdbc.driver.OracleDriver ap_ev_search  200601 50000 1500000 geo_master
