<?xml version="1.0"?>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  exclude-result-prefixes="java xsl html"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template match="TREC">

<html>
<head>
  <title>Scope notes</title>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
  <style type="text/css">
    body {font-family:arial,verdana,geneva; margin: 5; padding: 5; background: #CEEBFF;}
  </style>
</head>

<body bgcolor="#CEEBFF" topmargin="0">
<table border="0" cellspacing="0" cellpadding="0" width="99%">
  <tr><td valign="top" height="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
  <tr><td valign="top" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td></tr>
  <tr><td valign="top" height="10"><img src="/engresources/images/s.gif" border="0" height="10"/></td></tr>
  <tr><td valign="top"><a CLASS="DBlueText"><b><xsl:value-of select="MT"/></b></a></td></tr>

	<xsl:apply-templates select="SN"/>
	<xsl:apply-templates select="DATE"/>
	<xsl:apply-templates select="HSN"/>
	<xsl:apply-templates select="CPAGE"/>

  <tr><td valign="top" height="20"><img src="/engresources/images/s.gif" border="0" height="20"/></td></tr>

</table>
</body>
</html>

</xsl:template>

<xsl:template match="CPAGE">
	<tr><td valign="top" height="6"><img src="/engresources/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top">
	<a CLASS="SmBlackText"><b>Related classification codes: </b>
		<xsl:apply-templates/>
	</a></td></tr>
</xsl:template>

<xsl:template match="CL">
	<xsl:apply-templates select="CID"/>:
	<xsl:value-of select="CTI" disable-output-escaping="yes"/>
	<xsl:if test="not(position()=last())">; </xsl:if>
</xsl:template>

<xsl:template match="SN">
	<tr><td valign="top" height="6"><img src="/engresources/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Scope notes: </b>
		<xsl:value-of select="."/>
	</a></td></tr>
</xsl:template>

<xsl:template match="DATE">
	<tr><td valign="top" height="6"><img src="/engresources/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Introduced: </b>
		<xsl:value-of select="."/>
	</a></td></tr>
</xsl:template>

<xsl:template match="OPT">
	(<xsl:apply-templates/>)
</xsl:template>

<xsl:template match="CID">
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="HSN">
	<tr><td valign="top" height="6"><img src="/engresources/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>History: </b>
		<xsl:value-of select="."/>
	</a></td></tr>
</xsl:template>

</xsl:stylesheet>