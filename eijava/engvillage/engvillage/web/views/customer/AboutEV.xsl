<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="xsl html"
>

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DBMASK">
	<xsl:value-of select="/PAGE/DBMASK"/>
</xsl:variable>

<html>

<head>
	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	<title>About Engineering Village</title>

</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

<center>

<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr><td valign="top">
        <xsl:apply-templates select="HEADER"/>
	</td></tr>
	<tr><td valign="top">
		<!-- Insert the Global Link table -->
		<xsl:apply-templates select="GLOBAL-LINKS">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		 	<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
		</xsl:apply-templates>

	</td></tr>
</table>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr><td valign="top" colspan="10" height="20" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" border="0"/></td></tr>
	<tr><td valign="top" colspan="10" height="20"><img src="/engresources/images/s.gif" border="0"/></td></tr>
</table>

</center>


<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
<tr><td valign="top"><a class="EvHeaderText">About Engineering Village</a></td></tr>
<tr><td valign="top" height="10"><img src="/engresources/images/s.gif" border="0" height="10"/></td></tr>
<tr><td valign="top"><a CLASS="MedBlackText">Engineering Village is a Web-based information service that offers a wide range of quality resources for information specialists, professionals, and researchers working in the applied science and engineering fields.<br/><br/>
Engineering Village features: </a>
<ul>
<li><a class="MedBlackText">Access to the top information resources in the applied science, technical and engineering fields:</a><br/>
<a class="MedBlackText"><b>Compendex </b><br/>
Compendex is the most comprehensive bibliographic database of engineering research available today, containing references and abstracts to over 5,000 engineering journals, conferences and technical reports. The broad subject areas of engineering and applied science are comprehensively represented. Coverage includes chemical and process engineering, computers and data processing, applied physics, electronics and communications, civil, mechanical and materials engineering, as well as narrower subtopics within all these major fields. Online coverage is from 1969 to the present. Around 250,000 new records are added to the database annually. Compendex is updated weekly to ensure access to critical developments in your field.</a><br/><br/>

<a class="MedBlackText"><b>Engineering Index Backfile within Compendex</b><br/>
The Engineering Index Backfile is available covering the information from the printed Engineering Index from 1884-1968. If your institution purchased the Backfile you will be able to search one Compendex database covering 130 years worth of engineering references. This will add about two million additional records to the database.</a><br/><br/>

<a class="MedBlackText"><b>Inspec</b><br/>
Inspec is a bibliographic database concentrating on electrical and electronic engineering, physics, information technology, and computer and control systems. The database contains over 7 million bibliographic records taken from 3,500 scientific and technical journals and 1,500 conference proceedings. Online coverage is from 1969 to the present, and files are updated weekly.</a><br/><br/>

<a class="MedBlackText"><b>NTIS</b><br/>
The National Technical Information Service (NTIS) is the premier source for accessing unclassified reports from influential U.S. and international government agencies. The database contains access to over two million critical citations from government departments such as NASA, the U.S. Department of Energy and the U.S. Department of Defense. The database is updated weekly. The NTIS database was created in 1964 but the material cited in it can date back as far as 1899. </a><br/><br/>

<a class="MedBlackText"><b>Referex Engineering</b><br/>
Referex Engineering provides access to the broadest and deepest available coverage of engineering reference titles in ebook format.  Collections within engineering include topic areas Materials and Mechanical, Electronics and Electrical, and Chemical, Petrochemical and Process.</a><br/><br/>

<a class="MedBlackText"><b>CRC ENGnetBASE</b><br/>
Your institution's add-on subscription to ENGnetBASE allows you access to some of the world's leading engineering handbooks published by CRC Press. As of October, 2003 ENGnetBASE has more than 190 titles available online with many more on the way as new books are published or updated. For a complete list of ENGnetBASE handbooks please see: http//www.engnetbase.com.  ENGnetBASE is produced by CRC Press, LLC. </a><br/><br/>


<a class="MedBlackText">Engineering Village also provides access to these partner sites: <br/>
<b>U.S. Patent and Trademark Office</b><br/>
<b>Esp@cenet</b><br/>
<b>Scirus</b><br/>
<b>GlobalSpec</b><br/>
<b>EEVL</b></a>
</li>
<br/><br/>
<li><a class="MedBlacktext">Two modes of searching: </a><br/>
<a class="MedBlackText"><b>Quick Search</b>, an easy-to-use form featuring pull-down menus and links to contextual help screens.<br/>
<b>Expert Search</b>, a powerful and flexible interface that supports Boolean searching on multiple fields.</a>
</li>
<li><a class="MedBlackText">Search assistance from information and industry professionals using the Engineering Village </a><a class="LgBlueLink" href="/controller/servlet/Controller?CID=referenceServices&amp;database={$DBMASK}">Reference Services</a>.
</li>
</ul>
<a class="MedBlackText">View or download Engineering Village user guides </a><a class="LgBlueLink" href="/controller/servlet/Controller?CID=help&amp;database={$DBMASK}">here.</a><br/>
<p>
<a class="MedBlackText">To request more information about Engineering Village, please complete our </a><a class="LgBlueLink" href="/controller/servlet/Controller?CID=feedback&amp;database={$DBMASK}">Customer Feedback form.</a>
</p>
<p>
<a class="MedBlackText">To learn about other Elsevier Engineering Information Inc.  products and services, please visit our Web site at </a><a class="LgBLueLink" HREF="#" onClick="window.open('http://www.ei.org', 'newpg', 'status=yes,resizable,scrollbars=yes,menubar=yes,location=yes,directories=yes,width=700,height=450')">http://www.ei.org</a>.
</p>
</td></tr>
</table>
</center>


<br/>

<!-- end of the lower area below the navigation bar -->
<xsl:apply-templates select="FOOTER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
</xsl:apply-templates>

</body>
</html>
</xsl:template>

</xsl:stylesheet>