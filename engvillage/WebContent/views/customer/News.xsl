<?xml version="1.0"?>

<xsl:stylesheet
	 version="1.0"
	 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	 >

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>

<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DATABASE">
	<xsl:value-of select="DATABASE"/>
</xsl:variable>

<!-- TODO: Once client customization is done the hardcoding of database to Compendex
	is to be changed to the default database's name for that user-->

<!-- TODO: THe search type has been harcoded to Quick -->


<!-- TODO: Once client customization is done the hardcoding of databaseid to cpx
	is to be changed to the default databaseid for that user-->

<html>

  <head>
    <title>News</title>
		<SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
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
			 	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
			 	<xsl:with-param name="LINK">News</xsl:with-param>
			</xsl:apply-templates>

		</td></tr>
	</table>
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
		<tr><td valign="top" colspan="11" height="20" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0"/></td></tr>
	</table>

</center>

<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
<tr><td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
</table>
</center>


<br/>

<br/>

<!-- end of the lower area below the navigation bar -->
<xsl:apply-templates select="FOOTER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
</xsl:apply-templates>

</body>
</html>
</xsl:template>

</xsl:stylesheet>