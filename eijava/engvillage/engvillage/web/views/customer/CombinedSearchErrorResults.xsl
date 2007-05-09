<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
>
<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl" />
<xsl:include href="Footer.xsl" />
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="NavigationBar.xsl" />

<xsl:template match="PAGE">

<html>
<head>
  <title>Error Combined Search</title>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" >

  <xsl:variable name="DATABASE"><xsl:value-of select="DBMASK"/></xsl:variable>

	<center>

  <xsl:apply-templates select="HEADER">
    <xsl:with-param name="SEARCH-TYPE" select="SESSION-DATA/SEARCH-TYPE"/>
  </xsl:apply-templates>

  <xsl:apply-templates select="GLOBAL-LINKS">
    <xsl:with-param name="SESSION-ID" select="SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>


	<!-- Insert the navigation bar -->

		<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
			<tr>
        <td align="left" valign="middle" height="24">
				  <img width="8" height="1" src="/engresources/images/s.gif" border="0" />
				  <a href="/controller/servlet/Controller?CID=quickSearch&amp;database={$DATABASE}">
					<img src="/engresources/images/ns.gif" border="0" />
					</a>
				</td>
			</tr>
		</table>

  	<table border="0" width="99%" cellspacing="0" cellpadding="0">
  		<tr>
  			<td valign="top" height="20"><img src="/engresources/images/s.gif" border="0" height="20"/></td>
  		</tr>
  		<tr>
  			<td valign="top"><a class="EvHeaderText">Search Error</a></td>
  		</tr>
		<tr>
			<td valign="top" height="10"><img src="/engresources/images/s.gif" border="0" height="10"/></td>
		</tr>
  		<tr>
  		<xsl:choose>
  			<xsl:when test="(//ERRSTR!='')">
  				<td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="//ERRSTR" />  Please check your search query.</a></td>
  			</xsl:when>
  			<xsl:otherwise>
  				<td valign="top"><a CLASS="MedBlackText">Your search did not find any results.  Please check your search query.</a></td>
  			</xsl:otherwise>
  		</xsl:choose>
  		</tr>
  	</table>


  <!-- INCLUDE THE FOOTER -->
	<xsl:apply-templates select="FOOTER">
		<xsl:with-param name="SESSION-ID" select="SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
	</xsl:apply-templates>

  </center>

<br/>

</body>
</html>
</xsl:template>
</xsl:stylesheet>
