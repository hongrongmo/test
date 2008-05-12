<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="xsl html"
>

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

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
    <li>
    <a class="MedBlackText">Access to the top information resources in the applied science, technical and engineering fields:</a><br/>
    <a class="MedBlackText"><b>Compendex</b><br/>Compendex is the most comprehensive bibliographic database of scientific and technical engineering research available, covering all engineering disciplines. It includes millions of bibliographic citations and abstracts from thousands of engineering journals and conference proceedings. When combined with the Engineering Index Backfile (1884-1969), Compendex covers well over 120 years of core engineering literature.</a><br/><br/>
    <a class="MedBlackText"><b>Ei Backfile</b><br/>Since its creation in 1884, the Engineering Index&#174; has covered virtually every major engineering innovation from around the world. It serves as the historical record of virtually every major engineering innovation in the past century.  The Engineering Index Backfile can be searched via controlled vocabulary or free text.   All volume years can be searched simultaneously, eliminating the need to scan multiple volumes. Combined with the Compendex&#174; database, the Ei Backfile provides users with the most comprehensive overview of well over 120 years of engineering literature.</a><br/><br/>
    <a class="MedBlackText"><b>Inspec</b><br/>Inspec includes bibliographic citations and indexed abstracts from publications in the fields of physics, electrical and electronic engineering, communications, computer science, control engineering, information technology, manufacturing and mechanical engineering, operations research, material science, oceanography, engineering mathematics, nuclear engineering, environmental science, geophysics, nanotechnology, biomedical technology and biophysics.</a><br/><br/>
    <a class="MedBlackText"><b>NTIS</b><br/>NTIS (The National Technical Information Service) is the premier database for accessing unclassified reports from influential U.S. and international government agencies. The database contains access to millions of critical citations from government departments such as NASA, the U.S. Department of Energy and the U.S. Department of Defense.</a><br/><br/>
    <a class="MedBlackText"><b>Referex</b><br/>Referex is comprised of six collections of professionally focussed e-books, bringing together key sources of engineering reference material. The database is fully searchable and delivers full-text content from the following collections: Chemical, Petrochemical and Process Engineering; Civil and Environmental Engineering; Computing; Electronics and Electrical Engineering; Mechanical Engineering and Materials; Networking and Security.</a><br/><br/>
    <a class="MedBlackText"><b>GeoBase</b><br/>GeoBase&#174; is a multidisciplinary database of indexed research literature on the earth sciences, including geology, human and physical geography, environmental sciences, oceanography, geomechanics, alternative energy sources, pollution, waste management and nature conservation. Covering thousands of peer-reviewed journals, trade publications, book series and conference proceedings, GeoBase has the most international coverage of any database in the field.</a><br/><br/>
    <a class="MedBlackText"><b>GeoRef</b><br/>The American Geological Institute's GeoRef database provides a comprehensive history of geology and its subfields.  With more than 2.9 million records, it includes the information researchers need to evaluate geology and its role in the context of history, economics, engineering, and more.</a><br/><br/>
    <a class="MedBlackText"><b>EI Patents</b><br/>EI Patents comprises millions of patents from the US Patent and Trademark Office and European Patent Office. This database can be cross-searched with other databases on Engineering Village, retrieving results both from patents and scientific literature, a significant advantage for both researchers and businesses. Ei Patents offers more reliable and analytical patent searching features than are supported by the generic free web.</a><br/><br/>
    <a class="MedBlackText"><b>EnCompassLIT</b><br/>EnCompassLIT&#153; is a bibliographic service uniquely devoted to covering technical literature published worldwide on the downstream petroleum, petrochemical, natural gas, energy and allied industries. Upstream coverage focuses solely on oil field chemicals. The EnCompass Thesaurus facilitates precision searching via controlled vocabulary.</a><br/><br/>
    <a class="MedBlackText"><b>EnCompassPAT</b><br/>EnCompassPAT&#153; is a patent service uniquely devoted to covering worldwide patents on the downstream petroleum, petrochemical, natural gas, energy and allied industries. Patents indexed are selected from 40 international patenting authorities. The EnCompass Thesaurus facilitates precision searching via controlled vocabulary.</a><br/><br/>
    <a class="MedBlackText"><b>Chimica</b><br/>Chimica provides access to hundreds of the most influential international journals focused on chemistry and chemical engineering, with emphasis on applied and analytical chemistry, extending to physical chemistry, health and safety, organic and inorganic chemistry, and materials science. Coverage in Chimica starts in 1970.</a><br/><br/>
    <a class="MedBlackText"><b>CBNB</b><br/>Chemical Business NewsBase (CBNB) is the leading provider of global chemical business news and information. Search for facts, figures, views, and comments on the chemical industry worldwide, from 1985 to the present.  Sources include hundreds of core trade journals, newspapers and company newsletters, plus books, market research reports, annual and interim company reports, press releases and other "grey literature" sources.</a><br/><br/>
    <a class="MedBlackText"><b>PaperChem</b><br/>PaperChem is a database comprised of indexed bibliographic citations and abstracts from journals, conference proceedings, and technical reports focused on pulp and paper technology. Coverage is from 1967 to present, and covers such topics as the chemistry of cellulose, corrugated and particle board; films, foils and laminates; forestry and pulpwood, lignin and extractives, non-wovens, and much more.</a><br/><br/>
    </li>
    <br/>
    <li><a class="MedBlacktext">Three modes of searching: </a><br/>
    <a class="MedBlackText"><b>Easy Search</b>, the simplest way to search with a single field text box.<br/></a>
    <a class="MedBlackText"><b>Quick Search</b>, an easy-to-use form featuring pull-down menus and links to contextual help screens.<br/>
    <b>Expert Search</b>, a powerful and flexible interface that supports Boolean searching on multiple fields.</a>
    </li>
    <li><a class="MedBlackText">Search assistance from information and industry professionals using the Engineering Village </a><a class="LgBlueLink" href="/controller/servlet/Controller?CID=referenceServices&amp;database={$DBMASK}">Reference Services</a>.
    </li>
  </ul>
  <a class="MedBlackText">View or download Engineering Village user guides </a><a class="LgBlueLink" href="/controller/servlet/Controller?CID=help&amp;database={$DBMASK}">here.</a><br/>
  <p><a class="MedBlackText">To request more information about Engineering Village, please complete our </a><a class="LgBlueLink" href="/controller/servlet/Controller?CID=feedback&amp;database={$DBMASK}">Customer Feedback form.</a></p>
  <p><a class="MedBlackText">To learn about other Elsevier Engineering Information products and services, please visit our Web site at </a><a class="LgBLueLink" HREF="#" onClick="window.open('http://www.ei.org', 'newpg', 'status=yes,resizable,scrollbars=yes,menubar=yes,location=yes,directories=yes,width=700,height=450')">http://www.ei.org</a>.</p>
  <p><a class="MedBlackText">Last revised:  May 15, 2008</a></p>
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