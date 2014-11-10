<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
	  xmlns:java="java:java.net.URLEncoder"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    exclude-result-prefixes="xsl html java DD"
>
  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl" />
<xsl:include href="Footer.xsl" />
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="common/forms/EBookSearchForm.xsl" />

<xsl:strip-space elements="html:* xsl:*" />

  <xsl:template match="QUICK-SEARCH">

    <xsl:variable name="YEAR-STRING">
      <xsl:value-of select="YEAR-STRING"/>
    </xsl:variable>

    <xsl:variable name="DATABASE">
      <xsl:value-of select="SELECTED-DATABASE"/>
    </xsl:variable>

    <xsl:variable name="SESSION-ID">
      <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>

    <xsl:variable name="DATABASE-DISPLAYNAME">
      <xsl:value-of select="DD:getDisplayName($DATABASE)"/>
    </xsl:variable>

    <xsl:variable name="USERMASK">
      <xsl:value-of select="//USERMASK"/>
    </xsl:variable>

    <xsl:variable name="SEARCH-WORD-1"><xsl:value-of select="//SESSION-DATA/SEARCH-PHRASE/SEARCH-PHRASE-1"/></xsl:variable>
    <xsl:variable name="SORT-OPTION"><xsl:value-of select="//SESSION-DATA/SORT-OPTION"/></xsl:variable>
    <xsl:variable name="LASTFOURUPDATES"><xsl:value-of select="//SESSION-DATA/LASTFOURUPDATES"/></xsl:variable>
    <xsl:variable name="START-YEAR"><xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:variable>
    <xsl:variable name="END-YEAR"><xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:variable>

    <!-- added Autostemming to XS -->
    <xsl:variable name="AUTOSTEMMING"><xsl:value-of select="//SESSION-DATA/AUTOSTEMMING"/></xsl:variable>

<html>
	<head>
		<META http-equiv="Expires" content="0"/>
		<META http-equiv="Pragma" content="no-cache"/>
		<META http-equiv="Cache-Control" content="no-cache"/>
		<title>Engineering Village - eBook Search</title>
		<SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	</head>
  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
    <center>

    	 <!-- INCLUDE THE HEADER -->
      <xsl:apply-templates select="HEADER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
        <xsl:with-param name="SEARCH-TYPE">Book</xsl:with-param>
      </xsl:apply-templates>

  	  <!-- INCLUDE THE GLOBAL LINKS BAR -->
      <xsl:apply-templates select="GLOBAL-LINKS">
  		  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  		  <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  		  <xsl:with-param name="LINK">Book</xsl:with-param>
  		</xsl:apply-templates>

	  <!-- Start of Ebook Search Form -->
		<xsl:apply-templates select="EBOOK-SEARCH">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		</xsl:apply-templates>
	  <!-- End of Ebook Search form -->

    	<!-- INCLUDE FOOTER  -->
    	<xsl:apply-templates select="FOOTER">
    		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    	</xsl:apply-templates>
    </center>
    
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/RemoteDbLink_V5.js"/>
  </body>
</html>

</xsl:template>

</xsl:stylesheet>
