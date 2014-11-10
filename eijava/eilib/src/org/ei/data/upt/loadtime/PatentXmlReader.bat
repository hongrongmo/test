set CLASSPATH= .;c:\baja\eijava\ext\xerces.jar;c:\jdk1.3.1_06\bin;C:\baja\eijava\dist\eilib.jar;c:\baja\eijava\ext\ojdbc14.jar;c:\baja\eijava\ext\xmlParserAPIs.jar;c:\baja\eijava\ext\oro.jar;c:\baja\eijava\ext\saxon.jar;c:\baja\eijava\ext\jdom.jar

set datafilename = 20080122_EP_Grants_BIBLIO_03
set loadnumber = 200810
echo %datafilename%
echo %loadnumber%
java org.ei.data.upt.loadtime.PatentXmlReader "C:\Documents and Settings\moh\My Documents\patent_document\zipfile\EP\grants" 200813_EP_Grants 200813