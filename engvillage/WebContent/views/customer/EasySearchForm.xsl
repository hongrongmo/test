<?xml version="1.0"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
	exclude-result-prefixes="java xsl html DD"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl" />
<xsl:include href="Footer.xsl" />
<xsl:include href="GlobalLinks.xsl"/>

<xsl:include href="common/forms/EasySearchForm.xsl" />

<xsl:template match="QUICK-SEARCH">

	<xsl:variable name="DATABASE">
		<xsl:value-of select="SELECTED-DATABASE"/>
	</xsl:variable>

	<xsl:variable name="DATABASE-DISPLAYNAME">
		<xsl:value-of select="DD:getDisplayName(SELECTED-DATABASE)"/>
	</xsl:variable>

	<xsl:variable name="SESSION-ID">
		<xsl:value-of select="SESSION-ID"/>
	</xsl:variable>

<html>
	<head>
		<META http-equiv="Expires" content="0"/>
		<META http-equiv="Pragma" content="no-cache"/>
		<META http-equiv="Cache-Control" content="no-cache"/>
		<title>Engineering Village - Easy Search</title>
		<SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
	</head>

	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

  <center>
  
	  <!-- APPLY THE HEADER -->
    <xsl:apply-templates select="HEADER">
      <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
      <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
      <xsl:with-param name="SEARCH-TYPE">EasyForm</xsl:with-param>
    </xsl:apply-templates>

	  <!-- APPLY THE TABBED NAVIGATION BAR -->
    <xsl:apply-templates select="GLOBAL-LINKS">
    	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    	<xsl:with-param name="LINK">Easy</xsl:with-param>
    </xsl:apply-templates>

    <!-- Empty Gray Navigation Bar -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
    	<tr><td valign="middle" height="24" bgcolor="#C3C8D1"><img src="/static/images/s.gif" border="0"/></td></tr>
    </table>

    <center>
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
      <tr>
        <td width="1" bgcolor="#C3C8D1"><img src="/static/images/s.gif" width="1"/></td>
        <td valign="top" bgcolor="#F5F5F5">
      
        <xsl:apply-templates select="EASY-SEARCH">
          <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
          <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        </xsl:apply-templates>
      
        </td>
        <td width="1" bgcolor="#C3C8D1"><img src="/static/images/s.gif" width="1"/></td>
      </tr>
    	<tr>
      	<td colspan="3" height="1" bgcolor="#C3C8D1"><img src="/static/images/s.gif" height="1"/></td>
      </tr>
      </table>
    </center>

	<!-- APPLY THE FOOTER -->
  <xsl:apply-templates select="FOOTER">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>

	<!-- ADDED BY JM autofocus ;-) -->
    <xsl:text disable-output-escaping="yes">
	  <![CDATA[
      <script TYPE="text/javascript" language="javascript">
        	if(typeof(document.easysearch.searchWord1) != 'undefined') 
        	{
        	  document.easysearch.searchWord1.focus();
		      }
      </script>
		]]>
    </xsl:text>

  </center>
  
	</body>
</html>

</xsl:template>
</xsl:stylesheet>
