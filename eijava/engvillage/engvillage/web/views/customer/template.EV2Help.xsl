<?xml version="1.0" ?>
   <xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java html xsl"
>

<xsl:template match="HELP-TEMPLATE" name="HELP-TEMPLATE">
  <xsl:param name="SELECTED-DB"/>
  <xsl:param name="CH-ICON"/>

    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>
    <script language="javascript">
    <xsl:comment>
    <xsl:text disable-output-escaping="yes">
    <![CDATA[
    var newWin;
    function openTrainingHelp(url)
    {
      newWin=window.open(url,'NewWindow','status=yes,resizable,scrollbars=1,menubar=yes,addressbar=1,width=700,height=500');
      if (window.focus) {newWin.focus()}
      return false;
    }
    ]]>
    </xsl:text>
   // </xsl:comment>
   </script>

<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
<tr><td valign="top">
	<table width="870" height="306" border="0">
	  <tr>
	    <td><a CLASS="taggroup_title">Help</a></td>
	    <td width="20"></td>
	    <td></td>
	  </tr>
	  <tr></tr>
	  <tr>
	    <td height="30" valign="top"><a CLASS="MedGreyTextTag"> Engineering Village help is available in the following formats:</a></td>
	    <td></td>
	    <td height="30" valign="top"><a CLASS="MedGreyTextTag"> Training Materials: </a></td>
	  </tr>
	  <tr>
	    <td>
	      <table border="0">
	       <tr>
		   <td  height = "1" valign="top"><a href="javascript: window.open('/EngineeringVillageHelp/Printed Documentation/Printed_Documentation.pdf','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');"><img src="/engresources/images/RobohelpPDF.gif" border="0"/></a></td>
		   <td  height = "1" valign="top"><a href="javascript: window.open('/EngineeringVillageHelp/Flash/Engineering_Village_Help.htm','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');"> <img src="/engresources/images/Flash.gif" border="0"/></a><img src="/engresources/images/s.gif" width="4" /></td>
		   <td  height = "1" valign="top"><a href="javascript: window.open('/EngineeringVillageHelp/Printed Documentation/Printed_Documentation.doc','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');"><img src="/engresources/images/Word.gif" border="0"/></a></td>
	       </tr>
	       <tr>
		   <td  height = "1" valign="top"><img src="/engresources/images/s.gif" width="17" height="9"/><a CLASS = "LgBlueLink" href="javascript: window.open('/EngineeringVillageHelp/Printed Documentation/Printed_Documentation.pdf','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');">PDF</a><img src="/engresources/images/s.gif" width="18" height="7"/></td>
		   <td  height = "1" valign="top"><img src="/engresources/images/s.gif" width="13" height="9"/><a CLASS = "LgBlueLink" href="javascript: window.open('/EngineeringVillageHelp/Flash/Engineering_Village_Help.htm','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');">Flash</a><img src="/engresources/images/s.gif" width="5" height="7"/></td>
		   <td  height = "1" valign="top"><img src="/engresources/images/s.gif" width="14" height="9"/><a CLASS = "LgBlueLink" href="javascript: window.open('/EngineeringVillageHelp/Printed Documentation/Printed_Documentation.doc','NewWindow','location=no,toolbar=no,menubar=no,status=no,scrollbars=yes,resizable=no,width=700,height=500');void('');">MS Word</a></td>
	       </tr>
	       	<tr><td valign="top"><img width="8" height="7" src="/engresources/images/s.gif" border="0" /></td></tr>
	      </table>
	      <a CLASS="MedGreyTextTag"> Chinese and Japanese versions of Engineering Village are also available:</a>
	      <table>
	      	<tr><td valign="top"><img width="8" height="7" src="/engresources/images/s.gif" border="0" /></td></tr>
	       <tr>
	           <td valign="top"><xsl:if test="($CH-ICON='true')"><a href="/controller/servlet/Controller?CID=chinahelp&amp;database={$SELECTED-DB}"><img src="/engresources/images/Chinese.gif" border="0"/></a></xsl:if> <img src="/engresources/images/s.gif" width="30" height="7"/><a href="javascript: window.open('/engresources/help/EVJapaneseHelp.pdf','NewWindow','status=yes,resizable,scrollbars=1,menubar=yes,addressbar=1,width=700,height=500');void('');"><img src="/engresources/images/Japnes.gif" border="0"/></a></td>
	       </tr>
	      </table>
	    </td>
	    <td></td>
	    <td valign="top">
		    <table  border="0">
		    <tr><td height="10" valign="top">
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/CPXsource.pdf')">Compendex source list</a></td></tr>
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/EngVillCompendex.ppt')">Searching Compendex on Engineering Village</a></td></tr>
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/RSSBlogs.ppt')">Using RSS and Blog this! on Engineering Village</a></td></tr>
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/faceted%20searching%20and%20browsing_NewT.ppt')">Faceted Searching and Browsing on Engineering Village</a></td></tr>
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/PatentsonEV2.ppt')">Patents searching on Engineering Village</a></td></tr>
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/Inspec.ppt')">Inspec on Engineering Village</a></td></tr>
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/NTIS.ppt')">NTIS on Engineering Village</a></td></tr>
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/NTISandPatents.ppt')">Combining NTIS and Patents Databases to Identify Technology Transfer of Government Funded Research</a></td></tr>
		    <tr><td valign="top"><a CLASS="LgBlueLink" href="#" onclick = "return openTrainingHelp('http://www.ei.org/documents/FromNanosciencetoNanotechnology.ppt')">From Nanoscience to Nanotechnology : Tracking carbon nanotubes products from research through development to patent issuance</a></td></tr>
		    </td></tr>
		    </table>
	    </td>
	  </tr>
	</table>
     </td>
  </tr>
</table>
</center>
</xsl:template>
</xsl:stylesheet>