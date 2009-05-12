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
	<xsl:apply-templates select="TYPE"/>
	<xsl:apply-templates select="COORDINATES"/>
	
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

<xsl:template match="TYPE">
	<tr><td valign="top" height="6"><img src="/engresources/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Type of term: </b>
		<xsl:value-of select="."/>
	</a></td></tr>
</xsl:template>

<xsl:template match="COORDINATES">
	<tr><td valign="top" height="6"><img src="/engresources/images/s.gif" border="0" height="6"/></td></tr>
	<tr><td valign="top"><a CLASS="SmBlackText"><b>Coordinates: </b>
		<xsl:value-of select="."/>
	</a>
	<tr><td valign="top" height="20"><img src="/engresources/images/s.gif" border="0" height="20"/></td></tr>
	
	<xsl:if test="/TREC/DRAWMAP">	
	  <xsl:variable name="COORD1">
	    <xsl:value-of select="//TREC/COORD1"/>
	  </xsl:variable>
	  <xsl:variable name="COORD2">
	    <xsl:value-of select="//TREC/COORD2"/>
	  </xsl:variable>
	  <xsl:variable name="COORD3">
	    <xsl:value-of select="//TREC/COORD3"/>
	  </xsl:variable>
	  <xsl:variable name="COORD4">
	    <xsl:value-of select="//TREC/COORD4"/>
	  </xsl:variable>
	  
	  <xsl:if test="$COORD1 != $COORD2 and $COORD3 != $COORD4">
	    <xsl:variable name="GMAPPATH">
	      <xsl:value-of select="concat($COORD2,',',$COORD3,'|',$COORD1,',',$COORD3,'|',$COORD1,',',$COORD4,'|',$COORD2,',',$COORD4,'|',$COORD2,',',$COORD3)"/>
	    </xsl:variable>
	    <tr>
	    <td>
	    <img src="{concat('http://maps.google.com/staticmap?path=rgba:0x0000FFff,weight:5|',$GMAPPATH,'&amp;size=250x200&amp;hl=en&amp;frame=true&amp;key=ABQIAAAA2JoOc6eMgOpYWlI72idgNRQ0Tqc8m8OJLX5cEM3TPnW6nFiZKRROkrDUTNbLerO7mgfClEI2yKOpJQ')}"/>
	    </td>
	    </tr>
	  </xsl:if>
	</xsl:if>
	
	</td></tr>
</xsl:template>

</xsl:stylesheet>