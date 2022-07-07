set JAVA_HOME=C:\jdk1.5.0_06
set JAVA_HOME="C:\Program Files\Java\jdk1.6.0_30"
%JAVA_HOME%\bin\java -version

java -Xms64m -Xmx512m -classpath c:\baja\eijava2012-03-02\ext\xerces.jar;c:\baja\eijava\ext\ojdbc14.jar;c:\baja\eijava\ext\oro.jar;c:\baja\eijava2012-03-02-thesaurus\dist\eilib.jar;c:\baja\eijava2012-03-02-thesaurus\ext\jdom.jar org.ei.thesaurus.georef.GeorefThesaurusXML THES2008.XML grf



