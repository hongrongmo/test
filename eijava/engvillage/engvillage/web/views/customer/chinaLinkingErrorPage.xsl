<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<!-- start of include files[includes header,globalnavigations and navigationbar] -->
<!-- <xsl:include href="SelectedSetHeader.xsl"/> -->
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="SelectedSetNavigationBar.xsl"/>
<xsl:include href="Footer.xsl"/>
<!-- end of include files -->


<xsl:template match="PAGE">

	<xsl:variable name="SESSION-ID">
		<xsl:value-of select="SESSION-ID"/>
	</xsl:variable>

	<xsl:variable name="SEARCH-TYPE">
		<xsl:value-of select="SEARCH-TYPE"/>
	</xsl:variable>

	<xsl:variable name="RESULTS-DATABASE">
		<xsl:value-of select="SESSION-DATA/DATABASE/SHORTNAME"/>
	</xsl:variable>

	<xsl:variable name="ENCODED-RESULT-DATABASE">
		<xsl:value-of select="java:encode($RESULTS-DATABASE)"/>
	</xsl:variable>

	<!-- check for search type ,if the search type value is null default value is Quick -->


	<html>
	<head>
		<title>Engineering Village Linking Error</title>
		<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	</head>

	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

		<!-- Start of logo table -->
		<center>
		<table border="0" width="99%" cellspacing="0" cellpadding="0">
			<tr><td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td><td valign="middle" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td></tr>
			<tr><td valign="top" height="5" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
			<tr><td valign="top" height="2" bgcolor="#3173B5" colspan="2"><img src="/engresources/images/spacer.gif" height="2" border="0"/></td></tr>
			<tr><td valign="top" height="10" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
		</table>
		</center>
		<!-- End of logo table -->
		<center>
		<!-- Display the message if there are no records in the selected set -->
		<table border="0" width="99%" cellspacing="0" cellpadding="0">

			<tr><td valign="top" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
			<tr><td valign="top"><A class="MedBlackText">Full-text linking for this article is unavailable at this time.</A></td></tr>

			<tr><td valign="top" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
			<tr><td valign="top"><A class="MedBlackText"><xsl:value-of select="/PAGE/ERROR-MESSAGE"/></A></td></tr>

      <xsl:for-each select="/PAGE/PUBLISHERS/PUBLISHER">
  			<tr><td valign="top" height="15" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
  			<tr><td valign="top"><li><a CLASS="SpLink"><xsl:attribute name="href"><xsl:value-of select="URL"/></xsl:attribute><xsl:value-of select="URL-LABEL"/></a></li></td></tr>
      </xsl:for-each>

		</table>

		</center>

		<!-- Include the footer -->

		<xsl:apply-templates select="FOOTER">
			  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			  <xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
		</xsl:apply-templates>

	</body>
	</html>

</xsl:template>

</xsl:stylesheet>