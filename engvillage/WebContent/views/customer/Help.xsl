<?xml version="1.0"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java html xsl"
>

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>
<xsl:include href="template.EV2Help.xsl"/>
<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
    <xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DATABASE">
    <xsl:value-of select="/PAGE/DBMASK"/>
</xsl:variable>
<html>

<head>
    <title>Engineering Village - Help</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

  <center>

    <xsl:apply-templates select="HEADER"/>

    <!-- Insert the Global Link table -->
    <xsl:apply-templates select="GLOBAL-LINKS">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="LINK">help</xsl:with-param>
    </xsl:apply-templates>

    <!-- Empty Gray Navigation Bar -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
        <tr><td valign="middle" height="24" bgcolor="#C3C8D1"><img src="/static/images/s.gif" border="0"/></td></tr>
    </table>

    <br/>

    <xsl:call-template name="HELP-TEMPLATE">
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="CH-ICON">true</xsl:with-param>
    </xsl:call-template>

    <br/><br/>

    <!-- end of the lower area below the navigation bar -->
    <xsl:apply-templates select="FOOTER">
            <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
            <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    </xsl:apply-templates>
  </center>

</body>
</html>
</xsl:template>

</xsl:stylesheet>