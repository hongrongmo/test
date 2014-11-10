<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLDecoder"
	exclude-result-prefixes="java html xsl"
>

<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="Footer.xsl" />


<xsl:template match="PAGE">

		<xsl:variable name="FOLDER-NAME">
			<xsl:value-of select="FOLDER-NAME"/>
		</xsl:variable>
		<xsl:variable name="FOLDER-ID">
      <xsl:value-of select="FOLDER-ID"/>
		</xsl:variable>

		<xsl:variable name="SESSION-ID">
			<xsl:value-of select="SESSION-ID"/>
		</xsl:variable>
		<xsl:variable name="DATABASE-ID">
      <xsl:value-of select="DATABASE-ID"/>
		</xsl:variable>


<html>
<head>
    <title>Records Saved</title>
  	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

  <!-- APPLY HEADER -->
  <xsl:apply-templates select="HEADER">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE-ID"/>
  </xsl:apply-templates>

	<center>
	
	<!-- Insert the Global Links -->
  <!-- logo, search history, selected records, my folders. end session -->
  <xsl:apply-templates select="GLOBAL-LINKS">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE-ID"/>
  </xsl:apply-templates>

	<!-- Empty Navigation Bar -->
  <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
  <tr >
  <td height="20" bgcolor="#C3C8D1" ><img src="/static/images/s.gif" /></td>
  </tr>
  </table>

  <!-- Area for confirmation message -->
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <tr><td valign="top" height="20"><img src="/static/images/s.gif" border="0" height="20"/></td></tr>
    <tr><td valign="top"><a class="EvHeaderText">Records Saved</a></td></tr>
    <tr>
      <td valign="top"><a class="MedBlackText">Your selected records have been saved to </a>
      <a CLASS="LgBlueLink">
        <xsl:attribute name="href">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=viewCitationSavedRecords&amp;database=<xsl:value-of select="$DATABASE-ID"/>&amp;folderid=<xsl:value-of select="$FOLDER-ID"/></xsl:attribute>
        <b><xsl:value-of select="$FOLDER-NAME"/></b>
      </a>
      &#160;<a class="MedBlackText">folder.</a>
      </td>
      </tr>
    <tr><td valign="top" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
    <tr>
      <td valign="top">
      <a class="MedBlackText">Return to </a><a CLASS="LgBlueLink"><xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="java:decode(BACKURL)"/></xsl:attribute>previous</a><a class="MedBlackText"> page.</a>
      </td>
    </tr>
    <tr><td valign="top" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
  </table>
  </center>
  
  <!-- Insert the Footer table -->
  <xsl:apply-templates select="FOOTER">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE-ID"/>
  </xsl:apply-templates>
  
</body>
</html>

</xsl:template>
</xsl:stylesheet>