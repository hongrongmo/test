<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<!--
   This xsl file display information about the selected set if there are no documents in the selected set
   All the business rules which govern the display format are also provided.
  -->
<!-- start of include files[includes header,globalnavigations and navigationbar] -->
<xsl:include href="Header.xsl"/>
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
		<xsl:value-of select="DBMASK"/>
	</xsl:variable>

  <html>
	<head>
		<title>Engineering Village Selected Set</title>
		<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	</head>

	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
	<center>

		<!-- Start of header -->
		<xsl:apply-templates select="HEADER"/>

		<!-- Start of globalnavigation bar -->
		<xsl:apply-templates select="GLOBAL-LINKS">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
		  <xsl:with-param name="LINK" select="$SEARCH-TYPE"/>
		</xsl:apply-templates>

		<!-- Start of top  blue bar for navigation-->
		<xsl:apply-templates select="SELECTED-SET-NAVIGATION-BAR"/>

		<!-- Display the message if there are no records in the selected set -->
		<table border="0" width="99%" cellspacing="0" cellpadding="0">
			<tr><td valign="top" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
			<tr><td valign="top"><a class="EvHeaderText">Selected Records</a></td></tr>
			<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>			
			<tr><td valign="top"><a CLASS="MedBlackText">Currently there are no records selected.</a></td></tr>
		</table>


		<!-- Include the footer -->

		<xsl:apply-templates select="FOOTER">
		  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		  <xsl:with-param name="SELECTED-DB" select="$RESULTS-DATABASE"/>
		</xsl:apply-templates>

	</center>

	</body>
	</html>

</xsl:template>

</xsl:stylesheet>