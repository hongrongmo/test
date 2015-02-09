set CLASSPATH= .;c:\Users\moh\git\EngineeringVillage\eijava\ext\xerces.jar;c:\jdk1.3.1_06\bin;C:\Users\moh\git\EngineeringVillage\eijava\dist\eilib.jar;c:\Users\moh\git\EngineeringVillage\eijava\ext\ojdbc14.jar;c:\Users\moh\git\EngineeringVillage\eijava\ext\xmlParserAPIs.jar;c:\Users\moh\git\EngineeringVillage\eijava\ext\oro.jar;c:\Users\moh\git\EngineeringVillage\eijava\ext\saxon.jar;c:\Users\moh\git\EngineeringVillage\eijava\ext\jdom.jar

set datafilename = 20080122_EP_Grants_BIBLIO_03
set loadnumber = 200810
echo %datafilename%
echo %loadnumber%
java org.ei.data.upt.loadtime.PatentXmlReader in out 201500
