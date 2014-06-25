<?xml version="1.0"?>
<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    exclude-result-prefixes="java html xsl"
>

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

    <!-- Table for the logo area -->
    <center>
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <tr><td valign="top"><img src="/static/images/ev2logo5.gif" border="0"/></td><td valign="middle" align="right"><a href="javascript:window.close();"><img src="/static/images/close.gif" border="0"/></a></td></tr>
    <tr><td valign="top" height="5" colspan="3"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
    <tr><td valign="top" height="2" bgcolor="#3173B5" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
    <tr><td valign="top" colspan="3" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
    </table>
    </center>

    <br/>

    <xsl:call-template name="HELP-TEMPLATE">
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="CH-ICON">false</xsl:with-param>
    </xsl:call-template>


    <br/><br/>

    <center>
    <table width="99%" cellspacing="0" cellpadding="0" border="0">
    <tr><td align="middle">
    <A CLASS="SmBlackText">&#169; 2011 Elsevier Inc. All rights reserved.</A>
    </td></tr>
    </table>
    </center>

</body>
</html>
</xsl:template>

</xsl:stylesheet>